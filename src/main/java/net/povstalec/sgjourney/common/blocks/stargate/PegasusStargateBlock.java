package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

import javax.annotation.Nullable;
import java.util.List;

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
		return new PegasusStargateEntity(pos, state);
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
		return createTickerHelper(type, BlockEntityInit.PEGASUS_STARGATE.get(), PegasusStargateEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		if(blockEntityTag == null)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.dynamic_symbols").withStyle(ChatFormatting.DARK_AQUA));
		else
		{
			if(blockEntityTag.contains(PegasusStargateEntity.DYNAMC_SYMBOLS) && blockEntityTag.getBoolean(PegasusStargateEntity.DYNAMC_SYMBOLS))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.dynamic_symbols").withStyle(ChatFormatting.DARK_AQUA));
			else
			{
				String pointOfOrigin = "";
				if(blockEntityTag.contains(AbstractStargateEntity.POINT_OF_ORIGIN))
					pointOfOrigin = ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(Conversion.stringToPointOfOrigin(blockEntityTag.getString(CartoucheEntity.SYMBOLS))), "Error");
				
				String symbols = "";
				if(blockEntityTag.contains(AbstractStargateEntity.SYMBOLS))
					symbols = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(AbstractStargateEntity.SYMBOLS))), "Error");
				
		        tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(": ").append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		        tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(": ").append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
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
