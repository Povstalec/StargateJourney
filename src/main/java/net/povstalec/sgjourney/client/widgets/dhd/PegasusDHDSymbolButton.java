package net.povstalec.sgjourney.client.widgets.dhd;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.PegasusDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;

public class PegasusDHDSymbolButton extends GenericDHDSymbolButton
{
	public static final ResourceLocation PEGASUS_BUTTONS = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/pegasus/pegasus_dhd_buttons.png");
	public static final ResourceLocation PEGASUS_BUTTONS_OVERLAY = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/pegasus/pegasus_dhd_buttons_overlay.png");
	
	protected final int canonSymbol;
	
	public PegasusDHDSymbolButton(int x, int y, int width, int height, PegasusDHDMenu menu, int screenWidth, int screenHeight,
								  float xCenter, float yCenter, int textureX, int textureY, int symbol, int canonSymbol, Position position)
	{
		super(x, y, width, height, menu, symbol, screenWidth, screenHeight, PEGASUS_BUTTONS, PEGASUS_BUTTONS_OVERLAY, xCenter, yCenter, textureX, textureY, position,
				new ColorUtil.RGBA(255, 255, 255), new ColorUtil.RGBA(65, 65, 65), new ColorUtil.RGBA(0, 242, 255));
		
		this.canonSymbol = canonSymbol;
		
		setTooltip(Tooltip.create(symbolComponent()));
	}
	
	public PegasusDHDSymbolButton(int x, int y, PegasusDHDMenu menu, int screenWidth, int screenHeight, int symbol, int canonSymbol, DefaultButton defaultButton)
	{
		this(x, y, defaultButton.width, defaultButton.height, menu, screenWidth, screenHeight, defaultButton.width / 2F + defaultButton.xOffset, defaultButton.height / 2F + defaultButton.yOffset,
				defaultButton.textureX, defaultButton.textureY, symbol, canonSymbol, defaultButton.position);
	}
	
	@Override
	public int getSymbol()
	{
		return ClientDHDConfig.pegasus_dhd_canon_button_layout.get() ? canonSymbol : symbol;
	}
}
