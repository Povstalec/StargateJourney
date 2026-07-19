package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
import net.povstalec.sgjourney.client.widgets.DumpTankButton;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystallizerEntity;
import net.povstalec.sgjourney.common.menu.CrystallizerMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public class CrystallizerScreen<T extends AbstractCrystallizerEntity<?>> extends SGJourneyContainerScreen<CrystallizerMenu<T>>
{
	public static final int HINT_OFFSET_Y = 174;
	public static final int BUCKET_HINT_OFFSET_X = 0;
	public static final int ENERGY_HINT_OFFSET_X = 16;
	public static final int CRYSTAL_BASE_HINT_OFFSET_X = 32;
	
	private final ResourceLocation texture;
	private FluidTankRenderer fluidTankRenderer;
	
    public CrystallizerScreen(CrystallizerMenu<T> menu, ResourceLocation texture, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
		this.texture = texture;
    }
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		
		super.init();
		
		assignFluidRenderer();
		this.addRenderableWidget(new DumpTankButton(x + 52, y + 16, button -> menu.pressDumpButton()));
	}
	
	private void assignFluidRenderer()
	{
		this.fluidTankRenderer = new FluidTankRenderer(menu.blockEntity.inputFluidTankCapacity(), true, 16, 52);
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
		
		this.itemHint(graphics, texture, x + 8, y + 17, BUCKET_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(graphics, texture, x + 142, y + 17, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(graphics, texture, x + 71, y + 17, CRYSTAL_BASE_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.renderEnergyVertical(graphics, texture, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
        this.renderProgress(graphics, x + 52, y + 40);
		
		fluidTankRenderer.render(graphics, x + 34, y + 17, menu.getFluidStack());
    }

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        //this.energyTooltip(stack, 8, 62, mouseX, mouseY);
		this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 52, this.menu.getEnergy(), this.menu.getEnergyCapacity());
        this.liquidNaquadahTooltip(graphics, mouseX, mouseY);
	}
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752);
    }
    
    protected void renderProgress(GuiGraphics graphics, int x, int y)
    {
    	float percentage = (float) this.menu.getProgress() / this.menu.getMaxProgress();
    	int actual = Math.round(54 * percentage);
    	graphics.blit(texture, x, y, 0, 166, actual, 8);
    }
    
    protected void liquidNaquadahTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
		if(this.isHovering(34, 17, 16, 54, mouseX, mouseY))
		{
			FluidStack fluidStack = this.menu.getFluidStack();
			String name = fluidStack.isEmpty() ? "tooltip.sgjourney.empty" : fluidStack.getFluidType().getDescriptionId(fluidStack);
			graphics.renderTooltip(this.font, ComponentHelper.unchangingFluidAmountComponent(name, fluidStack.getAmount(), menu.blockEntity.inputFluidTankCapacity(), ComponentHelper.fluidComponentColor(fluidStack.getFluid())), mouseX, mouseY);
		}
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return switch(slot)
		{
			case 0 -> !menu.blockEntity.crystalBaseHandler.getStackInSlot(0).isEmpty();
			case 1 -> !menu.blockEntity.primaryIngredientHandler.getStackInSlot(0).isEmpty();
			case 2 -> !menu.blockEntity.secondaryIngredientHandler.getStackInSlot(0).isEmpty();
			case 3 -> !menu.blockEntity.outputHandler.getStackInSlot(0).isEmpty();
			case 4 -> !menu.blockEntity.fluidInputHandler.getStackInSlot(0).isEmpty();
			default -> false;
		};
	}
	
	
	
	public static class Crystallizer extends CrystallizerScreen<CrystallizerEntity>
	{
		public Crystallizer(CrystallizerMenu<CrystallizerEntity> menu, Inventory inventory, Component component)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/crystallizer_gui.png"), inventory, component);
		}
	}
	
	public static class AdvancedCrystallizer extends CrystallizerScreen<AdvancedCrystallizerEntity>
	{
		public AdvancedCrystallizer(CrystallizerMenu<AdvancedCrystallizerEntity> menu, Inventory inventory, Component component)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/advanced_crystallizer_gui.png"), inventory, component);
		}
	}
}
