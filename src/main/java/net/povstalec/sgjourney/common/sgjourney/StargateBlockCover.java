package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.DataResult;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.sgjourney.common.blocks.SGJourneyWeatheringBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public class StargateBlockCover implements INBTSerializable<CompoundTag>
{
	private ArrayList<StargatePart> parts;
	
	public boolean canSinkGate = false;
	public HashMap<StargatePart, BlockState> blockStates = new HashMap<StargatePart, BlockState>();
	
	public StargateBlockCover(ArrayList<StargatePart> parts)
	{
		this.parts = parts;
	}
	
	public boolean setBlockAt(StargatePart part, BlockState state)
	{
		if(blockStates.containsKey(part))
			return false;
		
		blockStates.put(part, state);
		
		return true;
	}
	
	public Optional<BlockState> getBlockAt(StargatePart part)
	{
		if(blockStates.get(part) != null && blockStates.get(part).getBlock() instanceof AbstractStargateBlock)
			return Optional.empty();
		
		return Optional.ofNullable(blockStates.get(part));
	}
	
	public Optional<BlockState> removeBlockAt(StargatePart part)
	{
		BlockState oldState = blockStates.get(part);
		
		blockStates.remove(part);
		
		return Optional.ofNullable(oldState);
	}
	
	public boolean mineBlockAt(Level level, Player player, StargatePart part, BlockPos pos)
	{
		Optional<BlockState> removed = removeBlockAt(part);
		if(removed.isPresent())
		{
			if(!level.isClientSide())
			{
				BlockState state = removed.get();
				
				if(!player.isCreative() && player.hasCorrectToolForDrops(removed.get()))
					Block.dropResources(state, level, pos);
				
				level.levelEvent((Player) null, 2001, pos, Block.getId(state)); // Spawns breaking particles and makes a breaking sound
			}
			
			return true;
		}
		
		return false;
	}
	
	public ItemStack getStackAt(HitResult target, BlockGetter level, Player player, StargatePart part, BlockPos pos)
	{
		Optional<BlockState> removed = getBlockAt(part);
		if(removed.isPresent())
		{
			BlockState state = removed.get();
			
			return state.getCloneItemStack(target, level, pos, player);
		}
		
		return ItemStack.EMPTY;
	}
	
	public void doWeatheringAt(StargatePart part, BlockPos pos, ServerLevel level, RandomSource randomSource)
	{
		getBlockAt(part).ifPresent(coverBlockState ->
		{
			if(coverBlockState.getBlock() instanceof SGJourneyWeatheringBlock weatheringBlock && weatheringBlock.passesProbability(randomSource))
				weatheringBlock.changeOverTime(coverBlockState, level, pos, randomSource).ifPresent(newBlockState -> blockStates.put(part, newBlockState));
		});
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();

		for(Map.Entry<StargatePart, BlockState> entry : blockStates.entrySet())
		{
			DataResult<Tag> blockStateTag = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue());
			
			Optional<Tag> result = blockStateTag.result();
			
			if(result.isPresent())
				tag.put(entry.getKey().getSerializedName(), result.get());
		}
		
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		for(StargatePart part : parts)
		{
			if(tag.contains(part.getSerializedName()))
			{
				DataResult<BlockState> stateResult = BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get(part.getSerializedName()));
				Optional<BlockState> result = stateResult.result();
				
				if(result.isPresent())
					blockStates.put(part, result.get());
			}
		}
	}
}
