package net.povstalec.sgjourney.client.screens.graver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.models.block.CartoucheBakedModel;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.graver.CartoucheEngravingMenu;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.ParsingResult;
import net.povstalec.sgjourney.common.packets.ServerboundEngravingUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

public abstract class CartoucheEngravingScreen<M extends CartoucheEngravingMenu<?>> extends AbstractEngravingScreen<M>
{
	public static final float X_SYMBOL_SPACE = 64F * 10F / 16F;
	public static final float Y_SYMBOL_SPACE = 64F * 26F / 16F;
	
	public static final float CARTOUCHE_HEIGHT = 128;
	public static final float CARTOUCHE_HALF_AT = 1 + CARTOUCHE_HEIGHT / 2F;
	
	protected final ResourceLocation texture;
	protected ColorUtil.RGBA rgba;
	
	protected EditBox editBox;
	protected final Address.Mutable address;
	protected final boolean wasDimensionAddress;
	
	public CartoucheEngravingScreen(M menu, ResourceLocation texture, Inventory playerInventory, Component title, ColorUtil.RGBA rgba)
	{
		super(menu, playerInventory, title);
		
		this.texture = texture;
		
		this.rgba = rgba;
		
		this.imageWidth = 176;
		this.imageHeight = 250;
		
		this.inventoryLabelY = this.imageHeight - 94;
		
		wasDimensionAddress = menu.blockEntity.getAddress() instanceof Address.Dimension;
		address = new Address.Mutable(menu.blockEntity.getAddress());
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
		BakedModel model = dispatcher.getBlockModel(menu.blockEntity.getBlockState());
		if(model instanceof CartoucheBakedModel cartoucheModel)
			this.rgba = new ColorUtil.RGBA(cartoucheModel.getSymbolTint());
		
		this.engravingButton = Button.builder(Component.translatable("screen.sgjourney.engraving.engrave"),
				button ->
				{
					// The player is attempting to overwrite the Cartouche's Dimension Address
					if(wasDimensionAddress && !address.equals(menu.blockEntity.getAddress()))
					{
						minecraft.setScreen(new ConfirmScreen(confirm ->
						{
							if(confirm)
								engrave();
							else
								minecraft.setScreen(this);
						},
							Component.translatable("screen.sgjourney.engraving.cartouche.overwrite_address"),
							Component.translatable("screen.sgjourney.engraving.cartouche.overwrite_address.warning"),
							CommonComponents.GUI_ACKNOWLEDGE,
							CommonComponents.GUI_CANCEL)
						{
							@Override
							public boolean isPauseScreen()
							{
								return false;
							}
						});
					}
					else
						engrave();
				})
			.bounds(leftPos + 121, topPos + 109, 56, 20).build();
		
		updateEngravingButton();
		this.addRenderableWidget(this.engravingButton);
		
		this.editBox = new EditBox(font, leftPos, topPos + 130, 176, 20, Component.translatable("tooltip.sgjourney.address"));
		this.editBox.setFilter(Address::canBeTransformedToAddress);
		
		this.editBox.setMaxLength(28);
		this.editBox.setValue(address.toString());
		this.editBox.setResponder(text ->
		{
			int[] addressArray = Address.addressStringToIntArray(text);
			if(ArrayHelper.contains(addressArray, 0))
			{
				address.reset();
				engravingButton.active = false;
				engravingButton.setTooltip(Tooltip.create(Component.translatable("screen.sgjourney.engraving.cartouche.should_not_contain_point_of_origin")));
			}
			else
			{
				ParsingResult parsingResult = Address.intArrayParsingResult(addressArray);
				if(parsingResult.isSuccess())
				{
					address.fromString(text);
					updateEngravingButton();
				}
				else
				{
					address.reset();
					engravingButton.active = false;
					engravingButton.setTooltip(Tooltip.create(parsingResult.getMessage()));
				}
			}
		});
		
		this.addRenderableWidget(this.editBox);
		this.setInitialFocus(this.editBox);
		
		this.menu.addSlotListener(new ContainerListener()
		{
			@Override
			public void slotChanged(@NotNull AbstractContainerMenu menu, int slot, @NotNull ItemStack stack)
			{
				updateEngravingButton();
			}
			
			@Override
			public void dataChanged(@NotNull AbstractContainerMenu menu, int slot, int dataSlot)
			{
				updateEngravingButton();
			}
		});
	}
	
	protected void updateEngravingButton()
	{
		boolean isAddressDifferent = !address.equals(menu.blockEntity.getAddress()) || !getSymbols().equals(menu.blockEntity.getSymbols());
		engravingButton.active = isAddressDifferent;
		engravingButton.setTooltip(isAddressDifferent ? null : Tooltip.create(Component.translatable("screen.sgjourney.engraving.cartouche.same_address")));
	}
	
	public void engrave()
	{
		ServerboundEngravingUpdatePacket packet = new ServerboundEngravingUpdatePacket(menu.blockEntity.getBlockPos());
		
		if(!address.equals(menu.blockEntity.getAddress()))
			packet.withAddress(new Address.Immutable(address));
		if(!getSymbols().equals(menu.blockEntity.getSymbols()))
			packet.withSymbols(getSymbols());
		
		PacketHandlerInit.INSTANCE.sendToServer(packet);
		onClose();
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
		
		this.itemHint(stack, leftPos + 124, topPos + 57, 176, 0, 0);
	}
	
	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
	{
		renderBackground(stack);
		super.render(stack, mouseX, mouseY, delta);
		renderTooltip(stack, mouseX, mouseY);
		
		itemTooltip(stack, mouseX, mouseY, 124, 57, 0, ComponentHelper.description("screen.sgjourney.engraving.symbol_block.insert_symbol_paper"));
	}
	
	@Override
	protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
		
		renderSymbols(poseStack);
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return !menu.tempContainer.getItem(slot).isEmpty();
	}
	
	
	
	public static class Stone extends CartoucheEngravingScreen<CartoucheEngravingMenu.Stone>
	{
		public Stone(CartoucheEngravingMenu.Stone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/cartouche/stone_cartouche_engraving_gui.png"), playerInventory, title, new ColorUtil.RGBA(90, 89, 90));
		}
	}
	
	public static class Sandstone extends CartoucheEngravingScreen<CartoucheEngravingMenu.Sandstone>
	{
		public Sandstone(CartoucheEngravingMenu.Sandstone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/cartouche/sandstone_cartouche_engraving_gui.png"), playerInventory, title, new ColorUtil.RGBA(198, 174, 113));
		}
	}
	
	public static class RedSandstone extends CartoucheEngravingScreen<CartoucheEngravingMenu.RedSandstone>
	{
		public RedSandstone(CartoucheEngravingMenu.RedSandstone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/cartouche/red_sandstone_cartouche_engraving_gui.png"), playerInventory, title, new ColorUtil.RGBA(142, 71, 11));
		}
	}
}
