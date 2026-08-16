package net.povstalec.sgjourney.common.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Unas extends Anthropoid
{
	private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
	
	public Unas(EntityType<? extends Unas> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public ResourceLocation texture()
	{
		return TEXTURE;
	}
}
