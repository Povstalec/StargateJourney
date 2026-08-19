package net.povstalec.sgjourney.client.widgets.dhd;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.UniverseDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;

public class UniverseDHDSymbolButton extends GenericDHDSymbolButton
{
	public static final ResourceLocation UNIVERSE_BUTTONS = StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_buttons.png");
	public static final ResourceLocation UNIVERSE_BUTTONS_OVERLAY = StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public UniverseDHDSymbolButton(int x, int y, int width, int height, UniverseDHDMenu menu, int screenWidth, int screenHeight,
	                               float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, UNIVERSE_BUTTONS, UNIVERSE_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(199, 220, 255));
		
		this.canonSymbol = canonSymbol;
		
		setTooltip(Tooltip.create(symbolComponent()));
	}
	
	public UniverseDHDSymbolButton(int x, int y, UniverseDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton)
	{
		this(x, y, defaultButton.width, defaultButton.height, menu, screenWidth, screenHeight, defaultButton.width / 2F + defaultButton.xOffset, defaultButton.height / 2F + defaultButton.yOffset,
				defaultButton.textureX, defaultButton.textureY, symbol, canonSymbol, defaultButton.position);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.universe_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
}
