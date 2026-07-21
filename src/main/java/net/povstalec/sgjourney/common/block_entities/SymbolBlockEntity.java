package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

import javax.annotation.Nullable;

public abstract class SymbolBlockEntity extends BlockEntity
{
	public static final String SYMBOL = "Symbol";
	public static final String SYMBOLS = "Symbols";
	public static final String SYMBOL_NUMBER = "SymbolNumber";
	public static final ResourceLocation EMPTY = StargateJourney.EMPTY_LOCATION;
	
	public int symbolNumber = 0;
	@Nullable
	public ResourceKey<PointOfOrigin> pointOfOrigin = null;
	@Nullable
	public ResourceKey<Symbols> symbols = null;
	
	public SymbolBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) 
	{
		super(entity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		if(!level.isClientSide())
		{
			if(pointOfOrigin == null)
				setPointOfOriginFromLevel(level);
			
			if(symbols == null)
				setSymbolsFromLevel(level);
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
		}
		
		super.onLoad();
	}
	
	@Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
    	super.loadAdditional(tag, registries);
    	
    	if(tag.contains(SYMBOL_NUMBER))
    		symbolNumber = tag.getInt(SYMBOL_NUMBER);
    	
    	if(tag.contains(SYMBOL))
    		pointOfOrigin = Conversion.stringToPointOfOrigin(tag.getString(SYMBOL));
    	
    	if(tag.contains(SYMBOLS))
    		symbols = Conversion.stringToSymbols(tag.getString(SYMBOLS));
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putInt(SYMBOL_NUMBER, symbolNumber);
		
		if(pointOfOrigin != null)
			tag.putString(SYMBOL, pointOfOrigin.location().toString());
		
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.location().toString());
		
		super.saveAdditional(tag, registries);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		return this.saveWithoutMetadata(registries);
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		ResourceKey<PointOfOrigin> oldPointOfOrigin = pointOfOrigin;
		ResourceKey<Symbols> oldSymbols = symbols;
		int oldSymbolNumber = symbolNumber;
		
		super.onDataPacket(net, packet, registries);
		
		boolean needsUpdate = pointOfOrigin != null && !pointOfOrigin.equals(oldPointOfOrigin);
		needsUpdate |= symbols != null && !symbols.equals(oldSymbols);
		needsUpdate |= symbolNumber != oldSymbolNumber;
		
		if(needsUpdate)
		{
			requestModelDataUpdate();
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
		}
	}
	
	@Override
	@NotNull
	public ModelData getModelData()
	{
		ModelData.Builder builder = ModelData.builder()
				.with(ModelProperties.SYMBOL_INDEX_PROPERTY, symbolNumber);
		
		if(symbolNumber == 0 && pointOfOrigin != null)
			builder.with(ModelProperties.POINT_OF_ORIGIN_PROPERTY, pointOfOrigin);
		else if(symbols != null)
			builder.with(ModelProperties.SYMBOLS_PROPERTY, symbols);
		
		return builder.build();
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public void setSymbolNumber(int symbolNumber)
	{
		this.symbolNumber = symbolNumber;
	}
	
	public int getSymbolNumber()
	{
		return this.symbolNumber;
	}
	
	public void setPointOfOrigin(@Nullable ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin;
	}
	
	public void setPointOfOriginFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		setPointOfOrigin(Universe.get(level).getPointOfOrigin(level.dimension()));
	}
	
	@Nullable
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(@Nullable ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols;
	}
	
	public void setSymbolsFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		setSymbols(Universe.get(level).getSymbols(level.dimension()));
	}
	
	@Nullable
	public ResourceKey<Symbols> getSymbols()
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
