package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.povstalec.sgjourney.common.config.ClientSkyConfig;
import net.povstalec.stellarview.StellarView;
import net.povstalec.stellarview.client.render.level.util.StellarViewLightmapEffects;
import net.povstalec.stellarview.client.resourcepack.ViewCenters;
import net.povstalec.stellarview.compatibility.enhancedcelestials.EnhancedCelestialsCompatibility;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StellarViewRendering extends DimensionSpecialEffects
{
	public StellarViewRendering(float cloudLevel, boolean hasGround, SkyType skyType,
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
	
	
	
	public static class StellarViewAbydosEffects extends StellarViewRendering
	{
		public StellarViewAbydosEffects()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_abydos_sky.get())
				return ViewCenters.renderViewCenterSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
			
	        return false;
	    }
		
		@Override
		public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
		{
			if(ClientSkyConfig.custom_abydos_sky.get())
			{
				StellarViewLightmapEffects.defaultLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
				
				if(StellarView.isEnhancedCelestialsLoaded())
					EnhancedCelestialsCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
			}
		}
	}
	
	public static class StellarViewChulakEffects extends StellarViewRendering
	{
		public StellarViewChulakEffects()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_chulak_sky.get())
				return ViewCenters.renderViewCenterSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
			
	        return false;
	    }
		
		@Override
		public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
		{
			if(ClientSkyConfig.custom_chulak_sky.get())
			{
				StellarViewLightmapEffects.defaultLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
				
				if(StellarView.isEnhancedCelestialsLoaded())
					EnhancedCelestialsCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
			}
		}
	}
	
	public static class StellarViewCavumTenebraeEffects extends StellarViewRendering
	{
		public StellarViewCavumTenebraeEffects()
		{
			super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_cavum_tenebrae_sky.get())
				return ViewCenters.renderViewCenterSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
			
	        return false;
	    }
		
		@Override
		public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
		{
			if(ClientSkyConfig.custom_cavum_tenebrae_sky.get())
			{
				StellarViewLightmapEffects.defaultLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
				
				if(StellarView.isEnhancedCelestialsLoaded())
					EnhancedCelestialsCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
			}
		}
	}
	
	public static class StellarViewLanteaEffects extends StellarViewRendering
	{
		public StellarViewLanteaEffects()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			if(ClientSkyConfig.custom_lantea_sky.get())
				return ViewCenters.renderViewCenterSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
			
	        return false;
	    }
		
		@Override
		public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
		{
			if(ClientSkyConfig.custom_lantea_sky.get())
			{
				StellarViewLightmapEffects.defaultLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
				
				if(StellarView.isEnhancedCelestialsLoaded())
					EnhancedCelestialsCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
			}
		}
	}
	
	public static class StellarViewAthosEffects extends StellarViewRendering
	{
		public StellarViewAthosEffects()
		{
			super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
		}
		
		@Override
		public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	    {
			return false;
	    }
	}
	
	public static void registerStellarViewEffects(RegisterDimensionSpecialEffectsEvent event)
	{
		event.register(SGJourneyDimensionSpecialEffects.ABYDOS_EFFECTS, new StellarViewAbydosEffects());
		event.register(SGJourneyDimensionSpecialEffects.CHULAK_EFFECTS, new StellarViewChulakEffects());
		event.register(SGJourneyDimensionSpecialEffects.CAVUM_TENEBRAE_EFFECTS, new StellarViewCavumTenebraeEffects());
		event.register(SGJourneyDimensionSpecialEffects.LANTEA_EFFECTS, new StellarViewLanteaEffects());
		event.register(SGJourneyDimensionSpecialEffects.ATHOS_EFFECTS, new StellarViewAthosEffects());
	}
}
