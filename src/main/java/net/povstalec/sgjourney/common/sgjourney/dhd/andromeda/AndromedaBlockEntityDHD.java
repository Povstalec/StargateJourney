package net.povstalec.sgjourney.common.sgjourney.dhd.andromeda;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.dhd.AndromedaDHDEntity;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;

import javax.annotation.Nullable;

public class AndromedaBlockEntityDHD extends AndromedaDHD implements SGJourneyBlockEntityDHD<AndromedaDHDEntity>
{
	protected final AndromedaDHDEntity dhd;
	
	public AndromedaBlockEntityDHD(AndromedaDHDEntity dhd)
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
	public AndromedaDHDEntity getDHDBlockEntity()
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
	public @Nullable Level getLevel()
	{
		return dhd.getLevel();
	}
}
