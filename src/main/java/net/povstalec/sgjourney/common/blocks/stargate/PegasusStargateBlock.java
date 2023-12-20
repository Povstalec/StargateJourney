package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		Registry<Symbols> symbolsRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("PointOfOrigin"))
		{
	    	String pointOfOrigin = "";
			ResourceLocation location = new ResourceLocation(stack.getTag().getCompound("BlockEntityTag").getString("PointOfOrigin"));
			if(location.toString().equals("sgjourney:empty"))
				pointOfOrigin = "Empty";
			else if(pointOfOriginRegistry.containsKey(location))
				pointOfOrigin = pointOfOriginRegistry.get(location).getName();
			else
				pointOfOrigin = "Error";
	        tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(Component.literal(": ")).append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		}
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Symbols"))
		{
			String symbols = "";
			ResourceLocation location = new ResourceLocation(stack.getTag().getCompound("BlockEntityTag").getString("Symbols"));
			if(location.toString().equals("sgjourney:empty"))
				symbols = "Empty";
			else if(symbolsRegistry.containsKey(location))
				symbols = symbolsRegistry.get(location).getName(!ClientStargateConfig.unique_symbols.get());
			else
				symbols = "Error";
	        tooltipComponents.add(Component.translatable("tooltip.sgjourney.symbols").append(Component.literal(": ")).append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		}
		
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
