package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoauldHostProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
	public static Capability<GoauldHost> GOAULD_HOST = CapabilityManager.get(new CapabilityToken<GoauldHost>() {});
	private GoauldHost goauldHost = null;
	private final LazyOptional<GoauldHost> optional = LazyOptional.of(this::getOrCreateGoauldHost);
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		getOrCreateGoauldHost().saveData(tag);
		
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		getOrCreateGoauldHost().loadData(tag);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == GOAULD_HOST)
			return optional.cast();
		return LazyOptional.empty();
	}
	
	private GoauldHost getOrCreateGoauldHost()
	{
		if(this.goauldHost == null)
			this.goauldHost = new GoauldHost();
		return this.goauldHost;
	}
}
