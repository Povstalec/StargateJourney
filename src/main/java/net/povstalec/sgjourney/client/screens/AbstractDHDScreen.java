package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class AbstractDHDScreen extends AbstractContainerScreen<AbstractDHDMenu>
{
	private ResourceLocation texture;
	
	public AbstractDHDScreen(AbstractDHDMenu menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, playerInventory, title);
        this.imageWidth = 192;
        this.imageHeight = 192;
        
        this.texture = texture;
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight + 1);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        renderTooltip(matrixStack, mouseX, mouseY);
	    
        //TODO Add Tooltips back
	    /*tooltip(matrixStack, 44, 6, mouseX, mouseY, "1");
	    tooltip(matrixStack, 64, 6, mouseX, mouseY, "2");
	    tooltip(matrixStack, 84, 6, mouseX, mouseY, "3");
	    tooltip(matrixStack, 104, 6, mouseX, mouseY, "4");
	    tooltip(matrixStack, 124, 6, mouseX, mouseY, "5");

	    tooltip(matrixStack, 10, 26, mouseX, mouseY, "6");
	    tooltip(matrixStack, 30, 26, mouseX, mouseY, "7");
	    tooltip(matrixStack, 50, 26, mouseX, mouseY, "8");
	    tooltip(matrixStack, 70, 26, mouseX, mouseY, "9");
	    tooltip(matrixStack, 90, 26, mouseX, mouseY, "10");
	    tooltip(matrixStack, 110, 26, mouseX, mouseY, "11");
	    tooltip(matrixStack, 130, 26, mouseX, mouseY, "12");
	    tooltip(matrixStack, 150, 26, mouseX, mouseY, "13");

	    tooltip(matrixStack, 10, 46, mouseX, mouseY, "14");
	    tooltip(matrixStack, 30, 46, mouseX, mouseY, "15");
	    tooltip(matrixStack, 50, 46, mouseX, mouseY, "16");

	    tooltip(matrixStack, 110, 46, mouseX, mouseY, "17");
	    tooltip(matrixStack, 130, 46, mouseX, mouseY, "18");
	    tooltip(matrixStack, 150, 46, mouseX, mouseY, "19");

	    tooltip(matrixStack, 10, 66, mouseX, mouseY, "20");
	    tooltip(matrixStack, 30, 66, mouseX, mouseY, "21");
	    tooltip(matrixStack, 50, 66, mouseX, mouseY, "22");

	    tooltip(matrixStack, 110, 66, mouseX, mouseY, "23");
	    tooltip(matrixStack, 130, 66, mouseX, mouseY, "24");
	    tooltip(matrixStack, 150, 66, mouseX, mouseY, "25");

	    tooltip(matrixStack, 10, 86, mouseX, mouseY, "26");
	    tooltip(matrixStack, 30, 86, mouseX, mouseY, "27");
	    tooltip(matrixStack, 50, 86, mouseX, mouseY, "28");
	    tooltip(matrixStack, 70, 86, mouseX, mouseY, "29");
	    tooltip(matrixStack, 90, 86, mouseX, mouseY, "30");
	    tooltip(matrixStack, 110, 86, mouseX, mouseY, "31");
	    tooltip(matrixStack, 130, 86, mouseX, mouseY, "32");
	    tooltip(matrixStack, 150, 86, mouseX, mouseY, "33");

	    tooltip(matrixStack, 44, 106, mouseX, mouseY, "34");
	    tooltip(matrixStack, 64, 106, mouseX, mouseY, "35");
	    tooltip(matrixStack, 84, 106, mouseX, mouseY, "36");
	    tooltip(matrixStack, 104, 106, mouseX, mouseY, "37");
	    tooltip(matrixStack, 124, 106, mouseX, mouseY, "38");*/
    }
    
    private void tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, String number)
    {
    	if(this.isHovering(x, y, 16, 16, (double) mouseX, (double) mouseY))
	    	renderTooltip(matrixStack, Component.literal(number), mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
    	
    }
    
    
}
