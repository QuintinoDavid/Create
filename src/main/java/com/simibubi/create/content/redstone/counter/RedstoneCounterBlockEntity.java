package com.simibubi.create.content.redstone.counter;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import java.util.List;

public class RedstoneCounterBlockEntity extends SmartBlockEntity{

    public enum Mode implements StringRepresentable {
        DIRECT,
        COMPARE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public int count;
    public int threshold;
    public Mode mode;
    boolean powered;
    private int previousBackPower = 0;
    private int previousSidePower = 0;

    public RedstoneCounterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.count = 0;
        this.threshold = 1;
        this.powered = false;
        this.mode = Mode.COMPARE;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
        updateBlockState();
    }
    public int getThreshold() {
        return threshold;
    }
    public void setThreshold(int threshold) {
        this.threshold = threshold;
        updateBlockState();
    }
    public boolean isPowered() {
        return powered;
    }
    public void setPowered(boolean powered) {
        this.powered = powered;
        updateBlockState();
    }
    public boolean isDirectMode() {
        return mode==Mode.DIRECT;
    }
    public void setMode(Mode mode) {
        this.mode = mode;
        updateBlockState();
    }

    public Mode getMode() {
        return mode;
    }

    public void toggleMode() {
        if (mode == Mode.DIRECT) {
            mode = Mode.COMPARE;
        } else {
            mode = Mode.DIRECT;
        }
        updateBlockState();
    }

    private void updateBlockState() {
        BlockState state = getBlockState();
        if (state != null) {
            state = state.setValue(RedstoneCounterBlock.COUNT, count)
                         .setValue(RedstoneCounterBlock.POWERED, powered)
                         .setValue(RedstoneCounterBlock.MODE, mode);
            level.setBlockAndUpdate(getBlockPos(), state);
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Count", count);
        compound.putInt("Threshold", threshold);
        compound.putBoolean("Powered", powered);
        compound.putString("Mode", mode.name());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        count = tag.getInt("Count");
        threshold = tag.getInt("Threshold");
        powered = tag.getBoolean("Powered");
        mode = Mode.valueOf(tag.getString("Mode"));
        updateBlockState();
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) return;

        BlockState state = getBlockState();
        Direction facing = state.getValue(RedstoneCounterBlock.FACING);
        BlockPos pos = getBlockPos();

        int backPower = level.getSignal(pos.relative(facing.getOpposite()), facing.getOpposite());

        int sidePowerRight = 0;
        int sidePowerLeft = 0;
        if(facing.getAxis().isHorizontal()) {
            sidePowerRight = level.getSignal(pos.relative(facing.getCounterClockWise()), facing.getCounterClockWise());
            sidePowerLeft = level.getSignal(pos.relative(facing.getClockWise()), facing.getClockWise());
        }
        if (backPower > 0 && previousBackPower == 0) {
            setCount(Math.min(count + 1, 64));
        }
        previousBackPower = backPower;
        if ((sidePowerLeft > 0 || sidePowerRight > 0) && previousSidePower == 0) {
            setCount(0);
        }
        previousSidePower = Math.max(sidePowerLeft, sidePowerRight);
        if (isDirectMode()) {
            setPowered(count > 0);
        } else { // compare mode
            setPowered(count >= threshold);
        }
        updateBlockState();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
