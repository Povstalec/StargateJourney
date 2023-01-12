package net.povstalec.sgjourney.blocks;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.block_entities.AbstractDHDEntity;
import net.povstalec.sgjourney.block_entities.ClassicDHDEntity;
import net.povstalec.sgjourney.client.screens.ClassicDHDScreen;

public class ClassicDHDBlock extends AbstractDHDBlock
{

	public ClassicDHDBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		ClassicDHDEntity dhd = new ClassicDHDEntity(pos, state);
		
		return dhd;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		
    	if (blockEntity instanceof AbstractDHDEntity dhd) 
    	{
    		dhd.getNearestStargate(16);
    		
    		if(level.isClientSide())
    			Minecraft.getInstance().setScreen(new ClassicDHDScreen(dhd, Component.translatable("screen.sgjourney.dhd")));
    	}
        return InteractionResult.SUCCESS;
    }
}
