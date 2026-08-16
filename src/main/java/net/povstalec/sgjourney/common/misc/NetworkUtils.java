package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class NetworkUtils
{
	public static void openMenu(ServerPlayer player, MenuProvider containerProvider, BlockPos pos)
	{
		player.openMenu(containerProvider, buf -> buf.writeBlockPos(pos));
	}
}
