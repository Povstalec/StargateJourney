package net.povstalec.sgjourney.client.widgets.dhd;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.screens.graver.DHDEngravingScreen;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.UniverseDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public abstract class UniverseDHDSymbolButton<M extends IDHDMenu> extends GenericDHDSymbolButton<M>
{
	public static final int[] CANON_SYMBOLS = {0, 12, 18, 21, 6, 37, 5, 28, 23, 33, 11, 36, 10, 20, 2, 3, 19, 8, 4, 31, 14, 34, 29, 15, 27, 9, 32, 38, 25, 22, 17, 13, 16, 1, 24, 35, 7, 26, 30};
	
	public static final ResourceLocation UNIVERSE_BUTTONS = StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_buttons.png");
	public static final ResourceLocation UNIVERSE_BUTTONS_OVERLAY = StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public UniverseDHDSymbolButton(int x, int y, int width, int height, M menu, int screenWidth, int screenHeight,
	                               float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position, Button.OnPress onPress)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, UNIVERSE_BUTTONS, UNIVERSE_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(199, 220, 255), onPress);
		
		this.canonSymbol = canonSymbol;
		
		setTooltip(Tooltip.create(symbolComponent()));
	}
	
	public UniverseDHDSymbolButton(int leftPos, int topPos, M menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton, Button.OnPress onPress)
	{
		this(leftPos + defaultButton.xPos, topPos + defaultButton.yPos, defaultButton.width, defaultButton.height, menu, screenWidth, screenHeight, defaultButton.width / 2F + defaultButton.symbolOffsetX, defaultButton.height / 2F + defaultButton.symbolOffsetY,
				defaultButton.textureX, defaultButton.textureY, symbol, canonSymbol, defaultButton.position, onPress);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.universe_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
	
	
	
	public static class Dialing extends UniverseDHDSymbolButton<UniverseDHDMenu>
	{
		public Dialing(int x, int y, int width, int height, UniverseDHDMenu menu, int screenWidth, int screenHeight, float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position, OnPress onPress)
		{
			super(x, y, width, height, menu, screenWidth, screenHeight, xCenter, yCenter, textureX, textureY, symbol, canonSymbol, position, onPress);
		}
		
		public Dialing(int leftPos, int topPos, UniverseDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton, OnPress onPress)
		{
			super(leftPos, topPos, menu, screenWidth, screenHeight, symbol, canonSymbol, defaultButton, onPress);
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
	
	
	
	public static class Engraving extends UniverseDHDSymbolButton<DHDEngravingMenu.Universe>
	{
		protected final DHDEngravingScreen.Universe screen;
		
		public Engraving(int x, int y, int width, int height, DHDEngravingScreen.Universe screen, int screenWidth, int screenHeight, float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position, OnPress onPress)
		{
			super(x, y, width, height, screen.getMenu(), screenWidth, screenHeight, xCenter, yCenter, textureX, textureY, symbol, canonSymbol, position, onPress);
			
			this.screen = screen;
		}
		
		public Engraving(int leftPos, int topPos, DHDEngravingScreen.Universe screen, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton, OnPress onPress)
		{
			super(leftPos, topPos, screen.getMenu(), screenWidth, screenHeight, symbol, canonSymbol, defaultButton, onPress);
			
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
