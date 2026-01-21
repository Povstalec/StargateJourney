package net.povstalec.sgjourney.common.blocks.transporter;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

public abstract class AbstractTransporterBlock extends BaseEntityBlock
{
	protected AbstractTransporterBlock(Properties properties)
	{
		super(properties);
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
        if(state.getBlock() != newState.getBlock())
        {
            BlockEntity entity = level.getBlockEntity(pos);
            
            if(entity instanceof AbstractTransporterEntity transporterEntity)
			{
				transporterEntity.disconnectTransporter();
				transporterEntity.removeTransporterFromNetwork();
			}
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof AbstractTransporterEntity transporter)
		{
			if(!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(BlockInit.GOAULD_TRANSPORT_RINGS.get());
				
				blockentity.saveToItem(itemstack);
				if(transporter.hasCustomName())
					itemstack.setHoverName(transporter.getCustomName());
					

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		String id = blockEntityTag != null && blockEntityTag.contains(AbstractTransporterEntity.TRANSPORTER_ID) ? blockEntityTag.getString(AbstractTransporterEntity.TRANSPORTER_ID) : "-";
		
		tooltipComponents.add(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));

        if(blockEntityTag != null && blockEntityTag.contains(AbstractTransporterEntity.GENERATION_STEP, CompoundTag.TAG_BYTE)
				&& StructureGenEntity.Step.SETUP == StructureGenEntity.Step.fromByte(blockEntityTag.getByte(AbstractTransporterEntity.GENERATION_STEP)))
            tooltipComponents.add(Component.translatable("tooltip.sgjourney.generates_inside_structure").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
