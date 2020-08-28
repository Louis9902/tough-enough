package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.api.temperature.Modifier;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockProximityModifier extends Modifier {
    private static final int HEAT_RANGE_HORIZONTAL = 2;
    private static final int HEAT_RANGE_VERTICAL = 1;
    private static final int MAXIMUM_HEAT = 16;
    private static final int BLOCK_TARGET_SCALE = 6;

    public BlockProximityModifier(Identifier id) {
        super(id);
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
        return 0;
    }

    @Override
    public int calculateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        BlockPos start = pos.add(HEAT_RANGE_HORIZONTAL, HEAT_RANGE_VERTICAL, HEAT_RANGE_HORIZONTAL);
        BlockPos end = pos.add(-HEAT_RANGE_HORIZONTAL, -HEAT_RANGE_VERTICAL, -HEAT_RANGE_HORIZONTAL);

        //we use a mutable blockPos here to avoid unnecessary allocations
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

        return (int) Math.round(sum * BLOCK_TARGET_SCALE);
    }

    @Override
    public boolean isPlayerModifier() {
        return false;
    }
}
