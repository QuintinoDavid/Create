package com.simibubi.create.content.redstone.telegraph;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureTelegraphPacket extends BlockEntityConfigurationPacket<TelegraphBlockEntity> {

    private TelegraphBlockEntity.Signal[] sequence;

    public ConfigureTelegraphPacket(BlockPos pos, TelegraphBlockEntity.Signal[] sequence) {
        super(pos);
        this.sequence = sequence;
    }

    public ConfigureTelegraphPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        sequence = new TelegraphBlockEntity.Signal[16];
        for (int i = 0; i < 16; i++) {
            sequence[i] = TelegraphBlockEntity.Signal.values()[buffer.readVarInt()];
        }
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        for (TelegraphBlockEntity.Signal signal : sequence) {
            buffer.writeVarInt(signal.ordinal());
        }
    }

    @Override
    protected void applySettings(TelegraphBlockEntity be) {
        for (int i = 0; i < 16; i++) {
            be.sequence[i] = sequence[i];
        }
        be.setChanged();
        be.sendData();
    }
}
