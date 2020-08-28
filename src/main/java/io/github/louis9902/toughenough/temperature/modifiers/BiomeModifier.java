package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.api.temperature.Modifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BiomeModifier extends Modifier {

    private final static int BIOME_TARGET_SCALE = 3;

    public BiomeModifier(Identifier id) {
        super(id);
    }

    @Override
    public int calculateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        float temp = world.getBiomeAccess().getBiome(pos).getTemperature(pos);

        //Map biome temperatures from their range -0.5 to 2.0 into a -1 to 1 range first
        //and multiply it with the wanted effect on the target the biome temp should have.
        //list of biome temps: https://minecraft.gamepedia.com/Biome#Temperature
        return (int) Math.round(((temp - 0.75) / 1.25) * BIOME_TARGET_SCALE);
    }

    @Override
    public boolean isPlayerModifier() {
        return false;
    }
}
