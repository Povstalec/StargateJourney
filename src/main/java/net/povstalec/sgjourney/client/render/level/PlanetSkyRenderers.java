package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import org.joml.Matrix4f;

import net.minecraft.client.multiplayer.ClientLevel;

public class PlanetSkyRenderers
{
	//============================================================================================
	//******************************************Milky Way*****************************************
	//============================================================================================
	
	public static class AbydosSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation NUT_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/nut.png");
		
		public AbydosSkyRenderer()
		{
			super(28843L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, NUT_TEXTURE, 50.0F);
	        
	        this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-120), (float) Math.toRadians(170));
	        this.renderMoon(tesselator, modelViewMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(80), (float) Math.toRadians(152.5));
	        this.renderMoon(tesselator, modelViewMatrix, 35.0F, level.getMoonPhase(), (float) Math.toRadians(-65), (float) Math.toRadians(150));
		}
	}
	
	public static class ChulakSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation DENNAL_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/dennal.png");
		public static final ResourceLocation CHAAPORIS_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/chaaporis.png");
		
		public ChulakSkyRenderer()
		{
			super(14812L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, DENNAL_TEXTURE, 30.0F, (float) Math.toRadians(90), (float) Math.toRadians(10));
			this.renderSun(tesselator, modelViewMatrix, CHAAPORIS_TEXTURE, 30.0F, (float) Math.toRadians(15), (float) Math.toRadians(-10));
	        
	        this.renderMoon(tesselator, modelViewMatrix, 45.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class CavumTenebraeSkyRenderer extends SGJourneySkyRenderer
	{
		public CavumTenebraeSkyRenderer()
		{
			super(28486L, 1000);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderBlackHole(tesselator, modelViewMatrix, 70.0F, 0.0F, 0.0F);
		}
	}
	
	public static class UnitasSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation UNITAS_STAR_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/unitas_star.png");
		
		public UnitasSkyRenderer()
		{
			super(87163L, 1800);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, UNITAS_STAR_TEXTURE, 30.0F);
			
			this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class RimaSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation RIMA_STAR_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/rima_star.png");
		
		public RimaSkyRenderer()
		{
			super(87163L, 1800);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, RIMA_STAR_TEXTURE, 30.0F);
			
			this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-126), (float) Math.toRadians(191));
			this.renderMoon(tesselator, modelViewMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(31), (float) Math.toRadians(152.5));
		}
	}
	
	//============================================================================================
	//******************************************Pegasus*******************************************
	//============================================================================================
	
	public static class LanteaSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation LANTEA_STAR_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/lantea_star.png");
		public LanteaSkyRenderer()
		{
			super(17892L, 2250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, LANTEA_STAR_TEXTURE, 30.0F);
	        
	        this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class AthosSkyRenderer extends SGJourneySkyRenderer
	{
		public static final ResourceLocation ATHOS_STAR_TEXTURE = StargateJourney.sgjourneyLocation("textures/environment/star/athos_star.png");
		
		public AthosSkyRenderer()
		{
			super(27392L, 1250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, ATHOS_STAR_TEXTURE, 30.0F);
	        
	        this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(40), (float) Math.toRadians(180));
		}
	}
}
