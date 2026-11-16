package net.povstalec.sgjourney.client.widgets.dhd;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.PegasusDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public abstract class PegasusDHDSymbolButton<M extends IDHDMenu> extends GenericDHDSymbolButton<M>
{
	public static final int[] CANON_SYMBOLS = {8, 34, 13, 1, 16, 19, 5, 11, 18, 17, 10, 31, 0, 6, 20, 33, 2, 38, 21, 12, 14, 29, 36, 4, 28, 35, 7, 32, 24, 22, 27, 26, 23, 25, 15, 37, 30, 3, 9};
	
	public static final ResourceLocation PEGASUS_BUTTONS = StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_buttons.png");
	public static final ResourceLocation PEGASUS_BUTTONS_OVERLAY = StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public PegasusDHDSymbolButton(int x, int y, int width, int height, M menu, int screenWidth, int screenHeight,
								  float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position, Button.OnPress onPress)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, PEGASUS_BUTTONS, PEGASUS_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(0, 242, 255), onPress);
		
		this.canonSymbol = canonSymbol;
		
		setTooltip(Tooltip.create(symbolComponent()));
	}
	
	public PegasusDHDSymbolButton(int leftPos, int topPos, M menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton, Button.OnPress onPress)
	{
		this(leftPos + defaultButton.xPos, topPos + defaultButton.yPos, defaultButton.width, defaultButton.height, menu, screenWidth, screenHeight, defaultButton.width / 2F + defaultButton.symbolOffsetX, defaultButton.height / 2F + defaultButton.symbolOffsetY,
				defaultButton.textureX, defaultButton.textureY, symbol, canonSymbol, defaultButton.position, onPress);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.pegasus_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
	
	
	
	public static class Dialing extends PegasusDHDSymbolButton<PegasusDHDMenu>
	{
		public Dialing(int x, int y, int width, int height, PegasusDHDMenu menu, int screenWidth, int screenHeight, float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position, OnPress onPress)
		{
			super(x, y, width, height, menu, screenWidth, screenHeight, xCenter, yCenter, textureX, textureY, symbol, canonSymbol, position, onPress);
		}
		
		public Dialing(int leftPos, int topPos, PegasusDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton, OnPress onPress)
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
}
