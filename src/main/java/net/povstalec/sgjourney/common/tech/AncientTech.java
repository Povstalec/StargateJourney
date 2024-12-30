package net.povstalec.sgjourney.common.tech;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.AncientGene;

public interface AncientTech
{
	default boolean canUseAncientTech(Entity user)
	{
		AncientGene cap = user.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
		if(cap != null)
			return cap.canUseAncientTechnology();
		
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
		AncientGene cap = user.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
		if(cap != null)
			return cap.getGeneType();
		
		return AncientGene.ATAGene.NONE;
	}
	
}
