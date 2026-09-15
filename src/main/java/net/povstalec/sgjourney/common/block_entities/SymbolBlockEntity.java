package net.povstalec.sgjourney.common.block_entities;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.PointOfOriginTable;
import net.povstalec.sgjourney.common.sgjourney.SymbolTable;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class SymbolBlockEntity extends BlockEntity implements StructureGenEntity
{
	public static final String SYMBOL = "Symbol";
	public static final String SYMBOLS = "Symbols";
	public static final String SYMBOL_NUMBER = "SymbolNumber";
	
	public static final String LOCAL_POINT_OF_ORIGIN = "local_point_of_origin";
	public static final String RANDOM_POINT_OF_ORIGIN = "random_point_of_origin";
	public static final String SYMBOL_TABLE = "symbol_table";
	public static final String POINT_OF_ORIGIN_TABLE = "point_of_origin_table";
	
	protected StructureGenEntity.Step generationStep = StructureGenEntity.Step.GENERATED;
	
	@Nullable
	protected ResourceKey<SymbolTable> symbolTable = null;
	@Nullable
	protected ResourceKey<PointOfOriginTable> pointOfOriginTable = null;
	
	protected int symbolNumber = -1;
	@Nullable
	protected ResourceKey<PointOfOrigin> pointOfOrigin = null;
	@Nullable
	protected ResourceKey<Symbols> symbols = null;
	
	public SymbolBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) 
	{
		super(entity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		if(!level.isClientSide())
		{
			if(generationStep == StructureGenEntity.Step.READY)
				generate();
			
			if(pointOfOrigin == null)
				setRandomPointOfOrigin();
			if(symbols == null)
				setSymbolsFromLevel(level);
			
			updateClient();
		}
		
		super.onLoad();
	}
	
	@Override
    public void load(@NotNull CompoundTag tag)
    {
    	super.load(tag);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(SYMBOL_TABLE))
			symbolTable = Conversion.stringToSymbolTableKey(tag.getString(SYMBOL_TABLE));
		if(tag.contains(POINT_OF_ORIGIN_TABLE))
			pointOfOriginTable = Conversion.stringToPointOfOriginTableKey(tag.getString(POINT_OF_ORIGIN_TABLE));
		
		symbolNumber = tag.getInt(SYMBOL_NUMBER);
		if(tag.contains(SYMBOL))
			pointOfOrigin = Conversion.stringToPointOfOrigin(tag.getString(SYMBOL));
		if(tag.contains(SYMBOLS))
			symbols = Conversion.stringToSymbols(tag.getString(SYMBOLS));
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(symbolTable != null)
			tag.putString(SYMBOL_TABLE, symbolTable.location().toString());
		if(pointOfOriginTable != null)
			tag.putString(POINT_OF_ORIGIN_TABLE, pointOfOriginTable.location().toString());
		
		tag.putInt(SYMBOL_NUMBER, symbolNumber);
		if(pointOfOrigin != null)
			tag.putString(SYMBOL, pointOfOrigin.location().toString());
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.location().toString());
		
		super.saveAdditional(tag);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		ResourceKey<PointOfOrigin> oldPointOfOrigin = pointOfOrigin;
		ResourceKey<Symbols> oldSymbols = symbols;
		int oldSymbolNumber = symbolNumber;
		
		super.onDataPacket(net, packet);
		
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
	
	public void updateClient()
	{
		if(level != null && !level.isClientSide())
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
	}
	
	@Override
	public void setChanged()
	{
		super.setChanged();
		updateClient();
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public void setSymbolNumber(int symbolNumber)
	{
		this.symbolNumber = symbolNumber;
		setChanged();
	}
	
	public int getSymbolNumber()
	{
		return this.symbolNumber;
	}
	
	public void setPointOfOrigin(@Nullable ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin;
		setChanged();
	}
	
	public void setPointOfOriginFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		this.pointOfOrigin = Universe.get(level).getPointOfOrigin(level.dimension());
		setChanged();
	}
	
	@Nullable
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(@Nullable ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols;
		setChanged();
	}
	
	public void setSymbolsFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		this.symbols = Universe.get(level).getSymbols(level.dimension());
	}
	
	@Nullable
	public ResourceKey<Symbols> getSymbols()
	{
		return this.symbols;
	}
	
	public void setSymbolTable(@Nullable ResourceKey<SymbolTable> symbolTable)
	{
		this.symbolTable = symbolTable;
		setChanged();
	}
	
	@Nullable
	public ResourceKey<SymbolTable> getSymbolTable()
	{
		return this.symbolTable;
	}
	
	public void setSymbolFromSymbolTable()
	{
		if(level.isClientSide())
			return;
		
		SymbolTable symbolTable = SymbolTable.getSymbolTable(level.getServer(), this.symbolTable);
		Either<ResourceKey<PointOfOrigin>, SymbolTable.Symbol> eitherSymbol = SymbolTable.randomSymbol(level.getRandom(), symbolTable);
		
		if(eitherSymbol != null)
		{
			eitherSymbol.ifLeft(pointOfOrigin ->
			{
				this.pointOfOrigin = pointOfOrigin;
				this.symbolNumber = 0;
			});
			eitherSymbol.ifRight(symbol ->
			{
				this.symbols = symbol.symbols() != null ? symbol.symbols() : Universe.get(level).getSymbols(level.dimension());
				this.symbolNumber = symbol.getSymbolNumber(level.getRandom());
			});
		}
		
		this.symbolTable = null;
		this.setChanged();
	}
	
	public void setPointOfOriginTable(@Nullable ResourceKey<PointOfOriginTable> pointOfOriginTable)
	{
		this.pointOfOriginTable = pointOfOriginTable;
		setChanged();
	}
	
	@Nullable
	public ResourceKey<PointOfOriginTable> getPointOfOriginTable()
	{
		return this.pointOfOriginTable;
	}
	
	public void setPointOfOriginFromTable()
	{
		if(level.isClientSide())
			return;
		
		PointOfOriginTable pointOfOriginTable = PointOfOriginTable.getPointOfOriginTable(this.pointOfOriginTable);
		ResourceKey<PointOfOrigin> pointOfOrigin = PointOfOriginTable.randomPointOfOrigin(level.getRandom(), pointOfOriginTable);
		
		if(pointOfOrigin != null)
			this.pointOfOrigin = pointOfOrigin;
		
		this.pointOfOriginTable = null;
		setChanged();
	}
	
	public void setRandomPointOfOrigin()
	{
		if(level.isClientSide())
			return;
		
		setPointOfOrigin(PointOfOrigin.randomPointOfOrigin(level.getServer(), level.dimension()));
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void setGenerationStep(Step step)
	{
		this.generationStep = step;
	}
	
	@Override
	public Step generationStep()
	{
		return generationStep;
	}
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		if(generationStep == Step.SETUP)
			generationStep = Step.READY; // Marks the Cartouche as ready for generation
	}
	
	public void generate()
	{
		if(symbolTable != null)
			setSymbolFromSymbolTable();
		if(pointOfOriginTable != null)
			setPointOfOriginFromTable();
		
		generationStep = Step.GENERATED;
		setChanged();
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
