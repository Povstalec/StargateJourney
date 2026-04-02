package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.data.ModelData;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
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
	public static final String ADDRESS_TABLE = "AddressTable";
	public static final String DIMENSION = "Dimension";
	public static final String GALAXY = "Galaxy";
	public static final String SYMBOLS = "Symbols";
	public static final String ADDRESS = "Address";
	
	protected StructureGenEntity.Step generationStep = StructureGenEntity.Step.GENERATED;

	@Nullable
	private ResourceLocation addressTable;
	
	private ResourceLocation symbols;
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
    public void load(CompoundTag tag)
    {
    	super.load(tag);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(ADDRESS_TABLE))
    		addressTable = ResourceLocation.tryParse(tag.getString(ADDRESS_TABLE));
    	if(tag.contains(SYMBOLS))
    		symbols = ResourceLocation.tryParse(tag.getString(SYMBOLS));
		
		if(tag.contains(DIMENSION))
		{
			if(tag.contains(ADDRESS))
			{
				if(tag.contains(GALAXY))
					address = new Address.Dimension(Conversion.stringToDimension(tag.getString(DIMENSION)), Optional.ofNullable(Conversion.stringToGalaxyKey(tag.getString(GALAXY))), tag.getIntArray(ADDRESS));
				else
					address = new Address.Dimension(Conversion.stringToDimension(tag.getString(DIMENSION)), Optional.empty(), tag.getIntArray(ADDRESS));
			}
			else
			{
				if(tag.contains(GALAXY))
					address = new Address.Dimension(Conversion.stringToDimension(tag.getString(DIMENSION)), Optional.ofNullable(Conversion.stringToGalaxyKey(tag.getString(GALAXY))));
				else
					address = new Address.Dimension(Conversion.stringToDimension(tag.getString(DIMENSION)), Optional.empty());
			}
			
		}
		else if(tag.contains(ADDRESS))
			address = new Address.Immutable(tag.getIntArray(ADDRESS));
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(addressTable != null)
			tag.putString(ADDRESS_TABLE, addressTable.toString());
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.toString());
		
		if(address instanceof Address.Dimension dimensionAddress)
		{
			tag.putString(DIMENSION, dimensionAddress.getDimension().location().toString());
			if(dimensionAddress.getGalaxy() != null)
				tag.putString(GALAXY,  dimensionAddress.getGalaxy().location().toString());
		}
		if(address != null) // We always save the address because we want to send it in the client update packet
			address.saveToCompoundTag(tag, ADDRESS);
		
		super.saveAdditional(tag);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		Address oldAddress = address;
		ResourceLocation oldSymbols = symbols;
		
		super.onDataPacket(net, packet);
		
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
	
	public void setSymbols(ResourceLocation symbols)
	{
		this.symbols = symbols;
	}
	
	public ResourceLocation getSymbols()
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
		Address address = AddressTable.randomAddress(level.getServer(), addressTable);
		
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
		
		setSymbols(Universe.get(level).getSymbols(level.dimension()).location());
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
