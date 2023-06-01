package net.povstalec.sgjourney.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;

public class SoundAccess
{
	protected static Minecraft minecraft = Minecraft.getInstance();
	
    public static void playWormholeIdleSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    		stargate.playWormholeIdleSound();
    }
	
    public static void playRotationSound(BlockPos pos, boolean stop)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stop)
    			stargate.stopRotationSound();
    		else
    			stargate.playRotationSound();
    	}
    }
	
    public static void playMilkyWayBuildupSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof MilkyWayStargateEntity stargate)
    		stargate.playBuildupSound();
    }
}
