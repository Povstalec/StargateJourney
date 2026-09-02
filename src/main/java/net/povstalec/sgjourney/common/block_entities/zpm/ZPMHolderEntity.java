package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class ZPMHolderEntity extends AbstractZPMHolderEntity
{
	public ZPMHolderEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_HOLDER.get(), pos, state);
	}
	
	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_zpm_holder_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
