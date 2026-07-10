package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
import net.povstalec.sgjourney.client.widgets.DumpTankButton;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.HeavyNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.NaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.menu.LiquidizerMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public abstract class LiquidizerScreen<T extends AbstractNaquadahLiquidizerEntity<?>> extends SGJourneyContainerScreen<LiquidizerMenu<T>>
{
	public static final int HINT_OFFSET_Y = 174;
	public static final int BUCKET_HINT_OFFSET_X = 0;
	public static final int ENERGY_HINT_OFFSET_X = 16;
	public static final int ITEM_INPUT_HINT_OFFSET_X = 32;
	
	private final ResourceLocation texture;
	private FluidTankRenderer inputFluidTankRenderer;
	private FluidTankRenderer outputFluidTankRenderer;
	
    public LiquidizerScreen(LiquidizerMenu<T> menu, ResourceLocation texture, Inventory inventory, Component component)
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
		this.addRenderableWidget(new DumpTankButton(x + 52, y + 60, button -> menu.pressDumpButton(true)));
		this.addRenderableWidget(new DumpTankButton(x + 78, y + 60, button -> menu.pressDumpButton(false)));
	}
	
	private void assignFluidRenderer()
	{
		this.inputFluidTankRenderer = new FluidTankRenderer(menu.blockEntity.inputFluidTankCapacity(), true, 16, 52);
		this.outputFluidTankRenderer = new FluidTankRenderer(menu.blockEntity.outputFluidTankCapacity(), true, 16, 52);
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
		
		this.itemHint(graphics, texture, x + 8, y + 17, BUCKET_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(graphics, texture, x + 116, y + 17, BUCKET_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(graphics, texture, x + 142, y + 17, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(graphics, texture, x + 62, y + 17, ITEM_INPUT_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.renderEnergyVertical(graphics, texture, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
		this.renderProgress(graphics, x + 52, y + 40);
		
		inputFluidTankRenderer.render(graphics, x + 34, y + 17, menu.getInputFluidStack());
		outputFluidTankRenderer.render(graphics, x + 90, y + 17, menu.getOutputFluidStack());
    }

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
		this.energyTooltip(graphics, mouseX, mouseY, 162, 17, 6, 52, this.menu.getEnergy(), this.menu.getEnergyCapacity());
		this.liquidFluidInputTooltip(graphics, mouseX, mouseY);
		this.liquidFluidOutputTooltip(graphics, mouseX, mouseY);
	}
	
	protected void renderProgress(GuiGraphics graphics, int x, int y)
    {
    	float percentage = (float) this.menu.getProgress() / this.menu.getMaxProgress();
    	int actual = Math.round(36 * percentage);
		graphics.blit(texture, x, y, 0, 166, actual, 8);
    }
    
    protected void liquidFluidInputTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
    	if(this.isHovering(34, 17, 16, 54, mouseX, mouseY))
	    {
    		FluidStack fluidStack = this.menu.getInputFluidStack();
			String name = fluidStack.isEmpty() ? "tooltip.sgjourney.empty" : fluidStack.getTranslationKey();
			graphics.renderTooltip(this.font, ComponentHelper.unchangingFluidAmountComponent(name, fluidStack.getAmount(), menu.blockEntity.inputFluidTankCapacity(), ComponentHelper.fluidComponentColor(fluidStack.getFluid())), mouseX, mouseY);
	    }
    }
    
    protected void liquidFluidOutputTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
    	if(this.isHovering(90, 17, 16, 54, mouseX, mouseY))
	    {
    		FluidStack fluidStack = this.menu.getOutputFluidStack();
			String name = fluidStack.isEmpty() ? "tooltip.sgjourney.empty" : fluidStack.getTranslationKey();
			graphics.renderTooltip(this.font, ComponentHelper.unchangingFluidAmountComponent(name, fluidStack.getAmount(), menu.blockEntity.outputFluidTankCapacity(), ComponentHelper.fluidComponentColor(fluidStack.getFluid())), mouseX, mouseY);
	    }
    }
	
	@Override
	protected boolean hasItem(int slot)
	{
		return switch(slot)
		{
			case 0 -> !menu.blockEntity.itemInputHandler.getStackInSlot(0).isEmpty();
			case 1 -> !menu.blockEntity.fluidItemInputHandler.getStackInSlot(0).isEmpty();
			case 2 -> !menu.blockEntity.fluidItemInputHandler.getStackInSlot(1).isEmpty();
			case 3 -> !menu.blockEntity.fluidItemOutputHandler.getStackInSlot(0).isEmpty();
			case 4 -> !menu.blockEntity.fluidItemOutputHandler.getStackInSlot(1).isEmpty();
			case 5 -> !menu.blockEntity.energyItemHandler.getStackInSlot(0).isEmpty();
			default -> false;
		};
	}
	
	
    
    public static class LiquidNaquadah extends LiquidizerScreen<NaquadahLiquidizerEntity>
    {
		public LiquidNaquadah(LiquidizerMenu<NaquadahLiquidizerEntity> menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/naquadah_liquidizer_gui.png"), inventory, component);
		}
    }
    
    public static class HeavyLiquidNaquadah extends LiquidizerScreen<HeavyNaquadahLiquidizerEntity>
    {
		public HeavyLiquidNaquadah(LiquidizerMenu<HeavyNaquadahLiquidizerEntity> menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/heavy_naquadah_liquidizer_gui.png"), inventory, component);
		}
    }
}
