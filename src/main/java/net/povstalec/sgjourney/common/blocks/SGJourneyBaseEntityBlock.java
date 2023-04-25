package net.povstalec.sgjourney.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;

public abstract class SGJourneyBaseEntityBlock extends BaseEntityBlock
{
	protected String listName;
	
	protected SGJourneyBaseEntityBlock(Properties properties, String listName)
	{
		super(properties);
		this.listName = listName;
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity entity = level.getBlockEntity(pos);
            
            if (entity instanceof SGJourneyBlockEntity blockEntity)
                blockEntity.removeFromBlockEntityList();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		String id;
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("ID"))
			id = stack.getTag().getCompound("BlockEntityTag").getString("ID");
		else
			id = "";
		
        tooltipComponents.add(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));

        if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("AddToNetwork") && !stack.getTag().getCompound("BlockEntityTag").getBoolean("AddToNetwork"))
            tooltipComponents.add(Component.literal("Won't be added to network").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
	
	public static ItemStack excludeFromNetwork(ItemStack stack)
	{
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putBoolean("AddToNetwork", false);
		stack.addTagElement("BlockEntityTag", compoundtag);
		
		return stack;
	}
}
