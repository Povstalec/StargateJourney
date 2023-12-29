package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.menu.NaquadahLiquidizerMenu;

public class NaquadahLiquidizerScreen extends AbstractContainerScreen<NaquadahLiquidizerMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/naquadah_liquidizer_gui.png");
	private FluidTankRenderer renderer;
	
    public NaquadahLiquidizerScreen(NaquadahLiquidizerMenu menu, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
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
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        //this.renderEnergy(pPoseStack, x + 8, y + 62);
        this.renderProgress(graphics, x + 28, y + 37);
        
        renderer.render(graphics, x + 12, y + 20, menu.getFluid1());
        renderer.render(graphics, x + 148, y + 20, menu.getFluid2());
    }

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        //this.energyTooltip(stack, 8, 62, mouseX, mouseY);
        this.liquidFluid1Tooltip(graphics, 12, 20, mouseX, mouseY);
        this.liquidFluid2Tooltip(graphics, 148, 20, mouseX, mouseY);
	}
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
    	graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	    //this.font.draw(stack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    
    protected void renderProgress(GuiGraphics graphics, int x, int y)
    {
    	float percentage = (float) this.menu.getProgress() / AbstractNaquadahLiquidizerEntity.MAX_PROGRESS;
    	int actual = Math.round(119 * percentage);
    	graphics.blit(TEXTURE, x, y, 0, 166, actual, 12);
    }
    
    /*protected void renderEnergy(PoseStack stack, int x, int y)
    {
    	float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
    	int actual = Math.round(160 * percentage);
    	this.blit(stack, x, y, 0, 168, actual, 6);
    }*/
    
    protected void liquidFluid1Tooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 16, 54, (double) mouseX, (double) mouseY))
	    {
    		FluidStack fluidStack = new FluidStack(menu.getDesiredFluid1(), 1);
    		graphics.renderTooltip(this.font, Component.translatable(fluidStack.getTranslationKey()).append(Component.literal(": " + this.menu.getFluid1().getAmount() + "/" + AbstractNaquadahLiquidizerEntity.TANK_CAPACITY + "mB")).withStyle(ChatFormatting.GREEN), mouseX, mouseY);
	    }
    }
    
    protected void liquidFluid2Tooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 16, 54, (double) mouseX, (double) mouseY))
	    {
    		FluidStack fluidStack = new FluidStack(menu.getDesiredFluid2(), 1);
    		graphics.renderTooltip(this.font, Component.translatable(fluidStack.getTranslationKey()).append(Component.literal(": " + this.menu.getFluid2().getAmount() + "/" + AbstractNaquadahLiquidizerEntity.TANK_CAPACITY + "mB")).withStyle(ChatFormatting.GREEN), mouseX, mouseY);
	    }
    }
    
    /*protected void energyTooltip(PoseStack stack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 160, 6, (double) mouseX, (double) mouseY))
	    {
	    	renderTooltip(stack, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + this.menu.getEnergy() + "/" + this.menu.getMaxEnergy() + " FE")).withStyle(ChatFormatting.DARK_RED), mouseX, mouseY);
	    }
    }*/
}
