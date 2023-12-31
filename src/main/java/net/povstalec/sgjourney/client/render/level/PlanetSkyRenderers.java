package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.multiplayer.ClientLevel;

public class PlanetSkyRenderers
{
	public static class AbydosSkyRenderer extends SGJourneySkyRenderer
	{
		public AbydosSkyRenderer()
		{
			super(28843L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, 50.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-120), (float) Math.toRadians(170));
	        this.renderMoon(bufferbuilder, lastMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(80), (float) Math.toRadians(152.5));
	        this.renderMoon(bufferbuilder, lastMatrix, 35.0F, level.getMoonPhase(), (float) Math.toRadians(-65), (float) Math.toRadians(150));
		}
	}
	
	public static class ChulakSkyRenderer extends SGJourneySkyRenderer
	{
		public ChulakSkyRenderer()
		{
			super(14812L, 1500);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, 30.0F);
	        
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
			this.renderBlackHole(bufferbuilder, lastMatrix, 50.0F, 0.0F, 0.0F);
		}
	}
	
	public static class LanteaSkyRenderer extends SGJourneySkyRenderer
	{
		public LanteaSkyRenderer()
		{
			super(17892L, 2250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, 30.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), 0.0F, (float) Math.toRadians(180));
		}
	}
	
	public static class AthosSkyRenderer extends SGJourneySkyRenderer
	{
		public AthosSkyRenderer()
		{
			super(27392L, 1250);
		}
		
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, 30.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(40), (float) Math.toRadians(180));
		}
	}
}
