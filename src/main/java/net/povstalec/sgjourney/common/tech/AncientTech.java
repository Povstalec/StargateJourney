package net.povstalec.sgjourney.common.tech;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.AncientGeneProvider;

public interface AncientTech
{
	/**
	 * @param user LivingEntity attempting to use this technology
	 * @return True of the user can use this technology, otherwise false
	 */
	default boolean canUseAncientTech(LivingEntity user)
	{
		Optional<Boolean> canUse = user.getCapability(AncientGeneProvider.ANCIENT_GENE).map(AncientGene::canUseAncientTechnology);
		
		return canUse.orElse(false);
	}
	
	default AncientGene.ATAGene getGeneType(Entity user)
	{
		Optional<AncientGene.ATAGene> geneType = user.getCapability(AncientGeneProvider.ANCIENT_GENE).map(AncientGene::getGeneType);
		
		return geneType.orElse(AncientGene.ATAGene.NONE);
	}
	
}
