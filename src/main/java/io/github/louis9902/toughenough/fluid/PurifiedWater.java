package io.github.louis9902.toughenough.fluid;

import io.github.louis9902.toughenough.init.ToughEnoughFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class PurifiedWater extends WaterFluid {
    @Override
    public Fluid getFlowing() {
        return ToughEnoughFluids.FLOWING_PURIFIED_WATER;
    }

    @Override
    public Fluid getStill() {
        return ToughEnoughFluids.STILL_PURIFIED_WATER;
    }

    @Override
    public Item getBucketItem() {
        return ToughEnoughFluids.PURIFIED_WATER_BUCKET;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    public BlockState toBlockState(FluidState state) {
        return ToughEnoughFluids.PURIFIED_WATER_BLOCK.getDefaultState().with(Properties.LEVEL_15, method_15741(state));
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }


    public static class Flowing extends PurifiedWater {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends PurifiedWater {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
