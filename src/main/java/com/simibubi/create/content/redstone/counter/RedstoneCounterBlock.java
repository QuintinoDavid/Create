package com.simibubi.create.content.redstone.counter;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public class RedstoneCounterBlock extends WrenchableDirectionalBlock implements IBE<RedstoneCounterBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<RedstoneCounterBlockEntity.Mode> MODE =
        EnumProperty.create("mode", RedstoneCounterBlockEntity.Mode.class);
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 0, 64);

    public RedstoneCounterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(MODE, RedstoneCounterBlockEntity.Mode.DIRECT)
            .setValue(COUNT, 0));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(POWERED, MODE, COUNT);
        super.createBlockStateDefinition(builder);
    }

    public String getSerializedName() {
        return Create.asResource("redstone_counter").toString();
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof RedstoneCounterBlockEntity counter) {
            // Only emit from the front
            Direction facing = state.getValue(FACING).getOpposite();
            if (side == facing) {
                if (counter.mode == RedstoneCounterBlockEntity.Mode.DIRECT)
                    return Math.min(counter.count, 15);
                else // COMPARE
                    return counter.count >= counter.threshold ? 15 : 0;
            }
        }
        return 0;
    }

    @Override
    public BlockEntityType<? extends RedstoneCounterBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REDSTONE_COUNTER.get();
    }

    @Override
    public Class<RedstoneCounterBlockEntity> getBlockEntityClass() {
        return RedstoneCounterBlockEntity.class;
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RedstoneCounterBlockEntity counter) {
                ItemStack held = player.getItemInHand(hand);
                boolean holdingStick = held.is(Items.STICK);
                boolean Sneaking = player.isShiftKeyDown();

                if (holdingStick) {
                    counter.toggleMode();
                    player.displayClientMessage(Component.literal("Mode: " + counter.mode.name()), true);
                } else {
                    int old = counter.getThreshold();
                    if (Sneaking) {
                        counter.setThreshold(Math.max(0, old - 1));
                        player.displayClientMessage(Component.literal("Threshold: " + counter.getThreshold()), true);
                    } else {
                        counter.setThreshold(Math.min(64, old + 1));
                        player.displayClientMessage(Component.literal("Threshold: " + counter.getThreshold()), true);
                    }
                }
                counter.setChanged();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }



    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis().isHorizontal()) {
            return state.setValue(FACING, rot.rotate(facing));
        }
        return state.setValue(FACING, Direction.NORTH);
    }

}
