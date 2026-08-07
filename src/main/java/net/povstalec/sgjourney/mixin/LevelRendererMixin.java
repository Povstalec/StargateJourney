package net.povstalec.sgjourney.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.povstalec.sgjourney.client.render.level.SGJourneyDimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
	@Inject(method = "renderSky", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraftforge/client/ISkyRenderHandler;render(IFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Minecraft;)V",
		shift = At.Shift.AFTER))
	public void renderSky(PoseStack stack, Matrix4f matrix, float partialTicks, Camera camera, boolean isFoggy, Runnable setupFog, CallbackInfo ci)
	{
		ClientLevel level = Minecraft.getInstance().level;
		
		if(level != null && level.effects() instanceof SGJourneyDimensionSpecialEffects sgJourneyDimensionSpecialEffects)
			sgJourneyDimensionSpecialEffects.renderSky(level, partialTicks, stack, camera, matrix, isFoggy, setupFog);
	}
}
