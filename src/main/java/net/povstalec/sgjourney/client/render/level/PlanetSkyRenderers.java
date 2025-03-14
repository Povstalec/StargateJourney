package net.povstalec.sgjourney.client.render.level;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.ClientLevel;

public class PlanetSkyRenderers
{
	//============================================================================================
	//******************************************Milky Way*****************************************
	//============================================================================================
	
	public static class AbydosSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation NUT_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/nut.png");
		
		public AbydosSkyRenderer()
		{
			super(28843L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, NUT_TEXTURE, 50.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-120), (float) Math.toRadians(170));
	        this.renderMoon(bufferbuilder, lastMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(80), (float) Math.toRadians(152.5));
	        this.renderMoon(bufferbuilder, lastMatrix, 35.0F, level.getMoonPhase(), (float) Math.toRadians(-65), (float) Math.toRadians(150));
		}
	}
	
	public static class ChulakSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation DENNAL_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/dennal.png");
		public static final ResourceLocation CHAAPORIS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/chaaporis.png");
		
		public ChulakSkyRenderer()
		{
			super(14812L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, DENNAL_TEXTURE, 30.0F, (float) Math.toRadians(90), (float) Math.toRadians(10));
			this.renderSun(bufferbuilder, lastMatrix, CHAAPORIS_TEXTURE, 30.0F, (float) Math.toRadians(15), (float) Math.toRadians(-10));
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 45.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class CavumTenebraeSkyRenderer extends SGJourneySkyRenderer
	{
		public CavumTenebraeSkyRenderer()
		{
			super(28486L, 1000);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderBlackHole(bufferbuilder, lastMatrix, 70.0F, 0.0F, 0.0F);
		}
	}
	
	public static class UnitasSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation UNITAS_STAR_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/unitas_star.png");
		
		public UnitasSkyRenderer()
		{
			super(87163L, 1800);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, UNITAS_STAR_TEXTURE, 30.0F);
		}
	}
	
	public static class RimaSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation RIMA_STAR_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/rima_star.png");
		
		public RimaSkyRenderer()
		{
			super(87163L, 1800);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, RIMA_STAR_TEXTURE, 30.0F);
			
			this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-126), (float) Math.toRadians(191));
			this.renderMoon(bufferbuilder, lastMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(31), (float) Math.toRadians(152.5));
		}
	}
	
	//============================================================================================
	//******************************************Pegasus*******************************************
	//============================================================================================
	
	public static class LanteaSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation LANTEA_STAR_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/lantea_star.png");
		public LanteaSkyRenderer()
		{
			super(17892L, 2250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, LANTEA_STAR_TEXTURE, 30.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class AthosSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation ATHOS_STAR_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/environment/star/athos_star.png");
		
		public AthosSkyRenderer()
		{
			super(27392L, 1250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, ATHOS_STAR_TEXTURE, 30.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(40), (float) Math.toRadians(180));
		}
	}
}
