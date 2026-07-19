package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.AddressTable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class CartoucheEntity extends BlockEntity implements StructureGenEntity
{
	public static final String ADDRESS_TABLE = "address_table";
	public static final String DIMENSION = "dimension";
	public static final String GALAXY = "galaxy";
	public static final String SYMBOLS = "symbols";
	public static final String ADDRESS = "address";
	
	protected StructureGenEntity.Step generationStep = StructureGenEntity.Step.GENERATED;

	private ResourceLocation addressTable;
	
	private ResourceKey<Symbols> symbols = null;
	@Nullable
	private Address address;
	
	public CartoucheEntity(BlockEntityType<?> cartouche, BlockPos pos, BlockState state) 
	{
		super(cartouche, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		if(!level.isClientSide())
		{
			if(getHalf() == DoubleBlockHalf.LOWER)
			{
				if(generationStep == StructureGenEntity.Step.READY)
					generate();
				
				tryGenerateAddress();
				
				updateUpperHalf();
			}
			else
				updateFromLowerHalf();
		}
		
		super.onLoad();
	}
	
	@Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
    	super.loadAdditional(tag, registries);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(ADDRESS_TABLE))
    		addressTable = ResourceLocation.tryParse(tag.getString(ADDRESS_TABLE));
    	if(tag.contains(SYMBOLS))
    		symbols = Conversion.stringToSymbols(tag.getString(SYMBOLS));
		
		if(tag.contains(ADDRESS, Tag.TAG_COMPOUND)) // Dimension Address is saved to a tag, load it
			address = Address.Dimension.loadFromCompoundTag(tag, ADDRESS);
		else if(tag.contains(DIMENSION, Tag.TAG_STRING)) // Dimension is saved as a String, load it along with other stuff that forms the Dimension Address //TODO For legacy reasons
			address = Address.Dimension.loadFromCompoundTag(tag, ADDRESS, DIMENSION, GALAXY);
		else if(tag.contains(ADDRESS, Tag.TAG_INT_ARRAY)) // Immutable Address is saved as an array, load it
			address = new Address.Immutable(tag.getIntArray(ADDRESS));
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(addressTable != null)
			tag.putString(ADDRESS_TABLE, addressTable.toString());
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.location().toString());
		if(address != null)
			address.saveToCompoundTag(tag, ADDRESS);
		
		super.saveAdditional(tag, registries);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		CompoundTag tag = new CompoundTag();
		
		if(address instanceof Address.Dimension dimensionAddress)
			dimensionAddress.saveToCompoundTagAsArray(tag, ADDRESS);
		else if(address != null)
			address.saveToCompoundTag(tag, ADDRESS);
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.location().toString());
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		Address oldAddress = address;
		ResourceKey<Symbols> oldSymbols = symbols;
		
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			if(tag.contains(ADDRESS, Tag.TAG_INT_ARRAY))
				address = Address.Immutable.loadFromCompoundTag(tag, ADDRESS);
			else
				address = new Address.Immutable();
			
			if(tag.contains(SYMBOLS, Tag.TAG_STRING))
				symbols = Conversion.stringToSymbols(tag.getString(SYMBOLS));
		}
		
		boolean needsUpdate = address != null && !address.equals(oldAddress);
		needsUpdate |= symbols != null && !symbols.equals(oldSymbols);
		
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
		ModelData.Builder builder = ModelData.builder();
		
		if(address != null)
			builder.with(ModelProperties.ADDRESS_PROPERTY, address);
		if(symbols != null)
			builder.with(ModelProperties.SYMBOLS_PROPERTY, symbols);
		
		return builder.build();
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public void setDimension(ResourceLocation dimension)
	{
		this.address = new Address.Dimension(Conversion.locationToDimension(dimension), Optional.empty());
	}
	
	public void setSymbols(ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols;
	}
	
	public ResourceKey<Symbols> getSymbols()
	{
		return this.symbols;
	}
	
	public void setAddress(@Nullable Address address)
	{
		this.address = address;
	}
	
	public Address getAddress()
	{
		if(this.address == null)
			return new Address.Immutable();
		
		return this.address;
	}
	
	public void setAddressTable(@Nullable ResourceLocation addressTable)
	{
		this.addressTable = addressTable;
	}
	
	@Nullable
	public ResourceLocation getAddressTable()
	{
		return this.addressTable;
	}
	
	public DoubleBlockHalf getHalf()
	{
		return getBlockState().getValue(CartoucheBlock.HALF);
	}
	
	//============================================================================================
	//****************************************Functionality***************************************
	//============================================================================================
	
	@Override
	public void setChanged()
	{
		if(getHalf() == DoubleBlockHalf.UPPER)
			updateFromLowerHalf();
		else
			updateUpperHalf();
		
		super.setChanged();
	}
	
	public void updateUpperHalf()
	{
		if(getHalf() == DoubleBlockHalf.UPPER)
			return;
		
		Direction direction = getBlockState().getValue(CartoucheBlock.FACING);
		Orientation orientation = getBlockState().getValue(CartoucheBlock.ORIENTATION);
		
		if(level != null && level.getBlockEntity(worldPosition.relative(Orientation.getMultiDirection(direction, Direction.UP, orientation))) instanceof CartoucheEntity upperCartouche)
			upperCartouche.setChanged();
	}
	
	public void updateFromLowerHalf()
	{
		if(getHalf() == DoubleBlockHalf.LOWER)
			return;
		
		Direction direction = getBlockState().getValue(CartoucheBlock.FACING);
		Orientation orientation = getBlockState().getValue(CartoucheBlock.ORIENTATION);
		
		if(level != null && level.getBlockEntity(worldPosition.relative(Orientation.getMultiDirection(direction, Direction.DOWN, orientation))) instanceof CartoucheEntity lowerCartouche)
		{
			setAddress(lowerCartouche.address);
			setSymbols(lowerCartouche.symbols);
		}
	}
	
	public void setAddressFromAddressTable()
	{
		AddressTable addressTable = AddressTable.getAddressTable(level, this.addressTable);
		Address address = AddressTable.randomAddress((ServerLevel) level, addressTable);
		
		if(address != null)
			setAddress(address);
		
		this.addressTable = null;
		
		this.setChanged();
	}
	
	public void tryGenerateAddress()
	{
		if(address instanceof Address.Dimension dimensionAddress)
			dimensionAddress.generate(level.getServer());
	}
	
	public void setSymbolsFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		setSymbols(Universe.get(level).getSymbols(level.dimension()));
	}
	
	public void setDimensionFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		setDimension(level.dimension().location());
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
		if(addressTable != null)
			setAddressFromAddressTable();
		
		if(symbols == null)
			setSymbolsFromLevel(level);
		
		generationStep = Step.GENERATED;
	}
	
	
	
	public static class Stone extends CartoucheEntity
	{
		public Stone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.STONE_CARTOUCHE.get(), pos, state);
		}
	}
	
	public static class Sandstone extends CartoucheEntity
	{
		public Sandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.SANDSTONE_CARTOUCHE.get(), pos, state);
		}
	}
	
	public static class RedSandstone extends CartoucheEntity
	{
		public RedSandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.RED_SANDSTONE_CARTOUCHE.get(), pos, state);
		}
	}

}
