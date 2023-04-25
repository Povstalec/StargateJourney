package net.povstalec.sgjourney.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.energy_gen.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.block_entities.energy_gen.NaquadahGeneratorMarkIIEntity;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

public class NaquadahGeneratorMarkIIBlock extends NaquadahGeneratorBlock
{
	
	public NaquadahGeneratorMarkIIBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new NaquadahGeneratorMarkIIEntity(pos, state);
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof NaquadahGeneratorEntity)
		{
			if (!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), NaquadahGeneratorEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	long capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
    	
    	long energyPerTick = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_energy_per_tick.get();
    	
    	int energy = 0;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.literal("Energy: " + energy + "/" + capacity +" FE").withStyle(ChatFormatting.DARK_RED));
        tooltipComponents.add(Component.literal(energyPerTick + " FE/Tick").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
