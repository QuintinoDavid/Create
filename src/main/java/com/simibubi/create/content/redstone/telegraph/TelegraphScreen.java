package com.simibubi.create.content.redstone.telegraph;

import java.util.Arrays;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TelegraphScreen extends AbstractSimiScreen {

    private static final int SEQUENCE_LENGTH = 16;
    private final TelegraphBlockEntity telegraph;
    private final SelectionScrollInput[] sequenceInputs = new SelectionScrollInput[SEQUENCE_LENGTH];
    private final AllGuiTextures background = AllGuiTextures.THRESHOLD_SWITCH; // Use your own texture if needed

    public TelegraphScreen(TelegraphBlockEntity telegraph) {
        super(CreateLang.translateDirect("gui.telegraph.title"));
        this.telegraph = telegraph;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        setWindowOffset(-20, 0);
        super.init();

        int x = guiLeft + 10;
        int y = guiTop + 30;

        TelegraphBlockEntity.Signal[] sequence = telegraph.getSequence();

        // Layout: 16 inputs in a row (or split into 2 rows of 8 if you prefer)
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            int row = i / 8;
            int col = i % 8;
            int inputX = x + col * 18;
            int inputY = y + row * 24;

            sequenceInputs[i] = new SelectionScrollInput(inputX, inputY, 16, 20)
                .forOptions(
                    Arrays.stream(TelegraphBlockEntity.Signal.values())
                        .map(s -> Component.translatable("gui.telegraph.signal." + s.name().toLowerCase()))
                        .toList()
                )
                .setState(sequence[i].ordinal())
                .withCallback(this::onSequenceChanged);
            addRenderableWidget(sequenceInputs[i]);
        }
    }

    private void onSequenceChanged(int index, int state) {
        TelegraphBlockEntity.Signal[] newSequence = telegraph.getSequence().clone();
        newSequence[index] = TelegraphBlockEntity.Signal.values()[state];
        AllPackets.getChannel().sendToServer(
            new ConfigureTelegraphPacket(telegraph.getBlockPos(), newSequence)
        );
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        background.render(graphics, x, y);
        graphics.drawString(font, title, x + background.getWidth() / 2 - font.width(title) / 2, y + 4, 0x592424, false);
        // Optionally, render a preview of the block/item here
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
