package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.InterfaceModeButton;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.menu.InterfaceMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import java.util.ArrayList;
import java.util.List;

public class InterfaceScreen extends SGJourneyContainerScreen<InterfaceMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/interface_gui.png");
    
    protected EditBox commandEdit;
    
    public InterfaceScreen(InterfaceMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }
    
    public static long parsePositiveOrZero(String text)
    {
        try
        {
            long value = Long.parseLong(text);
            return value > 0 ? value : 0;
        }
        catch (NumberFormatException e) { return 0; }
    }
    
    protected boolean isShiftDown()
    {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT) ||
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_RSHIFT);
    }
    
    public InterfaceMode getMode()
    {
        return this.menu.getMode();
    }
    
    @Override
    protected void init()
    {
        this.commandEdit = new EditBox(this.font, this.width / 2 - 54, this.height / 2 - 66, 124, 20, Component.translatable("tooltip.sgjourney.energy_target"));
        this.commandEdit.setFilter(text ->
        {
            if(text.isEmpty())
                return true;
            
           try { return Long.parseLong(text) >= 0; }
           catch (NumberFormatException e) { return false; }
        });
        
        this.commandEdit.setMaxLength(19);
        this.addWidget(this.commandEdit);
        this.setInitialFocus(this.commandEdit);
        this.commandEdit.setResponder(text -> menu.setEnergyTargetAndMode(parsePositiveOrZero(text), menu.getMode()));
        
        this.commandEdit.setValue(String.valueOf(menu.getEnergyTarget()));
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        this.addRenderableWidget(new InterfaceModeButton(x + 9, y + 19, Component.empty(), Component.empty(), button ->
        {
            if(isShiftDown())
                menu.setEnergyTargetAndMode(parsePositiveOrZero(this.commandEdit.getValue()), menu.getMode().previous(this.menu.getInterfaceType().hasAdvancedCrystalMethods()));
            else
                menu.setEnergyTargetAndMode(parsePositiveOrZero(this.commandEdit.getValue()), menu.getMode().next(this.menu.getInterfaceType().hasAdvancedCrystalMethods()));
        }, this));
        
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        this.renderEnergyVertical(graphics, TEXTURE, x + 162, y + 17, 6, 138, 176, 0, this.menu.getEnergy(), this.menu.getMaxEnergy());
    }
    
    protected void modeTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, Component name, Component... components)
    {
        if(this.isHovering(x, y, width, height, mouseX, mouseY))
        {
            ArrayList<Component> tooltips = new ArrayList<>();
            tooltips.add(name);
            tooltips.addAll(List.of(components));
            
            graphics.renderComponentTooltip(this.font, tooltips, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        this.commandEdit.render(graphics, mouseX, mouseY, delta);
        
        renderTooltip(graphics, mouseX, mouseY);
        
        this.tooltip(graphics, mouseX, mouseY, 33, 16, 126, 20, ComponentHelper.energy("tooltip.sgjourney.energy_target", this.menu.getEnergyTarget()),
                ComponentHelper.description("tooltip.sgjourney.interface.energy_target.description"));
        this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 138, "tooltip.sgjourney.energy_buffer", this.menu.getEnergy(), this.menu.getMaxEnergy());
        
        this.modeTooltip(graphics, mouseX, mouseY, 9, 19, 16, 16,
                Component.translatable("block.sgjourney.interface.mode").append(": ").append(this.menu.getMode().getName()),
                this.menu.getMode().getUsage());
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        
        graphics.drawString(this.font, ComponentHelper.energy(menu.getEnergyBlockEnergy()), 20, 56, 0xffffff, false);
        graphics.drawString(this.font, Component.translatable("info.sgjourney.open_time").append(":").withStyle(ChatFormatting.DARK_AQUA), 20, 66, 0xffffff, false);
        graphics.drawString(this.font, ComponentHelper.tickTimer(menu.getStargateOpenTime(), Stargate.getMaxGateOpenTime(), ChatFormatting.DARK_AQUA), 20, 76, 0xffffff, false);
        graphics.drawString(this.font, Component.translatable("info.sgjourney.last_traveler_time").append(":").withStyle(ChatFormatting.DARK_PURPLE), 20, 86, 0xffffff, false);
        graphics.drawString(this.font, Component.literal(Conversion.ticksToString(menu.getStargateTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE), 20, 96, 0xffffff, false);
    }
}
