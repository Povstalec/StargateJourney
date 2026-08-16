package net.povstalec.sgjourney.common.fluids;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;

public class NaquadahFluidType extends FluidType
{
	public static final ResourceLocation STILL_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");
	public static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_flow");
	public static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.withDefaultNamespace("misc/underwater");
	
	public NaquadahFluidType(Properties properties)
	{
		super(properties);
	}
}
