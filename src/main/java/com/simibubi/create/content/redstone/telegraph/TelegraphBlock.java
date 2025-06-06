package com.simibubi.create.content.redstone.telegraph;

import com.simibubi.create.Create;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.AllBlockEntityTypes;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.fml.DistExecutor;

import com.simibubi.create.AllItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.createmod.catnip.gui.ScreenOpener;
import com.simibubi.create.content.redstone.telegraph.TelegraphBlockEntity;
import com.simibubi.create.content.redstone.telegraph.TelegraphScreen;

public class TelegraphBlock extends Block implements IBE<TelegraphBlockEntity> {

    public TelegraphBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    public String getSerializedName() {
        return Create.asResource("telegraph").toString();
    }

    @Override
    public BlockEntityType<? extends TelegraphBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REDSTONE_TELEGRAPH.get();
    }

    @Override
    public Class<TelegraphBlockEntity> getBlockEntityClass() {
        return TelegraphBlockEntity.class;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
		if (player != null && AllItems.WRENCH.isIn(player.getItemInHand(hand)))
			return InteractionResult.PASS;
		//DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
		//	() -> () -> withBlockEntityDo(level, pos, be -> this.displayScreen(be, player)));
		return InteractionResult.SUCCESS;
    }

    @OnlyIn(value = Dist.CLIENT)
	protected void displayScreen(TelegraphBlockEntity be, Player player) {
		if (player instanceof LocalPlayer)
			ScreenOpener.open(new TelegraphScreen(be));
	}

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        int power = 14;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TelegraphBlockEntity telegraph) {
            if( telegraph.isEmiting()) {
                switch (telegraph.getCurrentSignal()) {
                    case SHORT:
                        return (telegraph.getCurrSignalTick() < 14 &&
                                telegraph.getCurrSignalTick() >= 6  ) ? power : 0;
                    case LONG:
                        return power;
                    default: //NONE
                        return 0;
                }
            }
        }
        return 0;
    }
}
