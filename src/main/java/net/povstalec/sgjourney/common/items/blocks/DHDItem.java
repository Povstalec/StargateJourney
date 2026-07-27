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
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;

public class DHDItem extends BlockItem
{
	public DHDItem(Block block, Properties properties)
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
					
					return setupBlockEntity(level, blockentity, compoundtag);
				}
			}
		}
		else
		{
			BlockEntity baseEntity = level.getBlockEntity(pos);
			if(baseEntity instanceof AbstractDHDEntity dhd)
			{
				dhd.setupServerAutoCache();
				dhd.generateAdditional(StructureGenEntity.Step.READY);
			}
		}
		
		return false;
	}
	
	private static boolean setupBlockEntity(Level level, BlockEntity baseEntity, CompoundTag info)
	{
		if(baseEntity instanceof AbstractDHDEntity dhd)
		{
			StructureGenEntity.Step generationStep;
			
			if(info.contains(AbstractDHDEntity.GENERATION_STEP, CompoundTag.TAG_BYTE))
				generationStep = StructureGenEntity.Step.fromByte(info.getByte(AbstractDHDEntity.GENERATION_STEP));
			else
				generationStep = StructureGenEntity.Step.GENERATED;
			
			if(info.contains(AbstractDHDEntity.GENERATION_STEP, CompoundTag.TAG_BYTE) && StructureGenEntity.Step.SETUP == StructureGenEntity.Step.fromByte(info.getByte(AbstractDHDEntity.GENERATION_STEP)))
				dhd.setToGenerate();
			
			dhd.setupServerAutoCache();
			if(generationStep == StructureGenEntity.Step.GENERATED)
				dhd.generateAdditional(StructureGenEntity.Step.GENERATED);
			else
			{
				dhd.generateAdditional(StructureGenEntity.Step.SETUP);
				// Clear symbols manually when placing the gate here, because Minecraft fires onLoad() before any kind of useful loading of information actually happens
				if(dhd instanceof PegasusDHDEntity pegasusDHD)
					pegasusDHD.clearSymbols();
			}
			
			return true;
		}
		
		return false;
	}
	
}
