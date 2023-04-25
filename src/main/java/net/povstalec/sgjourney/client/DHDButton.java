package net.povstalec.sgjourney.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;
import net.povstalec.sgjourney.common.stargate.Symbols;

@OnlyIn(Dist.CLIENT)
public class DHDButton extends Button
{
	
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/gui/milky_way_dhd_widgets.png");
	
    public DHDButton(int x, int y, AbstractDHDMenu menu, int i)
	{
		super(x, y, 16, 16, symbol(menu.symbolsType, i), (n) -> {menu.engageChevron(i);}, Button.DEFAULT_NARRATION);
	}
    
    @Override
    public void renderButton(PoseStack p_93676_, int p_93677_, int p_93678_, float p_93679_) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(p_93676_, this.getX(), this.getY(), 0, i * 16, this.width, this.height);
        this.blit(p_93676_, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(p_93676_, minecraft, p_93677_, p_93678_);
        int j = getFGColor();
        drawCenteredString(p_93676_, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
	
    private static Component symbol(String symbolsType, int i)
    {
    	MutableComponent symbols = Component.literal("" + i); //Symbols.unicode(i)
		//Style style = symbols.getStyle().withFont(new ResourceLocation(symbolsType));
		//symbols = symbols.withStyle(style);
		return symbols;
    }
    
}
