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
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.createCompositeState(true));
	}

	public static RenderType eventHorizonFront(ResourceLocation resourceLocation)
	{
		return create("event_horizon_front", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(CULL)
				.setWriteMaskState(COLOR_WRITE) //TODO Maybe remove this
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(false));
	}

	public static RenderType eventHorizonBack(ResourceLocation resourceLocation)
	{
		return create("event_horizon_back", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(CULL)
				.setWriteMaskState(COLOR_WRITE) //TODO Maybe remove this
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(true));
	}

	public static RenderType vortex(ResourceLocation resourceLocation)
	{
		return create("vortex", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(false));
	}

	public static RenderType shield(ResourceLocation resourceLocation)
	{
		return create("shield", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(CULL)
				.setWriteMaskState(COLOR_WRITE) //TODO Maybe remove this
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(true));
	}

	public static RenderType iris(ResourceLocation resourceLocation)
	{
		return create("iris", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
	
	public static RenderType stargate(ResourceLocation resourceLocation)
	{
		return create("stargate", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
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
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
	
	public static RenderType engagedChevron(ResourceLocation resourceLocation)
	{
		return create("engaged_chevron", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, 
				RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}
}
