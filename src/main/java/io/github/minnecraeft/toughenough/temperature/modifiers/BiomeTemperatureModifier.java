package io.github.minnecraeft.toughenough.temperature.modifiers;

import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureModifier;
import io.github.minnecraeft.toughenough.temperature.DefaultTemperatureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BiomeTemperatureModifier extends TemperatureModifier {

    private final static int BIOME_TARGET_SCALE = 3;

    public BiomeTemperatureModifier(Identifier id) {
        super(id);
    }

    @Override
    public int calculateFromPlayer(@NotNull PlayerEntity player) {
        int result = super.calculateFromPlayer(player);
        {
            DebugMonitor monitor = ToughEnoughComponents.DEBUGGER_MONITOR.get(player);
            if (monitor.isDebugging())
                monitor.section(DefaultTemperatureManager.DEBUG_TARGET_MODIFIERS).putInt(getIdentifier().getPath(), result);
        }
        return result;
    }

    @Override
    public int calculateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        float temp = world.getBiomeAccess().getBiome(pos).getTemperature(pos);

        // map biome temperatures from their range -0.5 to 2.0 into a -1 to 1 range first
        // and multiply it with the wanted effect on the target the biome temp should have.
        // list of biome temps: https://minecraft.gamepedia.com/Biome#Temperature
        return (int) Math.round(((temp - 0.75) / 1.25) * BIOME_TARGET_SCALE);
    }

    @Override
    public boolean isPlayerModifier() {
        return false;
    }
}
