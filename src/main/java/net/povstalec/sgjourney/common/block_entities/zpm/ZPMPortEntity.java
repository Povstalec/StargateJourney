package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.blocks.zpm.HorizontalDirectionalZPMEnergyHolderBlock;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class ZPMPortEntity extends AbstractZPMEnergyExtractorEntity
{
	public ZPMPortEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_PORT.get(), pos, state);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return side == getBlockState().getValue(HorizontalDirectionalZPMEnergyHolderBlock.FACING).getOpposite();
	}

	@Override
	public long getMaxEnergyExtract()
	{
		return CommonZPMConfig.zpm_port_max_transfer.get();
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, ZPMPortEntity hub)
	{
		if(level.isClientSide())
			return;
		
		hub.outputEnergy(state.getValue(HorizontalDirectionalZPMEnergyHolderBlock.FACING).getOpposite());
	}
	
	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_zpm_port_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
