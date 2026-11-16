package net.povstalec.sgjourney.common.sgjourney.dhd.milky_way;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;

import javax.annotation.Nullable;

public class MilkyWayBlockEntityDHD extends MilkyWayDHD implements SGJourneyBlockEntityDHD<MilkyWayDHDEntity>
{
	protected final MilkyWayDHDEntity dhd;
	
	public MilkyWayBlockEntityDHD(MilkyWayDHDEntity dhd)
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
	public MilkyWayDHDEntity getDHDBlockEntity()
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
	
	@Override
	public void updateFromBlockEntity(MilkyWayDHDEntity stargateEntity) {}
}
