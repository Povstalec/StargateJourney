package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.InterfaceModeButton;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.menu.InterfaceMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyStargate;

import java.util.ArrayList;
import java.util.List;

public abstract class InterfaceScreen<T extends AbstractInterfaceEntity> extends SGJourneyContainerScreen<InterfaceMenu<T>>
{
	public static final int HINT_OFFSET_Y = 222;
	public static final int ENERGY_HINT_OFFSET_X = 0;
	
	private final ResourceLocation texture;
	
	protected EditBox editBox;
	
	public InterfaceScreen(InterfaceMenu<T> menu, ResourceLocation texture, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.texture = texture;
		
		this.imageWidth = 199;
		this.imageHeight = 222;
		
		this.titleLabelX += 23;
		this.inventoryLabelX += 23;
		this.inventoryLabelY += 56;
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
	
	public InterfaceMode getMode()
	{
		return this.menu.getMode();
	}
	
	@Override
	protected void init()
	{
		this.editBox = new EditBox(this.font, this.width / 2 - 69, this.height / 2 - 89, 124, 20, Component.translatable("tooltip.sgjourney.energy_target"));
		this.editBox.setFilter(text ->
		{
			if(text.isEmpty())
				return true;
			
		   try { return Long.parseLong(text) >= 0; }
		   catch (NumberFormatException e) { return false; }
		});
		
		this.editBox.setMaxLength(19);
		this.addRenderableWidget(this.editBox);
		this.setInitialFocus(this.editBox);
		this.editBox.setResponder(text -> menu.setEnergyTargetAndMode(parsePositiveOrZero(text), menu.getMode()));
		
		this.editBox.setValue(String.valueOf(menu.getEnergyTarget()));
		
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		this.addRenderableWidget(new InterfaceModeButton(x + 4, y + 23, Component.empty(), Component.empty(), button ->
		{
			if(isShiftDown())
				menu.setEnergyTargetAndMode(parsePositiveOrZero(this.editBox.getValue()), menu.getMode().previous(this.menu.getInterfaceType().hasAdvancedCrystalMethods()));
			else
				menu.setEnergyTargetAndMode(parsePositiveOrZero(this.editBox.getValue()), menu.getMode().next(this.menu.getInterfaceType().hasAdvancedCrystalMethods()));
		}, this));
		
		super.init();
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
		
		this.itemHint(graphics, texture, x + 165, y + 18, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		
		this.renderEnergyVertical(graphics, texture, x + 162, y + 17, 6, 138, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
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
		
		renderTooltip(graphics, mouseX, mouseY);
		
		this.tooltip(graphics, mouseX, mouseY, 33, 16, 126, 20, ComponentHelper.energy("tooltip.sgjourney.energy_target", this.menu.getEnergyTarget()),
				ComponentHelper.description("tooltip.sgjourney.interface.energy_target.description"));
		this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 138, "tooltip.sgjourney.energy_buffer", this.menu.getEnergy(), this.menu.getEnergyCapacity());
		
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
		graphics.drawString(this.font, ComponentHelper.tickTimer(menu.getStargateOpenTime(), SGJourneyStargate.MAX_OPEN_TIME, ChatFormatting.DARK_AQUA), 20, 76, 0xffffff, false);
		graphics.drawString(this.font, Component.translatable("info.sgjourney.last_traveler_time").append(":").withStyle(ChatFormatting.DARK_PURPLE), 20, 86, 0xffffff, false);
		graphics.drawString(this.font, Component.literal(Conversion.ticksToString(menu.getStargateTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE), 20, 96, 0xffffff, false);
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return !menu.blockEntity.energyItemHandler.getStackInSlot(0).isEmpty();
	}
	
	
	
	public static class Basic extends InterfaceScreen<BasicInterfaceEntity>
	{
		public Basic(InterfaceMenu<BasicInterfaceEntity> menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/interface/basic_interface_gui.png"), inventory, component);
		}
	}
	
	public static class Crystal extends InterfaceScreen<CrystalInterfaceEntity>
	{
		public Crystal(InterfaceMenu<CrystalInterfaceEntity> menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/interface/crystal_interface_gui.png"), inventory, component);
		}
	}
	
	public static class AdvancedCrystal extends InterfaceScreen<AdvancedCrystalInterfaceEntity>
	{
		public AdvancedCrystal(InterfaceMenu<AdvancedCrystalInterfaceEntity> menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/interface/advanced_crystal_interface_gui.png"), inventory, component);
		}
	}
}
