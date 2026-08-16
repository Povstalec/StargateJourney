package net.povstalec.sgjourney.common.tech;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.povstalec.sgjourney.common.capabilities.AncientGene;

public interface AncientTech
{
	/**
	 * @param user LivingEntity attempting to use this technology
	 * @return True of the user can use this technology, otherwise false
	 */
	default boolean canUseAncientTech(LivingEntity user)
	{
		AncientGene cap = user.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
		if(cap != null)
			return cap.canUseAncientTechnology();
		
		return false;
	}
	
	default AncientGene.ATAGene getGeneType(Entity user)
	{
		AncientGene cap = user.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
		if(cap != null)
			return cap.getGeneType();
		
		return AncientGene.ATAGene.NONE;
	}
	
}
