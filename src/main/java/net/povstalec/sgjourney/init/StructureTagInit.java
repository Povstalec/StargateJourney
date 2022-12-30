package net.povstalec.sgjourney.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.povstalec.sgjourney.StargateJourney;

public interface StructureTagInit
{
	//Structure that has a Stargate. Used to find Stargates in dimensions that are not in the Stargate Network(WIP)
	
	TagKey<Structure> ON_ARCHEOLOGIST_MAPS = create("on_archeologist_maps");
	
	TagKey<Structure> BURIED_STARGATE = create("buried_stargate");
	TagKey<Structure> HAS_STARGATE = create("has_stargate");
	TagKey<Structure> GOAULD_TEMPLE = create("goauld_temple");
	
	private static TagKey<Structure> create(String name)
	{
		return TagKey.create(Registries.STRUCTURE, new ResourceLocation(StargateJourney.MODID, name));
	}
}
