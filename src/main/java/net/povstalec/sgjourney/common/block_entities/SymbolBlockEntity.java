package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.packets.ClientboundSymbolUpdatePacket;

public abstract class SymbolBlockEntity extends BlockEntity
{
	public static final String SYMBOL = "symbol";
	public static final String SYMBOLS = "symbols";
	public static final String SYMBOL_NUMBER = "symbol_number";
	public static final String EMPTY = StargateJourney.EMPTY;
	
	private boolean isNew = false;
	public int symbolNumber = 0;
	public String pointOfOrigin = EMPTY;
	public String symbols = EMPTY;
	
	public SymbolBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) 
	{
		super(entity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(level.isClientSide())
			return;
		
		if(!isNew)
		{
			if(pointOfOrigin.equals(EMPTY))
				setPointOfOrigin(level);
			
			if(symbols.equals(EMPTY))
				setSymbols(level);
		}
	}
	
	@Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
    	super.loadAdditional(tag, registries);
    	
    	if(tag.contains(SYMBOL_NUMBER))
    		symbolNumber = tag.getInt(SYMBOL_NUMBER);
    	
    	if(tag.contains(SYMBOL))
    		pointOfOrigin = tag.getString(SYMBOL);
    	
    	if(tag.contains(SYMBOLS))
    		symbols = tag.getString(SYMBOLS);
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putInt(SYMBOL_NUMBER, symbolNumber);
		
		if(pointOfOrigin != null)
			tag.putString(SYMBOL, pointOfOrigin);
		
		if(symbols != null)
			tag.putString(SYMBOLS, symbols);
		
		super.saveAdditional(tag, registries);
	}
	
	public void setNew()
	{
		this.isNew = true;
	}
	
	public int getSymbolNumber()
	{
		return this.symbolNumber;
	}
	
	public void setPointOfOrigin(Level level)
	{
		if(level.isClientSide())
			return;
		
		pointOfOrigin = Universe.get(level).getPointOfOrigin(level.dimension()).location().toString();
	}
	
	public String getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(Level level)
	{
		if(level.isClientSide())
			return;
		
		symbols = Universe.get(level).getSymbols(level.dimension()).location().toString();
	}
	
	public String getSymbols()
	{
		return this.symbols;
	}
	
	public void tick(Level level, BlockPos pos, BlockState state)
	{
		if(level.isClientSide())
			return;
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientboundSymbolUpdatePacket(worldPosition, symbolNumber, pointOfOrigin, symbols));
	}
	
	
	
	public static class Stone extends SymbolBlockEntity
	{
		public Stone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.STONE_SYMBOL.get(), pos, state);
		}
		
	}
	
	public static class Sandstone extends SymbolBlockEntity
	{
		public Sandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.SANDSTONE_SYMBOL.get(), pos, state);
		}
		
	}
	
	public static class RedSandstone extends SymbolBlockEntity
	{
		public RedSandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.RED_SANDSTONE_SYMBOL.get(), pos, state);
		}
		
	}

}
