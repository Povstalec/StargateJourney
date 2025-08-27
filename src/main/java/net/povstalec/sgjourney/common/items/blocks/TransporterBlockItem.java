package net.povstalec.sgjourney.common.items.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;

public class TransporterBlockItem extends BlockItem
{
	public TransporterBlockItem(Block block, Properties properties)
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
		
		if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			CompoundTag compoundtag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
			BlockEntity blockentity = level.getBlockEntity(pos);
			if(blockentity != null)
			{
				if(!level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
					return false;
				
				CompoundTag compoundtag1 = blockentity.saveWithoutMetadata(minecraftserver.registryAccess());
				CompoundTag compoundtag2 = compoundtag1.copy();
				
				compoundtag1.merge(compoundtag);
				
				if(!compoundtag1.equals(compoundtag2))
				{
					blockentity.loadCustomOnly(compoundtag1, minecraftserver.registryAccess());
					blockentity.setChanged();
					
					return setupBlockEntity(level, blockentity, compoundtag, stack);
				}
			}
		}
		else
			return setupBlockEntity(level, level.getBlockEntity(pos), new CompoundTag(), stack);
		
		return false;
	}
	
	private static boolean setupBlockEntity(Level level, BlockEntity baseEntity, CompoundTag info, ItemStack stack)
	{
		if(baseEntity instanceof AbstractTransporterEntity transporter)
		{
			StructureGenEntity.Step generationStep;
			
			if(info.contains(AbstractTransporterEntity.GENERATION_STEP, CompoundTag.TAG_BYTE))
				generationStep = StructureGenEntity.Step.fromByte(info.getByte(AbstractTransporterEntity.GENERATION_STEP));
			else
				generationStep = StructureGenEntity.Step.GENERATED;
			
			if(stack.has(DataComponents.CUSTOM_NAME))
				transporter.setCustomName(stack.getHoverName());
			
			if(generationStep == StructureGenEntity.Step.GENERATED)
			{
				// Registers it as one of the Block Entities in the list
				transporter.addTransporterToNetwork();
			}
		}
		
		return false;
	}
	
}
