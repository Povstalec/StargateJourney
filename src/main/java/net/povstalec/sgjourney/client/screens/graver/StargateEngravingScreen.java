package net.povstalec.sgjourney.client.screens.graver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
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
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.graver.StargateEngravingMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class StargateEngravingScreen<M extends StargateEngravingMenu<?>, SG extends AbstractStargateModel<?, ?>> extends SGJourneyContainerScreen<M>
{
	public static final int MAX_LIGHT = 15728880;
	
	protected final ResourceLocation texture;
	
	protected SG stargateModel;
	
	public StargateEngravingScreen(M menu, ResourceLocation texture, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		this.texture = texture;
		
		this.imageWidth = 176;
		this.imageHeight = 176;
		
		this.inventoryLabelY = this.imageHeight - 94;
	}
	
	protected abstract SG createStargateModel();
	
	@Override
	protected void init()
	{
		super.init();
		
		stargateModel = createStargateModel();
		
		
		/*BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
		BakedModel model = dispatcher.getBlockModel(menu.blockEntity.getBlockState());
		if(model instanceof SymbolBlockBakedModel symbolBlockModel)
			this.rgba = new ColorUtil.RGBA(symbolBlockModel.getSymbolTint());
		
		this.gravingButton = Button.builder(Component.translatable("screen.sgjourney.graving.engrave"), button -> engrave())
			.bounds(leftPos + 121, topPos + 45, 56, 20).build();
		
		updateGravingButton();
		this.addRenderableWidget(this.gravingButton);
		
		this.editBox = new EditBox(font, leftPos + 2, topPos + 44, 52, 20, Component.translatable("tooltip.sgjourney.symbol"));
		this.editBox.setFilter(StargateEngravingScreen::canParseAsPositiveNumber);
		
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
				updateGravingButton();
			}
			else
			{
				int parsedNumber = Integer.parseInt(text);
				
				if(parsedNumber < Address.MIN_SYMBOL || parsedNumber > Address.MAX_SYMBOL)
				{
					symbolNumber = -1;
					gravingButton.active = false;
					gravingButton.setTooltip(Tooltip.create(Component.translatable("screen.sgjourney.graving.symbol_block.out_of_bounds")));
				}
				else
				{
					symbolNumber = parsedNumber;
					updateGravingButton();
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
				updateGravingButton();
			}
			
			@Override
			public void dataChanged(@NotNull AbstractContainerMenu menu, int slot, int dataSlot)
			{
				updateGravingButton();
			}
		});*/
	}
	
	/*public void updateGravingButton()
	{
		boolean isSymbolDifferent = symbolNumber != menu.blockEntity.getSymbolNumber() ||
			(symbolNumber == 0 ?
				!Objects.equals(getPointOfOrigin(), menu.blockEntity.getPointOfOrigin()) :
				!Objects.equals(getSymbols(), menu.blockEntity.getSymbols()));
		gravingButton.active = isSymbolDifferent;
		gravingButton.setTooltip(isSymbolDifferent ? null : Tooltip.create(Component.translatable("screen.sgjourney.graving.symbol_block.same_symbol")));
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
		ServerboundGravingUpdatePacket packet = new ServerboundGravingUpdatePacket(menu.blockEntity.getBlockPos());
		
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
	}*/
	
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
	
	/*public void renderSymbol(PoseStack stack)
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
	}*/
	
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
		
		itemTooltip(stack, mouseX, mouseY, 124, 24, 0, ComponentHelper.description("screen.sgjourney.graving.symbol_block.insert_symbol_paper"));
		
		stack.pushPose();
		stack.translate(leftPos + imageWidth / 2F, topPos + 40, 0);
		stack.scale(-10, -10, -10);
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
