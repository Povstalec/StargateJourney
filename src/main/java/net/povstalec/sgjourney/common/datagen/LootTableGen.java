package net.povstalec.sgjourney.common.datagen;

import java.util.List;
import java.util.Set;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class LootTableGen
{
	public static LootTableProvider create(PackOutput output)
	{
		return new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTableGen::new, LootContextParamSets.BLOCK)));
	}
}
