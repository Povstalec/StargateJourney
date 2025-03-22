package net.povstalec.sgjourney.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.povstalec.sgjourney.StargateJourney;

public class DamageSourceInit
{
	public static final ResourceKey<DamageType> KAWOOSH = ResourceKey.create(Registries.DAMAGE_TYPE, StargateJourney.sgjourneyLocation("kawoosh"));
	public static final ResourceKey<DamageType> REVERSE_WORMHOLE = ResourceKey.create(Registries.DAMAGE_TYPE, StargateJourney.sgjourneyLocation("reverse_wormhole"));
	public static final ResourceKey<DamageType> IRIS = ResourceKey.create(Registries.DAMAGE_TYPE, StargateJourney.sgjourneyLocation("iris"));
	
	public static DamageSource damageSource(MinecraftServer server, ResourceKey<DamageType> damageType)
	{
		RegistryAccess registries = server.registryAccess();
		Registry<DamageType> registry = registries.registryOrThrow(Registries.DAMAGE_TYPE);
		
		return new DamageSource(registry.getHolderOrThrow(damageType));
	}
}
