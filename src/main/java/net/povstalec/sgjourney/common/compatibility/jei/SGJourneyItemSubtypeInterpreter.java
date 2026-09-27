package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.common.block_entities.CartoucheBlockEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;
import net.povstalec.sgjourney.common.items.VialItem;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.Address;
import org.jetbrains.annotations.NotNull;

/**
 * Credit for all this goes to cookta2012
 */
public class SGJourneyItemSubtypeInterpreter
{
	public static class StargateVariant implements IIngredientSubtypeInterpreter<ItemStack>
	{
		public static final StargateVariant INSTANCE = new StargateVariant();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			ResourceLocation resourceLocation = StargateVariantItem.getVariant(ingredient);
			if(resourceLocation != null)
				return resourceLocation.toString();
			
			return NONE;
		}
	}
	
	public static class StargateUpgrade implements IIngredientSubtypeInterpreter<ItemStack>
	{
		public static final StargateUpgrade INSTANCE = new StargateUpgrade();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			return StargateUpgradeItem.getStargateString(ingredient).orElse(NONE);
			
		}
	}
	
	public static class FluidHolder implements IIngredientSubtypeInterpreter<ItemStack>
	{
		public static final FluidHolder INSTANCE = new FluidHolder();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			return ingredient.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid().toString()).orElse(NONE);
			
		}
	}
	
	public static class GenerationStep implements IIngredientSubtypeInterpreter<ItemStack>
	{
		public static final GenerationStep INSTANCE = new GenerationStep();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
				return AbstractStargateEntity.GENERATION_STEP + blockEntityTag.getByte(AbstractStargateEntity.GENERATION_STEP);
			
			return NONE;
		}
	}
	
	public static class Cartouche extends GenerationStep
	{
		public static final Cartouche INSTANCE = new Cartouche();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
			{
				StringBuilder stringBuilder = new StringBuilder(super.apply(ingredient, context));
				
				if(blockEntityTag.contains(CartoucheBlockEntity.LOCAL_ADDRESS))
					stringBuilder.append(";").append(CartoucheBlockEntity.LOCAL_ADDRESS).append(blockEntityTag.getByte(CartoucheBlockEntity.LOCAL_ADDRESS));
				
				return stringBuilder.toString();
			}
			
			return NONE;
		}
	}
	
	public static class SymbolBlock extends GenerationStep
	{
		public static final SymbolBlock INSTANCE = new SymbolBlock();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
			{
				StringBuilder stringBuilder = new StringBuilder(super.apply(ingredient, context));
				
				if(blockEntityTag.contains(SymbolBlockEntity.LOCAL_POINT_OF_ORIGIN) && blockEntityTag.getBoolean(SymbolBlockEntity.LOCAL_POINT_OF_ORIGIN))
					stringBuilder.append(";").append(SymbolBlockEntity.LOCAL_POINT_OF_ORIGIN);
				
				if(blockEntityTag.contains(SymbolBlockEntity.RANDOM_POINT_OF_ORIGIN) && blockEntityTag.getBoolean(SymbolBlockEntity.RANDOM_POINT_OF_ORIGIN))
					stringBuilder.append(";").append(SymbolBlockEntity.RANDOM_POINT_OF_ORIGIN);
				
				return stringBuilder.toString();
			}
			
			return NONE;
		}
	}
	
	public static class Stargate extends GenerationStep
	{
		public static final Stargate INSTANCE = new Stargate();
		
		@Override
		public @NotNull String apply(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
			{
				StringBuilder stringBuilder = new StringBuilder(super.apply(ingredient, context));
				
				if(blockEntityTag.contains(AbstractStargateEntity.LOCAL_POINT_OF_ORIGIN) && blockEntityTag.getBoolean(AbstractStargateEntity.LOCAL_POINT_OF_ORIGIN))
					stringBuilder.append(";").append(AbstractStargateEntity.LOCAL_POINT_OF_ORIGIN);
				
				if(blockEntityTag.contains(PegasusStargateEntity.DYNAMC_SYMBOLS) && blockEntityTag.getBoolean(PegasusStargateEntity.DYNAMC_SYMBOLS))
					stringBuilder.append(";").append(PegasusStargateEntity.DYNAMC_SYMBOLS);
				
				return stringBuilder.toString();
			}
			
			return NONE;
		}
	}
}
