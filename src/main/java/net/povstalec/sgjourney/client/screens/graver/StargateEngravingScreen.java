package net.povstalec.sgjourney.client.screens.graver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.block_entity.AbstractStargateModel;
import net.povstalec.sgjourney.client.models.block_entity.ClassicStargateModel;
import net.povstalec.sgjourney.client.models.block_entity.MilkyWayStargateModel;
import net.povstalec.sgjourney.client.models.block_entity.UniverseStargateModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClassicStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.MilkyWayStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.UniverseStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.graver.StargateEngravingMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.packets.ServerboundEngravingUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class StargateEngravingScreen<M extends StargateEngravingMenu<?>, SG extends AbstractStargateModel<?, ?>> extends SGJourneyContainerScreen<M>
{
	public static final int MAX_LIGHT = 15728880;
	
	private static final int ENGRAVING_BUTTON_WIDTH = 56;
	private static final int ENGRAVING_BUTTON_HEIGHT = 20;
	
	protected final ResourceLocation texture;
	
	protected SG stargateModel;
	
	protected Button engravingButton;
	
	public StargateEngravingScreen(M menu, ResourceLocation texture, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		this.texture = texture;
		
		this.imageWidth = 176;
		this.imageHeight = 222;
		
		this.inventoryLabelY = this.imageHeight - 94;
	}
	
	protected abstract SG createStargateModel();
	
	@Override
	protected void init()
	{
		super.init();
		
		stargateModel = createStargateModel();
		
		this.engravingButton = Button.builder(Component.translatable("screen.sgjourney.engraving.engrave"), button -> engrave())
			.bounds(leftPos + 88 - ENGRAVING_BUTTON_WIDTH / 2, topPos + 70 - ENGRAVING_BUTTON_HEIGHT / 2, ENGRAVING_BUTTON_WIDTH, ENGRAVING_BUTTON_HEIGHT).build();
		
		updateEngravingButton();
		this.addRenderableWidget(this.engravingButton);
		
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
		boolean isDifferent = !Objects.equals(getPointOfOrigin(), menu.blockEntity.symbolInfo().pointOfOrigin()) ||
		!Objects.equals(getSymbols(), menu.blockEntity.symbolInfo().symbols());
		engravingButton.active = isDifferent;
		engravingButton.setTooltip(isDifferent ? null : Tooltip.create(Component.translatable("screen.sgjourney.engraving.stargate.same_symbols")));
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
		ResourceKey<Symbols> symbols = SymbolPaperItem.getSymbols(menu.tempContainer.getItem(0));
		if(symbols != null)
			return symbols;
		
		return menu.blockEntity.symbolInfo().symbols();
	}
	
	@Nullable
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		ResourceKey<PointOfOrigin> pointOfOrigin = SymbolPaperItem.getPointOfOrigin(menu.tempContainer.getItem(0));
		if(pointOfOrigin != null)
			return pointOfOrigin;
		
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
		
		this.itemHint(stack, leftPos + 80, topPos + 81, 176, 0, 0);
	}
	
	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
	{
		renderBackground(stack);
		super.render(stack, mouseX, mouseY, delta);
		renderTooltip(stack, mouseX, mouseY);
		
		itemTooltip(stack, mouseX, mouseY, 80, 81, 0, ComponentHelper.description("screen.sgjourney.engraving.symbol_block.insert_symbol_paper"));
		
		stack.pushPose();
		stack.translate(leftPos + imageWidth / 2F, topPos + 71, 0);
		stack.scale(-16, -16, -16);
		stack.mulPose(Axis.YP.rotationDegrees(-180));
		
		MultiBufferSource.BufferSource source = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		renderStargate(stack, source, mouseX, mouseY, delta);
		source.endBatch();
		
		stack.popPose();
	}
	
	protected abstract void renderStargate(PoseStack stack, MultiBufferSource source, int mouseX, int mouseY, float partialTick);
	
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
	
	
	
	public static class Universe extends StargateEngravingScreen<StargateEngravingMenu.Universe, UniverseStargateModel>
	{
		public Universe(StargateEngravingMenu.Universe menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/stargate/universe_stargate_engraving_gui.png"), playerInventory, title);
		}
		
		protected UniverseStargateModel createStargateModel()
		{
			return new UniverseStargateModel()
			{
				@Override
				protected @Nullable ClientPointOfOrigin getPointOfOrigin(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant)
				{
					if(stargateVariant.symbols().permanentPointOfOrigin().isPresent())
						return ClientPointOfOrigin.getPointOfOrigin(stargateVariant.symbols().permanentPointOfOrigin().get());
					else
						return ClientPointOfOrigin.getPointOfOrigin(Universe.this.getPointOfOrigin());
				}
				
				@Override
				protected @Nullable ClientSymbols getSymbols(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant)
				{
					if(stargateVariant.symbols().permanentSymbols().isPresent())
						return ClientSymbols.getSymbols(stargateVariant.symbols().permanentSymbols().get());
					else
						return ClientSymbols.getSymbols(Universe.this.getSymbols());
				}
			};
		}
		
		@Override
		public void renderStargate(PoseStack stack, MultiBufferSource source, int mouseX, int mouseY, float partialTick)
		{
			stargateModel.renderStargate(menu.blockEntity, stargateModel.getClientVariant(menu.blockEntity), partialTick, stack, source, MAX_LIGHT, OverlayTexture.NO_OVERLAY);
		}
	}
	
	public static class MilkyWay extends StargateEngravingScreen<StargateEngravingMenu.MilkyWay, MilkyWayStargateModel>
	{
		public MilkyWay(StargateEngravingMenu.MilkyWay menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/stargate/milky_way_stargate_engraving_gui.png"), playerInventory, title);
		}
		
		protected MilkyWayStargateModel createStargateModel()
		{
			return new MilkyWayStargateModel()
			{
				@Override
				protected @Nullable ClientPointOfOrigin getPointOfOrigin(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
				{
					if(stargateVariant.symbols().permanentPointOfOrigin().isPresent())
						return ClientPointOfOrigin.getPointOfOrigin(stargateVariant.symbols().permanentPointOfOrigin().get());
					else
						return ClientPointOfOrigin.getPointOfOrigin(MilkyWay.this.getPointOfOrigin());
				}
				
				@Override
				protected @Nullable ClientSymbols getSymbols(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
				{
					if(stargateVariant.symbols().permanentSymbols().isPresent())
						return ClientSymbols.getSymbols(stargateVariant.symbols().permanentSymbols().get());
					else
						return ClientSymbols.getSymbols(MilkyWay.this.getSymbols());
				}
			};
		}
		
		@Override
		public void renderStargate(PoseStack stack, MultiBufferSource source, int mouseX, int mouseY, float partialTick)
		{
			stargateModel.renderStargate(menu.blockEntity, stargateModel.getClientVariant(menu.blockEntity), partialTick, stack, source, MAX_LIGHT, OverlayTexture.NO_OVERLAY);
		}
	}
	
	public static class Classic extends StargateEngravingScreen<StargateEngravingMenu.Classic, ClassicStargateModel>
	{
		public Classic(StargateEngravingMenu.Classic menu, Inventory playerInventory, Component title)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/engraving/stargate/classic_stargate_engraving_gui.png"), playerInventory, title);
		}
		
		@Override
		protected ClassicStargateModel createStargateModel()
		{
			return new ClassicStargateModel()
			{
				@Override
				protected @Nullable ClientPointOfOrigin getPointOfOrigin(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant)
				{
					if(stargateVariant.symbols().permanentPointOfOrigin().isPresent())
						return ClientPointOfOrigin.getPointOfOrigin(stargateVariant.symbols().permanentPointOfOrigin().get());
					else
						return ClientPointOfOrigin.getPointOfOrigin(Classic.this.getPointOfOrigin());
				}
				
				@Override
				protected @Nullable ClientSymbols getSymbols(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant)
				{
					if(stargateVariant.symbols().permanentSymbols().isPresent())
						return ClientSymbols.getSymbols(stargateVariant.symbols().permanentSymbols().get());
					else
						return ClientSymbols.getSymbols(Classic.this.getSymbols());
				}
			};
		}
		
		@Override
		public void renderStargate(PoseStack stack, MultiBufferSource source, int mouseX, int mouseY, float partialTick)
		{
			stargateModel.renderStargate(menu.blockEntity, stargateModel.getClientVariant(menu.blockEntity), partialTick, stack, source, MAX_LIGHT, OverlayTexture.NO_OVERLAY);
		}
	}
}
