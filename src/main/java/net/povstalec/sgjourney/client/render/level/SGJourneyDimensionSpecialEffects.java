package net.povstalec.sgjourney.client.render.level;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientSkyConfig;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public abstract class SGJourneyDimensionSpecialEffects extends DimensionSpecialEffects
{
	// Milky Way
	public static final ResourceLocation ABYDOS_EFFECTS = new ResourceLocation(StargateJourney.MODID, "abydos");
	public static final ResourceLocation CHULAK_EFFECTS = new ResourceLocation(StargateJourney.MODID, "chulak");
	public static final ResourceLocation UNITAS_EFFECTS = new ResourceLocation(StargateJourney.MODID, "unitas");
	public static final ResourceLocation RIMA_EFFECTS = new ResourceLocation(StargateJourney.MODID, "rima");
	public static final ResourceLocation CAVUM_TENEBRAE_EFFECTS = new ResourceLocation(StargateJourney.MODID, "cavum_tenebrae");
	// Pegasus
	public static final ResourceLocation LANTEA_EFFECTS = new ResourceLocation(StargateJourney.MODID, "lantea");
	public static final ResourceLocation ATHOS_EFFECTS = new ResourceLocation(StargateJourney.MODID, "athos");
	
	@Nullable
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
		if(customSky())
		{
			if(stellarViewSky())
				return StellarViewCompatibility.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
			else if(skyRenderer != null)
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
			return true;
		}
		
        return false;
    }
	
	@Override
	public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }
	
	@Override
	public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
	{
		if(stellarViewSky())
			StellarViewCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
	}
	
	public boolean stellarViewSky()
	{
		return StargateJourney.isStellarViewLoaded();
	}
	
	public boolean customSky()
	{
		return true;
	}
	
	//============================================================================================
	//******************************************Milky Way*****************************************
	//============================================================================================
	
	public static class Abydos extends SGJourneyDimensionSpecialEffects
	{
		public Abydos()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.AbydosSkyRenderer();
		}
		
		public boolean customSky()
		{
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
		
		public boolean customSky()
		{
			return ClientSkyConfig.custom_chulak_sky.get();
		}
	}
	
	public static class CavumTenebrae extends SGJourneyDimensionSpecialEffects
	{
		public CavumTenebrae()
		{
			super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.CavumTenebraeSkyRenderer();
		}
		
		public boolean customSky()
		{
			return ClientSkyConfig.custom_cavum_tenebrae_sky.get();
		}
	}
	
	public static class Unitas extends SGJourneyDimensionSpecialEffects
	{
		public Unitas()
		{
			super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.UnitasSkyRenderer();
		}
		
		public boolean customSky()
		{
			return ClientSkyConfig.custom_unitas_sky.get();
		}
	}
	
	public static class Rima extends SGJourneyDimensionSpecialEffects
	{
		public Rima()
		{
			super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.RimaSkyRenderer();
		}
		
		public boolean customSky()
		{
			return ClientSkyConfig.custom_rima_sky.get();
		}
	}
	
	//============================================================================================
	//******************************************Pegasus*******************************************
	//============================================================================================
	
	public static class Lantea extends SGJourneyDimensionSpecialEffects
	{
		public Lantea()
		{
			super(386.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.LanteaSkyRenderer();
		}
		
		public boolean customSky()
		{
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
		
		public boolean customSky()
		{
			return ClientSkyConfig.custom_athos_sky.get();
		}
	}
	
	
	
	public static void registerStargateJourneyEffects(RegisterDimensionSpecialEffectsEvent event)
	{
		// Milky Way
		event.register(SGJourneyDimensionSpecialEffects.ABYDOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Abydos());
    	event.register(SGJourneyDimensionSpecialEffects.CHULAK_EFFECTS, new SGJourneyDimensionSpecialEffects.Chulak());
		event.register(SGJourneyDimensionSpecialEffects.UNITAS_EFFECTS, new SGJourneyDimensionSpecialEffects.Unitas());
		event.register(SGJourneyDimensionSpecialEffects.RIMA_EFFECTS, new SGJourneyDimensionSpecialEffects.Rima());
    	event.register(SGJourneyDimensionSpecialEffects.CAVUM_TENEBRAE_EFFECTS, new SGJourneyDimensionSpecialEffects.CavumTenebrae());
		// Pegasus
    	event.register(SGJourneyDimensionSpecialEffects.LANTEA_EFFECTS, new SGJourneyDimensionSpecialEffects.Lantea());
    	event.register(SGJourneyDimensionSpecialEffects.ATHOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Athos());
	}
}
