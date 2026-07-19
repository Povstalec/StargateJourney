package net.povstalec.sgjourney.client.models.item;

import com.google.common.collect.Maps;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.povstalec.sgjourney.client.models.block.SymbolBlockModelLoader;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

public class SymbolBlockItemOverrides extends ItemOverrides
{
	protected final Map<SymbolModelKey, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
	protected final ItemOverrides nested;
	protected final ModelBaker baker;
	protected final IGeometryBakingContext owner;
	protected final SymbolBlockModelLoader.SymbolBlockModelGeometry parent;
	
	public SymbolBlockItemOverrides(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner, SymbolBlockModelLoader.SymbolBlockModelGeometry parent)
	{
		this.nested = nested;
		this.baker = baker;
		this.owner = owner;
		this.parent = parent;
	}
	
	@Override
	public @Nullable BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed)
	{
		BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
		if(overridden != originalModel)
			return overridden;
		
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		int symbolNumber = getSymbolNumber(blockEntityTag);
		String id = symbolNumber == 0 ? getPointOfOrigin(blockEntityTag) : getSymbols(blockEntityTag);
		SymbolModelKey key = new SymbolModelKey(symbolNumber, id);
		
		if(cache.containsKey(key))
			return cache.get(key);
		
		SymbolBlockModelLoader.SymbolBlockModelGeometry unbaked = this.parent.withSymbolNumberAndTexture(symbolNumber, id == null ? null : ResourceLocation.tryParse(id));
		BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this);
		
		cache.put(key, bakedModel);
		return bakedModel;
	}
	
	public static int getSymbolNumber(CompoundTag blockEntityTag)
	{
		if(blockEntityTag != null && blockEntityTag.contains(SymbolBlockEntity.SYMBOL_NUMBER, Tag.TAG_INT))
			return blockEntityTag.getInt(SymbolBlockEntity.SYMBOL_NUMBER);
		
		return 0;
	}
	
	public static String getPointOfOrigin(CompoundTag blockEntityTag)
	{
		if(blockEntityTag != null && blockEntityTag.contains(SymbolBlockEntity.SYMBOL, Tag.TAG_STRING))
			return blockEntityTag.getString(SymbolBlockEntity.SYMBOL);
		
		return null;
	}
	
	public static String getSymbols(CompoundTag blockEntityTag)
	{
		if(blockEntityTag != null && blockEntityTag.contains(SymbolBlockEntity.SYMBOLS, Tag.TAG_STRING))
			return blockEntityTag.getString(SymbolBlockEntity.SYMBOLS);
		
		return null;
	}
	
	
	public record SymbolModelKey(int symbolNumber, @Nullable String id)
	{
		@Override
		public boolean equals(Object other)
		{
			if(this == other)
				return true;
			
			if(other instanceof SymbolModelKey key)
			{
				if(this.symbolNumber != key.symbolNumber)
					return false;
				
				if(this.id == null)
					return key.id == null;
				else
					return this.id.equals(key.id);
			}
			
			return false;
		}
	}
}
