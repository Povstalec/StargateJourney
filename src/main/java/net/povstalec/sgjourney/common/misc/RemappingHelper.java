package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RemappingHelper
{
	private static final Map<ResourceLocation, Supplier<? extends Item>> OLD_ITEM_MAPPINGS = new HashMap<>();
	private static final Map<ResourceLocation, Supplier<? extends Block>> OLD_BLOCK_MAPPINGS = new HashMap<>();
	private static final Map<ResourceLocation, Supplier<? extends BlockEntityType<?>>> OLD_BLOCK_ENTITY_MAPPINGS = new HashMap<>();
	
	private static void remapItem(@NotNull String oldName, @NotNull Supplier<? extends Item> itemSupplier)
	{
		OLD_ITEM_MAPPINGS.put(StargateJourney.sgjourneyLocation(oldName), itemSupplier);
	}
	
	private static void remapBlock(@NotNull String oldName, @NotNull Supplier<? extends Block> blockSupplier)
	{
		OLD_BLOCK_MAPPINGS.put(StargateJourney.sgjourneyLocation(oldName), blockSupplier);
		if(ForgeRegistries.ITEMS.containsKey(StargateJourney.sgjourneyLocation(oldName))) // Register BlockItem, if the Block has one
			remapItem(oldName, () -> ForgeRegistries.ITEMS.getValue(StargateJourney.sgjourneyLocation(oldName)));
	}
	
	private static void remapBlockEntity(@NotNull String oldName, @NotNull Supplier<? extends BlockEntityType<?>> blockEntitySupplier, @NotNull Supplier<? extends Block> blockSupplier)
	{
		OLD_BLOCK_ENTITY_MAPPINGS.put(StargateJourney.sgjourneyLocation(oldName), blockEntitySupplier);
		remapBlock(oldName, blockSupplier);
	}
	
	
	
	private static void remapItems(List<MissingMappingsEvent.Mapping<Item>> mappings)
	{
		for(MissingMappingsEvent.Mapping<Item> mapping : mappings)
		{
			ResourceLocation oldKey = mapping.getKey();
			Supplier<? extends Item> supplier = OLD_ITEM_MAPPINGS.get(oldKey);
			if(supplier != null)
			{
				ResourceLocation newKey = ForgeRegistries.ITEMS.getKey(supplier.get());
				if(newKey != null)
				{
					mapping.remap(supplier.get());
					StargateJourney.LOGGER.debug("Remapped Item {} to {}", oldKey, newKey);
				}
			}
		}
	}
	
	private static void remapBlocks(List<MissingMappingsEvent.Mapping<Block>> mappings)
	{
		for(MissingMappingsEvent.Mapping<Block> mapping : mappings)
		{
			ResourceLocation oldKey = mapping.getKey();
			Supplier<? extends Block> supplier = OLD_BLOCK_MAPPINGS.get(oldKey);
			if(supplier != null)
			{
				ResourceLocation newKey = ForgeRegistries.BLOCKS.getKey(supplier.get());
				if(newKey != null)
				{
					mapping.remap(supplier.get());
					StargateJourney.LOGGER.debug("Remapped Block {} to {}", oldKey, newKey);
				}
			}
		}
	}
	
	private static void remapBlockEntities(List<MissingMappingsEvent.Mapping<BlockEntityType<?>>> mappings)
	{
		for(MissingMappingsEvent.Mapping<BlockEntityType<?>> mapping : mappings)
		{
			ResourceLocation oldKey = mapping.getKey();
			Supplier<? extends BlockEntityType<?>> supplier = OLD_BLOCK_ENTITY_MAPPINGS.get(oldKey);
			if(supplier != null)
			{
				ResourceLocation newKey = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(supplier.get());
				if(newKey != null)
				{
					mapping.remap(supplier.get());
					StargateJourney.LOGGER.debug("Remapped Block Entity Type {} to {}", oldKey, newKey);
				}
			}
		}
	}
	
	public static void startRemapping(MissingMappingsEvent event)
	{
		remapItems(event.getMappings(Registries.ITEM, StargateJourney.MODID));
		remapBlocks(event.getMappings(Registries.BLOCK, StargateJourney.MODID));
		// For some reason, the Forge snapshot doesn't have an entry for minecraft:block_entity_type, so this straight up just doesn't fire, how amazing
		remapBlockEntities(event.getMappings(Registries.BLOCK_ENTITY_TYPE, StargateJourney.MODID));
	}
	
	public static void setupRemapping()
	{
		// Block Entities
		//remapBlockEntity("transport_rings", BlockEntityInit.GOAULD_TRANSPORT_RINGS, BlockInit.GOAULD_TRANSPORT_RINGS);
		//remapBlockEntity("ring_panel", BlockEntityInit.GOAULD_RING_PANEL, BlockInit.GOAULD_RING_PANEL);
		
		// Blocks
		remapBlock("transport_rings", BlockInit.GOAULD_TRANSPORT_RINGS);
		remapBlock("ring_panel", BlockInit.GOAULD_RING_PANEL);
		
		// Items
		remapItem("naquadah_alloy", ItemInit.NAQUADAH_IRON_ALLOY);
		remapItem("naquadah_alloy_nugget", ItemInit.NAQUADAH_IRON_NUGGET);
		remapItem("naquadah_alloy_iris", ItemInit.NAQUADAH_IRON_IRIS);
		remapItem("ring_remote", ItemInit.GOAULD_RING_REMOTE);
	}
}
