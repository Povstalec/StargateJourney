package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SGJourneyRenderTypes extends RenderType
{
	public SGJourneyRenderTypes(String name, VertexFormat format, Mode mode, int bufferSize,
			boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState)
	{
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType symbol(ResourceLocation resourceLocation)
	{
		return create("symbol", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true));
	}

	public static RenderType eventHorizonFront(ResourceLocation resourceLocation, float xOffset, float zOffset)
	{
		return create("event_horizon_front", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(xOffset, zOffset))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(false));
	}

	public static RenderType eventHorizonBack(ResourceLocation resourceLocation, float xOffset, float zOffset)
	{
		return create("event_horizon_back", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(xOffset, zOffset))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(CULL)
				.setWriteMaskState(COLOR_WRITE)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(true));
	}

	public static RenderType shield(ResourceLocation resourceLocation)
	{
		return create("shield", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(CULL)
				.setWriteMaskState(COLOR_WRITE)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(true));
	}

	public static RenderType vortex(ResourceLocation resourceLocation, float xOffset, float zOffset)
	{
		return create("vortex", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(xOffset, zOffset))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(false));
	}
	
	public static RenderType stargate(ResourceLocation resourceLocation)
	{
		return create("stargate", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0.0F, -0.0625F))
				.setTransparencyState(NO_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
	
	public static RenderType stargateRing(ResourceLocation resourceLocation)
	{
		return create("stargate_ring", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0.0F, -0.0625F))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
	
	public static RenderType chevron(ResourceLocation resourceLocation)
	{
		return create("chevron", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0.0F, -0.0625F))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
	
	public static RenderType engagedChevron(ResourceLocation resourceLocation)
	{
		return create("engaged_chevron", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0.0F, -0.0625F))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
}
