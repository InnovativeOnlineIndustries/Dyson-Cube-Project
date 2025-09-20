package com.buuz135.dysoncubeproject.client.gui;

import com.buuz135.dysoncubeproject.util.NumberUtils;
import com.buuz135.dysoncubeproject.world.ClientDysonSphere;
import com.buuz135.dysoncubeproject.world.DysonSphereConfiguration;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.DyeColor;

import java.awt.*;
import java.text.DecimalFormat;

public class DysonProgressGuiAddon extends BasicScreenAddon {

    private String dysonID;

    public DysonProgressGuiAddon(String dysonID, int posX, int posY) {
        super(posX, posY);
        this.dysonID = dysonID;
    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        var dyson = ClientDysonSphere.DYSON_SPHERE_PROGRESS.getSpheres().computeIfAbsent(dysonID, s -> new DysonSphereConfiguration());
        var y = 0;
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.BLUE + "Dyson Information", this.getPosX() + guiX, this.getPosY() + guiY, 0xFFFFFF, false);
        ++y;
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.BLUE + "Progress: " + new DecimalFormat().format(dyson.getProgress() * 100) + "%", this.getPosX() + guiX, this.getPosY() + guiY + Minecraft.getInstance().font.lineHeight * y, 0xFFFFFF, false);
        ++y;
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.BLUE + "Power Gen: " + NumberUtils.getFormatedBigNumber(dyson.getSolarPanels() * DysonSphereConfiguration.POWER_PER_SOLAR_PANEL) + "FE", this.getPosX() + guiX, this.getPosY() + guiY + Minecraft.getInstance().font.lineHeight * y, 0xFFFFFF, false);
        ++y;
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.BLUE + "Power Con: " + NumberUtils.getFormatedBigNumber(0) + "FE", this.getPosX() + guiX, this.getPosY() + guiY + Minecraft.getInstance().font.lineHeight * y, 0xFFFFFF, false);
        ++y;
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.BLUE + "Beams: " + NumberUtils.getFormatedBigNumber(dyson.getBeams()), this.getPosX() + guiX, this.getPosY() + guiY + Minecraft.getInstance().font.lineHeight * y, 0xFFFFFF, false);
        ++y;
        guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.BLUE + "Sails: " + NumberUtils.getFormatedBigNumber(dyson.getSolarPanels()) + "/" + NumberUtils.getFormatedBigNumber(dyson.getMaxSolarPanels()), this.getPosX() + guiX, this.getPosY() + guiY + Minecraft.getInstance().font.lineHeight * y, 0xFFFFFF, false);
        ++y;
        if (dyson.getSolarPanels() >= dyson.getMaxSolarPanels()) {
            guiGraphics.drawString(Minecraft.getInstance().font, ChatFormatting.RED + "Needs more beams", this.getPosX() + guiX, this.getPosY() + guiY + Minecraft.getInstance().font.lineHeight * y, 0xFFFFFF, false);
            ++y;
        }

        var color = 0xFF000000 | DyeColor.CYAN.getTextColor();
        Rectangle area = new Rectangle(this.getPosX() + guiX - 4, this.getPosY() + guiY - 4, 108, Minecraft.getInstance().font.lineHeight * y + 4);
        AssetUtil.drawHorizontalLine(guiGraphics, area.x, area.x + area.width, area.y, color);
        AssetUtil.drawHorizontalLine(guiGraphics, area.x, area.x + area.width, area.y + area.height, color);
        AssetUtil.drawVerticalLine(guiGraphics, area.x, area.y, area.y + area.height, color);
        AssetUtil.drawVerticalLine(guiGraphics, area.x + area.width, area.y, area.y + area.height, color);


    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
