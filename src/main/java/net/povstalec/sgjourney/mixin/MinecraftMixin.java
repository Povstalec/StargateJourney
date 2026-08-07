package net.povstalec.sgjourney.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.povstalec.sgjourney.client.render.level.SGJourneyDimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
	@Inject(method = "<init>", at = @At("TAIL"))
	public void initMinecraft(GameConfig config, CallbackInfo ci)
	{
		SGJourneyDimensionSpecialEffects.registerStargateJourneyEffects();
	}
}
