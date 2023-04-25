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
	
	private boolean isLocationValid(String location)
	{
		String[] split = location.split(":");
		
		if(split.length > 2)
			return false;
		
		if(!ResourceLocation.isValidNamespace(split[0]))
			return false;
		
		return ResourceLocation.isValidPath(split[1]);
	}
	
	public AbstractStargateModel()
	{
		
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
				.addBox(-3.0F, 56.0F, 0.5F, 6.0F, 1.0F, 4.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_center", CubeListBuilder.create()
				.texOffs(22, 5)
				.addBox(-1.5F, 50.0F, 0.5F, 3.0F, 6.0F, 4.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_right", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(-2.0F, -6.1847F, 0.0F, 2.0F, 6.1847F, 4.0F),
				PartPose.offsetAndRotation(3.0F, 56.0F, 0.5F, 0.0F, 0.0F, (float) Math.toRadians(-15)));
		chevronLight.addOrReplaceChild("chevron_left", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(0.0F, -6.1847F, 0.0F, 2.0F, 6.1847F, 4.0F),
				PartPose.offsetAndRotation(-3.0F, 56.0F, 0.5F, 0.0F, 0.0F, (float) Math.toRadians(15)));
	}
	
	public static void createOuterChevron(PartDefinition outerChevron)
	{
		outerChevron.addOrReplaceChild("chevron_f", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(-2.0F, 46.0F, 3.5F, 4.0F, 2.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		outerChevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		outerChevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(0.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
	}
	
	public static void createBackChevron(PartDefinition backChevron)
	{
		backChevron.addOrReplaceChild("chevron_b", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(-2.0F, 46.0F, 0.0F, 4.0F, 2.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, -4.5F));
		backChevron.addOrReplaceChild("chevron_right_b", CubeListBuilder.create()
				.texOffs(10, 40)
				.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		backChevron.addOrReplaceChild("chevron_left_b", CubeListBuilder.create()
				.texOffs(10, 40)
				.addBox(0.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
		
		backChevron.addOrReplaceChild("chevron_b_top", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, 56.0F, 0.5F, 6.0F, 1.0F, 5.0F), 
				PartPose.offset(0.0F, 0.0F, -5.0F));
		backChevron.addOrReplaceChild("chevron_b_center", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-1.5F, 50.0F, 0.5F, 3.0F, 6.0F, 5.0F), 
				PartPose.offset(0.0F, 0.0F, -5.0F));
		backChevron.addOrReplaceChild("chevron_b_right", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-2F, -6.1847F, 0.0F, 2.0F, 6.1847F, 5.0F),
				PartPose.offsetAndRotation(3.0F, 56.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(-15)));
		backChevron.addOrReplaceChild("chevron_b_left", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, -6.1847F, 0.0F, 2.0F, 6.1847F, 5.0F),
				PartPose.offsetAndRotation(-3.0F, 56.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(15)));
	}
	
	public static void createOuterRing(PartDefinition outerRing)
	{
		for(int i = 0; i < 9; i++)
		{
			outerRing.addOrReplaceChild("outer_ring_" + 4 * i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * 4 * i)));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 1), CubeListBuilder.create()
					.texOffs(0, 42)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 1))));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 2), CubeListBuilder.create()
					.texOffs(0, 28)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 2))));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 3), CubeListBuilder.create()
					.texOffs(0, 14)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 3))));
		}
	}
	
	public static void createBackRing(PartDefinition backRing)
	{
		for(int i = 0; i < 9; i++)
		{
			backRing.addOrReplaceChild("back_ring_" + 4 * i, CubeListBuilder.create()
					.texOffs(34, -2)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * 4 * i)));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 1), CubeListBuilder.create()
					.texOffs(34, 6)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 1))));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 2), CubeListBuilder.create()
					.texOffs(34, 14)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 2))));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 3), CubeListBuilder.create()
					.texOffs(34, 6)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 3))));
		}
	}
	
	public static void createInnerRing(PartDefinition innerRing)
	{
		for(int i = 0; i < 36; i++)
		{
			innerRing.addOrReplaceChild("inner_ring_" + i, CubeListBuilder.create()
					.texOffs(34, 24)
					.addBox(-4.0F, -43.0F, -3.5F, 8.0F, 3.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
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
					.addBox(-4.0F, -50.0F, 0.5F, 8.0F, 8.0F, 2.0F), 
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
					.addBox(-0.5F, -50.0F, 0.5F, 1.0F, 8.0F, 2.5F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - (angle/2 + angle * i))));
		}
	}
}
