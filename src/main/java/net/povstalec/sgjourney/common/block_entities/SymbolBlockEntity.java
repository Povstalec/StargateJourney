package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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

public abstract class SymbolBlockEntity extends BlockEntity
{
	public static final String SYMBOL = "Symbol";
	public static final String SYMBOLS = "Symbols";
	public static final String SYMBOL_NUMBER = "SymbolNumber";
	public static final ResourceLocation EMPTY = StargateJourney.EMPTY_LOCATION;
	
	public int symbolNumber = 0;
	public ResourceLocation pointOfOrigin = EMPTY;
	public ResourceLocation symbols = EMPTY;
	
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
		
		if(pointOfOrigin.equals(EMPTY))
			setPointOfOrigin(level);
		
		if(symbols.equals(EMPTY))
			setSymbols(level);
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);
    	
    	if(tag.contains(SYMBOL_NUMBER))
    		symbolNumber = tag.getInt(SYMBOL_NUMBER);
    	
    	if(tag.contains(SYMBOL))
    		pointOfOrigin = new ResourceLocation(tag.getString(SYMBOL));
    	
    	if(tag.contains(SYMBOLS))
    		symbols = new ResourceLocation(tag.getString(SYMBOLS));
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.putInt(SYMBOL_NUMBER, symbolNumber);
		
		if(pointOfOrigin != null)
			tag.putString(SYMBOL, pointOfOrigin.toString());
		
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.toString());
		
		super.saveAdditional(tag);
	}
	
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	public CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public int getSymbolNumber()
	{
		return this.symbolNumber;
	}
	
	public void setPointOfOrigin(Level level)
	{
		if(level.isClientSide())
			return;
		
		pointOfOrigin = Universe.get(level).getPointOfOrigin(level.dimension()).location();
	}
	
	public ResourceLocation getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(Level level)
	{
		if(level.isClientSide())
			return;
		
		symbols = Universe.get(level).getSymbols(level.dimension()).location();
	}
	
	public ResourceLocation getSymbols()
	{
		return this.symbols;
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
