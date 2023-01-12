package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.AbstractDHDEntity;
import net.povstalec.sgjourney.client.ClassicDHDButton;
import net.povstalec.sgjourney.client.DHDBigButton;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.network.ServerboundDHDUpdatePacket;

public class ClassicDHDScreen extends Screen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd_background.png");
	
	private AbstractDHDEntity dhd;
	protected int imageWidth = 176;
	protected int imageHeight = 176;
	
	public ClassicDHDScreen(AbstractDHDEntity dhd,Component pTitle)
	{
		super(pTitle);
		this.dhd = dhd;
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton(x + 72, y + 48, (n) -> {engageChevron(0);}, new ResourceLocation(StargateJourney.MODID, "textures/gui/milky_way_dhd_widgets.png")));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 44, y + 6, this, 1));
		this.addRenderableWidget(new ClassicDHDButton(x + 64, y + 6, this, 2));
		this.addRenderableWidget(new ClassicDHDButton(x + 84, y + 6, this, 3));
		this.addRenderableWidget(new ClassicDHDButton(x + 104, y + 6, this, 4));
		this.addRenderableWidget(new ClassicDHDButton(x + 124, y + 6, this, 5));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 10, y + 26, this, 6));
		this.addRenderableWidget(new ClassicDHDButton(x + 30, y + 26, this, 7));
		this.addRenderableWidget(new ClassicDHDButton(x + 50, y + 26, this, 8));
		this.addRenderableWidget(new ClassicDHDButton(x + 70, y + 26, this, 9));
		this.addRenderableWidget(new ClassicDHDButton(x + 90, y + 26, this, 10));
		this.addRenderableWidget(new ClassicDHDButton(x + 110, y + 26, this, 11));
		this.addRenderableWidget(new ClassicDHDButton(x + 130, y + 26, this, 12));
		this.addRenderableWidget(new ClassicDHDButton(x + 150, y + 26, this, 13));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 10, y + 46, this, 14));
		this.addRenderableWidget(new ClassicDHDButton(x + 30, y + 46, this, 15));
		this.addRenderableWidget(new ClassicDHDButton(x + 50, y + 46, this, 16));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 110, y + 46, this, 17));
		this.addRenderableWidget(new ClassicDHDButton(x + 130, y + 46, this, 18));
		this.addRenderableWidget(new ClassicDHDButton(x + 150, y + 46, this, 19));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 10, y + 66, this, 20));
		this.addRenderableWidget(new ClassicDHDButton(x + 30, y + 66, this, 21));
		this.addRenderableWidget(new ClassicDHDButton(x + 50, y + 66, this, 22));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 110, y + 66, this, 23));
		this.addRenderableWidget(new ClassicDHDButton(x + 130, y + 66, this, 24));
		this.addRenderableWidget(new ClassicDHDButton(x + 150, y + 66, this, 25));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 10, y + 86, this, 26));
		this.addRenderableWidget(new ClassicDHDButton(x + 30, y + 86, this, 27));
		this.addRenderableWidget(new ClassicDHDButton(x + 50, y + 86, this, 28));
		this.addRenderableWidget(new ClassicDHDButton(x + 70, y + 86, this, 29));
		this.addRenderableWidget(new ClassicDHDButton(x + 90, y + 86, this, 30));
		this.addRenderableWidget(new ClassicDHDButton(x + 110, y + 86, this, 31));
		this.addRenderableWidget(new ClassicDHDButton(x + 130, y + 86, this, 32));
		this.addRenderableWidget(new ClassicDHDButton(x + 150, y + 86, this, 33));
		
		this.addRenderableWidget(new ClassicDHDButton(x + 44, y + 106, this, 34));
		this.addRenderableWidget(new ClassicDHDButton(x + 64, y + 106, this, 35));
		this.addRenderableWidget(new ClassicDHDButton(x + 84, y + 106, this, 36));
		this.addRenderableWidget(new ClassicDHDButton(x + 104, y + 106, this, 37));
		this.addRenderableWidget(new ClassicDHDButton(x + 124, y + 106, this, 38));
	}
	
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight + 1);
    }
    
    private void tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, String number)
    {
    	if(mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16)
	    {
	    	renderTooltip(matrixStack, Component.literal(number), mouseX, mouseY);
	    }
    }
    
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
	    
	    tooltip(matrixStack, x + 44, y + 6, mouseX, mouseY, "1");
	    tooltip(matrixStack, x + 64, y + 6, mouseX, mouseY, "2");
	    tooltip(matrixStack, x + 84, y + 6, mouseX, mouseY, "3");
	    tooltip(matrixStack, x + 104, y + 6, mouseX, mouseY, "4");
	    tooltip(matrixStack, x + 124, y + 6, mouseX, mouseY, "5");

	    tooltip(matrixStack, x + 10, y + 26, mouseX, mouseY, "6");
	    tooltip(matrixStack, x + 30, y + 26, mouseX, mouseY, "7");
	    tooltip(matrixStack, x + 50, y + 26, mouseX, mouseY, "8");
	    tooltip(matrixStack, x + 70, y + 26, mouseX, mouseY, "9");
	    tooltip(matrixStack, x + 90, y + 26, mouseX, mouseY, "10");
	    tooltip(matrixStack, x + 110, y + 26, mouseX, mouseY, "11");
	    tooltip(matrixStack, x + 130, y + 26, mouseX, mouseY, "12");
	    tooltip(matrixStack, x + 150, y + 26, mouseX, mouseY, "13");

	    tooltip(matrixStack, x + 10, y + 46, mouseX, mouseY, "14");
	    tooltip(matrixStack, x + 30, y + 46, mouseX, mouseY, "15");
	    tooltip(matrixStack, x + 50, y + 46, mouseX, mouseY, "16");

	    tooltip(matrixStack, x + 110, y + 46, mouseX, mouseY, "17");
	    tooltip(matrixStack, x + 130, y + 46, mouseX, mouseY, "18");
	    tooltip(matrixStack, x + 150, y + 46, mouseX, mouseY, "19");

	    tooltip(matrixStack, x + 10, y + 66, mouseX, mouseY, "20");
	    tooltip(matrixStack, x + 30, y + 66, mouseX, mouseY, "21");
	    tooltip(matrixStack, x + 50, y + 66, mouseX, mouseY, "22");

	    tooltip(matrixStack, x + 110, y + 66, mouseX, mouseY, "23");
	    tooltip(matrixStack, x + 130, y + 66, mouseX, mouseY, "24");
	    tooltip(matrixStack, x + 150, y + 66, mouseX, mouseY, "25");

	    tooltip(matrixStack, x + 10, y + 86, mouseX, mouseY, "26");
	    tooltip(matrixStack, x + 30, y + 86, mouseX, mouseY, "27");
	    tooltip(matrixStack, x + 50, y + 86, mouseX, mouseY, "28");
	    tooltip(matrixStack, x + 70, y + 86, mouseX, mouseY, "29");
	    tooltip(matrixStack, x + 90, y + 86, mouseX, mouseY, "30");
	    tooltip(matrixStack, x + 110, y + 86, mouseX, mouseY, "31");
	    tooltip(matrixStack, x + 130, y + 86, mouseX, mouseY, "32");
	    tooltip(matrixStack, x + 150, y + 86, mouseX, mouseY, "33");

	    tooltip(matrixStack, x + 44, y + 106, mouseX, mouseY, "34");
	    tooltip(matrixStack, x + 64, y + 106, mouseX, mouseY, "35");
	    tooltip(matrixStack, x + 84, y + 106, mouseX, mouseY, "36");
	    tooltip(matrixStack, x + 104, y + 106, mouseX, mouseY, "37");
	    tooltip(matrixStack, x + 124, y + 106, mouseX, mouseY, "38");
	    
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        renderBackground(stack);
        renderBg(stack, partialTick, mouseX, mouseY);
        this.renderLabels(stack, mouseX, mouseY);
        super.render(stack, mouseX, mouseY, partialTick);
        
    }
    
    @Override
    public boolean isPauseScreen()
    {
    	return false;
    }
    
    public void engageChevron(int symbol)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundDHDUpdatePacket(dhd.getBlockPos(), symbol));
    }
	
}
