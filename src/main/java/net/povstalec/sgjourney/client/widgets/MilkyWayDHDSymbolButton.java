package net.povstalec.sgjourney.client.widgets;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.MilkyWayDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;

public class MilkyWayDHDSymbolButton extends GenericDHDSymbolButton
{
	public static final ResourceLocation MILKY_WAY_BUTTONS = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way/milky_way_dhd_buttons.png");
	public static final ResourceLocation MILKY_WAY_BUTTONS_OVERLAY = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way/milky_way_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public MilkyWayDHDSymbolButton(int x, int y, int width, int height, MilkyWayDHDMenu menu, int screenWidth, int screenHeight,
								   float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, MILKY_WAY_BUTTONS, MILKY_WAY_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(255, 136, 0));
		
		this.canonSymbol = canonSymbol;
	}
	
	public MilkyWayDHDSymbolButton(int x, int y, MilkyWayDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton)
	{
		this(x, y, defaultButton.width, defaultButton.height, menu, screenWidth, screenHeight, defaultButton.width / 2F + defaultButton.xOffset, defaultButton.height / 2F + defaultButton.yOffset,
				defaultButton.textureX, defaultButton.textureY, symbol, canonSymbol, defaultButton.position);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.milky_way_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
}
