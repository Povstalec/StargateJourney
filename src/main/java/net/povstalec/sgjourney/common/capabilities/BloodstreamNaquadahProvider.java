package net.povstalec.sgjourney.common.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class BloodstreamNaquadahProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
	public static Capability<BloodstreamNaquadah> BLOODSTREAM_NAQUADAH = CapabilityManager.get(new CapabilityToken<BloodstreamNaquadah>() {});
	private BloodstreamNaquadah hasNaquadah = null;
	private final LazyOptional<BloodstreamNaquadah> optional = LazyOptional.of(this::getOrCreateNaquadahBloodstream);
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		getOrCreateNaquadahBloodstream().saveData(tag);
		
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		getOrCreateNaquadahBloodstream().loadData(tag);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == BLOODSTREAM_NAQUADAH)
			return optional.cast();
		return LazyOptional.empty();
	}
	
	private BloodstreamNaquadah getOrCreateNaquadahBloodstream()
	{
		if(this.hasNaquadah == null)
			this.hasNaquadah = new BloodstreamNaquadah();
		return this.hasNaquadah;
	}
	
}
