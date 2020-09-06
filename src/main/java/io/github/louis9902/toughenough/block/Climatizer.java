package io.github.louis9902.toughenough.block;

import io.github.louis9902.toughenough.block.blockentity.ClimatizerEntity;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Climatizer extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<Action> ACTION = EnumProperty.of("action", Action.class);

    private static final VoxelShape RAY_TRACE_SHAPE;

    private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 0.0, 1.0, 15.0, 4.0, 15.0),
            createCuboidShape(2.0, 4.0, 2.0, 14.0, 9.0, 14.0)
    );
    private static final VoxelShape UP_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 12.0, 1.0, 15.0, 16.0, 15.0),
            createCuboidShape(2.0, 7.0, 2.0, 14.0, 12.0, 14.0)
    );
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 1.0, 0.0, 15.0, 15.0, 4.0),
            createCuboidShape(2.0, 2.0, 4.0, 14.0, 14.0, 9.0)
    );
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 1.0, 12.0, 15.0, 15.0, 16.0),
            createCuboidShape(2.0, 2.0, 7.0, 14.0, 14.0, 12.0)
    );
    private static final VoxelShape EAST_SHAPE = VoxelShapes.union(
            createCuboidShape(12.0, 1.0, 1.0, 16.0, 15.0, 15.0),
            createCuboidShape(7.0, 2.0, 2.0, 12.0, 14.0, 14.0)
    );
    private static final VoxelShape WEST_SHAPE = VoxelShapes.union(
            createCuboidShape(0.0, 1.0, 1.0, 4.0, 15.0, 15.0),
            createCuboidShape(4.0, 2.0, 2.0, 9.0, 14.0, 14.0)
    );

    static {
        RAY_TRACE_SHAPE = createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
    }


    public Climatizer(Settings settings) {
        super(settings);
        BlockState state = getStateManager().getDefaultState();
        setDefaultState(state.with(FACING, Direction.NORTH).with(ACTION, Action.OFF));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case DOWN:
                return DOWN_SHAPE;
            case UP:
                return UP_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
        }
        return UP_SHAPE;
    }

    public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new ClimatizerEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
        builder.add(ACTION);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            //This will call the createScreenHandlerFactory method from blockWithEntity, which will return our blockEntity casted
            //to a namedScreenHandlerFactory
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                //With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public enum Action implements StringIdentifiable {

        OFF("off"), HEAT("heat"), COOL("cool");

        private final String identifier;

        Action(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

}


