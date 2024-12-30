package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class DHDBigButton extends DHDButton
{
	public ResourceLocation widgetsLocation;
	public AbstractDHDMenu menu;

	private static final int RADIUS = 27;
	private static final int DIAMETER = RADIUS * 2;
	private static final int RADIUS_2 = RADIUS * RADIUS;
	
    public DHDBigButton(int x, int y, AbstractDHDMenu menu, OnPress press, ResourceLocation widgets)
	{
		super(x, y, DIAMETER, DIAMETER, Component.empty(), press);
		
		this.menu = menu;
		
		widgetsLocation = widgets;
	}
    
    protected int getYImage(boolean isHovering)
    {
    	if(isHovering)
    	{
    		if(this.menu.isCenterButtonEngaged())
    			return 3;
    		else
    			return 1;
    	}
    	
    	if(this.menu.isCenterButtonEngaged())
			return 2;
		else
			return 0;
    }
    
	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		if(this.visible)
		{
			this.isHovered = ((Math.pow(mouseX - (this.getX() + RADIUS), 2) + Math.pow(mouseY - (this.getY() + RADIUS), 2)) <= RADIUS_2);
			
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, widgetsLocation);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			int yOffset = this.getYImage(this.isHoveredOrFocused());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			graphics.blit(widgetsLocation, this.getX(), this.getY(), 0, yOffset * DIAMETER, this.width, this.height);
			int j = getFGColor();
			graphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
		}
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.active && this.visible &&
				((Math.pow(mouseX - (this.getX() + RADIUS), 2) + Math.pow(mouseY - (this.getY() + RADIUS), 2)) <= RADIUS_2);
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		return this.active && this.visible &&
				((Math.pow(mouseX - (this.getX() + RADIUS), 2) + Math.pow(mouseY - (this.getY() + RADIUS), 2)) <= RADIUS_2);
	}
	
	public static final class MilkyWay extends DHDBigButton
	{
		public MilkyWay(int x, int y, AbstractDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/milky_way/milky_way_dhd_big_red_button.png"));
		}
	}
	
	public static final class Pegasus extends DHDBigButton
	{
		public Pegasus(int x, int y, AbstractDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_big_blue_button.png"));
		}
	}
	
	public static final class Classic extends DHDBigButton
	{
		public Classic(int x, int y, AbstractDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_big_red_button.png"));
		}
	}
}
