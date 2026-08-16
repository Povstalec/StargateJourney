package net.povstalec.sgjourney.client.render.level;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.povstalec.stellarview.StellarView;
import net.povstalec.stellarview.api.client.StellarViewRendering;
import net.povstalec.stellarview.client.render.level.util.StellarViewLightmapEffects;
import net.povstalec.stellarview.compatibility.enhancedcelestials.EnhancedCelestialsCompatibility;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StellarViewCompatibility
{
	public static boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
	{
		return StellarViewRendering.renderViewCenterSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
	}
	
	public static void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors)
	{
		StellarViewLightmapEffects.defaultLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
		
		if(StellarView.isEnhancedCelestialsLoaded())
			EnhancedCelestialsCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, colors);
	}
}
