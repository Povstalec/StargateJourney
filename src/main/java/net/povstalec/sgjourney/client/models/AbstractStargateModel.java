package net.povstalec.sgjourney.client.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class AbstractStargateModel
{
	protected static final float DEFAULT_DISTANCE = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	protected static final float STARGATE_RING_X_SHRINK = 0.001F;
	
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

	protected static final float DEFAULT_DISTANCE_FROM_CENTER = 56.0F;
	protected static final int BOXES_PER_RING = 36;
	
	protected static final float CHEVRON_LIGHT_ANGLE = 15.0F;
	protected static final float CHEVRON_LIGHT_Z = 4.0F;
	protected static final float CHEVRON_LIGHT_Z_OFFSET = 0.5F;
	protected static final float CHEVRON_BACK_LIGHT_Z = CHEVRON_LIGHT_Z + 1;
	
	protected static final float CHEVRON_LIGHT_TOP_X = 6.0F;
	protected static final float CHEVRON_LIGHT_TOP_Y = 1.0F;

	protected static final float CHEVRON_LIGHT_CENTER_X = 3.0F;
	protected static final float CHEVRON_LIGHT_CENTER_Y = 6.0F;
	
	protected static final float CHEVRON_LIGHT_SIDE_X = 2.0F;
	protected static final float CHEVRON_LIGHT_SIDE_Y = 6.1847F;
	
	protected static final float OUTER_CHEVRON_ANGLE = 22.5F;
	protected static final float OUTER_CHEVRON_Z_OFFSET = 3.5F;
	
	protected static final float OUTER_CHEVRON_CENTER_X = 4.0F;
	protected static final float OUTER_CHEVRON_CENTER_Y = 2.0F;
	protected static final float OUTER_CHEVRON_CENTER_Z = 1.0F;
	
	protected static final float OUTER_CHEVRON_SIDE_X = 2.0F;
	protected static final float OUTER_CHEVRON_SIDE_Y = 10.0F;
	protected static final float OUTER_CHEVRON_SIDE_Z = 1.0F;

	protected static final float OUTER_CHEVRON_Y_OFFSET = CHEVRON_LIGHT_CENTER_Y + 2.0F + OUTER_CHEVRON_CENTER_Y; // The + 2.0F is there because there's a 2 pixel gap between the Chevron Light and Outer Chevron
	
	protected static final float SYMBOL_RING_X = 8.0F;
	protected static final float SYMBOL_RING_Y = 8.0F;
	protected static final float SYMBOL_RING_Z = 2.0F;
	protected static final float SYMBOL_RING_Y_OFFSET = SYMBOL_RING_Y - 2.0F; // The - 2.0F is there because the Symbol Ring is slightly bigger than the open space created by the rest of the ring, to prevent players from being able to see through when it's rotating
	protected static final float SYMBOL_RING_Z_OFFSET = 0.5F;
	protected static final float DIVIDER_X = 1.0F;
	
	protected String stargateName;
	protected ResourceLocation stargateTexture;
	protected ResourceLocation engagedTexture;
	
	public AbstractStargateModel(String stargateName)
	{
		this.stargateName = stargateName;
		
		stargateTexture = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/" + stargateName + "/" + stargateName +"_stargate.png");
		engagedTexture = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/" + stargateName + "/" + stargateName +"_stargate_engaged.png");
	}
	
	protected ResourceLocation getSymbolTexture(AbstractStargateEntity stargate, int symbol)
	{
		Minecraft minecraft = Minecraft.getInstance();
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
	
	protected ResourceLocation getStargateTexture()
	{
		return this.stargateTexture;
	}
	
	protected ResourceLocation getEngagedTexture()
	{
		return this.engagedTexture;
	}
	
	private boolean isLocationValid(String location)
	{
		String[] split = location.split(":");
		
		if(split.length > 2)
			return false;
		
		if(!ResourceLocation.isValidNamespace(split[0]))
			return false;
		
		return ResourceLocation.isValidPath(split[1]);
	}
}
