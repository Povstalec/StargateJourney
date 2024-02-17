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
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class DHDSymbolButton extends DHDButton
{
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/gui/milky_way_dhd_widgets.png");
	
	protected ResourceLocation widgets;
	protected int widgetX = 0;
	protected int widgetY = 0;
	
    public DHDSymbolButton(int x, int y, int xSize, int ySize, AbstractDHDMenu menu, int symbol, ResourceLocation widgets)
	{
		super(x, y, xSize, ySize, symbol(menu.symbolsType, symbol), (button) -> {menu.engageChevron(symbol);});
		
		this.widgets = widgets;
	}
	
    public DHDSymbolButton(int x, int y, AbstractDHDMenu menu, int symbol)
	{
		this(x, y, 16, 16, menu, symbol, WIDGETS_LOCATION);
	}
	    
	@Override
	protected int getYImage(boolean isHovering)
	{
		int i = 0;
		
		if(isHovering)
			i = 1;
		
		return i;
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
        this.blit(poseStack, this.getX(), this.getY(), widgetX, widgetY + i * this.getHeight(), this.width, this.height);
        this.blit(poseStack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
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
        public MilkyWay(int x, int y, AbstractDHDMenu menu, MilkyWayButton button)
        {
        	super(x, y, button.getXSize(), button.getYSize(), menu, button.getSymbol(), new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way_dhd_widgets.png"));
        	
        	this.widgetX = button.getX();
        	this.widgetY = button.getY();
        }
    	
        public enum MilkyWayButton
        {
        	BUTTON_1(12, 54, 0, 28, 27),
        	BUTTON_2(18, 82, 0, 31, 31);

        	private int symbol;
        	private int x;
        	private int y;
        	private int xSize;
        	private int ySize;
        	
        	MilkyWayButton(int canonSymbol, int widgetX, int widgetY, int xSize, int ySize)
        	{
        		this.symbol = canonSymbol;
        		this.x = widgetX;
        		this.y = widgetY;
        		this.xSize = xSize;
        		this.ySize = ySize;
        	}
        	
        	public int getSymbol()
        	{
        		return this.symbol;
        	}
        	
        	public int getX()
        	{
        		return this.x;
        	}
        	
        	public int getY()
        	{
        		return this.y;
        	}
        	
        	public int getXSize()
        	{
        		return this.xSize;
        	}
        	
        	public int getYSize()
        	{
        		return this.ySize;
        	}
        }
    }
    
}
