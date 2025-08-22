package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

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
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight + 1);
		
		this.renderEnergy(pPoseStack, x + 162, y + 17);
		
		this.itemHint(pPoseStack, x + 80, y + 35, 0, 168, 0);
		
		this.itemHint(pPoseStack, x + 80, y + 17, crystalHintOffset, 168, 1);
		this.itemHint(pPoseStack, x + 98, y + 17, crystalHintOffset, 168, 2);
		this.itemHint(pPoseStack, x + 98, y + 35, crystalHintOffset, 168, 3);
		this.itemHint(pPoseStack, x + 98, y + 53, crystalHintOffset, 168, 4);
		this.itemHint(pPoseStack, x + 80, y + 53, crystalHintOffset, 168, 5);
		this.itemHint(pPoseStack, x + 62, y + 53, crystalHintOffset, 168, 6);
		this.itemHint(pPoseStack, x + 62, y + 35, crystalHintOffset, 168, 7);
		this.itemHint(pPoseStack, x + 62, y + 17, crystalHintOffset, 168, 8);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        renderTooltip(matrixStack, mouseX, mouseY);
		
		this.energyTooltip(matrixStack, 162, 17, mouseX, mouseY);
		this.crystalEffectTooltip(matrixStack, 14, 22, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.advanced_protocols")
				.append(Component.literal(": " + menu.enableAdvancedProtocols())).withStyle(ChatFormatting.AQUA), Component.translatable("test"));
		this.crystalEffectTooltip(matrixStack, 14, 34, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_target")
				.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergyTarget()))).withStyle(ChatFormatting.DARK_RED), Component.translatable("test"));
		this.crystalEffectTooltip(matrixStack, 14, 46, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_transfer")
				.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.maxEnergyDeplete()) + "/t")).withStyle(ChatFormatting.GOLD), Component.translatable("test"));
		this.crystalEffectTooltip(matrixStack, 14, 58, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.communication_range_1")
				.append(Component.literal(": " + menu.getMaxDistance() + " "))
				.append(Component.translatable("tooltip.sgjourney.dhd.communication_range_2")).withStyle(ChatFormatting.GRAY), Component.translatable("test"));
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
    	this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(matrixStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
	
	protected void renderEnergy(PoseStack matrixStack, int x, int y)
	{
		float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
		int actual = Math.round(52 * percentage);
		this.blit(matrixStack, x, y + 52 - actual, 176, 0, 6, actual);
	}
	
	protected void energyTooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY)
	{
		this.tooltip(matrixStack, mouseX, mouseY, x, y, 6, 52, ComponentHelper.energy(this.menu.getEnergy(), this.menu.getMaxEnergy()));
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return this.menu.hasItem(slot);
	}
	
	protected void crystalEffectTooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, Component... components)
	{
		this.tooltip(matrixStack, mouseX, mouseY, x, y, 16, 6, components);
	}
}
