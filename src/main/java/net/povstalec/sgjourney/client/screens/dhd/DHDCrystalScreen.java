package net.povstalec.sgjourney.client.screens.dhd;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyStargate;

public class DHDCrystalScreen<T extends DHDCrystalMenu<?>> extends SGJourneyContainerScreen<T>
{
	public static final int HINT_OFFSET_Y = 166;
	public static final int LARGE_CRYSTAL_HINT_OFFSET_X = 0;
	public static final int CRYSTAL_HINT_OFFSET_X = 16;
	public static final int ENERGY_HINT_OFFSET_X = 32;
	
	public final ResourceLocation texture;
	
	public DHDCrystalScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, playerInventory, title);
		
		this.texture = texture;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
		
		this.renderEnergyVertical(graphics, texture, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		this.itemHint(graphics, texture, x + 80, y + 35, LARGE_CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.itemHint(graphics, texture, x + 80, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(graphics, texture, x + 98, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(graphics, texture, x + 98, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 3);
		this.itemHint(graphics, texture, x + 98, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(graphics, texture, x + 80, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(graphics, texture, x + 62, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 6);
		this.itemHint(graphics, texture, x + 62, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 7);
		this.itemHint(graphics, texture, x + 62, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 8);
		
		this.itemHint(graphics, texture, x + 134, y + 27, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 9);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
		
		this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 52, "tooltip.sgjourney.energy_buffer", this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		this.crystalEffectTooltip(graphics, 14, 22, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.advanced_protocols")
				.append(Component.literal(": " + menu.enableAdvancedProtocols())).withStyle(ChatFormatting.AQUA),
				ComponentHelper.description("tooltip.sgjourney.dhd.advanced_protocols.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.advanced_protocols.usage"),
				ComponentHelper.tickTimer("info.sgjourney.open_time", menu.getStargateOpenTime(), SGJourneyStargate.MAX_OPEN_TIME, ChatFormatting.DARK_AQUA),
				ComponentHelper.tickTimer("info.sgjourney.last_traveler_time", menu.getStargateTimeSinceLastTraveler(), ControlCrystalItem.AUTOCLOSE_TICKS, ChatFormatting.DARK_PURPLE));
		this.crystalEffectTooltip(graphics, 14, 34, mouseX, mouseY, Component.translatable("tooltip.sgjourney.energy_target")
				.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergyTarget()))).withStyle(ChatFormatting.DARK_RED),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_target.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_target.usage"),
				ComponentHelper.energy("tooltip.sgjourney.dhd.stargate_energy", menu.getStargateEnergy()));
		this.crystalEffectTooltip(graphics, 14, 46, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_transfer")
				.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.maxEnergyDeplete()) + "/t")).withStyle(ChatFormatting.GOLD),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_transfer.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_transfer.usage"));
		this.crystalEffectTooltip(graphics, 14, 58, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.communication_range", menu.getMaxDistance()).withStyle(ChatFormatting.GRAY),
				ComponentHelper.description("tooltip.sgjourney.dhd.communication_range.description"),
				Component.translatable("info.sgjourney.networks").append(": " + menu.getNetworks()),
				ComponentHelper.description("tooltip.sgjourney.dhd.networks.description"),
				Component.translatable("info.sgjourney.network_restrictions").append(": " + menu.hasNetworkRestrictions()).withStyle(ChatFormatting.AQUA),
				ComponentHelper.usage("tooltip.sgjourney.dhd.communication_range.usage"),
				Component.translatable("tooltip.sgjourney.dhd.communication_range.usage.communication_crystal").withStyle(ChatFormatting.YELLOW),
				Component.translatable("tooltip.sgjourney.dhd.networks.usage.communication_crystal").withStyle(ChatFormatting.YELLOW),
				ComponentHelper.usage("tooltip.sgjourney.dhd.networks.usage.control_crystal"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 80, 35, 0, ComponentHelper.description("tooltip.sgjourney.dhd.large_crystal_slot.description"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 80, 17, 1, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 17, 2, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 35, 3, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 53, 4, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 80, 53, 5, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 53, 6, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 35, 7, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 17, 8, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 134, 27, 9, ComponentHelper.description("tooltip.sgjourney.dhd.energy_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 134, 53, 10, ComponentHelper.description("tooltip.sgjourney.dhd.energy_fuel_slot.description"));
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
	
	@Override
	protected boolean hasItem(int slot)
	{
		if(slot < 0 || slot > 10)
			return false;
		
		if(slot < 9)
			return !menu.blockEntity.crystalHandler.getStackInSlot(slot).isEmpty();
		else
			return !menu.blockEntity.energyItemHandler.getStackInSlot(slot - 9).isEmpty();
	}
	
	protected void crystalEffectTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Component... components)
	{
		this.tooltip(graphics, mouseX, mouseY, x, y, 16, 6, components);
	}
	
	
	
	public static class MilkyWay extends DHDCrystalScreen<DHDCrystalMenu.MilkyWay>
	{
		public MilkyWay(DHDCrystalMenu.MilkyWay menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/milky_way/milky_way_dhd_crystal_gui.png"));
		}
	}
	
	public static class Pegasus extends DHDCrystalScreen<DHDCrystalMenu.Pegasus>
	{
		public Pegasus(DHDCrystalMenu.Pegasus menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_crystal_gui.png"));
		}
	}
	
	public static class Classic extends DHDCrystalScreen<DHDCrystalMenu.Classic>
	{
		public Classic(DHDCrystalMenu.Classic menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_crystal_gui.png"));
		}
	}
}
