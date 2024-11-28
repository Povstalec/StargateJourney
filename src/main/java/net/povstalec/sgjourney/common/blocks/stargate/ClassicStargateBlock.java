package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class ClassicStargateBlock extends AbstractStargateBaseBlock
{
	public ClassicStargateBlock(Properties properties)
	{
		super(properties, 8.0D, 0.0D);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		 ClassicStargateEntity stargate = new ClassicStargateEntity(pos, state);
		
		 return stargate;
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
			if(oldEntity instanceof AbstractStargateEntity stargate)
			{
				if(!level.isClientSide())
					tag = stargate.serializeStargateInfo(new CompoundTag(), level.getServer().registryAccess());
			}
			
			Direction direction = level.getBlockState(pos).getValue(FACING);
			Orientation orientation = level.getBlockState(pos).getValue(ORIENTATION);
			
			// Check if there's enough space for the Stargate (Not all Stargates have the same size)
			for(StargatePart part : baseBlock.get().getParts())
			{
				BlockState partState = level.getBlockState(part.getRingPos(pos, direction, orientation));
				if(!part.equals(StargatePart.BASE) && (!partState.canBeReplaced() && !(partState.getBlock() instanceof AbstractStargateBlock)))
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
				if(newEntity instanceof AbstractStargateEntity stargate)
				{
					if(!level.isClientSide())
					{
						stargate.deserializeStargateInfo(tag, level.getServer().registryAccess(), true);
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
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(player.getItemInHand(hand).is(ItemInit.STARGATE_UPGRADE_CRYSTAL.get()))
			return upgradeStargate(level, pos, player, hand) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;
		
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.CLASSIC_STARGATE.get(), ClassicStargateEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		
		if(clientPacketListener != null)
		{
			RegistryAccess registries = clientPacketListener.registryAccess();
			Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
			Registry<Symbols> symbolsRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
	    	
	    	String pointOfOrigin = "";
			boolean hasData = stack.has(DataComponents.BLOCK_ENTITY_DATA);
			if(hasData && stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe().contains("PointOfOrigin"))
			{
				ResourceLocation location = ResourceLocation.parse(stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe().getString("PointOfOrigin"));
				if(location.toString().equals("sgjourney:empty"))
					pointOfOrigin = "Empty";
				else if(pointOfOriginRegistry.containsKey(location))
					pointOfOrigin = pointOfOriginRegistry.get(location).getName();
				else
					pointOfOrigin = "Error";
			}
			String symbols = "";
			if(hasData && stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe().contains("Symbols"))
			{
				ResourceLocation location = ResourceLocation.parse(stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe().getString("Symbols"));
				if(location.toString().equals("sgjourney:empty"))
					symbols = "Empty";
				else if(symbolsRegistry.containsKey(location))
					symbols = symbolsRegistry.get(location).getTranslationName(!ClientStargateConfig.unique_symbols.get());
				else
					symbols = "Error";
			}
			
	        tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(Component.literal(": ")).append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
	        tooltipComponents.add(Component.translatable(Symbols.symbolsOrSet()).append(Component.literal(": ")).append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		}
		
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
