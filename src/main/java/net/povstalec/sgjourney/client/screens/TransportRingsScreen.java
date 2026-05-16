package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.TransportRingsMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public class TransportRingsScreen<T extends TransportRingsMenu<?>> extends SGJourneyContainerScreen<T>
{
	public static final int HINT_OFFSET_Y = 166;
	public static final int LARGE_CRYSTAL_HINT_OFFSET_X = 0;
	public static final int CRYSTAL_HINT_OFFSET_X = 16;
	public static final int ENERGY_HINT_OFFSET_X = 32;
	
	protected ResourceLocation texture;
	
	public TransportRingsScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, playerInventory, title);
		
		this.texture = texture;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
		
		this.renderEnergyVertical(poseStack, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
		
		this.itemHint(poseStack, x + 80, y + 35, LARGE_CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.itemHint(poseStack, x + 80, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(poseStack, x + 98, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(poseStack, x + 98, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 3);
		this.itemHint(poseStack, x + 98, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(poseStack, x + 80, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(poseStack, x + 62, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 6);
		this.itemHint(poseStack, x + 62, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 7);
		this.itemHint(poseStack, x + 62, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 8);
		
		this.itemHint(poseStack, x + 134, y + 27, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 9);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);
		
		this.energyTooltip(poseStack, mouseX, mouseY, 162, 17, 6, 52, "tooltip.sgjourney.energy", this.menu.getEnergy(), this.menu.getEnergyCapacity());
		
		/*this.crystalEffectTooltip(poseStack, 14, 22, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.advanced_protocols")
				.append(Component.literal(": " + menu.enableAdvancedProtocols())).withStyle(ChatFormatting.AQUA),
				ComponentHelper.description("tooltip.sgjourney.dhd.advanced_protocols.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.advanced_protocols.usage"),
				ComponentHelper.tickTimer("info.sgjourney.open_time", menu.getStargateOpenTime(), SGJourneyStargate.MAX_OPEN_TIME, ChatFormatting.DARK_AQUA),
				ComponentHelper.tickTimer("info.sgjourney.last_traveler_time", menu.getStargateTimeSinceLastTraveler(), 200, ChatFormatting.DARK_PURPLE));
		this.crystalEffectTooltip(poseStack, 14, 34, mouseX, mouseY, Component.translatable("tooltip.sgjourney.energy_target")
				.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergyTarget()))).withStyle(ChatFormatting.DARK_RED),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_target.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_target.usage"),
				ComponentHelper.energy("tooltip.sgjourney.dhd.stargate_energy", menu.getStargateEnergy()));
		this.crystalEffectTooltip(poseStack, 14, 46, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_transfer")
				.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.maxEnergyDeplete()) + "/t")).withStyle(ChatFormatting.GOLD),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_transfer.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_transfer.usage"));
		this.crystalEffectTooltip(poseStack, 14, 58, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.communication_range_1")
				.append(Component.literal(": " + menu.getMaxDistance() + " "))
				.append(Component.translatable("tooltip.sgjourney.dhd.communication_range_2")).withStyle(ChatFormatting.GRAY),
				ComponentHelper.description("tooltip.sgjourney.dhd.communication_range.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.communication_range.usage"));*/
		
		this.itemTooltip(poseStack, mouseX, mouseY, 80, 35, 0, ComponentHelper.description("tooltip.sgjourney.dhd.large_crystal_slot.description"));
		
		this.itemTooltip(poseStack, mouseX, mouseY, 80, 17, 1, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 98, 17, 2, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 98, 35, 3, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 98, 53, 4, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 80, 53, 5, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 62, 53, 6, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 62, 35, 7, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(poseStack, mouseX, mouseY, 62, 53, 8, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		
		this.itemTooltip(poseStack, mouseX, mouseY, 134, 27, 9, ComponentHelper.description("tooltip.sgjourney.dhd.energy_slot.description"));
    }
    
    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
	{
    	this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
	
	@Override
	protected boolean hasItem(int slot)
	{
		if(slot < 0 || slot > 9)
			return false;
		
		if(slot >= 8)
			return !menu.blockEntity.energyItemHandler.getStackInSlot(0).isEmpty();
		else
			return !menu.blockEntity.crystalHandler.getStackInSlot(slot).isEmpty();
	}
	
	protected void crystalEffectTooltip(PoseStack poseStack, int x, int y, int mouseX, int mouseY, Component... components)
	{
		this.tooltip(poseStack, mouseX, mouseY, x, y, 16, 6, components);
	}
	
	
	
	public static class Ancient extends TransportRingsScreen<TransportRingsMenu.Ancient>
	{
		public Ancient(TransportRingsMenu.Ancient menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd_crystal_gui.png"));
		}
	}
	
	public static class Goauld extends TransportRingsScreen<TransportRingsMenu.Goauld>
	{
		public Goauld(TransportRingsMenu.Goauld menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd_crystal_gui.png"));
		}
	}
}
