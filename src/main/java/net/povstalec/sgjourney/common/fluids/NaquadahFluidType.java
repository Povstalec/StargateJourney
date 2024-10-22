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
	private static final ResourceLocation STILL_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");
	private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_flow");
	private static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.withDefaultNamespace("misc/underwater");
	
	public NaquadahFluidType(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
		consumer.accept(new IClientFluidTypeExtensions()
		{
			@Override
			public ResourceLocation getStillTexture()
			{
				return STILL_TEXTURE;
			}
			
			@Override
			public ResourceLocation getFlowingTexture()
			{
				return FLOWING_TEXTURE;
			}
			
			@Override
			public @Nullable ResourceLocation getOverlayTexture()
			{
				return OVERLAY_TEXTURE;
			}
			
			@Override
			public int getTintColor()
			{
				return 0xffb0f329;
			}
			
			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
			{
				return new Vector3f(115.0F / 255.0F, 197.0F / 255.0F, 34.0F / 255.0F);
			}
			
			@Override
			public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
			{
				RenderSystem.setShaderFogStart(1f);
				RenderSystem.setShaderFogEnd(6f);
			}
		});
    }

}
