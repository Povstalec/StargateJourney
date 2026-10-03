package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.povstalec.sgjourney.common.block_entities.CartoucheBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.Address;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Credit for all this goes to cookta2012
 */
public class SGJourneyItemSubtypeInterpreter
{
	public static class StargateVariant implements ISubtypeInterpreter<ItemStack>
	{
		public static final StargateVariant INSTANCE = new StargateVariant();
		
		@Override
		public @Nullable Object getSubtypeData(ItemStack ingredient, @NotNull UidContext context)
		{
			if(ingredient.has(DataComponentInit.STARGATE_VARIANT))
				return ingredient.get(DataComponentInit.STARGATE_VARIANT);
			
			return null;
		}
		
		@Override
		public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			return "";
		}
	}
	
	public static class StargateUpgrade implements ISubtypeInterpreter<ItemStack>
	{
		public static final StargateUpgrade INSTANCE = new StargateUpgrade();
		
		@Override
		public @Nullable Object getSubtypeData(ItemStack ingredient, @NotNull UidContext context)
		{
			if(ingredient.has(DataComponentInit.STARGATE_UPGRADE))
				return ingredient.get(DataComponentInit.STARGATE_UPGRADE);
			
			return null;
		}
		
		@Override
		public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			return "";
		}
	}
	
	public static class FluidHolder implements ISubtypeInterpreter<ItemStack>
	{
		public static final FluidHolder INSTANCE = new FluidHolder();
		
		@Override
		public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			if(ingredient.getCapability(Capabilities.FluidHandler.ITEM) != null)
				return ingredient.getCapability(Capabilities.FluidHandler.ITEM).getFluidInTank(0).getFluid();
			
			return null;
		}
		
		@Override
		public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			return "";
		}
	}
	
	public static class GenerationStep implements ISubtypeInterpreter<ItemStack>
	{
		public static final GenerationStep INSTANCE = new GenerationStep();
		
		@Override
		public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			
			return getStepSubtypeData(blockEntityTag);
		}
		
		@Override
		public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			return "";
		}
		
		protected Object getStepSubtypeData(@Nullable CompoundTag blockEntityTag)
		{
			if(blockEntityTag != null && blockEntityTag.contains(StructureGenEntity.GENERATION_STEP))
				return StructureGenEntity.Step.fromByte(blockEntityTag.getByte(AbstractStargateEntity.GENERATION_STEP));
			
			return null;
		}
	}
	
	public static class Cartouche extends GenerationStep
	{
		public static final Cartouche INSTANCE = new Cartouche();
		
		@Override
		public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
			{
				return List.of(
					getStepSubtypeData(blockEntityTag),
					Address.Type.fromLength(blockEntityTag.getByte(CartoucheBlockEntity.LOCAL_ADDRESS))
				);
			}
			
			return null;
		}
	}
	
	public static class SymbolBlock extends GenerationStep
	{
		public static final SymbolBlock INSTANCE = new SymbolBlock();
		
		@Override
		public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
			{
				return List.of(
					getStepSubtypeData(blockEntityTag),
					blockEntityTag.getBoolean(SymbolBlockEntity.LOCAL_POINT_OF_ORIGIN),
					blockEntityTag.getBoolean(SymbolBlockEntity.RANDOM_POINT_OF_ORIGIN)
				);
			}
			
			return null;
		}
	}
	
	public static class Stargate extends GenerationStep
	{
		public static final Stargate INSTANCE = new Stargate();
		
		@Override
		public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context)
		{
			CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(ingredient);
			if(blockEntityTag != null)
			{
				return List.of(
					getStepSubtypeData(blockEntityTag),
					blockEntityTag.getBoolean(AbstractStargateEntity.LOCAL_POINT_OF_ORIGIN),
					blockEntityTag.getBoolean(PegasusStargateEntity.DYNAMC_SYMBOLS)
				);
			}
			
			return null;
		}
	}
}
