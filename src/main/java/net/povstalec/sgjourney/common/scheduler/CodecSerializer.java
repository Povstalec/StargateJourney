package net.povstalec.sgjourney.common.scheduler;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.TimerCallback;
import net.povstalec.sgjourney.StargateJourney;
import org.jetbrains.annotations.NotNull;

public class CodecSerializer<T, C extends TimerCallback<T>> extends TimerCallback.Serializer<T, C> {

	private final Codec<C> codec;

	public CodecSerializer(ResourceLocation pId, Class<C> pCls, Codec<C> codec)
	{
		super(pId, pCls);
		this.codec = codec;
	}

	@Override
	public void serialize(@NotNull CompoundTag compoundTag, @NotNull C timerCallback)
	{
		codec.encodeStart(NbtOps.INSTANCE, timerCallback).resultOrPartial(StargateJourney.LOGGER::error).ifPresent(
				data -> compoundTag.merge((CompoundTag) data));
	}

	@Override
	public @NotNull C deserialize(@NotNull CompoundTag compoundTag)
	{
		// The potentially thrown error is caught by the timer, so it won't crash the game.
		return codec.parse(NbtOps.INSTANCE, compoundTag).resultOrPartial(StargateJourney.LOGGER::error).orElseThrow();
	}
}
