package net.povstalec.sgjourney.common.blocks.symbols;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.symbols.SymbolBlockEntity;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;

public abstract class SymbolBlock extends BaseEntityBlock
{

	protected SymbolBlock(Properties properties)
	{
		super(properties);
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
        if (!level.isClientSide())
        {
            return (localLevel, pos, blockState, entity) -> {
                if (entity instanceof SymbolBlockEntity symbol) 
                {
                	symbol.tick(localLevel, pos, blockState);
                }
            };
        }
        return null;
    }
    
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
    	
    	String symbol = "";
    	CompoundTag tag = stack.getTag();
    	if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Symbol") && tag != null)
    	{
        	String pointOfOrigin = tag.getCompound("BlockEntityTag").getString("Symbol");
        	
        	if(pointOfOriginRegistry.get(new ResourceLocation(pointOfOrigin)) != null)
        		symbol = pointOfOriginRegistry.get(new ResourceLocation(pointOfOrigin)).getName();
        	else
        		symbol = "Error";
    	}
    	
		tooltipComponents.add(Component.literal("Symbol: " + symbol).withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
