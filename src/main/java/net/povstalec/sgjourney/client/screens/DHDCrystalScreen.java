package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public class DHDCrystalScreen extends SGJourneyContainerScreen<DHDCrystalMenu>
{
	private static final ResourceLocation TEXTURE = StargateJourney.sgjourneyLocation("textures/gui/dhd_crystal_gui.png");
	
	public static final int HINT_OFFSET_Y = 168;
	public static final int CRYSTAL_HINT_OFFSET_X = 16;
	public static final int ENERGY_HINT_OFFSET_X = 32;
	
	public DHDCrystalScreen(DHDCrystalMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
		super(pMenu, pPlayerInventory, pTitle);
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
		
		this.renderEnergyVertical(graphics, TEXTURE, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		this.itemHint(graphics, TEXTURE, x + 80, y + 35, 0, 168, 0);
		
		this.itemHint(graphics, TEXTURE, x + 80, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(graphics, TEXTURE, x + 98, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(graphics, TEXTURE, x + 98, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 3);
		this.itemHint(graphics, TEXTURE, x + 98, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(graphics, TEXTURE, x + 80, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(graphics, TEXTURE, x + 62, y + 53, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 6);
		this.itemHint(graphics, TEXTURE, x + 62, y + 35, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 7);
		this.itemHint(graphics, TEXTURE, x + 62, y + 17, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 8);
		
		this.itemHint(graphics, TEXTURE, x + 134, y + 27, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 9);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
	{
		renderBackground(graphics, mouseX, mouseY, delta);
		super.render(graphics, mouseX, mouseY, delta);
		renderTooltip(graphics, mouseX, mouseY);
		
		this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 52, "tooltip.sgjourney.energy_buffer", this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		this.crystalEffectTooltip(graphics, 14, 22, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.advanced_protocols")
						.append(Component.literal(": " + menu.enableAdvancedProtocols())).withStyle(ChatFormatting.AQUA),
				ComponentHelper.description("tooltip.sgjourney.dhd.advanced_protocols.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.advanced_protocols.usage"),
				ComponentHelper.tickTimer("info.sgjourney.open_time", menu.getStargateOpenTime(), Stargate.getMaxGateOpenTime(), ChatFormatting.DARK_AQUA),
				ComponentHelper.tickTimer("info.sgjourney.last_traveler_time", menu.getStargateTimeSinceLastTraveler(), 200, ChatFormatting.DARK_PURPLE));
		this.crystalEffectTooltip(graphics, 14, 34, mouseX, mouseY, Component.translatable("tooltip.sgjourney.energy_target")
						.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergyTarget()))).withStyle(ChatFormatting.DARK_RED),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_target.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_target.usage"),
				ComponentHelper.energy("tooltip.sgjourney.dhd.stargate_energy", menu.getStargateEnergy()));
		this.crystalEffectTooltip(graphics, 14, 46, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_transfer")
						.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.maxEnergyDeplete()) + "/t")).withStyle(ChatFormatting.GOLD),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_transfer.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_transfer.usage"));
		this.crystalEffectTooltip(graphics, 14, 58, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.communication_range_1")
						.append(Component.literal(": " + menu.getMaxDistance() + " "))
						.append(Component.translatable("tooltip.sgjourney.dhd.communication_range_2")).withStyle(ChatFormatting.GRAY),
				ComponentHelper.description("tooltip.sgjourney.dhd.communication_range.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.communication_range.usage"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 80, 35, 0, ComponentHelper.description("tooltip.sgjourney.dhd.large_crystal_slot.description"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 80, 17, 1, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 17, 2, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 35, 3, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 98, 53, 4, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 80, 53, 5, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 53, 6, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 35, 7, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 62, 53, 6, ComponentHelper.description("tooltip.sgjourney.dhd.crystal_slot.description"));
		
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
		return this.menu.hasItem(slot);
	}
	
	protected void crystalEffectTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Component... components)
	{
		this.tooltip(graphics, mouseX, mouseY, x, y, 16, 6, components);
	}
}
