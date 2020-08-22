package io.github.louis9902.toughenough.stats;

import io.github.louis9902.toughenough.init.Gameplay;
import io.github.louis9902.toughenough.item.Drinkable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

/**
 * TODO:
 * - make player not regenerate twice (hunger and thirst)
 * - give player sprinting debuff when not enough thirst
 * - make game rule to disable thirst
 */
public class ThirstManager {

    public static final int MAX_THIRST_LEVEL = 20;
    public static final int MIN_THIRST_REGENERATION_LEVEL = 18;
    public static final float THIRST_EXHAUSTION_THRESHOLD = 4.0f;
    public static final float HYDRATION_MULTIPLIER_MAGIC_CONSTANT = 2.0F;

    private int prevThirstLevel;
    /**
     * The amount of
     */
    private int thirst;
    /**
     * This value determines how long a player can sustain the
     * current exhaustion before the thirst is used.
     */
    private float hydration;
    private float exhaustion;

    /**
     * This value represents the time passed since the user took the last time
     * damage because of thirst.
     */
    private int counter;

    public ThirstManager() {
        thirst = 20;
        hydration = 5.0f;
    }

    public void add(int thirst, float hydration) {
        this.thirst = Math.min(MAX_THIRST_LEVEL, this.thirst + thirst);
        this.hydration = Math.min((float)this.thirst, this.hydration + (thirst * hydration * HYDRATION_MULTIPLIER_MAGIC_CONSTANT));
    }

    public void drink(ItemStack stack) {
        Drinkable drinkable = stack.getItem() instanceof Drinkable ? ((Drinkable) stack.getItem()) : null;
        if (drinkable != null) {
            this.add(drinkable.getThirst(), drinkable.getHydrationModifier());
        }
    }

    private static boolean canPlayerRegenerateHealth(PlayerEntity player) {
        return player.getHealth() > 0.0f && player.getHealth() < player.getMaxHealth();
    }

    public void update(PlayerEntity player) {
        if (!Gameplay.isThirstEnabled(player.world) || player.isCreative()) return;

        Difficulty difficulty = player.world.getDifficulty();

        // if the exhaustion is higher than the max we normalise
        if (this.exhaustion > THIRST_EXHAUSTION_THRESHOLD) {
            this.exhaustion -= THIRST_EXHAUSTION_THRESHOLD;

            if (this.hydration > 0.0f) {
                // if we have hydration we drain some of it
                this.hydration = Math.max(this.hydration - 1.0f, 0.0f);
            } else if (difficulty != Difficulty.PEACEFUL) {
                // otherwise we drain the thirst
                this.thirst = Math.max(this.thirst - 1, 0);
            }
            // TODO: make food also dependant on thirst and the other way around
            boolean regenerate = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);

            if (regenerate && canPlayerRegenerateHealth(player)) {
                // check if player can regenerate naturally
                this.counter++;

                if (this.thirst >= MAX_THIRST_LEVEL && this.hydration > 0.0f) {
                    // if so we want to use the fast regeneration first (hydration)
                    if (this.counter >= 10 /* 0.5 sec */) {
                        float heal = Math.min(this.hydration, 6.0f);
                        player.heal(heal / 6.0f);
                        this.addExhaustion(heal / 6.0f);
                        this.counter = 0;
                    }
                } else if (this.thirst >= MIN_THIRST_REGENERATION_LEVEL) {
                    // if we dont have hydration anymore we drain some of the normal thirst
                    if (this.counter >= 80 /* 4 sec */) {
                        player.heal(1.0F);
                        this.addExhaustion(6.0f);
                        this.counter = 0;
                    }
                }
            } else if (this.thirst <= 0) {
                // check if player needs to get some damage because of thirst
                this.counter++;
                if (this.counter >= 80 /* 4 sec */) {
                    boolean bypass = player.getHealth() > 10.0F;
                    boolean isHard = difficulty == Difficulty.HARD;
                    boolean isNormal = difficulty == Difficulty.NORMAL && player.getHealth() > 1.0F;
                    if (bypass || isHard || isNormal) {
                        player.damage(DamageSource.STARVE, 1.0F);
                    }
                    this.counter = 0;
                }
            } else {
                // just do nothing here keep counter low
                this.counter = 0;
            }
        }
    }

    public void fromTag(CompoundTag compound) {
        if (compound.contains("thirst", 99)) {
            this.thirst = compound.getInt("thirstLevel");
            this.hydration = compound.getInt("thirstHydrationLevel");
            this.exhaustion = compound.getFloat("thirstExhaustionLevel");
        }
    }

    public void toTag(CompoundTag compound) {
        compound.putInt("thirstLevel", this.thirst);
        compound.putFloat("thirstHydrationLevel", this.hydration);
        compound.putFloat("thirstExhaustionLevel", this.exhaustion);
    }

    public void addExhaustion(float exhaustion) {
        // INVESTIGATE: may need some bounds check
        this.exhaustion += exhaustion;
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public void setHydration(float hydration) {
        this.hydration = hydration;
    }

    public void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }

    public int getThirst() {
        return thirst;
    }

    public float getHydration() {
        return hydration;
    }

    public float getExhaustion() {
        return exhaustion;
    }
}
