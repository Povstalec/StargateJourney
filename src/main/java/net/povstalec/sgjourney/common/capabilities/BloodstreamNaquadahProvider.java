package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class BloodstreamNaquadahProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
	public static Capability<BloodstreamNaquadah> BLOODSTREAM_NAQUADAH = CapabilityManager.get(new CapabilityToken<BloodstreamNaquadah>() {});
	private BloodstreamNaquadah hasNaquadah = null;
	private final LazyOptional<BloodstreamNaquadah> optional = LazyOptional.of(this::getOrCreateNaquadahBloodstream);
	
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		CompoundTag tag = new CompoundTag();
		getOrCreateNaquadahBloodstream().saveData(tag);
		
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
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
