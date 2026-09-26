package net.povstalec.sgjourney.client.screens.graver;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.SGJourneyCycleButton;
import net.povstalec.sgjourney.client.widgets.dhd.*;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.packets.ServerboundEngravingUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class DHDEngravingScreen<M extends DHDEngravingMenu<?>> extends AbstractEngravingScreen<M>
{
	private static final int ENGRAVING_BUTTON_WIDTH = 56;
	private static final int ENGRAVING_BUTTON_HEIGHT = 20;
	
	public final List<Renderable> dhdRenderables = Lists.newArrayList();
	
	protected final ResourceLocation texture;
	
	protected final ResourceLocation dhdTexture;
	
	protected final int dhdImageWidth;
	protected final int dhdImageHeight;
	
	protected Selected selected = Selected.BOTH;
	protected SGJourneyCycleButton<Selected> selectButton;
	
	public DHDEngravingScreen(M menu, ResourceLocation texture, Inventory playerInventory, Component title,
							  ResourceLocation dhdTexture, int dhdImageWidth, int dhdImageHeight)
	{
		super(menu, playerInventory, title);
		
		this.texture = texture;
		
		this.dhdTexture = dhdTexture;
		
		this.dhdImageWidth = dhdImageWidth;
		this.dhdImageHeight = dhdImageHeight;
		
		this.imageWidth = 176;
		this.imageHeight = 222;
		
		this.inventoryLabelY = this.imageHeight - 94;
	}
	
	protected <T extends Renderable> T addDHDRenderable(T renderable)
	{
		this.dhdRenderables.add(renderable);
		return renderable;
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		this.selectButton = new SGJourneyCycleButton.Builder<Selected>(selected -> Component.empty())
			.withValues(Selected.values()).withTooltip(selected -> Tooltip.create(selected.tooltip))
			.displayOnlyValue()
			.create(StargateJourney.sgjourneyLocation("textures/gui/widgets.png"), leftPos + 27, topPos + 106, 20, 20, 10, 0, Component.empty(),
				(button, selected) ->
				{
					this.selectButton.xImageOffset = selected.ordinal();
					this.selected = selected;
				});
		updateSelectionButton();
		
		this.addRenderableWidget(this.selectButton);
		
		this.engravingButton = Button.builder(Component.translatable("screen.sgjourney.engraving.engrave"), button -> engrave())
			.bounds(leftPos + 49, topPos + 106, ENGRAVING_BUTTON_WIDTH, ENGRAVING_BUTTON_HEIGHT).build();
		
		updateEngravingButton();
		this.addRenderableWidget(this.engravingButton);
		
		this.menu.addSlotListener(new ContainerListener()
		{
			@Override
			public void slotChanged(@NotNull AbstractContainerMenu menu, int slot, @NotNull ItemStack stack)
			{
				updateEngravingButton();
				updateSelectionButton();
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
		boolean isDifferent = !Objects.equals(getPointOfOrigin(), menu.blockEntity.symbolInfo().pointOfOrigin()) ||
		!Objects.equals(getSymbols(), menu.blockEntity.symbolInfo().symbols());
		engravingButton.active = isDifferent;
		engravingButton.setTooltip(isDifferent ? null : Tooltip.create(Component.translatable("screen.sgjourney.engraving.dhd.same_symbols")));
	}
	
	public void updateSelectionButton()
	{
		if(SymbolPaperItem.getSymbols(menu.tempContainer.getItem(0)) != null && SymbolPaperItem.getPointOfOrigin(menu.tempContainer.getItem(0)) != null)
		{
			selected = Selected.BOTH;
			this.selectButton.active = true;
		}
		else if(SymbolPaperItem.getPointOfOrigin(menu.tempContainer.getItem(0)) != null)
		{
			selected = Selected.POINT_OF_ORIGIN;
			this.selectButton.active = false;
		}
		else if(SymbolPaperItem.getSymbols(menu.tempContainer.getItem(0)) != null)
		{
			selected = Selected.SYMBOLS;
			this.selectButton.active = false;
		}
		else
		{
			selected = Selected.BOTH;
			this.selectButton.active = false;
		}
		
		selectButton.setValue(selected);
		this.selectButton.xImageOffset = selected.ordinal();
	}
	
	public void engrave()
	{
		ServerboundEngravingUpdatePacket packet = new ServerboundEngravingUpdatePacket(menu.blockEntity.getBlockPos());
		
		ResourceKey<PointOfOrigin> pointOfOrigin = getPointOfOrigin();
		if(pointOfOrigin != null && !pointOfOrigin.equals(menu.blockEntity.symbolInfo().pointOfOrigin()))
			packet.withPointOfOrigin(pointOfOrigin);
		
		ResourceKey<Symbols> symbols = getSymbols();
		if(symbols != null && !symbols.equals(menu.blockEntity.symbolInfo().symbols()))
			packet.withSymbols(symbols);
		
		PacketHandlerInit.INSTANCE.sendToServer(packet);
		onClose();
	}
	
	@Nullable
	public ResourceKey<Symbols> getSymbols()
	{
		if(selected.engraveSymbols)
		{
			ResourceKey<Symbols> symbols = SymbolPaperItem.getSymbols(menu.tempContainer.getItem(0));
			if(symbols != null)
				return symbols;
		}
		
		return menu.blockEntity.symbolInfo().symbols();
	}
	
	@Nullable
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		if(selected.engravePointOfOrigin)
		{
			ResourceKey<PointOfOrigin> pointOfOrigin = SymbolPaperItem.getPointOfOrigin(menu.tempContainer.getItem(0));
			if(pointOfOrigin != null)
				return pointOfOrigin;
		}
		
		return menu.blockEntity.symbolInfo().pointOfOrigin();
	}
	
	@Override
	protected void renderBg(@NotNull PoseStack stack, float partialTick, int mouseX, int mouseY)
	{
		this.renderBackground(stack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		this.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		stack.pushPose();
		stack.translate(leftPos + imageWidth / 2F, topPos + 55, 0);
		stack.scale(0.5F, 0.5F, 0.5F);
		
		RenderSystem.setShaderTexture(0, dhdTexture);
		this.blit(stack, -dhdImageWidth / 2, -dhdImageHeight / 2, 0, 0, dhdImageWidth, dhdImageHeight);
		
		for(Renderable renderable : this.dhdRenderables)
		{
			renderable.render(stack, mouseX, mouseY, partialTick);
		}
		
		stack.popPose();
		
		this.itemHint(stack, leftPos + 8, topPos + 108, 176, 0, 0);
	}
	
	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
	{
		renderBackground(stack);
		super.render(stack, mouseX, mouseY, delta);
		renderTooltip(stack, mouseX, mouseY);
		
		itemTooltip(stack, mouseX, mouseY, 8, 108, 0, ComponentHelper.description("screen.sgjourney.engraving.dhd.insert_symbol_paper"));
	}
	
	@Override
	protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
		
		//renderSymbol(poseStack);
	}
	
	@Override
	protected boolean hasItem(int slot)
	{
		return !menu.tempContainer.getItem(slot).isEmpty();
	}
	
	
	
	public static class Universe extends DHDEngravingScreen<DHDEngravingMenu.Universe>
	{
		public Universe(DHDEngravingMenu.Universe menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/dhd/universe_dhd_engraving_gui.png"), playerInventory, title,
				StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_background.png"), 192, 192);
		}
		
		@Override
		protected void init()
		{
			super.init();
			
			addDHDRenderable(new UniverseDHDBigButton.Engraving(69 - dhdImageWidth / 2, 69 - dhdImageHeight / 2, menu, (n) -> {}));
			
			GenericDHDSymbolButton.DefaultButton defaultButton;
			for(int i = 0; i < 39; i++)
			{
				defaultButton = GenericDHDSymbolButton.DefaultButton.values()[i];
				addDHDRenderable(new UniverseDHDSymbolButton.Engraving(-dhdImageWidth / 2, -dhdImageHeight / 2, this, width, height, i, UniverseDHDSymbolButton.CANON_SYMBOLS[i], defaultButton, button -> {}));
			}
		}
	}
	
	public static class MilkyWay extends DHDEngravingScreen<DHDEngravingMenu.MilkyWay>
	{
		public MilkyWay(DHDEngravingMenu.MilkyWay menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/dhd/milky_way_dhd_engraving_gui.png"), playerInventory, title,
				StargateJourney.sgjourneyLocation("textures/gui/dhd/milky_way/milky_way_dhd_background.png"), 192, 192);
		}
		
		@Override
		protected void init()
		{
			super.init();
			
			addDHDRenderable(new MilkyWayDHDBigButton.Engraving(69 - dhdImageWidth / 2, 69 - dhdImageHeight / 2, menu, (n) -> {}));
			
			GenericDHDSymbolButton.DefaultButton defaultButton;
			for(int i = 0; i < 39; i++)
			{
				defaultButton = GenericDHDSymbolButton.DefaultButton.values()[i];
				addDHDRenderable(new MilkyWayDHDSymbolButton.Engraving(-dhdImageWidth / 2, -dhdImageHeight / 2, this, width, height, i, MilkyWayDHDSymbolButton.CANON_SYMBOLS[i], defaultButton, button -> {}));
			}
		}
	}
	
	public static class Classic extends DHDEngravingScreen<DHDEngravingMenu.Classic>
	{
		public Classic(DHDEngravingMenu.Classic menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/dhd/classic_dhd_engraving_gui.png"), playerInventory, title,
				StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_background.png"), 192, 192);
		}
		
		@Override
		protected void init()
		{
			super.init();
			
			addDHDRenderable(new ClassicDHDBigButton.Engraving(69 - dhdImageWidth / 2, 69 - dhdImageHeight / 2, menu, (n) -> {}));
			
			GenericDHDSymbolButton.DefaultButton defaultButton;
			for(int i = 0; i < 39; i++)
			{
				defaultButton = GenericDHDSymbolButton.DefaultButton.values()[i];
				addDHDRenderable(new ClassicDHDSymbolButton.Engraving(-dhdImageWidth / 2, -dhdImageHeight / 2, this, width, height, i, ClassicDHDSymbolButton.CANON_SYMBOLS[i], defaultButton, button -> {}));
			}
		}
	}
}
