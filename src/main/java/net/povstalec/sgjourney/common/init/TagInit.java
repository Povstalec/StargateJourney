package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.povstalec.sgjourney.StargateJourney;

public class TagInit
{
	public class Items
	{
		public static final TagKey<Item> REACTION_HEATER = tag("reaction_heater");
		
		private static TagKey<Item> tag(String name)
		{
            return ItemTags.create(new ResourceLocation(StargateJourney.MODID, name));
        }

        private static TagKey<Item> forgeTag(String name)
        {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
	}
	
	public class Blocks
	{
		public static final TagKey<Block> PLASMA_FLAMMABLE = tag("plasma_flammable");
		public static final TagKey<Block> STONE_SPIRE_PROTRUDES_THROUGH = tag("stone_spire_protrudes_through");
		
		private static TagKey<Block> tag(String name)
		{
            return BlockTags.create(new ResourceLocation(StargateJourney.MODID, name));
        }

        private static TagKey<Block> forgeTag(String name)
        {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
	}
	
	public class Structures
	{
		public static final TagKey<Structure> HAS_STARGATE = tag("has_stargate");
		public static final TagKey<Structure> BURIED_STARGATE = tag("buried_stargate");
		public static final TagKey<Structure> STARGATE_PEDESTAL = tag("stargate_pedestal");
		public static final TagKey<Structure> STARGATE_TEMPLE = tag("stargate_temple");
		public static final TagKey<Structure> STARGATE_OUTPOST = tag("stargate_outpost");

		public static final TagKey<Structure> ON_ARCHEOLOGIST_MAPS = tag("on_archeologist_maps");
		
		public static final TagKey<Structure> GOAULD_TEMPLE = tag("goauld_temple");
		public static final TagKey<Structure> CITY = tag("city");
		
		private static TagKey<Structure> tag(String name)
		{
			return TagKey.create(Registries.STRUCTURE, new ResourceLocation(StargateJourney.MODID, name));
		}
	}
}
