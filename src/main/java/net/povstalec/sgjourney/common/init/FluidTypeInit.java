package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.fluids.NaquadahFluidType;

public class FluidTypeInit
{
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, StargateJourney.MODID);
	
	
	public static final RegistryObject<FluidType> LIQUID_NAQUADAH_FLUID_TYPE = FLUID_TYPES.register("liquid_naquadah", () -> 
	new NaquadahFluidType(FluidType.Properties.create()
			.density(100000)
			.canSwim(false)
			.rarity(Rarity.RARE)
			.supportsBoating(true)
			.viscosity(100000)));
	
	public static final ForgeFlowingFluid.Properties LIQUID_NAQUADAH_PROPERTIES = new ForgeFlowingFluid.Properties(
			FluidTypeInit.LIQUID_NAQUADAH_FLUID_TYPE, FluidInit.LIQUID_NAQUADAH_SOURCE, FluidInit.LIQUID_NAQUADAH_FLOWING)
			.bucket(ItemInit.LIQUID_NAQUADAH_BUCKET)
			.block(BlockInit.LIQUID_NAQUADAH_BLOCK)
			.slopeFindDistance(2)
			.levelDecreasePerBlock(2);
	
	public static void register(IEventBus eventBus)
	{
		FLUID_TYPES.register(eventBus);
	}
}
