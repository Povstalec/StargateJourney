package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientSkyConfig;

public abstract class SGJourneyDimensionSpecialEffects extends DimensionSpecialEffects
{
	public static final ResourceLocation OVERWORLD_EFFECTS = new ResourceLocation("overworld");
	
	public static final ResourceLocation ABYDOS_EFFECTS = new ResourceLocation(StargateJourney.MODID, "abydos");
	public static final ResourceLocation CHULAK_EFFECTS = new ResourceLocation(StargateJourney.MODID, "chulak");
	public static final ResourceLocation CAVUM_TENEBRAE_EFFECTS = new ResourceLocation(StargateJourney.MODID, "cavum_tenebrae");
	public static final ResourceLocation LANTEA_EFFECTS = new ResourceLocation(StargateJourney.MODID, "lantea");
	public static final ResourceLocation ATHOS_EFFECTS = new ResourceLocation(StargateJourney.MODID, "athos");
	
	protected SGJourneySkyRenderer skyRenderer;
	
	public SGJourneyDimensionSpecialEffects(float cloudLevel, boolean hasGround, SkyType skyType, 
			boolean forceBrightLightmap, boolean constantAmbientLight)
	{
		super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
	}

	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight)
	{
		return biomeFogColor.multiply((double)(daylight * 0.94F + 0.06F), (double)(daylight * 0.94F + 0.06F), (double)(daylight * 0.91F + 0.09F));
	}

	@Override
	public boolean isFoggyAt(int x, int y)
	{
		return false;
	}

	@Override
	public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix)
    {
        return false;
    }
	
	@Override
	public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
    {
        return false;
    }
	
	@Override
	public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }
	
	
	
	public static class Abydos extends SGJourneyDimensionSpecialEffects
	{
		public Abydos()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.AbydosSkyRenderer();
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_abydos_sky.get())
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
	        return ClientSkyConfig.custom_abydos_sky.get();
	    }
	}
	
	public static class Chulak extends SGJourneyDimensionSpecialEffects
	{
		public Chulak()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.ChulakSkyRenderer();
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_chulak_sky.get())
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
	        return ClientSkyConfig.custom_chulak_sky.get();
	    }
	}
	
	public static class CavumTenebrae extends SGJourneyDimensionSpecialEffects
	{
		public CavumTenebrae()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.CavumTenebraeSkyRenderer();
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_cavum_tenebrae_sky.get())
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
	        return ClientSkyConfig.custom_cavum_tenebrae_sky.get();
	    }
	}
	
	public static class Lantea extends SGJourneyDimensionSpecialEffects
	{
		public Lantea()
		{
			super(386.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.LanteaSkyRenderer();
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_lantea_sky.get())
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
	        return ClientSkyConfig.custom_lantea_sky.get();
	    }
	}
	
	public static class Athos extends SGJourneyDimensionSpecialEffects
	{
		public Athos()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.AthosSkyRenderer();
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_athos_sky.get())
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
	        return ClientSkyConfig.custom_athos_sky.get();
	    }
	}
	
	public static void registerStargateJourneyEffects(RegisterDimensionSpecialEffectsEvent event)
	{
		event.register(SGJourneyDimensionSpecialEffects.ABYDOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Abydos());
    	event.register(SGJourneyDimensionSpecialEffects.CHULAK_EFFECTS, new SGJourneyDimensionSpecialEffects.Chulak());
    	event.register(SGJourneyDimensionSpecialEffects.CAVUM_TENEBRAE_EFFECTS, new SGJourneyDimensionSpecialEffects.CavumTenebrae());
    	event.register(SGJourneyDimensionSpecialEffects.LANTEA_EFFECTS, new SGJourneyDimensionSpecialEffects.Lantea());
    	event.register(SGJourneyDimensionSpecialEffects.ATHOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Athos());
	}
}
