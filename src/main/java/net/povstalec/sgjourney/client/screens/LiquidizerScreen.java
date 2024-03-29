package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.menu.LiquidizerMenu;

public abstract class LiquidizerScreen extends AbstractContainerScreen<LiquidizerMenu>
{
	private final ResourceLocation texture;
	private FluidTankRenderer renderer;
	
    public LiquidizerScreen(LiquidizerMenu menu, ResourceLocation texture, Inventory inventory, Component component)
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
		this.renderer = new FluidTankRenderer(AbstractNaquadahLiquidizerEntity.TANK_CAPACITY, true, 16, 54);
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
        
        //this.renderEnergy(pPoseStack, x + 8, y + 62);
        this.renderProgress(stack, x + 28, y + 37);
        
        renderer.render(stack, x + 12, y + 20, menu.getFluid1());
        renderer.render(stack, x + 148, y + 20, menu.getFluid2());
    }

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float delta)
    {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        renderTooltip(stack, mouseX, mouseY);
        
        //this.energyTooltip(stack, 8, 62, mouseX, mouseY);
        this.liquidFluid1Tooltip(stack, 12, 20, mouseX, mouseY);
        this.liquidFluid2Tooltip(stack, 148, 20, mouseX, mouseY);
	}
    
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) 
	{
		this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    //this.font.draw(stack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    
    protected void renderProgress(PoseStack stack, int x, int y)
    {
    	float percentage = (float) this.menu.getProgress() / AbstractNaquadahLiquidizerEntity.MAX_PROGRESS;
    	int actual = Math.round(119 * percentage);
    	this.blit(stack, x, y, 0, 166, actual, 12);
    }
    
    /*protected void renderEnergy(PoseStack stack, int x, int y)
    {
    	float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
    	int actual = Math.round(160 * percentage);
    	this.blit(stack, x, y, 0, 168, actual, 6);
    }*/
    
    protected void liquidFluid1Tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 16, 54, (double) mouseX, (double) mouseY))
	    {
    		FluidStack fluidStack = new FluidStack(menu.getDesiredFluid1(), 1);
	    	renderTooltip(matrixStack, Component.translatable(fluidStack.getTranslationKey()).append(Component.literal(": " + this.menu.getFluid1().getAmount() + "/" + AbstractNaquadahLiquidizerEntity.TANK_CAPACITY + "mB")).withStyle(ChatFormatting.GREEN), mouseX, mouseY);
	    }
    }
    
    protected void liquidFluid2Tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 16, 54, (double) mouseX, (double) mouseY))
	    {
    		FluidStack fluidStack = new FluidStack(menu.getDesiredFluid2(), 1);
	    	renderTooltip(matrixStack, Component.translatable(fluidStack.getTranslationKey()).append(Component.literal(": " + this.menu.getFluid2().getAmount() + "/" + AbstractNaquadahLiquidizerEntity.TANK_CAPACITY + "mB")).withStyle(ChatFormatting.GREEN), mouseX, mouseY);
	    }
    }
    
    /*protected void energyTooltip(PoseStack stack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 160, 6, (double) mouseX, (double) mouseY))
	    {
	    	renderTooltip(stack, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + this.menu.getEnergy() + "/" + this.menu.getMaxEnergy() + " FE")).withStyle(ChatFormatting.DARK_RED), mouseX, mouseY);
	    }
    }*/
    
    public static class LiquidNaquadah extends LiquidizerScreen
    {
		public LiquidNaquadah(LiquidizerMenu menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/naquadah_liquidizer_gui.png"), inventory, component);
		}
    }
    
    public static class HeavyLiquidNaquadah extends LiquidizerScreen
    {
		public HeavyLiquidNaquadah(LiquidizerMenu menu, Inventory inventory, Component component)
		{
			super(menu, new ResourceLocation(StargateJourney.MODID, "textures/gui/heavy_naquadah_liquidizer_gui.png"), inventory, component);
		}
    }
}
