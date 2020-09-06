package io.github.louis9902.toughenough.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SampleBlock extends Block {

    private static final VoxelShape RAY_TRACE_SHAPE;

    private static final VoxelShape ALIGN_Z_FLOOR;
    private static final VoxelShape ALIGN_Z_ROOF;

    static {
        RAY_TRACE_SHAPE = createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);

        //region Align Z Floot
        VoxelShape COIL_0_Z_FLOOR = VoxelShapes.combine(
                createCuboidShape(2.0, 4.0, 2.0, 14.0, 9.0, 4.0),
                createCuboidShape(4.0, 4.0, 2.0, 12.0, 7.0, 4.0),
                BooleanBiFunction.ONLY_FIRST
        );
        VoxelShape COIL_1_Z_FLOOR = VoxelShapes.combine(
                createCuboidShape(2.0, 4.0, 5.0, 14.0, 9.0, 7.0),
                createCuboidShape(4.0, 4.0, 5.0, 12.0, 7.0, 7.0),
                BooleanBiFunction.ONLY_FIRST
        );
        VoxelShape COIL_2_Z_FLOOR = VoxelShapes.combine(
                createCuboidShape(2.0, 4.0, 9.0, 14.0, 9.0, 11.0),
                createCuboidShape(4.0, 4.0, 9.0, 12.0, 7.0, 11.0),
                BooleanBiFunction.ONLY_FIRST
        );
        VoxelShape COIL_3_Z_FLOOR = VoxelShapes.combine(
                createCuboidShape(2.0, 4.0, 12.0, 14.0, 9.0, 14.0),
                createCuboidShape(4.0, 4.0, 12.0, 12.0, 7.0, 14.0),
                BooleanBiFunction.ONLY_FIRST
        );
        ALIGN_Z_FLOOR = VoxelShapes.union(
                createCuboidShape(1.0, 0.0, 1.0, 15.0, 4.0, 15.0),
                COIL_0_Z_FLOOR, COIL_1_Z_FLOOR, COIL_2_Z_FLOOR, COIL_3_Z_FLOOR
        );
        //endregion

        //region Align Z Roof
        VoxelShape COIL_0_Z_ROOF = VoxelShapes.combine(
                createCuboidShape(2.0, 7.0, 2.0, 14.0, 12.0, 4.0),
                createCuboidShape(4.0, 9.0, 2.0, 12.0, 12.0, 4.0),
                BooleanBiFunction.ONLY_FIRST
        );
        VoxelShape COIL_1_Z_ROOF = VoxelShapes.combine(
                createCuboidShape(2.0, 7.0, 5.0, 14.0, 12.0, 7.0),
                createCuboidShape(4.0, 9.0, 5.0, 12.0, 12.0, 7.0),
                BooleanBiFunction.ONLY_FIRST
        );
        VoxelShape COIL_2_Z_ROOF = VoxelShapes.combine(
                createCuboidShape(2.0, 7.0, 9.0, 14.0, 12.0, 11.0),
                createCuboidShape(4.0, 9.0, 9.0, 12.0, 12.0, 11.0),
                BooleanBiFunction.ONLY_FIRST
        );
        VoxelShape COIL_3_Z_ROOF = VoxelShapes.combine(
                createCuboidShape(2.0, 7.0, 12.0, 14.0, 12.0, 14.0),
                createCuboidShape(4.0, 9.0, 12.0, 12.0, 12.0, 14.0),
                BooleanBiFunction.ONLY_FIRST
        );
        ALIGN_Z_ROOF = VoxelShapes.union(
                createCuboidShape(1.0, 12.0, 1.0, 15.0, 16.0, 15.0),
                COIL_0_Z_ROOF, COIL_1_Z_ROOF, COIL_2_Z_ROOF, COIL_3_Z_ROOF
        );
        //endregion

    }

    public SampleBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        return ALIGN_Z_ROOF;
    }

    public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }
}
