package net.povstalec.sgjourney.mixin;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.structures.SGJourneyStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConfiguredStructureFeature.class)
public class ConfiguredStructureMixin
{
	@Unique
	private ConfiguredStructureFeature<?, ?> stargatejourney_1_18_2$self()
	{
		return (ConfiguredStructureFeature<?, ?>) (Object) this;
	}
	
	@Inject(method = "biomes", at = @At("HEAD"), cancellable = true)
	public void biomes(CallbackInfoReturnable<HolderSet<Biome>> cir)
	{
		if(stargatejourney_1_18_2$self().config instanceof SGJourneyStructure.Configuration sgjourneyConfig)
		{
			if(sgjourneyConfig.commonStargates() == null)
				cir.setReturnValue(stargatejourney_1_18_2$self().biomes);
			else if(sgjourneyConfig.commonStargates() != CommonGenerationConfig.common_stargate_generation.get())
				cir.setReturnValue(HolderSet.direct());
		}
	}
}
