package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.povstalec.sgjourney.StargateJourney;

public class TagInit
{
	public class Items
	{
		public static final TagKey<Item> REACTION_HEATER = tag("reaction_heater");
		public static final TagKey<Item> RAW_NAQUADAH = tag("raw_naquadah");
		
		private static TagKey<Item> tag(String name)
		{
            return ItemTags.create(new ResourceLocation(StargateJourney.MODID, name));
        }

        /*private static TagKey<Item> forgeTag(String name)
        {
            return ItemTags.create(new ResourceLocation("forge", name));
        }*/
	}
	
	public class Blocks
	{
		public static final TagKey<Block> IRIS_RESISTANT = tag("iris_resistant");
		public static final TagKey<Block> KAWOOSH_IMMUNE = tag("kawoosh_immune");
		public static final TagKey<Block> PLASMA_FLAMMABLE = tag("plasma_flammable");
		public static final TagKey<Block> STONE_SPIRE_PROTRUDES_THROUGH = tag("stone_spire_protrudes_through");
		public static final TagKey<Block> TRINIUM_ARROW_CAN_BREAK = tag("trinium_arrow_can_break");
		
		private static TagKey<Block> tag(String name)
		{
            return BlockTags.create(new ResourceLocation(StargateJourney.MODID, name));
        }

        /*private static TagKey<Block> forgeTag(String name)
        {
            return BlockTags.create(new ResourceLocation("forge", name));
        }*/
	}
	
	public class Entities
	{
		public static final TagKey<EntityType<?>> KAWOOSH_IMMUNE = tag("kawoosh_immune");
		public static final TagKey<EntityType<?>> WORMHOLE_IGNORES = tag("wormhole_ignores");
		public static final TagKey<EntityType<?>> WORMHOLE_CANNOT_TELEPORT = tag("wormhole_cannot_teleport");
		
		private static TagKey<EntityType<?>> tag(String name)
		{
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(StargateJourney.MODID, name));
        }

        /*private static TagKey<EntityType<?>> forgeTag(String name)
        {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", name));
        }*/
	}
	
	public class Structures
	{
		public static final TagKey<Structure> STARGATE_MAP = tag("stargate_map");
		public static final TagKey<Structure> NETWORK_STARGATE = tag("network_stargate");
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
