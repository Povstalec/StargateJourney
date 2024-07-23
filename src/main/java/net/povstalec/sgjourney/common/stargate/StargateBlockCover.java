package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.DataResult;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
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
	
	public Optional<BlockState> setBlockAt(StargatePart part, BlockState state)
	{
		BlockState oldState = blockStates.get(part);
		
		blockStates.put(part, state);
		
		return Optional.ofNullable(oldState);
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
			if(!level.isClientSide() && !player.isCreative() && player.hasCorrectToolForDrops(removed.get()))
				Block.dropResources(removed.get(), level, pos);
			
			return true;
		}
		
		return false;
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
