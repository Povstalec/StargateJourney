package net.povstalec.sgjourney.common.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class Jaffa extends Human
{
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/entity/player/wide/efe.png");
	
	public Jaffa(EntityType<? extends AgeableMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public ResourceLocation texture()
	{
		return TEXTURE;
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return AgeableMob.createMobAttributes()
				.add(Attributes.GRAVITY, 0.5f)
				.add(Attributes.MAX_HEALTH, 30.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.75D);
	}
}
