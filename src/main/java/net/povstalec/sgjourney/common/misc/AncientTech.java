package net.povstalec.sgjourney.common.misc;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.AncientGeneProvider;

public interface AncientTech
{
	default boolean canUseAncientTech(Entity user)
	{
		Optional<Boolean> canUse = user.getCapability(AncientGeneProvider.ANCIENT_GENE).map(cap -> cap.canUseAncientTechnology());
		
		if(canUse.isPresent())
			return canUse.get();
		return false;
	}
	
	/**
	 * 
	 * @param requirementsDisabled Whether or not the requirements for having Ancient Gene to use this have been disabled
	 * @param player
	 * @return
	 */
	default boolean canUseAncientTech(boolean requirementsDisabled, Entity user)
	{
		return requirementsDisabled ? true : canUseAncientTech(user);
	}
	
	default AncientGene.ATAGene getGeneType(Entity user)
	{
		Optional<AncientGene.ATAGene> geneType = user.getCapability(AncientGeneProvider.ANCIENT_GENE).map(cap -> cap.getGeneType());
		
		if(geneType.isPresent())
			return geneType.get();
		return AncientGene.ATAGene.NONE;
	}
	
}
