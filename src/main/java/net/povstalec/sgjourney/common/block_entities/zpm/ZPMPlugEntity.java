package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.blocks.zpm.ZPMPlugBlock;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class ZPMPlugEntity extends AbstractZPMEnergyExtractorEntity
{
	public ZPMPlugEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_PLUG.get(), pos, state);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return side == Direction.DOWN || side.getAxis() == getBlockState().getValue(ZPMPlugBlock.FACING).getAxis();
	}

	@Override
	public long getMaxEnergyExtract()
	{
		return CommonZPMConfig.zpm_plug_max_transfer.get();
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, ZPMPlugEntity hub)
	{
		if(level.isClientSide())
			return;
		
		hub.outputEnergy(Direction.DOWN);
		if(state.getValue(ZPMPlugBlock.FACING).getAxis() == Direction.Axis.X)
		{
			hub.outputEnergy(Direction.WEST);
			hub.outputEnergy(Direction.EAST);
		}
		else
		{
			hub.outputEnergy(Direction.NORTH);
			hub.outputEnergy(Direction.SOUTH);
		}
	}
	
	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_zpm_plug_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
