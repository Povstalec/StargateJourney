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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.StargatePart;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class MilkyWayStargateBlock extends AbstractStargateBaseBlock
{
	public MilkyWayStargateBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		 MilkyWayStargateEntity stargate = new MilkyWayStargateEntity(pos, state);
		
		 return stargate;
	}
	
	public BlockState ringState()
	{
		return BlockInit.MILKY_WAY_RING.get().defaultBlockState();
	}

	public Block getStargate()
	{
		return BlockInit.MILKY_WAY_STARGATE.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.MILKY_WAY_STARGATE.get(), MilkyWayStargateEntity::tick);
    }
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide)
			return;
		
		boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
		
		BlockEntity blockentity = level.getBlockEntity(pos);
		
		if(blockentity instanceof MilkyWayStargateEntity stargate)
		{
			if(hasSignal)
				stargate.updateSignal(StargatePart.BASE, level.getBestNeighborSignal(pos));
			else
				stargate.updateSignal(StargatePart.BASE, 0);
		}
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
				symbols = symbolsRegistry.get(location).getName();
			else
				symbols = "Error";
		}
		
        tooltipComponents.add(Component.literal("PoO: " + pointOfOrigin).withStyle(ChatFormatting.DARK_PURPLE));
        tooltipComponents.add(Component.literal("Symbols: " + symbols).withStyle(ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
