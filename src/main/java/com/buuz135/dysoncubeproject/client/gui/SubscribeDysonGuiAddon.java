package com.buuz135.dysoncubeproject.client.gui;

import com.buuz135.dysoncubeproject.world.ClientDysonSphere;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.FacingHandlerScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.buuz135.dysoncubeproject.DysonCubeProject;
import com.buuz135.dysoncubeproject.network.ClientSubscribeSphereMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple clickable addon that allows cycling the Dyson Sphere subscription.
 * It sends a button message with the selected sphere id to the server container.
 */
public class SubscribeDysonGuiAddon extends BasicScreenAddon {

    private final String dysonID;
    private int guiX, guiY;

    public SubscribeDysonGuiAddon(String dysonID, int posX, int posY) {
        super(posX, posY);
        this.dysonID = dysonID;
    }

    @Override
    public int getXSize() {
        return 16;
    }

    @Override
    public int getYSize() {
        return 16;
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        // no-op
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        // Draw simple button rectangle
        int x = guiX + getPosX();
        int y = guiY + getPosY();
        AssetUtil.drawAsset(guiGraphics, screen, assets.getAsset(AssetTypes.BUTTON_SIDENESS_PULL), x, y);
        this.guiX = guiX;
        this.guiY = guiY;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipLines() {
        return List.of(Component.translatable("gui.dysoncubeproject.subscribe"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX - this.guiX, mouseY - this.guiY)) {
            DysonCubeProject.NETWORK.sendToServer(new ClientSubscribeSphereMessage(dysonID));
            return true;
        }
        return false;
    }

}
