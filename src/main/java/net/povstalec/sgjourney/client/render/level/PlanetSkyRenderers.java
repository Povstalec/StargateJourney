package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.Tesselator;
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
		public AbydosSkyRenderer()
		{
			super(28843L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, 50.0F);
	        
	        this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-120), (float) Math.toRadians(170));
	        this.renderMoon(tesselator, modelViewMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(80), (float) Math.toRadians(152.5));
	        this.renderMoon(tesselator, modelViewMatrix, 35.0F, level.getMoonPhase(), (float) Math.toRadians(-65), (float) Math.toRadians(150));
		}
	}
	
	public static class ChulakSkyRenderer extends SGJourneySkyRenderer
	{
		public ChulakSkyRenderer()
		{
			super(14812L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, 30.0F, (float) Math.toRadians(90), (float) Math.toRadians(10));
			this.renderSun(tesselator, modelViewMatrix, 30.0F, (float) Math.toRadians(15), (float) Math.toRadians(-10));
	        
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
		public UnitasSkyRenderer()
		{
			super(87163L, 1800);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, 30.0F);
			
			this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	//============================================================================================
	//******************************************Pegasus*******************************************
	//============================================================================================
	
	public static class LanteaSkyRenderer extends SGJourneySkyRenderer
	{
		public LanteaSkyRenderer()
		{
			super(17892L, 2250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, 30.0F);
	        
	        this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class AthosSkyRenderer extends SGJourneySkyRenderer
	{
		public AthosSkyRenderer()
		{
			super(27392L, 1250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
		{
			this.renderSun(tesselator, modelViewMatrix, 30.0F);
	        
	        this.renderMoon(tesselator, modelViewMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(40), (float) Math.toRadians(180));
		}
	}
}
