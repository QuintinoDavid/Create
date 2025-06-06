package com.simibubi.create.content.redstone.telegraph;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.util.StringRepresentable;
import java.util.List;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import javax.annotation.Nullable;

public class TelegraphBlockEntity extends SmartBlockEntity {

    public enum Signal implements StringRepresentable {
        NONE,
        SHORT,
        LONG;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public Signal[] sequence;
    boolean emiting;

    public Signal currentSignal;
    public int currentIndex;
    public int CurrSignalTick;


    public TelegraphBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.sequence = new Signal[16];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = Signal.NONE;
        }
        this.emiting = false;
        this.currentSignal = Signal.NONE;
        this.currentIndex = 0;
        this.CurrSignalTick = 0;
    }

    public Signal getCurrentSignal() {
        return currentSignal;
    }

    public void setCurrentSignal(Signal currentSignal) {
        this.currentSignal = currentSignal;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        if (currentIndex < 0 || currentIndex >= sequence.length) {
            throw new IndexOutOfBoundsException("Index " + currentIndex + " is out of bounds for sequence of length " + sequence.length);
        }
        this.currentIndex = currentIndex;
    }

    public int getCurrSignalTick() {
        return CurrSignalTick;
    }

    public void setCurrSignalTick(int currSignalTick) {
        this.CurrSignalTick = currSignalTick;
    }

    public Signal[] getSequence() {
        return sequence;
    }

    public Signal getSequence(Signal[] sequence, int index) {
        if (index < 0 || index >= sequence.length) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for sequence of length " + sequence.length);
        }
        return sequence[index];
    }

    public void setSequence(Signal[] sequence, int index, Signal signal) {
        if (index < 0 || index >= sequence.length) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for sequence of length " + sequence.length);
        }
        sequence[index] = signal;
    }

    public boolean isEmiting() {
        return emiting;
    }

    public void setEmiting(boolean emiting) {
        this.emiting = emiting;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        emiting = tag.getBoolean("Emiting");
        currentIndex = tag.getInt("CurrentIndex");
        CurrSignalTick = tag.getInt("CurrSignalTick");
        currentSignal = Signal.valueOf(tag.getString("CurrentSignal"));
        if (tag.contains("Sequence")) {
            for (int i = 0; i < sequence.length; i++) {
                String key = "S" + i;
                if (tag.contains(key)) {
                    sequence[i] = Signal.valueOf(tag.getString(key));
                } else {
                    sequence[i] = Signal.NONE;
                }
            }
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Emiting", emiting);
        tag.putInt("CurrentIndex", currentIndex);
        tag.putInt("CurrSignalTick", CurrSignalTick);
        tag.putString("CurrentSignal", currentSignal.name());
        for (int i = 0; i < sequence.length; i++) {
            tag.putString("S" + i, sequence[i].name());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) return;

        BlockState state = getBlockState();
        BlockPos pos = getBlockPos();
        // Check if the block is emitting signals
        if (!emiting) return;

        if( currentIndex >= sequence.length && CurrSignalTick >= 20) { //end of sequence
            emiting = false;
            currentIndex = 0;
            CurrSignalTick = 0;
        } else if (CurrSignalTick == 20) { //end of current signal
            CurrSignalTick = 0;
            currentIndex++;
            currentSignal = sequence[currentIndex];
        } else {
            CurrSignalTick++;
        }

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
