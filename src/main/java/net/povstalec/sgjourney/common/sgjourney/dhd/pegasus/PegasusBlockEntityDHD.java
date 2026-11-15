package net.povstalec.sgjourney.common.sgjourney.dhd.pegasus;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;

import javax.annotation.Nullable;

public class PegasusBlockEntityDHD extends PegasusDHD implements SGJourneyBlockEntityDHD<PegasusDHDEntity>
{
	protected final PegasusDHDEntity dhd;
	
	public PegasusBlockEntityDHD(PegasusDHDEntity dhd)
	{
		this.dhd = dhd;
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return dhd.getBlockPos();
	}
	
	@Override
	@Nullable
	public PegasusDHDEntity getDHDBlockEntity()
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
