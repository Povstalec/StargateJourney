package net.povstalec.sgjourney.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;

public class ModelProperties
{
	public static final ModelProperty<Integer> SYMBOL_INDEX_PROPERTY = new ModelProperty<>(index -> index >= 0);
	public static final ModelProperty<ResourceLocation> SYMBOL_TEXTURE_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<ResourceLocation> POINT_OF_ORIGIN_TEXTURE_PROPERTY = new ModelProperty<>();
}
