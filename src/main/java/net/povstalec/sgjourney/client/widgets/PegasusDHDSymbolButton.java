package net.povstalec.sgjourney.client.widgets;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class PegasusDHDSymbolButton extends GenericDHDSymbolButton
{
	public static final ResourceLocation PEGASUS_OUTER_BUTTONS_1 = StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_outer_buttons_1.png");
	public static final ResourceLocation PEGASUS_OUTER_BUTTONS_2 = StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_outer_buttons_2.png");
	public static final ResourceLocation PEGASUS_INNER_BUTTONS = StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_inner_buttons.png");
	
	public enum PegasusButton
    {
    	BUTTON_1(1, 34, BUTTON_1_OFFSET, 0, BUTTON_1_WIDTH, 27, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_2(2, 13, BUTTON_2_OFFSET, 0, BUTTON_2_WIDTH, 31, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_3(3, 1, BUTTON_3_OFFSET, 0, BUTTON_3_WIDTH, 34, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_4(4, 16, BUTTON_4_OFFSET, 0, BUTTON_4_WIDTH, 31, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_5(5, 19, BUTTON_5_OFFSET, 0, BUTTON_5_WIDTH, 26, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_6(6, 5, BUTTON_6_OFFSET, 0, BUTTON_6_WIDTH, 29, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_7(7, 11, BUTTON_7_OFFSET, 0, BUTTON_7_WIDTH, 33, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_8(8, 18, BUTTON_8_OFFSET, 0, BUTTON_8_WIDTH, 33, PEGASUS_OUTER_BUTTONS_1),
    	BUTTON_9(9, 17, BUTTON_9_OFFSET, 93, BUTTON_9_WIDTH, 28, PEGASUS_OUTER_BUTTONS_1),
    	//--------
    	BUTTON_10(10, 10, BUTTON_10_OFFSET, 93, BUTTON_10_WIDTH, 28, PEGASUS_OUTER_BUTTONS_1),
    	//--------
    	BUTTON_11(11, 31, BUTTON_11_OFFSET, 0, BUTTON_11_WIDTH, 28, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_12(12, 6, BUTTON_12_OFFSET, 0, BUTTON_12_WIDTH, 33, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_13(13, 20, BUTTON_13_OFFSET, 0, BUTTON_13_WIDTH, 33, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_14(14, 33, BUTTON_14_OFFSET, 0, BUTTON_14_WIDTH, 29, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_15(15, 2, BUTTON_15_OFFSET, 0, BUTTON_15_WIDTH, 26, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_16(16, 38, BUTTON_16_OFFSET, 0, BUTTON_16_WIDTH, 31, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_17(17, 21, BUTTON_17_OFFSET, 0, BUTTON_17_WIDTH, 34, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_18(18, 12, BUTTON_18_OFFSET, 0, BUTTON_18_WIDTH, 31, PEGASUS_OUTER_BUTTONS_2),
    	BUTTON_19(19, 8, BUTTON_19_OFFSET, 84, BUTTON_19_WIDTH, 27, PEGASUS_OUTER_BUTTONS_2),
    	
    	
    	BUTTON_20(20, 14, BUTTON_20_OFFSET, 0, BUTTON_20_WIDTH, 18, PEGASUS_INNER_BUTTONS),
    	BUTTON_21(21, 29, BUTTON_21_OFFSET, 0, BUTTON_21_WIDTH, 25, PEGASUS_INNER_BUTTONS),
    	BUTTON_22(22, 36, BUTTON_22_OFFSET, 0, BUTTON_22_WIDTH, 29, PEGASUS_INNER_BUTTONS),
    	BUTTON_23(23, 4, BUTTON_23_OFFSET, 0, BUTTON_23_WIDTH, 30, PEGASUS_INNER_BUTTONS),
    	BUTTON_24(24, 28, BUTTON_24_OFFSET, 0, BUTTON_24_WIDTH, 29, PEGASUS_INNER_BUTTONS),
    	BUTTON_25(25, 35, BUTTON_25_OFFSET, 0, BUTTON_25_WIDTH, 30, PEGASUS_INNER_BUTTONS),
    	BUTTON_26(26, 7, BUTTON_26_OFFSET, 0, BUTTON_26_WIDTH, 30, PEGASUS_INNER_BUTTONS),
    	BUTTON_27(27, 32, BUTTON_27_OFFSET, 0, BUTTON_27_WIDTH, 27, PEGASUS_INNER_BUTTONS),
    	BUTTON_28(28, 24, BUTTON_28_OFFSET, 0, BUTTON_28_WIDTH, 22, PEGASUS_INNER_BUTTONS),
    	
    	BUTTON_29(29, 22, BUTTON_29_OFFSET, 90, BUTTON_29_WIDTH, 18, PEGASUS_INNER_BUTTONS),
    	
    	BUTTON_30(30, 27, BUTTON_30_OFFSET, 90, BUTTON_30_WIDTH, 22, PEGASUS_INNER_BUTTONS),
    	BUTTON_31(31, 26, BUTTON_31_OFFSET, 90, BUTTON_31_WIDTH, 27, PEGASUS_INNER_BUTTONS),
    	BUTTON_32(32, 23, BUTTON_32_OFFSET, 90, BUTTON_32_WIDTH, 30, PEGASUS_INNER_BUTTONS),
    	BUTTON_33(33, 25, BUTTON_33_OFFSET, 90, BUTTON_33_WIDTH, 30, PEGASUS_INNER_BUTTONS),
    	BUTTON_34(34, 15, BUTTON_34_OFFSET, 90, BUTTON_34_WIDTH, 29, PEGASUS_INNER_BUTTONS),
    	BUTTON_35(35, 37, BUTTON_35_OFFSET, 90, BUTTON_35_WIDTH, 30, PEGASUS_INNER_BUTTONS),
    	BUTTON_36(36, 30, BUTTON_36_OFFSET, 90, BUTTON_36_WIDTH, 29, PEGASUS_INNER_BUTTONS),
    	BUTTON_37(37, 3, BUTTON_37_OFFSET, 90, BUTTON_37_WIDTH, 25, PEGASUS_INNER_BUTTONS),
    	BUTTON_38(38, 9, BUTTON_38_OFFSET, 180, BUTTON_38_WIDTH, 18, PEGASUS_INNER_BUTTONS);
		
    	private int actualSymbol;
    	private int canonSymbol;
    	private int xTextureOffset;
    	private int yTextureOffset;
    	private int xSize;
    	private int ySize;
    	private ResourceLocation widgets;
    	
    	PegasusButton(int actualSymbol, int canonSymbol,
    			int widgetX, int widgetY,
    			int xSize, int ySize, ResourceLocation widgets)
    	{
    		this.actualSymbol = actualSymbol;
    		this.canonSymbol = canonSymbol;
    		this.xTextureOffset = widgetX;
    		this.yTextureOffset = widgetY;
    		this.xSize = xSize;
    		this.ySize = ySize;
    		this.widgets = widgets;
    	}
    	
    	public int getSymbol()
    	{
    		if(ClientDHDConfig.pegasus_dhd_canon_symbol_positions.get())
    			return this.canonSymbol;
    		
    		return this.actualSymbol;
    	}
    	
    	public int getTextureOffsetX()
    	{
    		return this.xTextureOffset;
    	}
    	
    	public int getTextureOffsetY()
    	{
    		return this.yTextureOffset;
    	}
    	
    	public int getXSize()
    	{
    		return this.xSize;
    	}
    	
    	public int getYSize()
    	{
    		return this.ySize;
    	}
    	
    	public ResourceLocation getWidgets()
    	{
    		return this.widgets;
    	}
    	
    	public double getAngle()
    	{
    		return ((this.actualSymbol - 1) % 19) * ANGLE;
    	}
    	
    	public boolean isOuter()
    	{
    		return this.actualSymbol < 20;
    	}
    }
	
	public PegasusDHDSymbolButton(int widgetX, int widgetY, int xSize, int ySize, AbstractDHDMenu menu, PegasusButton button)
    {
    	super(widgetX, widgetY, button.getXSize(), button.getYSize(), menu, button.getSymbol(), button.getWidgets(),
    			xSize / 2, ySize / 2, button.getAngle(),
    			button.isOuter() ? OUTER_SYMBOL_START_RADIUS_2 : INNER_SYMBOL_START_RADIUS_2,
    			button.isOuter() ? OUTER_SYMBOL_END_RADIUS_2 : INNER_SYMBOL_END_RADIUS_2,
    			button.getTextureOffsetX(), button.getTextureOffsetY());
    }
}
