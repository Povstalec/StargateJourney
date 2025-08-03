package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.menu.BatteryMenu;

public class BatteryScreen extends SGJourneyContainerScreen<BatteryMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/battery.png");

    public BatteryScreen(BatteryMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
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
        
        this.renderEnergy(graphics, x + 44, y + 18);
		
		this.itemHint(graphics, TEXTURE, x + 8, y + 36, 88, 166, 0);
		this.itemHint(graphics, TEXTURE, x + 152, y + 36, 104, 166, 1);
    }

    @Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);
		renderTooltip(graphics, mouseX, mouseY);
		
		this.energyTooltip(graphics, 44, 18, mouseX, mouseY);
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
		int actual = Math.round(52 * percentage);
		graphics.blit(TEXTURE, x, y + 52 - actual, 0, 166, 88, actual);
	}
	
	protected void energyTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
	{
		this.tooltip(graphics, mouseX, mouseY, x, y, 88, 52, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergy(), menu.getMaxEnergy()))).withStyle(ChatFormatting.DARK_RED));
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return this.menu.hasItem(slot);
	}
}
