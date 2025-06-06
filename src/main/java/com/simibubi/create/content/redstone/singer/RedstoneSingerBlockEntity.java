package com.simibubi.create.content.redstone.singer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import com.simibubi.create.AllBlockEntityTypes;
import net.minecraft.world.level.Level;
import com.simibubi.create.content.redstone.singer.RedstoneSingerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RedstoneSingerBlockEntity extends SmartBlockEntity {

    private int soundCooldown;

    public RedstoneSingerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.soundCooldown = 0;
    }

    public int getSoundCooldown() {
        return soundCooldown;
    }

    public void setSoundCooldown(int soundCooldown) {
        this.soundCooldown = soundCooldown;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) return;

        BlockState state = getBlockState();
        BlockPos pos = getBlockPos();

        boolean powered = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED);

        if (powered) {
            if (getSoundCooldown() <= 0) {
                level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.NOTE_BLOCK_BELL.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                setSoundCooldown(20); // 1 sec
            } else {
                setSoundCooldown(getSoundCooldown() - 1);
            }
        } else {
            setSoundCooldown(0);
        }
    }
}
