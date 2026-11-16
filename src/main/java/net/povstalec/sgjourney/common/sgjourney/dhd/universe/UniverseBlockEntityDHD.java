package net.povstalec.sgjourney.common.sgjourney.dhd.universe;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.dhd.UniverseDHDEntity;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;

import javax.annotation.Nullable;

public class UniverseBlockEntityDHD extends UniverseDHD implements SGJourneyBlockEntityDHD<UniverseDHDEntity>
{
	protected final UniverseDHDEntity dhd;
	
	public UniverseBlockEntityDHD(UniverseDHDEntity dhd)
	{
		this.dhd = dhd;
		loadFromBlockEntity(dhd);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return dhd.getBlockPos();
	}
	
	@Override
	@Nullable
	public UniverseDHDEntity getDHDBlockEntity()
	{
		return dhd;
	}
	
	@Override
	public MinecraftServer getServer()
	{
		return dhd.getLevel().getServer();
	}
	
	@Override
	public @Nullable ResourceKey<Level> getDimension()
	{
		return dhd.getLevel().dimension();
	}
}
