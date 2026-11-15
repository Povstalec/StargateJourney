package net.povstalec.sgjourney.common.sgjourney.dhd.classic;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;

import javax.annotation.Nullable;

public class ClassicBlockEntityDHD extends ClassicDHD implements SGJourneyBlockEntityDHD<ClassicDHDEntity>
{
	protected final ClassicDHDEntity dhd;
	
	public ClassicBlockEntityDHD(ClassicDHDEntity dhd)
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
	public ClassicDHDEntity getDHDBlockEntity()
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
