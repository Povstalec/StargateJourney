package net.povstalec.sgjourney.common.blocks.stargate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public class ClassicStargateBlock extends AbstractStargateBaseBlock
{
	public ClassicStargateBlock(Properties properties)
	{
		super(properties, 8.0D, 0.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.CLASSIC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		 ClassicStargateEntity stargate = new ClassicStargateEntity(pos, state);
		
		 return stargate;
	}
	
	public BlockState ringState()
	{
		return BlockInit.CLASSIC_RING.get().defaultBlockState();
	}
	
	public boolean upgradeStargate(Level level, BlockPos pos, Player player, InteractionHand hand)
	{
		if(!CommonStargateConfig.enable_classic_stargate_upgrades.get())
		{
			player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.upgrading_disabled"), true);
			return true;
		}
		
		ItemStack stack = player.getItemInHand(hand);
		Item item = stack.getItem();
		
		if(item instanceof StargateUpgradeItem upgrade)
		{
			AbstractStargateBlock baseBlock = upgrade.getStargateBaseBlock();
			AbstractStargateRingBlock ringBlock = upgrade.getStargateRingBlock();
			
			if(baseBlock == null || ringBlock == null)
			{
				player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.invalid_upgrade"), true);
				return true;
			}
			
			CompoundTag tag = new CompoundTag();

			BlockEntity oldEntity = level.getBlockEntity(pos);
			if(oldEntity instanceof AbstractStargateEntity stargate)
			{
				if(!level.isClientSide())
					tag = stargate.serializeStargateInfo();
			}
			
			Direction direction = level.getBlockState(pos).getValue(FACING);
			Orientation orientation = level.getBlockState(pos).getValue(ORIENTATION);
			
			// Check if there's enough space for the Stargate (Not all Stargates have the same size)
			for(StargatePart part : baseBlock.getStargateType().getParts())
			{
				BlockState partState = level.getBlockState(part.getRingPos(pos, direction, orientation));
				if(!part.equals(StargatePart.BASE) && (!partState.canBeReplaced() && !(partState.getBlock() instanceof AbstractStargateBlock)))
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.not_enough_space"), true);
					return true;
				}
			}
			
			for(StargatePart part : baseBlock.getStargateType().getParts())
			{
				if(!part.equals(StargatePart.BASE))
				{
					level.setBlock(part.getRingPos(pos, direction, orientation), 
							ringBlock.defaultBlockState()
							.setValue(AbstractStargateRingBlock.PART, part)
							.setValue(AbstractStargateRingBlock.FACING, direction)
							.setValue(AbstractStargateRingBlock.ORIENTATION, orientation), 3);
				}
			}
			
			level.setBlock(pos, baseBlock.defaultBlockState()
					.setValue(AbstractStargateRingBlock.FACING, direction)
					.setValue(AbstractStargateRingBlock.ORIENTATION, orientation), 3);
			
			BlockEntity newEntity = level.getBlockEntity(pos);
			if(newEntity instanceof AbstractStargateEntity stargate)
			{
				if(!level.isClientSide())
				{
					stargate.deserializeStargateInfo(tag, true);
					stargate.addToBlockEntityList();
				}
			}
			
			if(!player.isCreative())
				stack.shrink(1);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		return upgradeStargate(level, pos, player, hand) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.CLASSIC_STARGATE.get(), ClassicStargateEntity::tick);
    }
}
