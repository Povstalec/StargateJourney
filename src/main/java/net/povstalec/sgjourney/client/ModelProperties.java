package net.povstalec.sgjourney.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;
import net.povstalec.sgjourney.common.sgjourney.Address;

public class ModelProperties
{
	public static final ModelProperty<Integer> SYMBOL_INDEX_PROPERTY = new ModelProperty<>(index -> index >= 0);
	public static final ModelProperty<ResourceLocation> SYMBOLS_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<ResourceLocation> POINT_OF_ORIGIN_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Address> ADDRESS_PROPERTY = new ModelProperty<>();
}
