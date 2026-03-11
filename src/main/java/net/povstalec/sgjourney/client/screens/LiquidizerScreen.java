package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
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
	private FluidTankRenderer fluidTankRenderer;
	
    public LiquidizerScreen(LiquidizerMenu<T> menu, ResourceLocation texture, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
        this.texture = texture;
    }
	
	@Override
	public void init()
	{
		super.init();
		assignFluidRenderer();
	}
	
	private void assignFluidRenderer()
	{
		this.fluidTankRenderer = new FluidTankRenderer(AbstractNaquadahLiquidizerEntity.TANK_CAPACITY, true, 16, 52);
	}

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(stack, x, y, 0, 0, imageWidth, imageHeight);
		
		this.itemHint(stack, x + 8, y + 17, BUCKET_HINT_OFFSET_X, HINT_OFFSET_Y, 1);
		this.itemHint(stack, x + 116, y + 17, BUCKET_HINT_OFFSET_X, HINT_OFFSET_Y, 2);
		this.itemHint(stack, x + 142, y + 17, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(stack, x + 62, y + 17, ITEM_INPUT_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.renderEnergyVertical(stack, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
        this.renderProgress(stack, x + 52, y + 40);
        
        fluidTankRenderer.render(stack, x + 34, y + 17, menu.getFluid1());
        fluidTankRenderer.render(stack, x + 90, y + 17, menu.getFluid2());
    }

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float delta)
    {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        renderTooltip(stack, mouseX, mouseY);
		
		this.energyTooltip(stack, mouseX, mouseY, 162, 17, 6, 52, this.menu.getEnergy(), this.menu.getEnergyCapacity());
        this.liquidFluid1Tooltip(stack, 34, 17, mouseX, mouseY);
        this.liquidFluid2Tooltip(stack, 90, 17, mouseX, mouseY);
	}
    
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) 
	{
		this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(stack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    
    protected void renderProgress(PoseStack stack, int x, int y)
    {
    	float percentage = (float) this.menu.getProgress() / this.menu.getMaxProgress();
    	int actual = Math.round(36 * percentage);
    	this.blit(stack, x, y, 0, 166, actual, 8);
    }
    
    protected void liquidFluid1Tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 16, 54, (double) mouseX, (double) mouseY))
	    {
    		FluidStack fluidStack = new FluidStack(menu.getDesiredFluid1(), 1);
	    	renderTooltip(matrixStack, ComponentHelper.unchangingFluidAmountComponent(fluidStack.getTranslationKey(), this.menu.getFluid1().getAmount(), AbstractNaquadahLiquidizerEntity.TANK_CAPACITY, ComponentHelper.fluidComponentColor(menu.getDesiredFluid1())), mouseX, mouseY);
	    }
    }
    
    protected void liquidFluid2Tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 16, 54, (double) mouseX, (double) mouseY))
	    {
    		FluidStack fluidStack = new FluidStack(menu.getDesiredFluid2(), 1);
	    	renderTooltip(matrixStack, ComponentHelper.unchangingFluidAmountComponent(fluidStack.getTranslationKey(), this.menu.getFluid2().getAmount(), AbstractNaquadahLiquidizerEntity.TANK_CAPACITY, ComponentHelper.fluidComponentColor(menu.getDesiredFluid2())), mouseX, mouseY);
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
