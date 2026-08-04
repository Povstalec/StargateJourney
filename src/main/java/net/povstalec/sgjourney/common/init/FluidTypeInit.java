package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidTypeInit
{
	private static final ResourceLocation STILL_TEXTURE = new ResourceLocation("block/water_still");
	private static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation("block/water_flow");
	private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("misc/underwater");
	
	public static final ForgeFlowingFluid.Properties LIQUID_NAQUADAH_PROPERTIES = new ForgeFlowingFluid.Properties(
		FluidInit.LIQUID_NAQUADAH_SOURCE, FluidInit.LIQUID_NAQUADAH_FLOWING, FluidAttributes.builder(STILL_TEXTURE, FLOWING_TEXTURE)
		.density(100000)
		.viscosity(100000)
		.overlay(OVERLAY_TEXTURE)
		.color(0xffb0f329))
		.slopeFindDistance(2)
		.levelDecreasePerBlock(2)
		.bucket(ItemInit.LIQUID_NAQUADAH_BUCKET)
		.block(BlockInit.LIQUID_NAQUADAH_BLOCK);
	
	public static final ForgeFlowingFluid.Properties HEAVY_LIQUID_NAQUADAH_PROPERTIES = new ForgeFlowingFluid.Properties(
		FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE, FluidInit.HEAVY_LIQUID_NAQUADAH_FLOWING, FluidAttributes.builder(STILL_TEXTURE, FLOWING_TEXTURE)
		.density(100000)
		.viscosity(100000)
		.overlay(OVERLAY_TEXTURE)
		.color(0xff096c00))
		.slopeFindDistance(2)
		.levelDecreasePerBlock(2)
		.bucket(ItemInit.HEAVY_LIQUID_NAQUADAH_BUCKET)
		.block(BlockInit.HEAVY_LIQUID_NAQUADAH_BLOCK);
}
