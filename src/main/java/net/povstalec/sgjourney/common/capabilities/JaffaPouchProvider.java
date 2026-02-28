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

public class JaffaPouchProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
	public static final Capability<JaffaPouch> JAFFA_POUCH = CapabilityManager.get(new CapabilityToken<JaffaPouch>() {});
	private JaffaPouch jaffaPouch = null;
	private final LazyOptional<JaffaPouch> optional = LazyOptional.of(this::getOrCreateJaffaPouch);
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		getOrCreateJaffaPouch().saveData(tag);
		
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		getOrCreateJaffaPouch().loadData(tag);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == JAFFA_POUCH)
			return optional.cast();
		return LazyOptional.empty();
	}
	
	private JaffaPouch getOrCreateJaffaPouch()
	{
		if(this.jaffaPouch == null)
			this.jaffaPouch = new JaffaPouch();
		return this.jaffaPouch;
	}
}
