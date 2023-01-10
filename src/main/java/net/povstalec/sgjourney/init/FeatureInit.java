package net.povstalec.sgjourney.init;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.StargateJourney;

public class FeatureInit
{
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, StargateJourney.MODID);
}
