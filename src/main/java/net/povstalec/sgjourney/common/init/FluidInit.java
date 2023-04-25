package net.povstalec.sgjourney.common.init;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;

public class FluidInit
{
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, StargateJourney.MODID);
	
	
	public static final RegistryObject<FlowingFluid> LIQUID_NAQUADAH_SOURCE = FLUIDS.register("liquid_naquadah_source", 
			() -> new ForgeFlowingFluid.Source(FluidTypeInit.LIQUID_NAQUADAH_PROPERTIES));
	
	public static final RegistryObject<FlowingFluid> LIQUID_NAQUADAH_FLOWING = FLUIDS.register("liquid_naquadah_flowing", 
			() -> new ForgeFlowingFluid.Flowing(FluidTypeInit.LIQUID_NAQUADAH_PROPERTIES));
	
	
	
	public static void register(IEventBus eventBus)
	{
		FLUIDS.register(eventBus);
	}
}
