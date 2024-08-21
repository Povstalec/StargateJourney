package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class AbstractStargateModel<StargateEntity extends AbstractStargateEntity, Variant extends ClientStargateVariant>
{
	protected static final float DEFAULT_RADIUS = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	protected static final float DEFAULT_RING_HEIGHT = 1F;
	protected static final float STARGATE_RING_SHRINK = 0.001F;
	
	protected static final float DEFAULT_ANGLE = 360F / DEFAULT_SIDES;
	protected static final float NUMBER_OF_CHEVRONS = 9;
	protected static final float CHEVRON_ANGLE = 360F / 9;
	
	protected static final int MAX_LIGHT = 15728880;
	
	protected static final int DEFAULT_TEXTURE_SIZE = 64;
	
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	public static final ResourceLocation EMPTY_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/empty.png");
	public static final String EMPTY = StargateJourney.EMPTY;
	
	private static Minecraft minecraft = Minecraft.getInstance();
	
	protected final String namespace;
	protected final String name;
	
	protected final ResourceLocation stargateTexture;
	protected final ResourceLocation engagedTexture;
	
	protected Stargate.RGBA symbolColor = Stargate.RGBA.DEFAULT_RGBA;
	protected Stargate.RGBA engagedSymbolColor = Stargate.RGBA.DEFAULT_RGBA;
	
	protected final short numberOfSymbols;
	
	public AbstractStargateModel(ResourceLocation stargateName, short numberOfSymbols)
	{
		namespace = stargateName.getNamespace();
		name = stargateName.getPath();
		
		stargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate.png");
		engagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_engaged.png");
		
		this.numberOfSymbols = numberOfSymbols;
	}
	
	public ResourceLocation getResourceLocation()
	{
		return new ResourceLocation(namespace, name + "_stargate");
	}
	
	public boolean canUseVariant(StargateVariant variant)
	{
		return variant.getBaseStargate().equals(getResourceLocation());
	}
	
	public static Optional<StargateVariant> getVariant(AbstractStargateEntity stargate)
	{
		Optional<StargateVariant> optional = Optional.empty();
		
		if(!ClientStargateConfig.stargate_variants.get())
			return optional;
		
		String variantString = stargate.getVariant();
		
		if(variantString.equals(EMPTY))
			return optional;
		
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<StargateVariant> variantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
		
		optional = Optional.ofNullable(variantRegistry.get(new ResourceLocation(variantString)));
		
		return optional;
	}
	
	protected Optional<StargateVariant> getStargateVariant(StargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = AbstractStargateModel.getVariant(stargate);
		
		if(stargateVariant.isPresent() && this.canUseVariant(stargateVariant.get()))
			return stargateVariant;
		
		return Optional.empty();
	}
	
	protected Optional<PointOfOrigin> getPointOfOrigin(AbstractStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(stargateVariant.isPresent() && stargateVariant.get().getPointOfOrigin().isPresent()
				&& pointOfOriginRegistry.containsKey(stargateVariant.get().getPointOfOrigin().get()))
			return Optional.of(pointOfOriginRegistry.get(stargateVariant.get().getPointOfOrigin().get()));
		else
		{
			String pointOfOrigin = stargate.getPointOfOrigin();
			
			if(isLocationValid(pointOfOrigin) && pointOfOriginRegistry.containsKey(new ResourceLocation(pointOfOrigin)))
				return Optional.of(pointOfOriginRegistry.get(new ResourceLocation(pointOfOrigin)));
		}
		
		return Optional.empty();
	}
	
	protected ResourceLocation getPointOfOriginTexture(Optional<PointOfOrigin> pointOfOrigin)
	{
		if(pointOfOrigin.isPresent())
			return pointOfOrigin.get().texture();
		
		return EMPTY_LOCATION;
	}
	
	protected Optional<Symbols> getSymbols(AbstractStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<Symbols> symbolRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		if(stargateVariant.isPresent() && stargateVariant.get().getSymbols().isPresent()
				&& symbolRegistry.containsKey(stargateVariant.get().getSymbols().get()))
			return Optional.of(symbolRegistry.get(stargateVariant.get().getSymbols().get()));
		else
		{
			String symbols = stargate.getSymbols();
			
			if(isLocationValid(symbols) && symbolRegistry.containsKey(new ResourceLocation(symbols)))
				return Optional.of(symbolRegistry.get(new ResourceLocation(symbols)));
		}
		
		return Optional.empty();
	}
	
	protected ResourceLocation getSymbolTexture(Optional<Symbols> symbols)
	{
		if(symbols.isPresent())
			return symbols.get().getSymbolTexture();
		
		return EMPTY_LOCATION;
	}
	
	protected ResourceLocation getStargateTexture(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
			return stargateVariant.get().getTexture();
		
		return this.stargateTexture;
	}
	
	protected ResourceLocation getEngagedTexture(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
			return stargateVariant.get().getEngagedTexture();
		
		return this.engagedTexture;
	}
	
	private boolean isLocationValid(String location)
	{
		String[] split = location.split(":");
		
		if(split.length != 2)
			return false;
		
		if(!ResourceLocation.isValidNamespace(split[0]))
			return false;
		
		return ResourceLocation.isValidPath(split[1]);
	}
	
	//============================================================================================
	//******************************************Rendering*****************************************
	//============================================================================================
	
	/**
	 * Renders the Stargate. By default (no methods are overridden), the resulting rendered Stargate will be a generic model (a mix between the Milky Way and Pegasus Stargate)
	 * @param stargate Stargate Entity being rendered
	 * @param partialTick Partial Tick
	 * @param stack Pose Stack
	 * @param source Multi Buffer Source
	 * @param combinedLight Combined Light
	 * @param combinedOverlay Combined Overlay
	 */
	public void renderStargate(StargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		Optional<StargateVariant> stargateVariant = getStargateVariant(stargate);
		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture(stargate, stargateVariant)));
		this.renderRing(stargate, stargateVariant, partialTick, stack, consumer, source, combinedLight, combinedOverlay);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	}

	public abstract void renderRing(StargateEntity stargate, Optional<StargateVariant> stargateVariant, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay);
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	protected boolean isPrimaryChevronRaised(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		return false;
	}
	
	protected boolean isPrimaryChevronLowered(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		return false;
	}
	
	protected boolean isPrimaryChevronEngaged(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	protected boolean isChevronRaised(StargateEntity stargate, Optional<StargateVariant> stargateVariant, int chevronNumber)
	{
		return false;
	}
	
	protected boolean isChevronLowered(StargateEntity stargate, Optional<StargateVariant> stargateVariant, int chevronNumber)
	{
		return false;
	}
	
	protected boolean isChevronEngaged(StargateEntity stargate, Optional<StargateVariant> stargateVariant, int chevronNumber)
	{
		return stargate.chevronsRendered() >= chevronNumber;
	}

	protected abstract void renderPrimaryChevron(StargateEntity stargate, Optional<StargateVariant> stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged);
	
	protected abstract void renderChevron(StargateEntity stargate, Optional<StargateVariant> stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged);
	
	protected void renderChevrons(StargateEntity stargate, Optional<StargateVariant> stargateVariant, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer;
		
		if(StargateJourney.isOculusLoaded())
		{
			// Renders lit up parts of Chevrons
			consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(getEngagedTexture(stargate, stargateVariant)));
			
			if(isPrimaryChevronEngaged(stargate, stargateVariant))
				renderPrimaryChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, true);
			for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
			{
				if(isChevronEngaged(stargate, stargateVariant, chevronNumber))
					renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, chevronNumber, true);
			}
		}
		// Renders Chevrons
		consumer = source.getBuffer(SGJourneyRenderTypes.chevron(getStargateTexture(stargate, stargateVariant)));
				
		renderPrimaryChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, false);
		for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
		{
			renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, chevronNumber, false);
		}
		
		if(!StargateJourney.isOculusLoaded())
		{
			// Renders lit up parts of Chevrons
			consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(getEngagedTexture(stargate, stargateVariant)));
			
			if(isPrimaryChevronEngaged(stargate, stargateVariant))
				renderPrimaryChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, true);
			for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
			{
				if(isChevronEngaged(stargate, stargateVariant, chevronNumber))
					renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, chevronNumber, true);
			}
		}
	}

	//============================================================================================
	//******************************************Symbols*******************************************
	//============================================================================================
	
	/*protected boolean symbolsGlow(StargateEntity stargate, ClientStargateVariant stargateVariant, boolean isEngaged)
	{
		if(isEngaged)
			return stargate.isConnected() ? stargateVariant.symbols().engagedSymbolsGlow() : stargateVariant.symbols().encodedSymbolsGlow();
		else
			return stargateVariant.symbols().symbolsGlow();
	}*/
	
	protected boolean symbolsGlow(StargateEntity stargate, Optional<StargateVariant> stargateVariant, boolean isEngaged)
	{
		if(stargateVariant.isPresent())
		{
			if(isEngaged)
			{
				if(!stargate.isConnected() && stargateVariant.get().encodedSymbolsGlow().isPresent())
					return stargateVariant.get().encodedSymbolsGlow().get();
				
				else if(stargateVariant.get().engagedSymbolsGlow().isPresent())
					return stargateVariant.get().engagedSymbolsGlow().get();
			}
			
			if(!isEngaged && stargateVariant.get().symbolsGlow().isPresent())
				return stargateVariant.get().symbolsGlow().get();
		}
		
		return false;
	}
	
	protected boolean engageEncodedSymbols(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
		{
			if(stargateVariant.get().engageEncodedSymbols().isPresent())
				return stargateVariant.get().engageEncodedSymbols().get();
		}
		
		return false;
	}
	
	protected boolean engageSymbolsOnIncoming(StargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
		{
			if(stargateVariant.get().engageSymbolsOnIncoming().isPresent())
				return stargateVariant.get().engageSymbolsOnIncoming().get();
		}
		
		return false;
	}
	
	protected ColorUtil.IntRGBA getSymbolColor(StargateEntity stargate, ClientStargateVariant stargateVariant, Stargate.ChevronSymbolState state, boolean incoming)
	{
		if(state == Stargate.ChevronSymbolState.OFF)
			return stargateVariant.symbols().symbolColor();
		else if(state == Stargate.ChevronSymbolState.ENGAGED)
			return stargateVariant.symbols().engagedSymbolColor(); // TODO Split for incoming
		else
			return stargateVariant.symbols().encodedSymbolColor(); // TODO Split for incoming
	}
	
	protected Stargate.RGBA getSymbolColor(StargateEntity stargate, Optional<StargateVariant> stargateVariant, boolean isEngaged)
	{
		if(stargateVariant.isPresent())
		{
			if(isEngaged)
			{
				if(!stargate.isConnected() && stargateVariant.get().getEncodedSymbolRGBA().isPresent())
					return stargateVariant.get().getEncodedSymbolRGBA().get();
				
				else if(stargateVariant.get().getEngagedSymbolRGBA().isPresent())
					return stargateVariant.get().getEngagedSymbolRGBA().get();
			}
			
			else if(!isEngaged && stargateVariant.get().getSymbolRGBA().isPresent())
				return stargateVariant.get().getSymbolRGBA().get();
		}
		
		return this.symbolColor;
	}
}
