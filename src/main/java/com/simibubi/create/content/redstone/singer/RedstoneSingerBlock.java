package com.simibubi.create.content.redstone.singer;

import com.simibubi.create.Create;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.AllBlockEntityTypes;

public class RedstoneSingerBlock extends Block implements IBE<RedstoneSingerBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RedstoneSingerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    public String getSerializedName() {
        return Create.asResource("redstone_singer").toString();
    }


    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    boolean powered = world.hasNeighborSignal(pos);
    if (powered != state.getValue(POWERED)) {
        world.setBlock(pos, state.setValue(POWERED, powered), 3);
    }
    super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public BlockEntityType<? extends RedstoneSingerBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REDSTONE_SINGER.get();
    }

    @Override
    public Class<RedstoneSingerBlockEntity> getBlockEntityClass() {
        return RedstoneSingerBlockEntity.class;
    }
}
