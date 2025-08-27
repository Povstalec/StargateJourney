package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class PegasusStargateBlock extends AbstractStargateBaseBlock
{
	public PegasusStargateBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		PegasusStargateEntity stargate = new PegasusStargateEntity(pos, state);
		
		return stargate;
	}

	@Override
	public AbstractStargateRingBlock getRing()
	{
		return BlockInit.PEGASUS_RING.get();
	}
	
	@Override
	public AbstractShieldingBlock getIris()
	{
		return BlockInit.PEGASUS_SHIELDING.get();
	}

	@Override
	public BlockState ringState()
	{
		return getRing().defaultBlockState();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if(level.isClientSide())
			return null;
		return createTickerHelper(type, BlockEntityInit.PEGASUS_STARGATE.get(), PegasusStargateEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		if(blockEntityTag == null)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.dynamic_symbols").withStyle(ChatFormatting.DARK_AQUA));
		else if(clientPacketListener != null)
		{
			RegistryAccess registries = clientPacketListener.registryAccess();
			Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
			Registry<Symbols> symbolsRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
	  
			if(blockEntityTag.contains(PegasusStargateEntity.DYNAMC_SYMBOLS) && blockEntityTag.getBoolean(PegasusStargateEntity.DYNAMC_SYMBOLS))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.dynamic_symbols").withStyle(ChatFormatting.DARK_AQUA));
			else
			{
		    	String pointOfOrigin = "";
				if(blockEntityTag.contains(AbstractStargateEntity.POINT_OF_ORIGIN))
				{
					ResourceLocation location = ResourceLocation.tryParse(blockEntityTag.getString(AbstractStargateEntity.POINT_OF_ORIGIN));
					if(location.equals(StargateJourney.EMPTY_LOCATION))
						pointOfOrigin = "Empty";
					else if(pointOfOriginRegistry.containsKey(location))
						pointOfOrigin = pointOfOriginRegistry.get(location).getName();
					else
						pointOfOrigin = "Error";
				}
				String symbols = "";
				if(blockEntityTag.contains(AbstractStargateEntity.SYMBOLS))
				{
					ResourceLocation location = ResourceLocation.tryParse(blockEntityTag.getString(AbstractStargateEntity.SYMBOLS));
					if(location.equals(StargateJourney.EMPTY_LOCATION))
						symbols = "Empty";
					else if(symbolsRegistry.containsKey(location))
						symbols = symbolsRegistry.get(location).getTranslationName(!ClientStargateConfig.unique_symbols.get());
					else
						symbols = "Error";
				}
		        tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(": ").append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		        tooltipComponents.add(Component.translatable(Symbols.symbolsOrSet()).append(": ").append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
			}
		}
		
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	public static ItemStack localSymbols(ItemStack stack, BlockEntityType<?> blockEntityType)
	{
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putBoolean(PegasusStargateEntity.DYNAMC_SYMBOLS, false);
		BlockEntity.addEntityType(compoundtag, blockEntityType);
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundtag));
		
		return stack;
	}
}
