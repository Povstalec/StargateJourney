package net.povstalec.sgjourney.client.widgets.dhd;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import org.jetbrains.annotations.NotNull;

public abstract class DHDBigButton<M extends IDHDMenu> extends DHDButton
{
	public ResourceLocation widgetsLocation;
	public M menu;

	private static final int RADIUS = 27;
	private static final int DIAMETER = RADIUS * 2;
	private static final int RADIUS_2 = RADIUS * RADIUS;
	
	protected boolean isEngaged = false;
	
    public DHDBigButton(int x, int y, M menu, OnPress press, ResourceLocation widgets)
	{
		super(x, y, DIAMETER, DIAMETER, Component.empty(), press);
		
		this.menu = menu;
		
		widgetsLocation = widgets;
		
		setTooltip(Tooltip.create(Component.translatable("tooltip.sgjourney.engage_stargate")));
	}
	
	protected void updateEngaged()
	{
		if(this.menu.isCenterButtonEngaged() != isEngaged)
		{
			isEngaged = this.menu.isCenterButtonEngaged();
			
			if(isEngaged)
				setTooltip(Tooltip.create(Component.translatable("tooltip.sgjourney.disconnect_stargate")));
			else
				setTooltip(Tooltip.create(Component.translatable("tooltip.sgjourney.engage_stargate")));
		}
	}
    
    @Override
    protected int getYImage(boolean isHovering)
    {
    	if(isHovering)
    	{
    		if(isEngaged)
    			return 3;
    		else
    			return 1;
    	}
    	
    	if(isEngaged)
			return 2;
		else
			return 0;
    }
	
	public boolean isOverButton(double mouseX, double mouseY)
	{
		return (Math.pow(mouseX - (this.getX() + RADIUS), 2) + Math.pow(mouseY - (this.getY() + RADIUS), 2)) <= RADIUS_2;
	}
    
	@Override
	public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, widgetsLocation);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int yOffset = this.getYImage(this.isHoveredOrFocused());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(poseStack, this.getX(), this.getY(), 0, yOffset * DIAMETER, this.width, this.height);
		this.renderBg(poseStack, minecraft, mouseX, mouseY);
		int j = getFGColor();
		drawCenteredString(poseStack, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
	
	@Override
	public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		if(this.visible)
		{
			updateEngaged();
			this.isHovered = isOverButton(mouseX, mouseY);
			
			this.renderButton(poseStack, mouseX, mouseY, partialTick);
			this.updateTooltip();
		}
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.active && this.visible && isOverButton(mouseX, mouseY);
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		return this.active && this.visible && isOverButton(mouseX, mouseY);
	}
}
