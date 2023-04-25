package net.povstalec.sgjourney.common.datagen;

import java.util.Set;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.common.init.BlockInit;

public class BlockLootTableGen extends BlockLootSubProvider
{
	protected BlockLootTableGen()
	{
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	protected void generate()
	{
		this.dropSelf(BlockInit.NAQUADAH_BLOCK.get());
	}

	@Override
	protected Iterable<Block> getKnownBlocks()
	{
		return BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
	}
}
