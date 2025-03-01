package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;

public class DHDCrystalScreen extends SGJourneyContainerScreen<DHDCrystalMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd_crystal_gui.png");
	
	protected final int crystalHintOffset;
	
	public DHDCrystalScreen(DHDCrystalMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
        super(pMenu, pPlayerInventory, pTitle);
		
		crystalHintOffset = pMenu.advancedCrystals() ? 32 : 16;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight + 1);
		
		this.renderEnergy(graphics, x + 162, y + 17);
		
		this.itemHint(graphics, TEXTURE, x + 80, y + 35, 0, 168, 0);
		
		this.itemHint(graphics, TEXTURE, x + 80, y + 17, crystalHintOffset, 168, 1);
		this.itemHint(graphics, TEXTURE, x + 98, y + 17, crystalHintOffset, 168, 2);
		this.itemHint(graphics, TEXTURE, x + 98, y + 35, crystalHintOffset, 168, 3);
		this.itemHint(graphics, TEXTURE, x + 98, y + 53, crystalHintOffset, 168, 4);
		this.itemHint(graphics, TEXTURE, x + 80, y + 53, crystalHintOffset, 168, 5);
		this.itemHint(graphics, TEXTURE, x + 62, y + 53, crystalHintOffset, 168, 6);
		this.itemHint(graphics, TEXTURE, x + 62, y + 35, crystalHintOffset, 168, 7);
		this.itemHint(graphics, TEXTURE, x + 62, y + 17, crystalHintOffset, 168, 8);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
		
		this.energyTooltip(graphics, 162, 17, mouseX, mouseY);
		this.crystalEffectTooltip(graphics, 14, 22, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.advanced_protocols")
				.append(Component.literal(": " + menu.enableAdvancedProtocols())).withStyle(ChatFormatting.AQUA));
		this.crystalEffectTooltip(graphics, 14, 34, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_target")
				.append(Component.literal(": " + menu.getEnergyTarget() + " FE")).withStyle(ChatFormatting.DARK_RED));
		this.crystalEffectTooltip(graphics, 14, 46, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_transfer")
				.append(Component.literal(": " + menu.maxEnergyDeplete() + " FE/t")).withStyle(ChatFormatting.GOLD));
		this.crystalEffectTooltip(graphics, 14, 58, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.communication_range_1")
				.append(Component.literal(": " + menu.getMaxDistance() + " "))
				.append(Component.translatable("tooltip.sgjourney.dhd.communication_range_2")).withStyle(ChatFormatting.GRAY));
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
	
	protected void renderEnergy(GuiGraphics graphics, int x, int y)
	{
		float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
		int actual = Math.round(160 * percentage);
		graphics.blit(TEXTURE, x, y, 0, 168, actual, 6);
	}
	
	protected void energyTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
	{
		this.tooltip(graphics, mouseX, mouseY, x, y, 6, 52, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + this.menu.getEnergy() + "/" + this.menu.getMaxEnergy() + " FE")).withStyle(ChatFormatting.DARK_RED));
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return this.menu.hasItem(slot);
	}
	
	protected void crystalEffectTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Component component)
	{
		this.tooltip(graphics, mouseX, mouseY, x, y, 16, 6, component);
	}
}
