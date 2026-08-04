package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientSkyConfig;

import javax.annotation.Nullable;

public abstract class SGJourneyDimensionSpecialEffects extends DimensionSpecialEffects
{
	// Milky Way
	public static final ResourceLocation ABYDOS_EFFECTS = StargateJourney.sgjourneyLocation("abydos");
	public static final ResourceLocation CHULAK_EFFECTS = StargateJourney.sgjourneyLocation("chulak");
	public static final ResourceLocation UNITAS_EFFECTS = StargateJourney.sgjourneyLocation("unitas");
	public static final ResourceLocation RIMA_EFFECTS = StargateJourney.sgjourneyLocation("rima");
	public static final ResourceLocation TOLLAN_EFFECTS = StargateJourney.sgjourneyLocation("tollan");
	public static final ResourceLocation CAVUM_TENEBRAE_EFFECTS = StargateJourney.sgjourneyLocation("cavum_tenebrae");
	// Pegasus
	public static final ResourceLocation LANTEA_EFFECTS = StargateJourney.sgjourneyLocation("lantea");
	public static final ResourceLocation ATHOS_EFFECTS = StargateJourney.sgjourneyLocation("athos");
	
	public static final ResourceLocation DESTINY_EFFECTS = StargateJourney.sgjourneyLocation("destiny");
	
	@Nullable
	protected SGJourneySkyRenderer skyRenderer;
	
	public SGJourneyDimensionSpecialEffects(float cloudLevel, boolean hasGround, SkyType skyType, 
			boolean forceBrightLightmap, boolean constantAmbientLight)
	{
		super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
		
		setSkyRenderHandler(this::renderSky);
		setCloudRenderHandler(this::renderClouds);
		setWeatherRenderHandler(this::renderSnowAndRain);
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
	
	public boolean renderClouds(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft, double camX, double camY, double camZ)
    {
        return false;
    }
	
	public boolean renderSky(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft)
    {
		if(customSky())
		{
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			Matrix4f projectionMatrix = poseStack.last().pose();
			boolean isFoggy = Minecraft.getInstance().level.effects().isFoggyAt(Mth.floor(camera.getPosition().x), Mth.floor(camera.getPosition().y)) || Minecraft.getInstance().gui.getBossOverlay().shouldCreateWorldFog();
			Runnable setupFog = () -> FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, Minecraft.getInstance().gameRenderer.getRenderDistance(), isFoggy, partialTick);
			
			if(stellarViewSky())
				return StellarViewCompatibility.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
			else if(skyRenderer != null)
				skyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
			
			return true;
		}
		
        return false;
    }
	
	public boolean renderSnowAndRain(int ticks, float partialTick, ClientLevel level, Minecraft minecraft, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }
	
	/*@Override
	public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
	{
		if(stellarViewSky())
			StellarViewCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
	}*/
	
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
		
		@Override
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
		
		@Override
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
		
		@Override
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
		
		@Override
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
			skyRenderer = new PlanetSkyRenderers.TollanSkyRenderer();
		}
		
		@Override
		public boolean customSky()
		{
			return ClientSkyConfig.custom_rima_sky.get();
		}
	}
	
	public static class Tollan extends SGJourneyDimensionSpecialEffects
	{
		public Tollan()
		{
			super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.TollanSkyRenderer();
		}
		
		@Override
		public boolean customSky()
		{
			return ClientSkyConfig.custom_tollan_sky.get();
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
		
		@Override
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
		
		@Override
		public boolean customSky()
		{
			return ClientSkyConfig.custom_athos_sky.get();
		}
	}
	
	//============================================================================================
	//******************************************Destiny*******************************************
	//============================================================================================
	
	public static class Destiny extends SGJourneyDimensionSpecialEffects
	{
		public Destiny()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
			skyRenderer = new PlanetSkyRenderers.DestinySkyRenderer();
		}
		
		@Override
		@Nullable
		public float[] getSunriseColor(float timeOfDay, float partialTicks)
		{
			return null;
		}
		
		@Override
		public boolean customSky()
		{
			return ClientSkyConfig.custom_destiny_sky.get();
		}
	}
	
	
	
	public static void registerStargateJourneyEffects()
	{
		// Milky Way
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.ABYDOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Abydos());
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.CHULAK_EFFECTS, new SGJourneyDimensionSpecialEffects.Chulak());
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.UNITAS_EFFECTS, new SGJourneyDimensionSpecialEffects.Unitas());
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.RIMA_EFFECTS, new SGJourneyDimensionSpecialEffects.Rima());
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.TOLLAN_EFFECTS, new SGJourneyDimensionSpecialEffects.Tollan());
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.CAVUM_TENEBRAE_EFFECTS, new SGJourneyDimensionSpecialEffects.CavumTenebrae());
		// Pegasus
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.LANTEA_EFFECTS, new SGJourneyDimensionSpecialEffects.Lantea());
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.ATHOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Athos());
		// Destiny
		DimensionSpecialEffects.EFFECTS.put(SGJourneyDimensionSpecialEffects.DESTINY_EFFECTS, new SGJourneyDimensionSpecialEffects.Destiny());
	}
}
