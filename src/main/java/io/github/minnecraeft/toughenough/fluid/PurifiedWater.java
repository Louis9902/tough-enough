package io.github.minnecraeft.toughenough.fluid;

import io.github.minnecraeft.toughenough.init.ToughEnoughFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class PurifiedWater extends WaterFluid {

    public static final int COLOR = 169 << 24 | 54 << 16 | 181 << 8 | 251;

    @Override
    public Fluid getFlowing() {
        return ToughEnoughFluids.PURIFIED_WATER_FLOWING;
    }

    @Override
    public Fluid getStill() {
        return ToughEnoughFluids.PURIFIED_WATER_STILL;
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
