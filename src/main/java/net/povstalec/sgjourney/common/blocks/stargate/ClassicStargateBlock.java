package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

public class ClassicStargateBlock extends RotatingStargateBaseBlock
{
	public ClassicStargateBlock(Properties properties)
	{
		super(properties, 8.0D, 0.0D);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new ClassicStargateEntity(pos, state);
	}
	
	@Override
	public AbstractStargateRingBlock getRing()
	{
		return BlockInit.CLASSIC_RING.get();
	}
	
	@Override
	public AbstractShieldingBlock getIris()
	{
		return BlockInit.CLASSIC_SHIELDING.get();
	}

	@Override
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
			Optional<AbstractStargateBaseBlock> baseBlock = upgrade.getStargateBaseBlock(stack);
			
			if(!baseBlock.isPresent())
			{
				player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.invalid_upgrade"), true);
				return true;
			}
			
			CompoundTag tag = new CompoundTag();

			BlockEntity oldEntity = level.getBlockEntity(pos);
			if(oldEntity instanceof AbstractStargateEntity<?> stargate)
			{
				if(stargate.isConnected())
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.connected_during_upgrade"), true);
					return true;
				}
				
				if(!level.isClientSide())
					tag = stargate.serializeStargateInfo(new CompoundTag());
			}
			
			Direction direction = level.getBlockState(pos).getValue(FACING);
			Orientation orientation = level.getBlockState(pos).getValue(ORIENTATION);
			
			// Check if there's enough space for the Stargate (Not all Stargates have the same size)
			for(StargatePart part : baseBlock.get().getParts())
			{
				BlockState partState = level.getBlockState(part.getRingPos(pos, direction, orientation));
				if(!part.equals(StargatePart.BASE) && (!partState.getMaterial().isReplaceable() && !(partState.getBlock() instanceof AbstractStargateBlock)))
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.not_enough_space"), true);
					return true;
				}
			}
			
			if(level.getBlockState(pos).getBlock() instanceof AbstractStargateBaseBlock oldBaseBlock)
			{
				for(StargatePart part : oldBaseBlock.getParts())
				{
					level.setBlock(part.getRingPos(pos, direction, orientation), Blocks.AIR.defaultBlockState(), 3);
				}
				
				for(StargatePart part : baseBlock.get().getParts())
				{
					if(!part.equals(StargatePart.BASE))
					{
						level.setBlock(part.getRingPos(pos, direction, orientation), 
								baseBlock.get().getRing().defaultBlockState()
								.setValue(AbstractStargateRingBlock.PART, part)
								.setValue(AbstractStargateRingBlock.FACING, direction)
								.setValue(AbstractStargateRingBlock.ORIENTATION, orientation), 3);
					}
				}
				
				level.setBlock(pos, baseBlock.get().defaultBlockState()
						.setValue(AbstractStargateRingBlock.FACING, direction)
						.setValue(AbstractStargateRingBlock.ORIENTATION, orientation), 3);
				
				BlockEntity newEntity = level.getBlockEntity(pos);
				if(newEntity instanceof AbstractStargateEntity<?> stargate)
				{
					if(!level.isClientSide())
					{
						stargate.deserializeStargateInfo(tag, true);
						stargate.addStargateToNetwork();
					}
				}
				
				if(!player.isCreative())
					stack.shrink(1);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(player.getItemInHand(hand).is(ItemInit.STARGATE_UPGRADE_CRYSTAL.get()))
			return upgradeStargate(level, pos, player, hand) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		
		return super.use(state, level, pos, player, hand, result);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.CLASSIC_STARGATE.get(), ClassicStargateEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		if(blockEntityTag != null)
		{
	    	String pointOfOrigin = "";
			if(blockEntityTag.contains(AbstractStargateEntity.POINT_OF_ORIGIN))
				pointOfOrigin = ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(Conversion.stringToPointOfOrigin(blockEntityTag.getString(CartoucheEntity.SYMBOLS))), "Error");
			
			String symbols = "";
			if(blockEntityTag.contains(AbstractStargateEntity.SYMBOLS))
				symbols = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(AbstractStargateEntity.SYMBOLS))), "Error");
			
	        tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(Component.literal(": ")).append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
	        tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(Component.literal(": ")).append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		}
		
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
