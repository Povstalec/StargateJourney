package net.povstalec.sgjourney.client;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.client.screens.ArcheologistNotebookScreen;
import net.povstalec.sgjourney.client.screens.DialerScreen;
import net.povstalec.sgjourney.client.screens.GDOScreen;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public class ClientAccess
{
	protected static Minecraft minecraft = Minecraft.getInstance();
	
	public static void openArcheologistNotebookScreen(UUID playerId, boolean mainHand, CompoundTag tag)
	{
		minecraft.setScreen(new ArcheologistNotebookScreen(playerId, mainHand, tag));
	}
	
	public static void updateDialer(UUID playerId)
	{
		minecraft.setScreen(new DialerScreen());
	}
	
	public static void openGDOScreen(UUID playerId, boolean mainHand, String idc, int frequency)
	{
		minecraft.setScreen(new GDOScreen(playerId, mainHand, idc, frequency));
	}
	
	public static void spawnStargateParticles(BlockPos pos, HashMap<StargatePart, BlockState> blockStates)
	{
		final BlockState state = minecraft.level.getBlockState(pos);
		
		if(state.getBlock() instanceof final AbstractStargateBlock stargateBlock)
		{
			StargatePart part = state.getValue(AbstractStargateBlock.PART);
			Direction direction = state.getValue(AbstractStargateBlock.FACING);
			Orientation orientation = state.getValue(AbstractStargateBlock.ORIENTATION);
			
			BlockPos basePos = part.getBaseBlockPos(pos, direction, orientation);
			
			for(Map.Entry<StargatePart, BlockState> entry : blockStates.entrySet())
			{
				BlockPos coverPos = entry.getKey().getRingPos(basePos, direction, orientation);
				
				minecraft.particleEngine.destroy(coverPos, stargateBlock.defaultBlockState());
			}
		}
	}
}