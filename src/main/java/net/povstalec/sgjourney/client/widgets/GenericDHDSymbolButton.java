package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.CoordinateHelper.CoordinateSystems;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public abstract class GenericDHDSymbolButton extends DHDSymbolButton
{
	public static final int OUTER_BUTTON_Y_OFFSET_1 = 0;
	public static final int OUTER_BUTTON_Y_OFFSET_2 = 33;
	public static final int OUTER_BUTTON_Y_OFFSET_3 = 65;
	
	public static final int INNER_BUTTON_Y_OFFSET_1 = 98;
	public static final int INNER_BUTTON_Y_OFFSET_2 = 128;
	public static final int INNER_BUTTON_Y_OFFSET_3 = 158;
	
	public enum Position
	{
		INNER(19, 8, 33, 61),
		OUTER(20, 16, 63, 88);
		
		public final int totalButtons;
		public final double angle;
		public final int symbolSize;
		public final int innerRadiusSqr;
		public final int outerRadiusSqr;
		
		Position(int totalButtons, int symbolSize, int innerRadius, int outerRadius)
		{
			this.totalButtons = totalButtons;
			this.angle = 360.0 / this.totalButtons;
			this.symbolSize = symbolSize;
			this.innerRadiusSqr = innerRadius * innerRadius;
			this.outerRadiusSqr = outerRadius * outerRadius;
		}
		
		public double symbolAngle(int symbol)
		{
			if(this == INNER)
				return ((symbol - 1) % totalButtons) * angle + angle / 2;
			
			return (symbol % totalButtons) * angle;
		}
	}
	
	public enum DefaultButton
	{
		// Outer Buttons
		BUTTON_0(0, OUTER_BUTTON_Y_OFFSET_1, 25, 26, Position.OUTER),
		BUTTON_1(BUTTON_0.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 29, 27, Position.OUTER),
		BUTTON_2(BUTTON_1.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 32, 32, Position.OUTER),
		BUTTON_3(BUTTON_2.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 32, 33, Position.OUTER),
		BUTTON_4(BUTTON_3.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 28, 29, Position.OUTER),
		BUTTON_5(BUTTON_4.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 26, 25, Position.OUTER),
		BUTTON_6(BUTTON_5.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 28, 29, Position.OUTER),
		BUTTON_7(BUTTON_6.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 32, 33, Position.OUTER),
		
		BUTTON_8(0, OUTER_BUTTON_Y_OFFSET_2, 32, 32, Position.OUTER),
		BUTTON_9(BUTTON_8.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 29, 27, Position.OUTER),
		BUTTON_10(BUTTON_9.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 25, 26, Position.OUTER),
		BUTTON_11(BUTTON_10.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 29, 27, Position.OUTER),
		BUTTON_12(BUTTON_11.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 32, 32, Position.OUTER),
		BUTTON_13(BUTTON_12.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 32, 33, Position.OUTER),
		BUTTON_14(BUTTON_13.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 28, 29, Position.OUTER),
		BUTTON_15(BUTTON_14.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 26, 25, Position.OUTER),
		
		BUTTON_16(0, OUTER_BUTTON_Y_OFFSET_3, 28, 29, Position.OUTER),
		BUTTON_17(BUTTON_16.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 32, 33, Position.OUTER),
		BUTTON_18(BUTTON_17.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 32, 32, Position.OUTER),
		BUTTON_19(BUTTON_18.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 29, 27, Position.OUTER),
		// Inner Buttons
		BUTTON_20(0, INNER_BUTTON_Y_OFFSET_1, 29, 18, Position.INNER),
		BUTTON_21(BUTTON_20.xEnd(), INNER_BUTTON_Y_OFFSET_1, 30, 25, Position.INNER),
		BUTTON_22(BUTTON_21.xEnd(), INNER_BUTTON_Y_OFFSET_1, 28, 29, Position.INNER),
		BUTTON_23(BUTTON_22.xEnd(), INNER_BUTTON_Y_OFFSET_1, 22, 30, Position.INNER),
		BUTTON_24(BUTTON_23.xEnd(), INNER_BUTTON_Y_OFFSET_1, 18, 29, Position.INNER),
		BUTTON_25(BUTTON_24.xEnd(), INNER_BUTTON_Y_OFFSET_1, 20, 30, Position.INNER),
		BUTTON_26(BUTTON_25.xEnd(), INNER_BUTTON_Y_OFFSET_1, 27, 30, Position.INNER),
		BUTTON_27(BUTTON_26.xEnd(), INNER_BUTTON_Y_OFFSET_1, 30, 27, Position.INNER),
		BUTTON_28(BUTTON_27.xEnd(), INNER_BUTTON_Y_OFFSET_1, 30, 22, Position.INNER),
		
		BUTTON_29(0, INNER_BUTTON_Y_OFFSET_2, 28, 18, Position.INNER),
		BUTTON_30(BUTTON_29.xEnd(), INNER_BUTTON_Y_OFFSET_2, 30, 22, Position.INNER),
		BUTTON_31(BUTTON_30.xEnd(), INNER_BUTTON_Y_OFFSET_2, 30, 27, Position.INNER),
		BUTTON_32(BUTTON_31.xEnd(), INNER_BUTTON_Y_OFFSET_2, 27, 30, Position.INNER),
		BUTTON_33(BUTTON_32.xEnd(), INNER_BUTTON_Y_OFFSET_2, 20, 30, Position.INNER),
		BUTTON_34(BUTTON_33.xEnd(), INNER_BUTTON_Y_OFFSET_2, 18, 29, Position.INNER),
		BUTTON_35(BUTTON_34.xEnd(), INNER_BUTTON_Y_OFFSET_2, 22, 30, Position.INNER),
		BUTTON_36(BUTTON_35.xEnd(), INNER_BUTTON_Y_OFFSET_2, 28, 29, Position.INNER),
		BUTTON_37(BUTTON_36.xEnd(), INNER_BUTTON_Y_OFFSET_2, 30, 25, Position.INNER),
		
		BUTTON_38(0, INNER_BUTTON_Y_OFFSET_3, 29, 18, Position.INNER);
		
		public final int textureX;
		public final int textureY;
		public final int width;
		public final int height;
		public final Position position;
		
		DefaultButton(int textureX, int textureY, int width, int height, Position position)
		{
			this.textureX = textureX;
			this.textureY = textureY;
			this.width = width;
			this.height = height;
			this.position = position;
		}
		
		public int xEnd()
		{
			return textureX + width;
		}
	}
	
	protected final int screenCenterX;
	protected final int screenCenterY;
	
	protected final int xCenter;
	protected final int yCenter;
	protected final double buttonAngle;
	
	public final Position position;

	public GenericDHDSymbolButton(int x, int y, int width, int height, AbstractDHDMenu<?> menu, int symbol, int screenWidth, int screenHeight,
								  ResourceLocation widgets, ResourceLocation overlay, int xCenter, int yCenter, int textureX, int textureY, Position position,
								  ColorUtil.RGBA hoverColor, ColorUtil.RGBA disengagedColor, ColorUtil.RGBA engagedColor)
	{
		super(x, y, width, height, menu, symbol, widgets, overlay, hoverColor, disengagedColor, engagedColor);
		
		this.screenCenterX = screenWidth / 2;
		this.screenCenterY = screenHeight / 2;
		
    	this.xCenter = xCenter;
    	this.yCenter = yCenter;
    	this.buttonAngle = position.symbolAngle(this.symbol);
    	
    	this.textureX = textureX;
    	this.textureY = textureY;
		
		this.position = position;
		
		setTooltip(Tooltip.create(Component.literal(Integer.toString(getSymbol()))));
	}
	
	private static boolean isAngleInBounds(double lowerAngleBound, double angle, double upperAngleBound)
	{
		if(lowerAngleBound < 0)
			lowerAngleBound += 360;
		else
			lowerAngleBound %= 360;
		angle %= 360;
		upperAngleBound %= 360;
		
		return lowerAngleBound > upperAngleBound ?
				angle >= lowerAngleBound || angle <= upperAngleBound :
				angle > lowerAngleBound && angle < upperAngleBound;
	}
	
	public boolean isOverButton(double mouseX, double mouseY)
	{
		if((mouseX - screenCenterX) == 0 && (mouseY - screenCenterY) == 0)
			return false;
		
		float phi = CoordinateSystems.cartesianToPolarPhi((float) (mouseX - screenCenterX), (float) (mouseY - screenCenterY)) + 180;
		
		double xDist = mouseX - screenCenterX;
		double yDist = mouseY - screenCenterY;
		double distFromCenterSqr = xDist * xDist + yDist * yDist;
		
		return isAngleInBounds(this.buttonAngle - this.position.angle / 2, phi, this.buttonAngle + this.position.angle / 2) &&
				distFromCenterSqr > this.position.innerRadiusSqr && distFromCenterSqr < this.position.outerRadiusSqr;
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
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		super.render(poseStack, mouseX, mouseY, partialTick);
		if(this.visible)
		{
			this.isHovered = isOverButton(mouseX, mouseY);
			this.renderButton(poseStack, mouseX, mouseY, partialTick);
			updateTooltip();
		}
	}
	
	@Override
	public void renderSymbol(PoseStack poseStack)
	{
		if(getSymbol() == 0)
		{
			PointOfOrigin pointOfOrigin = ClientUtil.getPointOfOrigin(this.menu.blockEntity.symbolInfo().pointOfOrigin());
			if(pointOfOrigin != null)
			{
				
				if(isEngaged())
					renderPointOfOrigin(poseStack.last().pose(), this.getX() + this.xCenter, this.getY() + this.yCenter,
							this.position.symbolSize, this.position.symbolSize, pointOfOrigin, this.engagedColor);
				else
					renderPointOfOrigin(poseStack.last().pose(), this.getX() + this.xCenter, this.getY() + this.yCenter,
							this.position.symbolSize, this.position.symbolSize, pointOfOrigin, this.disengagedColor);
			}
		}
		else
		{
			Symbols symbols = ClientUtil.getSymbols(this.menu.blockEntity.symbolInfo().symbols());
			if(symbols != null && getSymbol() <= symbols.getSize())
			{
				
				if(isEngaged())
					renderSymbol(poseStack.last().pose(), this.getX() + this.xCenter, this.getY() + this.yCenter,
							this.position.symbolSize, this.position.symbolSize, symbols, getSymbol(), this.engagedColor);
				else
					renderSymbol(poseStack.last().pose(), this.getX() + this.xCenter, this.getY() + this.yCenter,
							this.position.symbolSize, this.position.symbolSize, symbols, getSymbol(), this.disengagedColor);
			}
		}
	}
}
