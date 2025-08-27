package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

import java.util.function.Function;

public class SGJourneyRenderTypes extends RenderType
{
	public SGJourneyRenderTypes(String name, VertexFormat format, Mode mode, int bufferSize,
			boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState)
	{
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}
	
	private static final Function<ResourceLocation, RenderType> SYMBOL = Util.memoize(resourceLocation ->
		create("symbol", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setOutputState(ITEM_ENTITY_TARGET)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> EVENT_HORIZON = Util.memoize(resourceLocation ->
			create("event_horizon", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setCullState(CULL)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(NO_OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> EVENT_HORIZON_AMD = Util.memoize(resourceLocation ->
			create("event_horizon_amd", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_EYES_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setCullState(CULL)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(NO_OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> VORTEX = Util.memoize(resourceLocation ->
			create("vortex", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(NO_TRANSPARENCY)
							.setCullState(CULL)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(NO_OVERLAY)
							.createCompositeState(false)));
	
	private static final Function<ResourceLocation, RenderType> SHIELD = Util.memoize(resourceLocation ->
			create("shield", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setCullState(CULL)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(NO_OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> IRIS = Util.memoize(resourceLocation ->
			create("iris", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> STARGATE = Util.memoize(resourceLocation ->
			create("stargate", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(NO_TRANSPARENCY)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> STARGATE_RING = Util.memoize(resourceLocation ->
			create("stargate_ring", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> CHEVRON = Util.memoize(resourceLocation ->
			create("chevron", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(OVERLAY)
							.createCompositeState(true)));
	
	private static final Function<ResourceLocation, RenderType> ENGAGED_CHEVRON = Util.memoize(resourceLocation ->
			create("engaged_chevron", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					RenderType.CompositeState.builder()
							.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
							.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
							.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
							.setLightmapState(LIGHTMAP)
							.setOverlayState(OVERLAY)
							.createCompositeState(true)));

	public static RenderType symbol(ResourceLocation resourceLocation)
	{
		return SYMBOL.apply(resourceLocation);
	}

	public static RenderType eventHorizon(ResourceLocation resourceLocation)
	{
		if(StargateJourney.shouldRenderAMD())
			return EVENT_HORIZON_AMD.apply(resourceLocation);
		
		return EVENT_HORIZON.apply(resourceLocation);
	}

	public static RenderType vortex(ResourceLocation resourceLocation)
	{
		return VORTEX.apply(resourceLocation);
	}

	public static RenderType shield(ResourceLocation resourceLocation)
	{
		return SHIELD.apply(resourceLocation);
	}

	public static RenderType iris(ResourceLocation resourceLocation)
	{
		return IRIS.apply(resourceLocation);
	}
	
	public static RenderType stargate(ResourceLocation resourceLocation)
	{
		return STARGATE.apply(resourceLocation);
	}
	
	public static RenderType stargateRing(ResourceLocation resourceLocation)
	{
		return STARGATE_RING.apply(resourceLocation);
	}
	
	public static RenderType chevron(ResourceLocation resourceLocation)
	{
		return CHEVRON.apply(resourceLocation);
	}
	
	public static RenderType engagedChevron(ResourceLocation resourceLocation)
	{
		return ENGAGED_CHEVRON.apply(resourceLocation);
	}
}
