package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;
import net.povstalec.sgjourney.common.misc.CoordinateHelper.CoordinateSystems;

public class DHDSymbolButton extends DHDButton
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
	
	public static final ResourceLocation PLACEHOLDER_WIDGETS = new ResourceLocation(StargateJourney.MODID, "textures/gui/milky_way_dhd_widgets.png");
	
	public static final ResourceLocation MILKY_WAY_OUTER_BUTTONS_1 = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way_dhd_outer_buttons_1.png");
	public static final ResourceLocation MILKY_WAY_OUTER_BUTTONS_2 = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way_dhd_outer_buttons_2.png");
	public static final ResourceLocation MILKY_WAY_INNER_BUTTONS = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way_dhd_inner_buttons.png");
	
	protected AbstractDHDMenu menu;
	protected ResourceLocation widgets;
	
	protected int symbol;
	protected int widgetTextureOffsetX = 0;
	protected int widgetTextureOffsetY = 0;
	
    public DHDSymbolButton(int x, int y, int xSize, int ySize, AbstractDHDMenu menu, int symbol, ResourceLocation widgets)
	{
		super(x, y, xSize, ySize, symbol(menu.symbolsType, symbol), (button) -> {menu.engageChevron(symbol);});
		
		this.menu = menu;
		this.widgets = widgets;
		
		this.symbol = symbol;
	}
	
    public DHDSymbolButton(int x, int y, AbstractDHDMenu menu, int symbol)
	{
		this(x, y, 16, 16, menu, symbol, PLACEHOLDER_WIDGETS);
	}
    
    @Override
    protected int getYImage(boolean isHovering)
    {
    	if(isHovering)
    		return 1;
    	
    	if(this.menu.isSymbolEngaged(this.symbol))
    		return 2;
    	
    	return 0;
    }
	
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, widgets);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.getX(), this.getY(), widgetTextureOffsetX, widgetTextureOffsetY + i * this.getHeight(), this.width, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(poseStack, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
	
    private static Component symbol(String symbolsType, int i)
    {
    	MutableComponent symbols = Component.literal("" + i); //Symbols.unicode(i)
		//Style style = symbols.getStyle().withFont(new ResourceLocation(symbolsType));
		//symbols = symbols.withStyle(style);
		return symbols;
    }
    
    public static class MilkyWay extends DHDSymbolButton
    {
    	public enum MilkyWayButton
        {
        	BUTTON_1(1, 12, BUTTON_1_OFFSET, 0, BUTTON_1_WIDTH, 27, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_2(2, 18, BUTTON_2_OFFSET, 0, BUTTON_2_WIDTH, 31, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_3(3, 21, BUTTON_3_OFFSET, 0, BUTTON_3_WIDTH, 34, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_4(4, 6, BUTTON_4_OFFSET, 0, BUTTON_4_WIDTH, 31, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_5(5, 37, BUTTON_5_OFFSET, 0, BUTTON_5_WIDTH, 26, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_6(6, 5, BUTTON_6_OFFSET, 0, BUTTON_6_WIDTH, 29, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_7(7, 28, BUTTON_7_OFFSET, 0, BUTTON_7_WIDTH, 33, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_8(8, 23, BUTTON_8_OFFSET, 0, BUTTON_8_WIDTH, 33, MILKY_WAY_OUTER_BUTTONS_1),
        	BUTTON_9(9, 33, BUTTON_9_OFFSET, 93, BUTTON_9_WIDTH, 28, MILKY_WAY_OUTER_BUTTONS_1),
        	//--------
        	BUTTON_10(10, 11, BUTTON_10_OFFSET, 93, BUTTON_10_WIDTH, 28, MILKY_WAY_OUTER_BUTTONS_1),
        	//--------
        	BUTTON_11(11, 36, BUTTON_11_OFFSET, 0, BUTTON_11_WIDTH, 28, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_12(12, 10, BUTTON_12_OFFSET, 0, BUTTON_12_WIDTH, 33, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_13(13, 20, BUTTON_13_OFFSET, 0, BUTTON_13_WIDTH, 33, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_14(14, 2, BUTTON_14_OFFSET, 0, BUTTON_14_WIDTH, 29, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_15(15, 3, BUTTON_15_OFFSET, 0, BUTTON_15_WIDTH, 26, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_16(16, 19, BUTTON_16_OFFSET, 0, BUTTON_16_WIDTH, 31, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_17(17, 8, BUTTON_17_OFFSET, 0, BUTTON_17_WIDTH, 34, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_18(18, 4, BUTTON_18_OFFSET, 0, BUTTON_18_WIDTH, 31, MILKY_WAY_OUTER_BUTTONS_2),
        	BUTTON_19(19, 31, BUTTON_19_OFFSET, 84, BUTTON_19_WIDTH, 27, MILKY_WAY_OUTER_BUTTONS_2),
        	
        	
        	BUTTON_20(20, 14, BUTTON_20_OFFSET, 0, BUTTON_20_WIDTH, 18, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_21(21, 34, BUTTON_21_OFFSET, 0, BUTTON_21_WIDTH, 25, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_22(22, 29, BUTTON_22_OFFSET, 0, BUTTON_22_WIDTH, 29, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_23(23, 15, BUTTON_23_OFFSET, 0, BUTTON_23_WIDTH, 30, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_24(24, 27, BUTTON_24_OFFSET, 0, BUTTON_24_WIDTH, 29, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_25(25, 9, BUTTON_25_OFFSET, 0, BUTTON_25_WIDTH, 30, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_26(26, 32, BUTTON_26_OFFSET, 0, BUTTON_26_WIDTH, 30, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_27(27, 38, BUTTON_27_OFFSET, 0, BUTTON_27_WIDTH, 27, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_28(28, 25, BUTTON_28_OFFSET, 0, BUTTON_28_WIDTH, 22, MILKY_WAY_INNER_BUTTONS),
        	
        	BUTTON_29(29, 22, BUTTON_29_OFFSET, 90, BUTTON_29_WIDTH, 18, MILKY_WAY_INNER_BUTTONS),
        	
        	BUTTON_30(30, 17, BUTTON_30_OFFSET, 90, BUTTON_30_WIDTH, 22, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_31(31, 13, BUTTON_31_OFFSET, 90, BUTTON_31_WIDTH, 27, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_32(32, 16, BUTTON_32_OFFSET, 90, BUTTON_32_WIDTH, 30, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_33(33, 1, BUTTON_33_OFFSET, 90, BUTTON_33_WIDTH, 30, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_34(34, 24, BUTTON_34_OFFSET, 90, BUTTON_34_WIDTH, 29, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_35(35, 35, BUTTON_35_OFFSET, 90, BUTTON_35_WIDTH, 30, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_36(36, 7, BUTTON_36_OFFSET, 90, BUTTON_36_WIDTH, 29, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_37(37, 26, BUTTON_37_OFFSET, 90, BUTTON_37_WIDTH, 25, MILKY_WAY_INNER_BUTTONS),
        	BUTTON_38(38, 30, BUTTON_38_OFFSET, 180, BUTTON_38_WIDTH, 18, MILKY_WAY_INNER_BUTTONS);

        	
        	private int actualSymbol;
        	private int canonSymbol;
        	private int xTextureOffset;
        	private int yTextureOffset;
        	private int xSize;
        	private int ySize;
        	private ResourceLocation widgets;
        	
        	MilkyWayButton(int actualSymbol, int canonSymbol,
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
        		if(ClientDHDConfig.canon_symbol_positions.get())
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
    	
    	private final int xCenter;
    	private final int yCenter;
    	private final double angle;
    	private final int innerRadius2;
    	private final int outerRadius2;
    	
    	public MilkyWay(int widgetX, int widgetY, int xSize, int ySize, AbstractDHDMenu menu, MilkyWayButton button)
        {
        	super(widgetX, widgetY, button.getXSize(), button.getYSize(), menu, button.getSymbol(), button.getWidgets());
        	
        	this.xCenter = xSize / 2;
        	this.yCenter = ySize / 2;
        	this.angle = button.getAngle();
        	this.innerRadius2 = button.isOuter() ? OUTER_SYMBOL_START_RADIUS_2 : INNER_SYMBOL_START_RADIUS_2;
        	this.outerRadius2 = button.isOuter() ? OUTER_SYMBOL_END_RADIUS_2 : INNER_SYMBOL_END_RADIUS_2;
        	
        	this.widgetTextureOffsetX = button.getTextureOffsetX();
        	this.widgetTextureOffsetY = button.getTextureOffsetY();
        }
    	
    	@Override
    	public boolean isMouseOver(double mouseX, double mouseY)
    	{
    		float phi = CoordinateSystems.cartesianToPolarPhi((float) (mouseX - xCenter), (float) (mouseY - yCenter)) + 180;
			
    		return this.active && this.visible &&
    				phi > this.angle && phi < this.angle + ANGLE &&
					((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY -yCenter, 2)) > this.innerRadius2) &&
					((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY -yCenter, 2)) < this.outerRadius2);
    	}

    	@Override
    	protected boolean clicked(double mouseX, double mouseY)
    	{
    		float phi = CoordinateSystems.cartesianToPolarPhi((float) (mouseX - xCenter), (float) (mouseY - yCenter)) + 180;
			
    		return this.active && this.visible &&
    				phi > this.angle && phi < this.angle + ANGLE &&
					((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY -yCenter, 2)) > this.innerRadius2) &&
					((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY -yCenter, 2)) < this.outerRadius2);
    	}
    	
    	@Override
    	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    	{
    		if(this.visible)
    		{
    			if((mouseY - yCenter) != 0)
    			{
    				float phi = CoordinateSystems.cartesianToPolarPhi((mouseX - xCenter), (mouseY - yCenter)) + 180;
    				//System.out.println(phi + " >= " + (-ANGLE));
    				this.isHovered = phi > this.angle && phi < this.angle + ANGLE &&
    						((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY -yCenter, 2)) > this.innerRadius2) &&
    						((Math.pow(mouseX - xCenter, 2) + Math.pow(mouseY -yCenter, 2)) < this.outerRadius2);
    			}
    			else
    				this.isHovered = false;	
    			
    			this.renderButton(poseStack, mouseX, mouseY, partialTick);
    			//this.updateTooltip();
    		}
    	}
    }
    
}
