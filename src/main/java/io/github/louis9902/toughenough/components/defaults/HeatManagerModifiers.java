package io.github.louis9902.toughenough.components.defaults;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HeatManagerModifiers {
    private static final int HEAT_RANGE_HORIZONTAL = 2;
    private static final int HEAT_RANGE_VERTICAL = 1;
    private static final int MAXIMUM_HEAT = 16;

    private static final int BIOME_TARGET_SCALE = 3;
    private static final int TIME_TARGET_SCALE = 2;
    private static final int BLOCK_TARGET_SCALE = 6;

    public static int blockProximityTarget(PlayerEntity player) {
        //we use a mutable blockPos here to avoid unnecessary allocations
        World world = player.getEntityWorld();
        BlockPos pos = player.getBlockPos();

        BlockPos start = pos.add(HEAT_RANGE_HORIZONTAL, HEAT_RANGE_VERTICAL, HEAT_RANGE_HORIZONTAL);
        BlockPos end = pos.add(-HEAT_RANGE_HORIZONTAL, -HEAT_RANGE_VERTICAL, -HEAT_RANGE_HORIZONTAL);

        //we ignore the distance of heating blocks to the player to save complexity.
        //as our searched range is rather small anyways this should not matter as much
        double sum = 0;
        for (BlockPos pos1 : BlockPos.Mutable.iterate(start, end)) {
            int blockHeat = getBlockHeat(world.getBlockState(pos1));
            sum += blockHeat;
        }
        //Clamp values to [0;20] and then scale it to [0;1]
        sum = MathHelper.clamp(sum, 0, MAXIMUM_HEAT) / MAXIMUM_HEAT;
        //Scale value to be in [0;BLOCK_TARGET_SCALE]
        System.out.println("sum = " + sum);

        int result = (int) Math.round(sum * BLOCK_TARGET_SCALE);

        System.out.println("result = " + result);
        return result;
    }

    public static int biomeTarget(PlayerEntity player) {
        int effectScale = 3;
        BlockPos pos = player.getBlockPos();
        float temp = player.getEntityWorld().getBiomeAccess().getBiome(pos).getTemperature(pos);

        //Map biome temperatures from their range -0.5 to 2.0 into a -1 to 1 range first
        //and multiply it with the wanted effect on the target the biome temp should have.
        //list of biome temps: https://minecraft.gamepedia.com/Biome#Temperature
        return (int) Math.round(((temp - 0.75) / 1.25) * BIOME_TARGET_SCALE);
    }

    public static int timeTarget(PlayerEntity player) {
        int effectScale = 2;
        long time = player.getEntityWorld().getTimeOfDay();
        //Scale time of day to temperature between -1 and 1
        //by modifying sin so that period is 24000 (length of one minecraft day)
        return (int) Math.round(Math.sin(time * ((2 * Math.PI) / 24000.0)) * TIME_TARGET_SCALE);
    }

    private static int getBlockHeat(BlockState state) {
        if (state.getMaterial().equals(Material.LAVA))
            return 8;
        else if (state.getMaterial().equals(Material.FIRE))
            return 4;
        else if (state.getBlock() instanceof CampfireBlock)
            return 4;
        else if (state.getBlock() instanceof AbstractFurnaceBlock)
            return state.get(AbstractFurnaceBlock.LIT) ? 2 : 0;
        else if (state.getBlock() instanceof TorchBlock)
            return 1;
        //else if (state.getMaterial().equals(Material.ICE) || state.getMaterial().equals(Material.DENSE_ICE))
        //    return -2;
        return 0;
    }
}
