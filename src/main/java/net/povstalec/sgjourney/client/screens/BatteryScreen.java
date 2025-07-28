package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
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
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
        
        this.renderEnergy(pPoseStack, x + 44, y + 18);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
        
        this.energyTooltip(pPoseStack, 44, 18, mouseX, mouseY);
    }
	
	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
	}
	
	protected void renderEnergy(PoseStack poseStack, int x, int y)
	{
		float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
		int actual = Math.round(52 * percentage);
		this.blit(poseStack, x, y + 52 - actual, 0, 166, 88, actual);
	}
	
	protected void energyTooltip(PoseStack poseStack, int x, int y, int mouseX, int mouseY)
	{
		this.tooltip(poseStack, mouseX, mouseY, x, y, 88, 52, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergy(), menu.getMaxEnergy()))).withStyle(ChatFormatting.DARK_RED));
	}
}
