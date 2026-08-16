package net.povstalec.sgjourney.client;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class ModelProperties
{
	public static final ModelProperty<Integer> SYMBOL_INDEX_PROPERTY = new ModelProperty<>(index -> index >= 0);
	public static final ModelProperty<ResourceKey<Symbols>> SYMBOLS_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<ResourceKey<PointOfOrigin>> POINT_OF_ORIGIN_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Address> ADDRESS_PROPERTY = new ModelProperty<>();
}
