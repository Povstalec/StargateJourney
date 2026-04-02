package net.povstalec.sgjourney.common.items.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;

import javax.annotation.Nullable;

public class SymbolBlockItem extends BlockItem
{
	public SymbolBlockItem(Block block, Properties properties)
	{
		super(block, properties);
	}
	
	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state)
	{
		return updateCustomBlockEntityTag(level, player, pos, stack);
	}
	
	public static boolean updateCustomBlockEntityTag(Level level, @Nullable Player player, BlockPos pos, ItemStack stack)
	{
		MinecraftServer minecraftserver = level.getServer();
		if(minecraftserver == null)
			return false;
		
		CompoundTag compoundtag = getBlockEntityData(stack);
		if(compoundtag != null)
		{
			BlockEntity blockentity = level.getBlockEntity(pos);
            if(blockentity != null)
            {
            	if(!level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
            		return false;
            	
            	CompoundTag compoundtag1 = blockentity.saveWithoutMetadata();
            	CompoundTag compoundtag2 = compoundtag1.copy();
            	
            	compoundtag1.merge(compoundtag);
            	
            	if(!compoundtag1.equals(compoundtag2))
            	{
            		blockentity.load(compoundtag1);
            		blockentity.setChanged();
            		
            		return setupBlockEntity(level, blockentity, compoundtag);
            	}
            }
		}
		else
			return setupBlockEntity(level, level.getBlockEntity(pos), new CompoundTag());
		
		return false;
	}
	
	private static boolean setupBlockEntity(Level level, BlockEntity baseEntity, CompoundTag info)
	{
		if(baseEntity instanceof SymbolBlockEntity cartouche)
		{
			if(info.contains(SymbolBlockEntity.SYMBOL_NUMBER, CompoundTag.TAG_INT))
				cartouche.setSymbolNumber(info.getInt(SymbolBlockEntity.SYMBOL_NUMBER));
			
			if(info.contains(SymbolBlockEntity.SYMBOL, CompoundTag.TAG_STRING))
				cartouche.setPointOfOrigin(ResourceLocation.tryParse(info.getString(SymbolBlockEntity.SYMBOL)));
			else
				cartouche.setPointOfOriginFromLevel(level);
			
			if(info.contains(SymbolBlockEntity.SYMBOLS, CompoundTag.TAG_STRING))
				cartouche.setSymbols(ResourceLocation.tryParse(info.getString(SymbolBlockEntity.SYMBOLS)));
			else
				cartouche.setSymbolsFromLevel(level);
		}
		
		return false;
	}
	
}
