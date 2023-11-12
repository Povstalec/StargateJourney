package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.StargatePart;
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
			for(StargatePart part : baseBlock.getParts())
			{
				BlockState partState = level.getBlockState(part.getRingPos(pos, direction, orientation));
				if(!part.equals(StargatePart.BASE) && (!partState.canBeReplaced() && !(partState.getBlock() instanceof AbstractStargateBlock)))
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.not_enough_space"), true);
					return true;
				}
			}
			
			for(StargatePart part : baseBlock.getParts())
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
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		Registry<Symbols> symbolsRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
    	
    	String pointOfOrigin = "";
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("PointOfOrigin"))
		{
			ResourceLocation location = new ResourceLocation(stack.getTag().getCompound("BlockEntityTag").getString("PointOfOrigin"));
			if(location.toString().equals("sgjourney:empty"))
				pointOfOrigin = "Empty";
			else if(pointOfOriginRegistry.containsKey(location))
				pointOfOrigin = pointOfOriginRegistry.get(location).getName();
			else
				pointOfOrigin = "Error";
		}
		String symbols = "";
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Symbols"))
		{
			ResourceLocation location = new ResourceLocation(stack.getTag().getCompound("BlockEntityTag").getString("Symbols"));
			if(location.toString().equals("sgjourney:empty"))
				symbols = "Empty";
			else if(symbolsRegistry.containsKey(location))
				symbols = symbolsRegistry.get(location).getName(!ClientStargateConfig.unique_symbols.get());
			else
				symbols = "Error";
		}
		
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(Component.literal(": ")).append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.symbols").append(Component.literal(": ")).append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
