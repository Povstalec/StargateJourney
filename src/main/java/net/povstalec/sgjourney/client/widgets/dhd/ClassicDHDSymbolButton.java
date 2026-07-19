package net.povstalec.sgjourney.client.widgets.dhd;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.ClassicDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;

public class ClassicDHDSymbolButton extends GenericDHDSymbolButton
{
	public static final ResourceLocation CLASSIC_BUTTONS = StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_buttons.png");
	public static final ResourceLocation CLASSIC_BUTTONS_OVERLAY = StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public ClassicDHDSymbolButton(int x, int y, int width, int height, ClassicDHDMenu menu, int screenWidth, int screenHeight,
								   float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, CLASSIC_BUTTONS, CLASSIC_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(255, 136, 0));
		
		this.canonSymbol = canonSymbol;
		
		setTooltip(Tooltip.create(symbolComponent()));
	}
	
	public ClassicDHDSymbolButton(int x, int y, ClassicDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton)
	{
		this(x, y, defaultButton.width, defaultButton.height, menu, screenWidth, screenHeight, defaultButton.width / 2F + defaultButton.xOffset, defaultButton.height / 2F + defaultButton.yOffset,
				defaultButton.textureX, defaultButton.textureY, symbol, canonSymbol, defaultButton.position);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.classic_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
}
