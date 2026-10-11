package net.povstalec.sgjourney.client.widgets.dhd;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.screens.graver.DHDEngravingScreen;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.dhd.AndromedaDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public abstract class AndromedaDHDSymbolButton<M extends IDHDMenu> extends GenericDHDSymbolButton<M>
{
	public static final int OUTER_BUTTON_Y_OFFSET_1 = 0;
	public static final int OUTER_BUTTON_Y_OFFSET_2 = 31;
	public static final int OUTER_BUTTON_Y_OFFSET_3 = 62;
	
	public static final int INNER_BUTTON_Y_OFFSET_1 = 93;
	public static final int INNER_BUTTON_Y_OFFSET_2 = 123;
	public static final int INNER_BUTTON_Y_OFFSET_3 = 153;
	
	public static final int[] CANON_SYMBOLS = {0, 12, 18, 21, 6, 37, 5, 28, 23, 33, 11, 36, 10, 20, 2, 3, 19, 8, 4, 31, 14, 34, 29, 15, 27, 9, 32, 38, 25, 22, 17, 13, 16, 1, 24, 35, 7, 26, 30, 39, 40, 41};
	
	public static final ResourceLocation ANDROMEDA_BUTTONS = StargateJourney.sgjourneyLocation("textures/gui/dhd/andromeda/andromeda_dhd_buttons.png");
	public static final ResourceLocation ANDROMEDA_BUTTONS_OVERLAY = StargateJourney.sgjourneyLocation("textures/gui/dhd/andromeda/andromeda_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public AndromedaDHDSymbolButton(int x, int y, int width, int height, M menu, int screenWidth, int screenHeight,
	                                float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, AndromedaPosition position, OnPress onPress)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, ANDROMEDA_BUTTONS, ANDROMEDA_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(125, 255, 0), onPress);
		
		this.canonSymbol = canonSymbol;
		
		setTooltip(symbolComponent());
	}
	
	public AndromedaDHDSymbolButton(int leftPos, int topPos, M menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, AndromedaButton andromedaButton, OnPress onPress)
	{
		this(leftPos + andromedaButton.xPos, topPos + andromedaButton.yPos, andromedaButton.width, andromedaButton.height, menu, screenWidth, screenHeight, andromedaButton.width / 2F + andromedaButton.symbolOffsetX, andromedaButton.height / 2F + andromedaButton.symbolOffsetY,
				andromedaButton.textureX, andromedaButton.textureY, symbol, canonSymbol, andromedaButton.position, onPress);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.andromeda_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
	
	public enum AndromedaPosition implements GenericDHDSymbolButton.Position
	{
		INNER(21, 10, 33, 61),
		OUTER(21, 16, 63, 88);
		
		public final int totalButtons;
		public final double angle;
		public final int symbolSize;
		public final int innerRadiusSqr;
		public final int outerRadiusSqr;
		
		AndromedaPosition(int totalButtons, int symbolSize, int innerRadius, int outerRadius)
		{
			this.totalButtons = totalButtons;
			this.angle = 360.0 / this.totalButtons;
			this.symbolSize = symbolSize;
			this.innerRadiusSqr = innerRadius * innerRadius;
			this.outerRadiusSqr = outerRadius * outerRadius;
		}
		
		@Override
		public int totalButtons()
		{
			return totalButtons;
		}
		
		@Override
		public double angle()
		{
			return angle;
		}
		
		@Override
		public int symbolSize()
		{
			return symbolSize;
		}
		
		@Override
		public int innerRadiusSqr()
		{
			return innerRadiusSqr;
		}
		
		@Override
		public int outerRadiusSqr()
		{
			return outerRadiusSqr;
		}
		
		@Override
		public double symbolAngle(int symbol)
		{
			return (symbol % totalButtons) * angle;
		}
	}
	
	public enum AndromedaButton
	{
		// Outer Buttons
		BUTTON_0(0, OUTER_BUTTON_Y_OFFSET_1, 25, 24, 0F, 0F, AndromedaPosition.OUTER, 8, 84),
		BUTTON_1(BUTTON_0.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 29, 27, 0F, 2F, AndromedaPosition.OUTER, 9, 59),
		BUTTON_2(BUTTON_1.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 32, 31, 1F, 2F, AndromedaPosition.OUTER, 17, 37),
		BUTTON_3(BUTTON_2.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 31, 31, 1F, 1F, AndromedaPosition.OUTER, 32, 21),
		BUTTON_4(BUTTON_3.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 28, 30, 2F, 0F, AndromedaPosition.OUTER, 53, 11),
		BUTTON_5(BUTTON_4.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 25, 26, 1F, 0F, AndromedaPosition.OUTER, 77, 8),
		BUTTON_6(BUTTON_5.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 25, 27, -2F, 0F, AndromedaPosition.OUTER, 102, 9),
		BUTTON_7(BUTTON_6.xEnd(), OUTER_BUTTON_Y_OFFSET_1, 31, 31, -1F, 1F, AndromedaPosition.OUTER, 119, 15),
		
		BUTTON_8(0, OUTER_BUTTON_Y_OFFSET_2, 31, 31, -1F, 1F, AndromedaPosition.OUTER, 137, 28),
		BUTTON_9(BUTTON_8.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 30, 28, 0F, 2F, AndromedaPosition.OUTER, 149, 48),
		BUTTON_10(BUTTON_9.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 27, 24, 0F, 2F, AndromedaPosition.OUTER, 157, 71),
		BUTTON_11(BUTTON_10.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 27, 24, 0F, -2F, AndromedaPosition.OUTER, 157, 97),
		BUTTON_12(BUTTON_11.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 30, 28, 0F, -2F, AndromedaPosition.OUTER, 149, 116),
		BUTTON_13(BUTTON_12.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 31, 31, -1F, -1F, AndromedaPosition.OUTER, 137, 133),
		BUTTON_14(BUTTON_13.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 31, 31, -1F, -1F, AndromedaPosition.OUTER, 119, 146),
		BUTTON_15(BUTTON_14.xEnd(), OUTER_BUTTON_Y_OFFSET_2, 25, 27, -2F, 0F, AndromedaPosition.OUTER, 102, 156),
		
		BUTTON_16(0, OUTER_BUTTON_Y_OFFSET_3, 25, 26, 1F, 0F, AndromedaPosition.OUTER, 77, 158),
		BUTTON_17(BUTTON_16.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 28, 30, 2F, 0F, AndromedaPosition.OUTER, 53, 151),
		BUTTON_18(BUTTON_17.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 31, 31, 1F, -1F, AndromedaPosition.OUTER, 32, 140),
		BUTTON_19(BUTTON_18.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 32, 31, 1F, -2F, AndromedaPosition.OUTER, 17, 124),
		BUTTON_20(BUTTON_19.xEnd(), OUTER_BUTTON_Y_OFFSET_3, 29, 27, 0F, -2F, AndromedaPosition.OUTER, 9, 106),
		// Inner Buttons
		BUTTON_21(0, INNER_BUTTON_Y_OFFSET_1, 28, 16, 0F, 0F, AndromedaPosition.INNER, 35, 88),
		BUTTON_22(BUTTON_21.xEnd(), INNER_BUTTON_Y_OFFSET_1, 29, 19, 0F, 2F, AndromedaPosition.INNER, 36, 71),
		BUTTON_23(BUTTON_22.xEnd(), INNER_BUTTON_Y_OFFSET_1, 29, 26, 1F, 2F, AndromedaPosition.INNER, 42, 55),
		BUTTON_24(BUTTON_23.xEnd(), INNER_BUTTON_Y_OFFSET_1, 27, 29, 1F, 0F, AndromedaPosition.INNER, 52, 44),
		BUTTON_25(BUTTON_24.xEnd(), INNER_BUTTON_Y_OFFSET_1, 21, 30, 2F, 0F, AndromedaPosition.INNER, 66, 37),
		BUTTON_26(BUTTON_25.xEnd(), INNER_BUTTON_Y_OFFSET_1, 16, 28, 1F, 0F, AndromedaPosition.INNER, 83, 35),
		BUTTON_27(BUTTON_26.xEnd(), INNER_BUTTON_Y_OFFSET_1, 18, 29, -2F, 0F, AndromedaPosition.INNER, 99, 35),
		BUTTON_28(BUTTON_27.xEnd(), INNER_BUTTON_Y_OFFSET_1, 24, 29, -1F, 0F, AndromedaPosition.INNER, 109, 40),
		BUTTON_29(BUTTON_28.xEnd(), INNER_BUTTON_Y_OFFSET_1, 29, 27, 0F, 0F, AndromedaPosition.INNER, 117, 49),
		BUTTON_30(BUTTON_29.xEnd(), INNER_BUTTON_Y_OFFSET_1, 30, 22, 0F, 1F, AndromedaPosition.INNER, 124, 63),
		
		BUTTON_31(0, INNER_BUTTON_Y_OFFSET_2, 29, 16, 1F, 2F, AndromedaPosition.INNER, 128, 79),
		BUTTON_32(BUTTON_31.xEnd(), INNER_BUTTON_Y_OFFSET_2, 29, 16, 1F, -2F, AndromedaPosition.INNER, 128, 97),
		BUTTON_33(BUTTON_32.xEnd(), INNER_BUTTON_Y_OFFSET_2, 30, 22, 0F, -1F, AndromedaPosition.INNER, 124, 107),
		BUTTON_34(BUTTON_33.xEnd(), INNER_BUTTON_Y_OFFSET_2, 29, 27, 0F, 0F, AndromedaPosition.INNER, 117, 116),
		BUTTON_35(BUTTON_34.xEnd(), INNER_BUTTON_Y_OFFSET_2, 24, 29, -1F, 0F, AndromedaPosition.INNER, 109, 123),
		BUTTON_36(BUTTON_35.xEnd(), INNER_BUTTON_Y_OFFSET_2, 18, 29, -2F, 0F, AndromedaPosition.INNER, 99, 128),
		BUTTON_37(BUTTON_36.xEnd(), INNER_BUTTON_Y_OFFSET_2, 16, 28, 1F, 0F, AndromedaPosition.INNER, 83, 129),
		BUTTON_38(BUTTON_37.xEnd(), INNER_BUTTON_Y_OFFSET_2, 21, 30, 2F, 0F, AndromedaPosition.INNER, 66, 125),
		BUTTON_39(BUTTON_38.xEnd(), INNER_BUTTON_Y_OFFSET_2, 27, 29, 1F, 0F, AndromedaPosition.INNER, 52, 119),
		BUTTON_40(BUTTON_39.xEnd(), INNER_BUTTON_Y_OFFSET_2, 29, 26, 1F, -2F, AndromedaPosition.INNER, 42, 111),
		
		BUTTON_41(0, INNER_BUTTON_Y_OFFSET_3, 29, 19, 0F, -2F, AndromedaPosition.INNER, 36, 102);
		
		public final int textureX;
		public final int textureY;
		public final int width;
		public final int height;
		public final float symbolOffsetX;
		public final float symbolOffsetY;
		public final AndromedaPosition position;
		public final int xPos;
		public final int yPos;
		
		AndromedaButton(int textureX, int textureY, int width, int height, float symbolOffsetX, float symbolOffsetY, AndromedaPosition position, int xPos, int yPos)
		{
			this.textureX = textureX;
			this.textureY = textureY;
			this.width = width;
			this.height = height;
			this.symbolOffsetX = symbolOffsetX;
			this.symbolOffsetY = symbolOffsetY;
			this.position = position;
			this.xPos = xPos;
			this.yPos = yPos;
		}
		
		public int xEnd()
		{
			return textureX + width;
		}
	}
	
	
	
	public static class Dialing extends AndromedaDHDSymbolButton<AndromedaDHDMenu>
	{
		public Dialing(int x, int y, int width, int height, AndromedaDHDMenu menu, int screenWidth, int screenHeight, float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, AndromedaPosition position, OnPress onPress)
		{
			super(x, y, width, height, menu, screenWidth, screenHeight, xCenter, yCenter, textureX, textureY, symbol, canonSymbol, position, onPress);
		}
		
		public Dialing(int leftPos, int topPos, AndromedaDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, AndromedaButton andromedaButton, OnPress onPress)
		{
			super(leftPos, topPos, menu, screenWidth, screenHeight, symbol, canonSymbol, andromedaButton, onPress);
		}
		
		@Override
		public ResourceKey<PointOfOrigin> getPointOfOrigin()
		{
			return this.menu.getDHD().symbolInfo().pointOfOrigin();
		}
		
		@Override
		public ResourceKey<Symbols> getSymbols()
		{
			return this.menu.getDHD().symbolInfo().symbols();
		}
	}
	
	
	
	public static class Engraving extends AndromedaDHDSymbolButton<DHDEngravingMenu.Andromeda>
	{
		protected final DHDEngravingScreen.Andromeda screen;
		
		public Engraving(int x, int y, int width, int height, DHDEngravingScreen.Andromeda screen, int screenWidth, int screenHeight, float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, AndromedaPosition position, OnPress onPress)
		{
			super(x, y, width, height, screen.getMenu(), screenWidth, screenHeight, xCenter, yCenter, textureX, textureY, symbol, canonSymbol, position, onPress);
			
			this.screen = screen;
		}
		
		public Engraving(int leftPos, int topPos, DHDEngravingScreen.Andromeda screen, int screenWidth, int screenHeight, int symbol, int canonSymbol, AndromedaButton andromedaButton, OnPress onPress)
		{
			super(leftPos, topPos, screen.getMenu(), screenWidth, screenHeight, symbol, canonSymbol, andromedaButton, onPress);
			
			this.screen = screen;
		}
		
		@Override
		public ResourceKey<PointOfOrigin> getPointOfOrigin()
		{
			return screen.getPointOfOrigin();
		}
		
		@Override
		public ResourceKey<Symbols> getSymbols()
		{
			return screen.getSymbols();
		}
		
		@Override
		public boolean isOverButton(double mouseX, double mouseY)
		{
			return false;
		}
		
		@Override
		public boolean isMouseOver(double mouseX, double mouseY)
		{
			return false;
		}
		
		@Override
		protected boolean clicked(double mouseX, double mouseY)
		{
			return false;
		}
	}
}
