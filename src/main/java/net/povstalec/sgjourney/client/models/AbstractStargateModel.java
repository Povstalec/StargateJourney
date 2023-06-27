package net.povstalec.sgjourney.client.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
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
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	public static final ResourceLocation EMPTY_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/empty.png");
	public static final String EMPTY = StargateJourney.EMPTY;
	
	protected String stargateName;
	
	/*
	 * X = Width
	 * Y = Height
	 * Z = Thickness
	 * 
	 * When viewing "(x_offset, y_offset, z_offset, x_size, y_size, z_size)":
	 * 
	 * If there is "-CONSTANT / 2" in the place of offset, it is used for centering
	 */

	protected static final float DEFAULT_ANGLE = 10.0F;
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
	
	protected static final float OUTER_RING_X = 10.0F;
	protected static final float OUTER_RING_Y = 7.0F;
	protected static final float OUTER_RING_Z = 7.0F;
	protected static final float OUTER_RING_X_CENTER = OUTER_RING_X / 2;
	protected static final float OUTER_RING_Z_CENTER = OUTER_RING_Z / 2;
	
	protected static final float BACK_RING_X = 9.0F;
	protected static final float BACK_RING_Y = 6.0F;
	protected static final float BACK_RING_Z = 4.0F;
	protected static final float BACK_RING_Z_OFFSET = 3.5F;
	protected static final float BACK_RING_X_CENTER = BACK_RING_X / 2;

	protected static final float INNER_RING_X = 8.0F;
	protected static final float INNER_RING_Y = 3.0F;
	protected static final float INNER_RING_Z = 7.0F;
	
	protected static final float SYMBOL_RING_X = 8.0F;
	protected static final float SYMBOL_RING_Y = 8.0F;
	protected static final float SYMBOL_RING_Z = 2.0F;
	protected static final float SYMBOL_RING_Y_OFFSET = SYMBOL_RING_Y - 2.0F; // The - 2.0F is there because the Symbol Ring is slightly bigger than the open space created by the rest of the ring, to prevent players from being able to see through when it's rotating
	protected static final float SYMBOL_RING_Z_OFFSET = 0.5F;
	protected static final float DIVIDER_X = 1.0F;
	
	public AbstractStargateModel(String stargateName)
	{
		this.stargateName = stargateName;
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
	
	protected ResourceLocation getChevronTexture(boolean lightsUp, boolean engaged)
	{
		String chevron = lightsUp ? this.stargateName + "_chevron" : stargateName + "_chevron_front";
		String path = "textures/entity/stargate/" + stargateName + "/";
		String suffix = engaged ? "_lit.png" : ".png";
		return new ResourceLocation(StargateJourney.MODID, path + chevron + suffix);
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
	
	public static void createChevron(PartDefinition chevron)
	{
		//	_
		//	V
		//	
		PartDefinition chevronLight = chevron.addOrReplaceChild("chevron_light", CubeListBuilder.create(), PartPose.ZERO);
		createChevronLight(chevronLight);
		
		//	
		//	\_/
		//	
		PartDefinition outerChevron = chevron.addOrReplaceChild("outer_chevron", CubeListBuilder.create(), PartPose.ZERO);
		createOuterChevron(outerChevron);
		
		PartDefinition backChevron = chevron.addOrReplaceChild("back_chevron", CubeListBuilder.create(), PartPose.ZERO);
		createBackChevron(backChevron);
	}
	
	public static void createChevronLight(PartDefinition chevronLight)
	{
		chevronLight.addOrReplaceChild("chevron_top", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-CHEVRON_LIGHT_TOP_X / 2, 0.0F, CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_TOP_X, CHEVRON_LIGHT_TOP_Y, CHEVRON_LIGHT_Z), 
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER, 0.0F));
		chevronLight.addOrReplaceChild("chevron_center", CubeListBuilder.create()
				.texOffs(22, 5)
				.addBox(-CHEVRON_LIGHT_CENTER_X / 2, 0.0F, CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_CENTER_X, CHEVRON_LIGHT_CENTER_Y, CHEVRON_LIGHT_Z), 
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER - CHEVRON_LIGHT_CENTER_Y, 0.0F));
		chevronLight.addOrReplaceChild("chevron_right", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(-CHEVRON_LIGHT_SIDE_X, -CHEVRON_LIGHT_SIDE_Y, CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_SIDE_X, CHEVRON_LIGHT_SIDE_Y, CHEVRON_LIGHT_Z),
				PartPose.offsetAndRotation(CHEVRON_LIGHT_TOP_X / 2, DEFAULT_DISTANCE_FROM_CENTER, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-CHEVRON_LIGHT_ANGLE)));
		chevronLight.addOrReplaceChild("chevron_left", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(0.0F, -CHEVRON_LIGHT_SIDE_Y, CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_SIDE_X, CHEVRON_LIGHT_SIDE_Y, CHEVRON_LIGHT_Z),
				PartPose.offsetAndRotation(-CHEVRON_LIGHT_TOP_X / 2, DEFAULT_DISTANCE_FROM_CENTER, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(CHEVRON_LIGHT_ANGLE)));
	}
	
	public static void createOuterChevron(PartDefinition outerChevron)
	{
		outerChevron.addOrReplaceChild("chevron_f", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(-OUTER_CHEVRON_CENTER_X / 2, 0.0F, OUTER_CHEVRON_Z_OFFSET, OUTER_CHEVRON_CENTER_X, OUTER_CHEVRON_CENTER_Y, OUTER_CHEVRON_CENTER_Z), 
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET, 0.0F));
		outerChevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(-OUTER_CHEVRON_SIDE_X, 0.0F, OUTER_CHEVRON_Z_OFFSET, OUTER_CHEVRON_SIDE_X, OUTER_CHEVRON_SIDE_Y, OUTER_CHEVRON_SIDE_Z), 
				PartPose.offsetAndRotation(OUTER_CHEVRON_SIDE_X, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-OUTER_CHEVRON_ANGLE)));
		outerChevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(0.0F, 0.0F, OUTER_CHEVRON_Z_OFFSET, OUTER_CHEVRON_SIDE_X, OUTER_CHEVRON_SIDE_Y, OUTER_CHEVRON_SIDE_Z), 
				PartPose.offsetAndRotation(-OUTER_CHEVRON_SIDE_X, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(OUTER_CHEVRON_ANGLE)));
	}
	
	public static void createBackChevron(PartDefinition backChevron)
	{
		backChevron.addOrReplaceChild("chevron_b", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(-OUTER_CHEVRON_CENTER_X / 2, 0.0F, -OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_CENTER_Z, OUTER_CHEVRON_CENTER_X, OUTER_CHEVRON_CENTER_Y, OUTER_CHEVRON_CENTER_Z), 
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET, 0.0F));
		backChevron.addOrReplaceChild("chevron_right_b", CubeListBuilder.create()
				.texOffs(10, 40)
				.addBox(-OUTER_CHEVRON_SIDE_X, 0.0F, -OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_CENTER_Z, OUTER_CHEVRON_SIDE_X, OUTER_CHEVRON_SIDE_Y, OUTER_CHEVRON_SIDE_Z), 
				PartPose.offsetAndRotation(OUTER_CHEVRON_SIDE_X, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-OUTER_CHEVRON_ANGLE)));
		backChevron.addOrReplaceChild("chevron_left_b", CubeListBuilder.create()
				.texOffs(10, 40)
				.addBox(0.0F, 0.0F, -OUTER_CHEVRON_Z_OFFSET - 1, OUTER_CHEVRON_SIDE_X, OUTER_CHEVRON_SIDE_Y, OUTER_CHEVRON_SIDE_Z), 
				PartPose.offsetAndRotation(-OUTER_CHEVRON_SIDE_X, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(OUTER_CHEVRON_ANGLE)));
		
		backChevron.addOrReplaceChild("chevron_b_top", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-CHEVRON_LIGHT_TOP_X / 2, 0.0F, CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_TOP_X, CHEVRON_LIGHT_TOP_Y, CHEVRON_BACK_LIGHT_Z), 
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER, -CHEVRON_BACK_LIGHT_Z));
		backChevron.addOrReplaceChild("chevron_b_center", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-CHEVRON_LIGHT_CENTER_X / 2, 0.0F, CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_CENTER_X, CHEVRON_LIGHT_CENTER_Y, CHEVRON_BACK_LIGHT_Z), 
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER - CHEVRON_LIGHT_CENTER_Y, -CHEVRON_BACK_LIGHT_Z));
		backChevron.addOrReplaceChild("chevron_b_right", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-CHEVRON_LIGHT_SIDE_X, -CHEVRON_LIGHT_SIDE_Y, -CHEVRON_BACK_LIGHT_Z + CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_SIDE_X, CHEVRON_LIGHT_SIDE_Y, CHEVRON_BACK_LIGHT_Z),
				PartPose.offsetAndRotation(CHEVRON_LIGHT_TOP_X / 2, DEFAULT_DISTANCE_FROM_CENTER, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-CHEVRON_LIGHT_ANGLE)));
		backChevron.addOrReplaceChild("chevron_b_left", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, -CHEVRON_LIGHT_SIDE_Y, -CHEVRON_BACK_LIGHT_Z + CHEVRON_LIGHT_Z_OFFSET, CHEVRON_LIGHT_SIDE_X, CHEVRON_LIGHT_SIDE_Y, CHEVRON_BACK_LIGHT_Z),
				PartPose.offsetAndRotation(-CHEVRON_LIGHT_TOP_X / 2, DEFAULT_DISTANCE_FROM_CENTER, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(CHEVRON_LIGHT_ANGLE)));
	}
	
	public static void createOuterRing(PartDefinition outerRing)
	{
		for(int i = 0; i < BOXES_PER_RING / 4; i++)
		{
			outerRing.addOrReplaceChild("outer_ring_" + 4 * i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-OUTER_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER, -OUTER_RING_Z / 2, OUTER_RING_X, OUTER_RING_Y, OUTER_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * 4 * i)));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 1), CubeListBuilder.create()
					.texOffs(0, 42)
					.addBox(-OUTER_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER, -OUTER_RING_Z / 2, OUTER_RING_X, OUTER_RING_Y, OUTER_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * (4 * i + 1))));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 2), CubeListBuilder.create()
					.texOffs(0, 28)
					.addBox(-OUTER_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER, -OUTER_RING_Z / 2, OUTER_RING_X, OUTER_RING_Y, OUTER_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * (4 * i + 2))));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 3), CubeListBuilder.create()
					.texOffs(0, 14)
					.addBox(-OUTER_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER, -OUTER_RING_Z / 2, OUTER_RING_X, OUTER_RING_Y, OUTER_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * (4 * i + 3))));
		}
	}
	
	public static void createBackRing(PartDefinition backRing)
	{
		for(int i = 0; i < BOXES_PER_RING / 4; i++)
		{
			backRing.addOrReplaceChild("back_ring_" + 4 * i, CubeListBuilder.create()
					.texOffs(34, -2)
					.addBox(-BACK_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER + OUTER_RING_Y, -BACK_RING_Z_OFFSET, BACK_RING_X, BACK_RING_Y, BACK_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * 4 * i)));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 1), CubeListBuilder.create()
					.texOffs(34, 6)
					.addBox(-BACK_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER + OUTER_RING_Y, -BACK_RING_Z_OFFSET, BACK_RING_X, BACK_RING_Y, BACK_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * (4 * i + 1))));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 2), CubeListBuilder.create()
					.texOffs(34, 14)
					.addBox(-BACK_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER + OUTER_RING_Y, -BACK_RING_Z_OFFSET, BACK_RING_X, BACK_RING_Y, BACK_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * (4 * i + 2))));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 3), CubeListBuilder.create()
					.texOffs(34, 6)
					.addBox(-BACK_RING_X_CENTER, -DEFAULT_DISTANCE_FROM_CENTER + OUTER_RING_Y, -BACK_RING_Z_OFFSET, BACK_RING_X, BACK_RING_Y, BACK_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * (4 * i + 3))));
		}
	}
	
	public static void createInnerRing(PartDefinition innerRing)
	{
		for(int i = 0; i < BOXES_PER_RING; i++)
		{
			innerRing.addOrReplaceChild("inner_ring_" + i, CubeListBuilder.create()
					.texOffs(34, 24)
					.addBox(-INNER_RING_X / 2, -DEFAULT_DISTANCE_FROM_CENTER + OUTER_RING_Y + BACK_RING_Y, -INNER_RING_Z / 2, INNER_RING_X, INNER_RING_Y, INNER_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-DEFAULT_ANGLE * i)));
		}
	}
	
	public static void createRing(PartDefinition ring)
	{
		PartDefinition outerRing = ring.addOrReplaceChild("outer_ring", CubeListBuilder.create(), PartPose.ZERO);
		createOuterRing(outerRing);
		
		PartDefinition backRing = ring.addOrReplaceChild("back_ring", CubeListBuilder.create(), PartPose.ZERO);
		createBackRing(backRing);
		
		PartDefinition innerRing = ring.addOrReplaceChild("inner_ring", CubeListBuilder.create(), PartPose.ZERO);
		createInnerRing(innerRing);
	}
	
	public static void createSymbolRing(PartDefinition symbolRing, int symbolCount)
	{
		double angle = (double) 360 / symbolCount;
		for(int i = 0; i < symbolCount; i++)
		{
			symbolRing.addOrReplaceChild("symbol_" + i, CubeListBuilder.create()
					.texOffs(-4, 6)
					.addBox(-SYMBOL_RING_X / 2, -DEFAULT_DISTANCE_FROM_CENTER + SYMBOL_RING_Y_OFFSET, SYMBOL_RING_Z_OFFSET, SYMBOL_RING_X, SYMBOL_RING_Y, SYMBOL_RING_Z), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i)));
		}
	}
	
	public static void createDividers(PartDefinition dividers, int symbolCount)
	{
		double angle = (double) 360 / symbolCount;
		for(int i = 0; i < symbolCount; i++)
		{
			dividers.addOrReplaceChild("divider_" + i, CubeListBuilder.create()
					.texOffs(34, 34)
					.addBox(-DIVIDER_X / 2, -DEFAULT_DISTANCE_FROM_CENTER + SYMBOL_RING_Y_OFFSET, SYMBOL_RING_Z_OFFSET, DIVIDER_X, SYMBOL_RING_Y, SYMBOL_RING_Z + 0.5F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - (angle / 2 + angle * i))));
		}
	}
}
