package net.povstalec.sgjourney.client.screens.graver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.CartoucheMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

public abstract class CartoucheGravingScreen<M extends CartoucheMenu<?>> extends SGJourneyContainerScreen<M>
{
	public static final float X_SYMBOL_SPACE = 64F * 10F / 16F;
	public static final float Y_SYMBOL_SPACE = 64F * 26F / 16F;
	
	public static final float CARTOUCHE_HEIGHT = 128;
	public static final float CARTOUCHE_HALF_AT = 1 + CARTOUCHE_HEIGHT / 2F;
	
	protected final ResourceLocation texture;
	protected final ColorUtil.RGBA rgba;
	
	protected EditBox editBox;
	protected final Address.Mutable address;
	
	public CartoucheGravingScreen(M menu, ResourceLocation texture, Inventory playerInventory, Component title, ColorUtil.RGBA rgba)
	{
		super(menu, playerInventory, title);
		
		this.texture = texture;
		this.rgba = rgba;
		
		this.imageWidth = 176;
		this.imageHeight = 252;
		
		this.inventoryLabelY = this.imageHeight - 94;
		
		address = new Address.Mutable(menu.blockEntity.getAddress());
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		this.editBox = new EditBox(this.font, leftPos + 8, topPos + 136, 176, 20, Component.translatable("tooltip.sgjourney.energy_target"))
		{
			public void ensureDash()
			{
				int pos = getCursorPosition();
				String value = getValue();
				
				if(!value.startsWith("-"))
				{
					setValue('-' + value);
					setCursorPosition(pos + 1);
				}
				if(!value.endsWith("-"))
				{
					setValue(value + '-');
					setCursorPosition(pos);
				}
			}
			
			@Override
			public void insertText(@NotNull String text)
			{
				super.insertText(text.replace(' ', '-'));
				ensureDash();
			}
			
			@Override
			public void deleteWords(int pos)
			{
				super.deleteWords(pos);
				ensureDash();
			}
			
			@Override
			public void deleteChars(int pos)
			{
				super.deleteChars(pos);
				ensureDash();
			}
		};
		this.editBox.setFilter(text -> acceptedAsAddress(text));
		
		this.editBox.setMaxLength(28);
		this.editBox.setResponder(address::fromString);
		this.editBox.setValue(address.toString());
		
		this.addRenderableWidget(this.editBox);
		this.setInitialFocus(this.editBox);
	}
	
	public static boolean acceptedAsAddress(String addressString)
	{
		String[] segments = addressString.split(Address.ADDRESS_DIVIDER);
		for(int i = 1; i < segments.length; ++i)
		{
			if(segments[i].isEmpty() || segments[i].length() > 2)
				return false;
			
			for(int j = 0; j < segments[i].length(); ++j)
			{
				if(!Address.isAllowedInAddress(segments[i].charAt(j)))
					return false;
			}
		}
		
		return true;
	}
	
	public ResourceKey<Symbols> getSymbols()
	{
		ResourceKey<Symbols> symbols = SymbolPaperItem.getSymbols(menu.tempContainer.getItem(0));
		if(symbols != null)
			return symbols;
		
		return menu.blockEntity.getSymbols();
	}
	
	public void renderSymbols(PoseStack stack)
	{
		float symbolSize = Y_SYMBOL_SPACE / address.getLength();
		if(symbolSize > X_SYMBOL_SPACE)
			symbolSize = X_SYMBOL_SPACE;
		
		ClientSymbols symbols = ClientSymbols.getSymbols(getSymbols());
		
		if(symbols == null)
			return;
		
		float xPos = (imageWidth - symbolSize) / 2F;
		float yStart = -symbolSize * address.getLength() / 2F;
		if(yStart > Y_SYMBOL_SPACE / 2F)
			yStart = Y_SYMBOL_SPACE / 2F;
		
		yStart += CARTOUCHE_HALF_AT;
		
		for(int i = 0; i < address.getLength(); i++)
		{
			float yPos = yStart + symbolSize * i;
			ClientUtil.renderSymbol(stack.last().pose(), xPos, yPos, xPos + symbolSize, yPos + symbolSize, symbols, address.symbolAt(i), rgba);
		}
	}
	
	@Override
	protected void renderBg(@NotNull PoseStack stack, float partialTick, int mouseX, int mouseY)
	{
		this.renderBackground(stack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		this.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
	
	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
	{
		renderBackground(stack);
		super.render(stack, mouseX, mouseY, delta);
		renderTooltip(stack, mouseX, mouseY);
	}
	
	@Override
	protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
		
		renderSymbols(poseStack);
	}
	
	
	
	public static class Stone extends CartoucheGravingScreen<CartoucheMenu.Stone>
	{
		public Stone(CartoucheMenu.Stone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/cartouche/stone_cartouche_gui.png"), playerInventory, title, new ColorUtil.RGBA(90, 89, 90));
		}
	}
	
	public static class Sandstone extends CartoucheGravingScreen<CartoucheMenu.Sandstone>
	{
		public Sandstone(CartoucheMenu.Sandstone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/cartouche/sandstone_cartouche_gui.png"), playerInventory, title, new ColorUtil.RGBA(198, 174, 113));
		}
	}
	
	public static class RedSandstone extends CartoucheGravingScreen<CartoucheMenu.RedSandstone>
	{
		public RedSandstone(CartoucheMenu.RedSandstone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/cartouche/red_sandstone_cartouche_gui.png"), playerInventory, title, new ColorUtil.RGBA(142, 71, 11));
		}
	}
}
