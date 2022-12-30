package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SGJourneyRenderTypes extends RenderType
{
	public SGJourneyRenderTypes(String p_173178_, VertexFormat p_173179_, Mode p_173180_, int p_173181_,
			boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}

	public static RenderType eventHorizon(ResourceLocation resourceLocation, float p_110438_, float p_110439_) {
	      return create("event_horizon", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(p_110438_, p_110439_)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(NO_OVERLAY).createCompositeState(false));
	   }
}
