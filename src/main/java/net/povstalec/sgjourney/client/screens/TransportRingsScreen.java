package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.menu.TransportRingsMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public class TransportRingsScreen<T extends TransportRingsMenu<?>> extends SGJourneyContainerScreen<T>
{
	public static final int HINT_OFFSET_Y = 166;
	public static final int CONTROL_CRYSTAL_HINT_OFFSET_X = 0;
	public static final int CRYSTAL_HINT_OFFSET_X = 16;
	public static final int ENERGY_HINT_OFFSET_X = 32;
	
	protected ResourceLocation texture;
	
	public TransportRingsScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, playerInventory, title);
		
		this.texture = texture;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		
		graphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
		
		this.renderEnergyVertical(graphics, texture, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
		
		this.itemHint(graphics, texture, x + 80, y + 35, CONTROL_CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.itemHint(graphics, texture, x + 80, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(graphics, texture, x + 98, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(graphics, texture, x + 98, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 3);
		this.itemHint(graphics, texture, x + 98, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(graphics, texture, x + 80, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(graphics, texture, x + 62, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 6);
		this.itemHint(graphics, texture, x + 62, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 7);
		this.itemHint(graphics, texture, x + 62, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 8);
		
		this.itemHint(graphics, texture, x + 142, y + 17, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 9);
    }
	
	private double energyPerBlock(int transferEfficiency)
	{
		return (double) CommonTransporterConfig.transporter_transport_distance_energy_cost.get() / transferEfficiency;
	}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
		
		this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 52, "tooltip.sgjourney.energy", this.menu.getEnergy(), this.menu.getEnergyCapacity());
		
		long totalEnergy = menu.getTotalEnergyStored();
		int transferEfficiency = menu.getTransferEfficiency();
		
		this.crystalEffectTooltip(graphics, 14, 22, mouseX, mouseY, Component.translatable("tooltip.sgjourney.transport_rings.connection_range", menu.getTransportRange()).withStyle(ChatFormatting.DARK_AQUA),
				ComponentHelper.description("tooltip.sgjourney.transport_rings.connection_range.description"),
				Component.translatable("tooltip.sgjourney.transport_rings.energy_reach", menu.getEnergyReach()).withStyle(ChatFormatting.RED),
				ComponentHelper.description("tooltip.sgjourney.transport_rings.energy_reach.description"),
				Component.translatable("tooltip.sgjourney.transport_rings.interdimensional_transport", menu.allowInterdimensionalTransport()).withStyle(ChatFormatting.AQUA),
				ComponentHelper.description("tooltip.sgjourney.transport_rings.interdimensional_transport.description"),
				ComponentHelper.usage("tooltip.sgjourney.transport_rings.interdimensional_transport.usage"));
		this.crystalEffectTooltip(graphics, 14, 34, mouseX, mouseY, ComponentHelper.energy("tooltip.sgjourney.transport_rings.total_energy", totalEnergy, menu.getTotalEnergyCapacity()),
				ComponentHelper.description("tooltip.sgjourney.transport_rings.total_energy.description"),
				ComponentHelper.usage("tooltip.sgjourney.transport_rings.total_energy.usage"));
		this.crystalEffectTooltip(graphics, 14, 46, mouseX, mouseY, Component.translatable("info.sgjourney.transfer_efficiency").append(": " + transferEfficiency).withStyle(ChatFormatting.GOLD),
				Component.translatable("tooltip.sgjourney.transport_rings.transfer_efficiency.fe_block", energyPerBlock(transferEfficiency)).withStyle(ChatFormatting.DARK_RED),
				ComponentHelper.description("tooltip.sgjourney.transport_rings.transfer_efficiency.description"),
				ComponentHelper.usage("tooltip.sgjourney.transport_rings.transfer_efficiency.usage"));
		this.crystalEffectTooltip(graphics, 14, 58, mouseX, mouseY, Component.translatable("info.sgjourney.networks").append(": " + menu.getNetworks()),
				ComponentHelper.description("tooltip.sgjourney.transport_rings.networks.description"),
				Component.translatable("info.sgjourney.network_restrictions").append(": " + menu.hasNetworkRestrictions()).withStyle(ChatFormatting.AQUA),
				ComponentHelper.usage("tooltip.sgjourney.transport_rings.networks.usage.communication_crystal"),
				ComponentHelper.usage("tooltip.sgjourney.transport_rings.networks.usage.control_crystal"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 80, 35, 0, ComponentHelper.description("tooltip.sgjourney.transport_rings.materialization_crystal_slot.description"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 80, 17, 1, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 17, 2, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 35, 3, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 53, 4, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 80, 53, 5, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 53, 6, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 35, 7, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 17, 8, ComponentHelper.description("tooltip.sgjourney.transport_rings.crystal_slot.description"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 142, 17, 9, ComponentHelper.description("tooltip.sgjourney.transport_rings.energy_slot.description"));
    }
	
	@Override
	protected boolean hasItem(int slot)
	{
		if(slot < 0 || slot > 9)
			return false;
		
		if(slot == 9)
			return !menu.blockEntity.energyItemHandler.getStackInSlot(0).isEmpty();
		else
			return !menu.blockEntity.crystalItemHandler.getStackInSlot(slot).isEmpty();
	}
	
	protected void crystalEffectTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Component... components)
	{
		this.tooltip(graphics, mouseX, mouseY, x, y, 16, 6, components);
	}
	
	
	
	public static class Ancient extends TransportRingsScreen<TransportRingsMenu.Ancient>
	{
		public Ancient(TransportRingsMenu.Ancient menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/transporter/ancient_transport_rings_gui.png"));
		}
	}
	
	public static class Goauld extends TransportRingsScreen<TransportRingsMenu.Goauld>
	{
		public Goauld(TransportRingsMenu.Goauld menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/transporter/goauld_transport_rings_gui.png"));
		}
	}
}
