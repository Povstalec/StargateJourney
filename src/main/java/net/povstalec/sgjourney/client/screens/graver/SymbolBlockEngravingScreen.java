package net.povstalec.sgjourney.client.screens.graver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.models.block.SymbolBlockBakedModel;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.graver.SymbolBlockEngravingMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.packets.ServerboundEngravingUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class SymbolBlockEngravingScreen<M extends SymbolBlockEngravingMenu<?>> extends SGJourneyContainerScreen<M>
{
	public static final float SYMBOL_SIZE = 64;
	
	protected final ResourceLocation texture;
	protected ColorUtil.RGBA rgba;
	
	protected EditBox editBox;
	protected Button engravingButton;
	protected int symbolNumber;
	
	public SymbolBlockEngravingScreen(M menu, ResourceLocation texture, Inventory playerInventory, Component title, ColorUtil.RGBA rgba)
	{
		super(menu, playerInventory, title);
		
		this.texture = texture;
		
		this.rgba = rgba;
		
		this.imageWidth = 176;
		this.imageHeight = 164;
		
		this.inventoryLabelY = this.imageHeight - 94;
		
		this.symbolNumber = menu.blockEntity.getSymbolNumber();
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
		BakedModel model = dispatcher.getBlockModel(menu.blockEntity.getBlockState());
		if(model instanceof SymbolBlockBakedModel symbolBlockModel)
			this.rgba = new ColorUtil.RGBA(symbolBlockModel.getSymbolTint());
		
		this.engravingButton = Button.builder(Component.translatable("screen.sgjourney.engraving.engrave"), button -> engrave())
			.bounds(leftPos + 121, topPos + 45, 56, 20).build();
		
		updateEngravingButton();
		this.addRenderableWidget(this.engravingButton);
		
		this.editBox = new EditBox(font, leftPos + 2, topPos + 44, 52, 20, Component.translatable("tooltip.sgjourney.symbol"));
		this.editBox.setFilter(SymbolBlockEngravingScreen::canParseAsPositiveNumber);
		
		this.editBox.setMaxLength(2);
		
		if(menu.blockEntity.getSymbols() == null && menu.blockEntity.getPointOfOrigin() == null)
		{
			this.editBox.setValue("");
		}
		else
			this.editBox.setValue(Integer.toString(symbolNumber));
		
		this.editBox.setResponder(text ->
		{
			if(text.isEmpty())
			{
				symbolNumber = -1;
				updateEngravingButton();
			}
			else
			{
				int parsedNumber = Integer.parseInt(text);
				
				if(parsedNumber < Address.MIN_SYMBOL || parsedNumber > Address.MAX_SYMBOL)
				{
					symbolNumber = -1;
					engravingButton.active = false;
					engravingButton.setTooltip(Tooltip.create(Component.translatable("screen.sgjourney.engraving.symbol_block.out_of_bounds")));
				}
				else
				{
					symbolNumber = parsedNumber;
					updateEngravingButton();
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
	
	public void updateEngravingButton()
	{
		boolean isSymbolDifferent = symbolNumber != menu.blockEntity.getSymbolNumber() ||
			(symbolNumber == 0 ?
				!Objects.equals(getPointOfOrigin(), menu.blockEntity.getPointOfOrigin()) :
				!Objects.equals(getSymbols(), menu.blockEntity.getSymbols()));
		engravingButton.active = isSymbolDifferent;
		engravingButton.setTooltip(isSymbolDifferent ? null : Tooltip.create(Component.translatable("screen.sgjourney.engraving.symbol_block.same_symbol")));
	}
	
	public static boolean canParseAsPositiveNumber(String text)
	{
		for(int i = 0; i < text.length(); i++)
		{
			if(!Character.isDigit(text.charAt(i)))
				return false;
		}
		
		return true;
	}
	
	public void engrave()
	{
		ServerboundEngravingUpdatePacket packet = new ServerboundEngravingUpdatePacket(menu.blockEntity.getBlockPos());
		
		if(symbolNumber != menu.blockEntity.getSymbolNumber())
		{
			if(symbolNumber >= 0)
				packet.withAddress(new Address.Immutable(symbolNumber));
			else
				packet.withAddress(new Address.Immutable());
		}
		
		if(symbolNumber == 0)
		{
			ResourceKey<PointOfOrigin> pointOfOrigin = getPointOfOrigin();
			if(pointOfOrigin != null && !pointOfOrigin.equals(menu.blockEntity.getPointOfOrigin()))
				packet.withPointOfOrigin(pointOfOrigin);
		}
		else
		{
			ResourceKey<Symbols> symbols = getSymbols();
			if(symbols != null && !symbols.equals(menu.blockEntity.getSymbols()))
				packet.withSymbols(symbols);
		}
		
		PacketHandlerInit.INSTANCE.sendToServer(packet);
		onClose();
	}
	
	@Nullable
	public ResourceKey<Symbols> getSymbols()
	{
		ResourceKey<Symbols> symbols = SymbolPaperItem.getSymbols(menu.tempContainer.getItem(0));
		if(symbols != null)
			return symbols;
		
		return menu.blockEntity.getSymbols();
	}
	
	@Nullable
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		ResourceKey<PointOfOrigin> pointOfOrigin = SymbolPaperItem.getPointOfOrigin(menu.tempContainer.getItem(0));
		if(pointOfOrigin != null)
			return pointOfOrigin;
		
		return menu.blockEntity.getPointOfOrigin();
	}
	
	public void renderSymbol(PoseStack stack)
	{
		float xPos = (imageWidth - SYMBOL_SIZE) / 2F;
		float yPos = 1; // There's a 1 pixel thick black border around it
		
		if(symbolNumber == 0)
		{
			ClientPointOfOrigin pointOfOrigin = ClientPointOfOrigin.getPointOfOrigin(getPointOfOrigin());
			if(pointOfOrigin == null)
				return;
			
			ClientUtil.renderPointOfOrigin(stack.last().pose(), xPos, yPos, xPos + SYMBOL_SIZE, yPos + SYMBOL_SIZE, pointOfOrigin, rgba);
		}
		else if(symbolNumber > 0)
		{
			ClientSymbols symbols = ClientSymbols.getSymbols(getSymbols());
			if(symbols == null)
				return;
			
			ClientUtil.renderSymbol(stack.last().pose(), xPos, yPos, xPos + SYMBOL_SIZE, yPos + SYMBOL_SIZE, symbols, symbolNumber, rgba);
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
		
		this.itemHint(stack, leftPos + 124, topPos + 24, 176, 0, 0);
	}
	
	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
	{
		renderBackground(stack);
		super.render(stack, mouseX, mouseY, delta);
		renderTooltip(stack, mouseX, mouseY);
		
		itemTooltip(stack, mouseX, mouseY, 124, 24, 0, ComponentHelper.description("screen.sgjourney.engraving.symbol_block.insert_symbol_paper"));
	}
	
	@Override
	protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
		
		renderSymbol(poseStack);
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return !menu.tempContainer.getItem(slot).isEmpty();
	}
	
	
	
	public static class Stone extends SymbolBlockEngravingScreen<SymbolBlockEngravingMenu.Stone>
	{
		public Stone(SymbolBlockEngravingMenu.Stone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/symbol_block/stone_symbol_engraving_gui.png"), playerInventory, title, new ColorUtil.RGBA(90, 89, 90));
		}
	}
	
	public static class Sandstone extends SymbolBlockEngravingScreen<SymbolBlockEngravingMenu.Sandstone>
	{
		public Sandstone(SymbolBlockEngravingMenu.Sandstone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/symbol_block/sandstone_symbol_engraving_gui.png"), playerInventory, title, new ColorUtil.RGBA(198, 174, 113));
		}
	}
	
	public static class RedSandstone extends SymbolBlockEngravingScreen<SymbolBlockEngravingMenu.RedSandstone>
	{
		public RedSandstone(SymbolBlockEngravingMenu.RedSandstone menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/symbol_block/red_sandstone_symbol_engraving_gui.png"), playerInventory, title, new ColorUtil.RGBA(142, 71, 11));
		}
	}
}
