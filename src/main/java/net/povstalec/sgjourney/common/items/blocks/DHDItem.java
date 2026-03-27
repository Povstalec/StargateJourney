package net.povstalec.sgjourney.common.items.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
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
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

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
		{
			BlockEntity baseEntity = level.getBlockEntity(pos);
			if(baseEntity instanceof AbstractDHDEntity dhd)
			{
				dhd.setStargate();
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
			
			dhd.setStargate();
			if(generationStep == StructureGenEntity.Step.GENERATED)
				dhd.generateAdditional(StructureGenEntity.Step.GENERATED);
			else
				dhd.generateAdditional(StructureGenEntity.Step.SETUP);
			
			if(baseEntity instanceof CrystalDHDEntity crystalDHD)
			{
				crystalDHD.recalculateCrystals();
				return true;
			}
		}
		
		return false;
	}
	
}
