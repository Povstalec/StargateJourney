package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.fluids.HeavyNaquadahFluidType;
import net.povstalec.sgjourney.common.fluids.NaquadahFluidType;

public class FluidTypeInit
{
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, StargateJourney.MODID);
	
	
	public static final DeferredHolder<FluidType, FluidType> LIQUID_NAQUADAH_FLUID_TYPE = FLUID_TYPES.register("liquid_naquadah", () ->
	new NaquadahFluidType(FluidType.Properties.create()
			.density(100000)
			.canSwim(false)
			.rarity(Rarity.RARE)
			.supportsBoating(true)
			.viscosity(100000)));
	
	public static final BaseFlowingFluid.Properties LIQUID_NAQUADAH_PROPERTIES = new BaseFlowingFluid.Properties(
			FluidTypeInit.LIQUID_NAQUADAH_FLUID_TYPE, FluidInit.LIQUID_NAQUADAH_SOURCE, FluidInit.LIQUID_NAQUADAH_FLOWING)
			.bucket(ItemInit.LIQUID_NAQUADAH_BUCKET)
			.block(BlockInit.LIQUID_NAQUADAH_BLOCK)
			.slopeFindDistance(2)
			.levelDecreasePerBlock(2);
	
	public static final DeferredHolder<FluidType, FluidType> HEAVY_LIQUID_NAQUADAH_FLUID_TYPE = FLUID_TYPES.register("heavy_liquid_naquadah", () ->
	new HeavyNaquadahFluidType(FluidType.Properties.create()
			.density(100000)
			.canSwim(false)
			.rarity(Rarity.RARE)
			.supportsBoating(true)
			.viscosity(100000)));
	
	public static final BaseFlowingFluid.Properties HEAVY_LIQUID_NAQUADAH_PROPERTIES = new BaseFlowingFluid.Properties(
			FluidTypeInit.HEAVY_LIQUID_NAQUADAH_FLUID_TYPE, FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE, FluidInit.HEAVY_LIQUID_NAQUADAH_FLOWING)
			.bucket(ItemInit.HEAVY_LIQUID_NAQUADAH_BUCKET)
			.block(BlockInit.HEAVY_LIQUID_NAQUADAH_BLOCK)
			.slopeFindDistance(2)
			.levelDecreasePerBlock(2);
	
	public static void register(IEventBus eventBus)
	{
		FLUID_TYPES.register(eventBus);
	}
}
