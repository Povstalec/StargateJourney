package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.RingPanelButton;
import net.povstalec.sgjourney.common.menu.RingPanelMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public class RingPanelScreen extends SGJourneyContainerScreen<RingPanelMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/ring_panel_gui.png");
	
	
	public static final int HINT_OFFSET_Y = 222;
	public static final int CRYSTAL_HINT_OFFSET_X = 0;
	public static final int ENERGY_HINT_OFFSET_X = 16;
	
    public RingPanelScreen(RingPanelMenu menu, Inventory playerInventory, Component title)
	{
        super(menu, playerInventory, title);
        this.imageHeight = 222;
    }
    
    @Override
    public void init()
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		super.init();
		
		this.addRenderableWidget(new RingPanelButton(x + 51, y + 48, button -> menu.pressButton(0), menu.getButtonAt(0)));
		this.addRenderableWidget(new RingPanelButton(x + 93, y + 48, button -> menu.pressButton(1), menu.getButtonAt(1)));
		this.addRenderableWidget(new RingPanelButton(x + 51, y + 66, button -> menu.pressButton(2), menu.getButtonAt(2)));
		this.addRenderableWidget(new RingPanelButton(x + 93, y + 66, button -> menu.pressButton(3), menu.getButtonAt(3)));
		this.addRenderableWidget(new RingPanelButton(x + 51, y + 84, button -> menu.pressButton(4), menu.getButtonAt(4)));
		this.addRenderableWidget(new RingPanelButton(x + 93, y + 84, button -> menu.pressButton(5), menu.getButtonAt(5)));
	}

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		
		graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight + 1);
		
		this.renderEnergyVertical(graphics, TEXTURE, x + 165, y + 36, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		this.itemHint(graphics, TEXTURE, x + 5, y + 36, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		this.itemHint(graphics, TEXTURE, x + 23, y + 36, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(graphics, TEXTURE, x + 5, y + 54, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(graphics, TEXTURE, x + 23, y + 54, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 3);
		this.itemHint(graphics, TEXTURE, x + 5, y + 72, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(graphics, TEXTURE, x + 23, y + 72, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		
		this.itemHint(graphics, TEXTURE, x + 137, y + 36, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 9);
		
		graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight + 1);
		
		this.renderEnergyVertical(graphics, TEXTURE, x + 165, y + 36, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		this.itemHint(graphics, TEXTURE, x + 5, y + 36, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		this.itemHint(graphics, TEXTURE, x + 23, y + 36, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(graphics, TEXTURE, x + 5, y + 54, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(graphics, TEXTURE, x + 23, y + 54, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 3);
		this.itemHint(graphics, TEXTURE, x + 5, y + 72, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(graphics, TEXTURE, x + 23, y + 72, CRYSTAL_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		
		this.itemHint(graphics, TEXTURE, x + 137, y + 36, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 9);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
		
		this.energyTooltip(graphics, mouseX, mouseY, 165, 36, 6, 52, "tooltip.sgjourney.energy_buffer", this.menu.getEnergy(), this.menu.getMaxEnergy());
		
		//TODO
		/*this.crystalEffectTooltip(poseStack, 136, 56, mouseX, mouseY, Component.translatable("tooltip.sgjourney.energy_target")
						.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.getEnergyTarget()))).withStyle(ChatFormatting.DARK_RED),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_target.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_target.usage"),
				ComponentHelper.energy("tooltip.sgjourney.dhd.stargate_energy", menu.getStargateEnergy()));
		this.crystalEffectTooltip(poseStack, 136, 68, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.energy_transfer")
						.append(Component.literal(": " + SGJourneyEnergy.energyToString(menu.maxEnergyDeplete()) + "/t")).withStyle(ChatFormatting.GOLD),
				ComponentHelper.description("tooltip.sgjourney.dhd.energy_transfer.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.energy_transfer.usage"));
		this.crystalEffectTooltip(poseStack, 136, 80, mouseX, mouseY, Component.translatable("tooltip.sgjourney.dhd.communication_range_1")
						.append(Component.literal(": " + menu.getMaxDistance() + " "))
						.append(Component.translatable("tooltip.sgjourney.dhd.communication_range_2")).withStyle(ChatFormatting.GRAY),
				ComponentHelper.description("tooltip.sgjourney.dhd.communication_range.description"),
				ComponentHelper.usage("tooltip.sgjourney.dhd.communication_range.usage"));*/
		
		this.itemTooltip(graphics, mouseX, mouseY, 5, 36, 0, ComponentHelper.description("tooltip.sgjourney.ring_panel.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 23, 36, 1, ComponentHelper.description("tooltip.sgjourney.ring_panel.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 5, 54, 2, ComponentHelper.description("tooltip.sgjourney.ring_panel.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 23, 54, 3, ComponentHelper.description("tooltip.sgjourney.ring_panel.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 5, 72, 4, ComponentHelper.description("tooltip.sgjourney.ring_panel.crystal_slot.description"));
		this.itemTooltip(graphics, mouseX, mouseY, 23, 72, 5, ComponentHelper.description("tooltip.sgjourney.ring_panel.crystal_slot.description"));
		
		this.itemTooltip(graphics, mouseX, mouseY, 137, 36, 6, ComponentHelper.description("tooltip.sgjourney.ring_panel.energy_slot.description"));
    }
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, 128, 4210752, false);
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
