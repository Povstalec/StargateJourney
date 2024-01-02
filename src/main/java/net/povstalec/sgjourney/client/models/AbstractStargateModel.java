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
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class AbstractStargateModel<StargateEntity extends AbstractStargateEntity>
{
	protected static final float DEFAULT_RADIUS = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	protected static final float DEFAULT_RING_HEIGHT = 1F;
	protected static final float STARGATE_RING_SHRINK = 0.001F;
	
	protected static final float DEFAULT_ANGLE = 360F / DEFAULT_SIDES;
	protected static final float NUMBER_OF_CHEVRONS = 9;
	protected static final float CHEVRON_ANGLE = 360F / 9;
	
	protected static final int MAX_LIGHT = 15728864;
	
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	public static final ResourceLocation EMPTY_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/empty.png");
	public static final String EMPTY = StargateJourney.EMPTY;

	private static final int[] dialed9ChevronConfiguration = new int[] {0, 1, 2, 3, 7, 8, 4, 5, 6};
	private static final int[] dialed8ChevronConfiguration = new int[] {0, 1, 2, 3, 7, 4, 5, 6};
	
	/*
	 * X = Width
	 * Y = Height
	 * Z = Thickness
	 * 
	 * When viewing "(x_offset, y_offset, z_offset, x_size, y_size, z_size)":
	 * 
	 * If there is "-CONSTANT / 2" in the place of offset, it is used for centering
	 */
	
	private static Minecraft minecraft = Minecraft.getInstance();
	
	protected final String namespace;
	protected final String name;
	
	protected final ResourceLocation stargateTexture;
	protected final ResourceLocation engagedTexture;
	
	protected Stargate.RGBA symbolColor = Stargate.RGBA.DEFAULT_RGBA;
	protected Stargate.RGBA engagedSymbolColor = Stargate.RGBA.DEFAULT_RGBA;
	
	public AbstractStargateModel(ResourceLocation stargateName)
	{
		namespace = stargateName.getNamespace();
		name = stargateName.getPath();
		
		stargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate.png");
		engagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_engaged.png");
	}
	
	public ResourceLocation getResourceLocation()
	{
		return new ResourceLocation(namespace, name);
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
	
	protected ResourceLocation getSymbolTexture(AbstractStargateEntity stargate, int symbol)
	{
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<Symbols> symbolRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(symbol > 0)
		{
			String symbols = stargate.getSymbols();
			
			if(isLocationValid(symbols) && symbolRegistry.containsKey(new ResourceLocation(symbols)))
				return symbolRegistry.get(new ResourceLocation(symbols)).texture(symbol - 1);
			
			else if(symbols.equals(EMPTY))
				return EMPTY_LOCATION;
			
			return ERROR_LOCATION;
		}
		else
		{
			String pointOfOrigin = stargate.getPointOfOrigin();
			
			if(isLocationValid(pointOfOrigin) && pointOfOriginRegistry.containsKey(new ResourceLocation(pointOfOrigin)))
				return pointOfOriginRegistry.get(new ResourceLocation(pointOfOrigin)).texture();
			
			else if(pointOfOrigin.equals(EMPTY))
				return EMPTY_LOCATION;
			
			return ERROR_LOCATION;
		}
	}
	
	protected Stargate.RGBA getSymbolColor(StargateEntity stargate, boolean isEngaged)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
		{
			if(!isEngaged && variant.get().getSymbolRGBA().isPresent())
				return variant.get().getSymbolRGBA().get();
			else if(isEngaged && variant.get().getEngagedSymbolRGBA().isPresent())
				return variant.get().getEngagedSymbolRGBA().get();
		}
		
		return this.symbolColor;
	}
	
	public static int getChevronConfiguration(boolean defaultOrder, int addresslength, int chevron)
	{
		int[] configuration;
		
		if(defaultOrder)
			return chevron;
		else
		{
			switch(addresslength)
			{
			case 7:
				configuration = dialed8ChevronConfiguration;
				break;
			case 8:
				configuration = dialed9ChevronConfiguration;
				break;
			default:
				return chevron;
			}
		}
		
		if(chevron >= configuration.length)
			return 0;
		
		int returned = configuration[chevron];
		return returned;
	}
	
	protected ResourceLocation getStargateTexture(StargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
			return variant.get().getTexture();
		
		return this.stargateTexture;
	}
	
	protected ResourceLocation getEngagedTexture(StargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
			return variant.get().getEngagedTexture();
		
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
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture(stargate)));
		this.renderRing(stargate, partialTick, stack, consumer, source, combinedLight, combinedOverlay);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}

	public abstract void renderRing(StargateEntity stargate, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay);
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	protected boolean isPrimaryChevronRaised(StargateEntity stargate)
	{
		return false;
	}
	
	protected boolean isPrimaryChevronLowered(StargateEntity stargate)
	{
		return false;
	}
	
	protected boolean isPrimaryChevronEngaged(StargateEntity stargate)
	{
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	protected boolean isChevronRaised(StargateEntity stargate, int chevronNumber)
	{
		return false;
	}
	
	protected boolean isChevronLowered(StargateEntity stargate, int chevronNumber)
	{
		return false;
	}
	
	protected boolean isChevronEngaged(StargateEntity stargate, int chevronNumber)
	{
		return stargate.chevronsRendered() >= chevronNumber;
	}

	protected abstract void renderPrimaryChevron(StargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged);
	
	protected abstract void renderChevron(StargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged);
	
	protected void renderChevrons(StargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		// Renders Chevrons
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.chevron(getStargateTexture(stargate)));
				
		renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, false);
		for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
		{
			renderChevron(stargate, stack, consumer, source, combinedLight, chevronNumber, false);
		}
		
		// Renders lit up parts of Chevrons
		consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(getEngagedTexture(stargate)));
		
		if(isPrimaryChevronEngaged(stargate))
			renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, true);
		for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
		{
			if(isChevronEngaged(stargate, chevronNumber))
				renderChevron(stargate, stack, consumer, source, combinedLight, chevronNumber, true);
		}
	}
}
