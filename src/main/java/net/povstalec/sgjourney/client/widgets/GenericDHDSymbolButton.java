package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;
import net.povstalec.sgjourney.common.misc.CoordinateHelper.CoordinateSystems;

public abstract class GenericDHDSymbolButton extends DHDSymbolButton
{
	public static final int BUTTON_1_OFFSET = 0;
	public static final int BUTTON_1_WIDTH = 28;

	public static final int BUTTON_2_OFFSET = BUTTON_1_OFFSET + BUTTON_1_WIDTH;
	public static final int BUTTON_2_WIDTH = 31;

	public static final int BUTTON_3_OFFSET = BUTTON_2_OFFSET + BUTTON_2_WIDTH;
	public static final int BUTTON_3_WIDTH = 34;

	public static final int BUTTON_4_OFFSET = BUTTON_3_OFFSET + BUTTON_3_WIDTH;
	public static final int BUTTON_4_WIDTH = 29;

	public static final int BUTTON_5_OFFSET = BUTTON_4_OFFSET + BUTTON_4_WIDTH;
	public static final int BUTTON_5_WIDTH = 28;

	public static final int BUTTON_6_OFFSET = BUTTON_5_OFFSET + BUTTON_5_WIDTH;
	public static final int BUTTON_6_WIDTH = 28;

	public static final int BUTTON_7_OFFSET = BUTTON_6_OFFSET + BUTTON_6_WIDTH;
	public static final int BUTTON_7_WIDTH = 33;

	public static final int BUTTON_8_OFFSET = BUTTON_7_OFFSET + BUTTON_7_WIDTH;
	public static final int BUTTON_8_WIDTH = 33;

	public static final int BUTTON_9_OFFSET = 0;
	public static final int BUTTON_9_WIDTH = 30;
	//--------
	public static final int BUTTON_10_OFFSET = BUTTON_9_OFFSET + BUTTON_9_WIDTH;
	public static final int BUTTON_10_WIDTH = 26;
	//--------
	public static final int BUTTON_11_OFFSET = 0;
	public static final int BUTTON_11_WIDTH = 30;

	public static final int BUTTON_12_OFFSET = BUTTON_11_OFFSET + BUTTON_11_WIDTH;
	public static final int BUTTON_12_WIDTH = 33;

	public static final int BUTTON_13_OFFSET = BUTTON_12_OFFSET + BUTTON_12_WIDTH;
	public static final int BUTTON_13_WIDTH = 33;

	public static final int BUTTON_14_OFFSET = BUTTON_13_OFFSET + BUTTON_13_WIDTH;
	public static final int BUTTON_14_WIDTH = 28;

	public static final int BUTTON_15_OFFSET = BUTTON_14_OFFSET + BUTTON_14_WIDTH;
	public static final int BUTTON_15_WIDTH = 28;

	public static final int BUTTON_16_OFFSET = BUTTON_15_OFFSET + BUTTON_15_WIDTH;
	public static final int BUTTON_16_WIDTH = 29;

	public static final int BUTTON_17_OFFSET = BUTTON_16_OFFSET + BUTTON_16_WIDTH;
	public static final int BUTTON_17_WIDTH = 34;

	public static final int BUTTON_18_OFFSET = BUTTON_17_OFFSET + BUTTON_17_WIDTH;
	public static final int BUTTON_18_WIDTH = 31;

	public static final int BUTTON_19_OFFSET = 0;
	public static final int BUTTON_19_WIDTH = 28;

	
	public static final int BUTTON_20_OFFSET = 0;
	public static final int BUTTON_20_WIDTH = 29;
	
	public static final int BUTTON_21_OFFSET = BUTTON_20_OFFSET + BUTTON_20_WIDTH;
	public static final int BUTTON_21_WIDTH = 30;
	
	public static final int BUTTON_22_OFFSET = BUTTON_21_OFFSET + BUTTON_21_WIDTH;
	public static final int BUTTON_22_WIDTH = 28;
	
	public static final int BUTTON_23_OFFSET = BUTTON_22_OFFSET + BUTTON_22_WIDTH;
	public static final int BUTTON_23_WIDTH = 22;
	
	public static final int BUTTON_24_OFFSET = BUTTON_23_OFFSET + BUTTON_23_WIDTH;
	public static final int BUTTON_24_WIDTH = 18;
	
	public static final int BUTTON_25_OFFSET = BUTTON_24_OFFSET + BUTTON_24_WIDTH;
	public static final int BUTTON_25_WIDTH = 20;
	
	public static final int BUTTON_26_OFFSET = BUTTON_25_OFFSET + BUTTON_25_WIDTH;
	public static final int BUTTON_26_WIDTH = 27;
	
	public static final int BUTTON_27_OFFSET = BUTTON_26_OFFSET + BUTTON_26_WIDTH;
	public static final int BUTTON_27_WIDTH = 30;
	
	public static final int BUTTON_28_OFFSET = BUTTON_27_OFFSET + BUTTON_27_WIDTH;
	public static final int BUTTON_28_WIDTH = 30;
	//--------
	public static final int BUTTON_29_OFFSET = 0;
	public static final int BUTTON_29_WIDTH = 28;
	//--------
	public static final int BUTTON_30_OFFSET = BUTTON_29_OFFSET + BUTTON_29_WIDTH;
	public static final int BUTTON_30_WIDTH = 30;
	
	public static final int BUTTON_31_OFFSET = BUTTON_30_OFFSET + BUTTON_30_WIDTH;
	public static final int BUTTON_31_WIDTH = 30;
	
	public static final int BUTTON_32_OFFSET = BUTTON_31_OFFSET + BUTTON_31_WIDTH;
	public static final int BUTTON_32_WIDTH = 27;
	
	public static final int BUTTON_33_OFFSET = BUTTON_32_OFFSET + BUTTON_32_WIDTH;
	public static final int BUTTON_33_WIDTH = 20;
	
	public static final int BUTTON_34_OFFSET = BUTTON_33_OFFSET + BUTTON_33_WIDTH;
	public static final int BUTTON_34_WIDTH = 18;
	
	public static final int BUTTON_35_OFFSET = BUTTON_34_OFFSET + BUTTON_34_WIDTH;
	public static final int BUTTON_35_WIDTH = 22;
	
	public static final int BUTTON_36_OFFSET = BUTTON_35_OFFSET + BUTTON_35_WIDTH;
	public static final int BUTTON_36_WIDTH = 28;
	
	public static final int BUTTON_37_OFFSET = BUTTON_36_OFFSET + BUTTON_36_WIDTH;
	public static final int BUTTON_37_WIDTH = 30;
	
	public static final int BUTTON_38_OFFSET = 0;
	public static final int BUTTON_38_WIDTH = 29;
	
	public static final int INNER_SYMBOL_START_RADIUS = 33;
	public static final int INNER_SYMBOL_START_RADIUS_2 = INNER_SYMBOL_START_RADIUS * INNER_SYMBOL_START_RADIUS;
	
	public static final int INNER_SYMBOL_END_RADIUS = 61;
	public static final int INNER_SYMBOL_END_RADIUS_2 = INNER_SYMBOL_END_RADIUS * INNER_SYMBOL_END_RADIUS;
	
	public static final int OUTER_SYMBOL_START_RADIUS = 63;
	public static final int OUTER_SYMBOL_START_RADIUS_2 = OUTER_SYMBOL_START_RADIUS * OUTER_SYMBOL_START_RADIUS;
	
	public static final int OUTER_SYMBOL_END_RADIUS = 88;
	public static final int OUTER_SYMBOL_END_RADIUS_2 = OUTER_SYMBOL_END_RADIUS * OUTER_SYMBOL_END_RADIUS;
	
	public static final double ANGLE = 360.0 / 19;
	
	private final int xCenter;
	private final int yCenter;
	private final double angle;
	private final int innerRadius2;
	private final int outerRadius2;

	public GenericDHDSymbolButton(int x, int y, int xSize, int ySize, AbstractDHDMenu menu, int symbol,
			ResourceLocation widgets, int xCenter, int yCenter, double angle, int innerRadius2, int outerRadius2,
			int widgetTextureOffsetX, int widgetTextureOffsetY)
	{
		super(x, y, xSize, ySize, menu, symbol, widgets);
		
    	this.xCenter = xCenter;
    	this.yCenter = yCenter;
    	this.angle = angle;
    	this.innerRadius2 = innerRadius2;
    	this.outerRadius2 = outerRadius2;
    	
    	this.widgetTextureOffsetX = widgetTextureOffsetX;
    	this.widgetTextureOffsetY = widgetTextureOffsetY;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		float phi = CoordinateSystems.cartesianToPolarPhi((float) (mouseX - xCenter), (float) (mouseY - yCenter)) + 180;
		
		return this.active && this.visible &&
				phi > this.angle && phi < this.angle + ANGLE &&
				((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY - yCenter, 2)) > this.innerRadius2) &&
				((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY - yCenter, 2)) < this.outerRadius2);
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		float phi = CoordinateSystems.cartesianToPolarPhi((float) (mouseX - xCenter), (float) (mouseY - yCenter)) + 180;
		
		return this.active && this.visible &&
				phi > this.angle && phi < this.angle + ANGLE &&
				((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY - yCenter, 2)) > this.innerRadius2) &&
				((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY - yCenter, 2)) < this.outerRadius2);
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		if(this.visible)
		{
			if((mouseY - yCenter) != 0)
			{
				float phi = CoordinateSystems.cartesianToPolarPhi((mouseX - xCenter), (mouseY - yCenter)) + 180;
				
				this.isHovered = phi > this.angle && phi < this.angle + ANGLE &&
						((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY - yCenter, 2)) > this.innerRadius2) &&
						((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY - yCenter, 2)) < this.outerRadius2);
			}
			else
				this.isHovered = false;	
			
			this.renderButton(poseStack, mouseX, mouseY, partialTick);
			//this.updateTooltip();
		}
	}
}
