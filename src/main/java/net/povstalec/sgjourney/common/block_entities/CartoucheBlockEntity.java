package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.data.ModelData;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.AddressTable;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class CartoucheBlockEntity extends BlockEntity implements StructureGenEntity
{
	public static final String ADDRESS_TABLE = "AddressTable";
	public static final String DIMENSION = "Dimension";
	public static final String GALAXY = "Galaxy";
	public static final String SYMBOLS = "Symbols";
	public static final String ADDRESS = "Address";
	
	public static final String LOCAL_ADDRESS = "local_address";
	
	protected StructureGenEntity.Step generationStep = StructureGenEntity.Step.GENERATED;

	@Nullable
	protected ResourceKey<AddressTable> addressTable = null;
	
	protected ResourceKey<Symbols> symbols = null;
	@Nullable
	protected Address address = new Address.Immutable();
	
	public CartoucheBlockEntity(BlockEntityType<?> cartouche, BlockPos pos, BlockState state)
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
			updateClient();
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
    		addressTable = Conversion.stringToAddressTableKey(tag.getString(ADDRESS_TABLE));
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
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(addressTable != null)
			tag.putString(ADDRESS_TABLE, addressTable.location().toString());
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.location().toString());
		if(address != null)
			address.saveToCompoundTag(tag, ADDRESS);
		
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
		CompoundTag tag = new CompoundTag();
		
		if(address != null)
			address.saveToCompoundTag(tag, ADDRESS);
		if(symbols != null)
			tag.putString(SYMBOLS, symbols.location().toString());
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		Address oldAddress = address;
		ResourceKey<Symbols> oldSymbols = symbols;
		
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			if(tag.contains(ADDRESS, Tag.TAG_COMPOUND)) // Dimension Address is saved to a tag, load it
				address = Address.Dimension.loadFromCompoundTag(tag, ADDRESS);
			else if(tag.contains(ADDRESS, Tag.TAG_INT_ARRAY)) // Immutable Address is saved as an array, load it
				address = new Address.Immutable(tag.getIntArray(ADDRESS));
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
	
	public void updateClient()
	{
		if(level != null && !level.isClientSide())
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public void setDimensionAddress(ResourceKey<Level> dimension, Address.Type type)
	{
		this.address = new Address.Dimension(dimension, Optional.empty(), type);
		setChanged();
	}
	
	public void setSymbols(ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols;
		setChanged();
	}
	
	public ResourceKey<Symbols> getSymbols()
	{
		return this.symbols;
	}
	
	public void setAddress(@Nullable Address address)
	{
		this.address = address;
		setChanged();
	}
	
	public Address getAddress()
	{
		if(this.address == null)
			return new Address.Immutable();
		
		return this.address;
	}
	
	public Address getUpToDateAddress()
	{
		if(tryGenerateAddress())
		{
			updateClient();
			setChanged();
		}
		
		return getAddress();
	}
	
	public void setAddressTable(@Nullable ResourceKey<AddressTable> addressTable)
	{
		this.addressTable = addressTable;
	}
	
	@Nullable
	public ResourceKey<AddressTable> getAddressTable()
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
		updateClient();
	}
	
	public void updateUpperHalf()
	{
		if(getHalf() == DoubleBlockHalf.UPPER)
			return;
		
		Direction direction = getBlockState().getValue(CartoucheBlock.FACING);
		Orientation orientation = getBlockState().getValue(CartoucheBlock.ORIENTATION);
		
		if(level != null && level.getBlockEntity(worldPosition.relative(Orientation.getMultiDirection(direction, Direction.UP, orientation))) instanceof CartoucheBlockEntity upperCartouche)
			upperCartouche.setChanged();
	}
	
	public void updateFromLowerHalf()
	{
		if(getHalf() == DoubleBlockHalf.LOWER)
			return;
		
		Direction direction = getBlockState().getValue(CartoucheBlock.FACING);
		Orientation orientation = getBlockState().getValue(CartoucheBlock.ORIENTATION);
		
		if(level != null && level.getBlockEntity(worldPosition.relative(Orientation.getMultiDirection(direction, Direction.DOWN, orientation))) instanceof CartoucheBlockEntity lowerCartouche)
		{
			this.address = lowerCartouche.address;
			this.symbols = lowerCartouche.symbols;
			updateClient();
		}
	}
	
	public void setAddressFromAddressTable()
	{
		AddressTable addressTable = AddressTable.getAddressTable(level.getServer(), this.addressTable);
		Address address = AddressTable.randomAddress((ServerLevel) level, addressTable);
		
		if(address != null)
			this.address = address;
		
		this.addressTable = null;
		
		this.setChanged();
	}
	
	public boolean tryGenerateAddress()
	{
		if(address instanceof Address.Dimension dimensionAddress)
			return dimensionAddress.generate(level.getServer());
		
		return false;
	}
	
	public void setSymbolsFromLevel(Level level)
	{
		if(level.isClientSide())
			return;
		
		this.symbols = Universe.get(level).getSymbols(level.dimension());
	}
	
	public void setDimensionAddressFromLevel(Level level, Address.Type type)
	{
		if(level.isClientSide())
			return;
		
		setDimensionAddress(level.dimension(), type);
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
		setChanged();
	}
	
	
	
	public static class Stone extends CartoucheBlockEntity
	{
		public Stone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.STONE_CARTOUCHE.get(), pos, state);
		}
	}
	
	public static class Sandstone extends CartoucheBlockEntity
	{
		public Sandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.SANDSTONE_CARTOUCHE.get(), pos, state);
		}
	}
	
	public static class RedSandstone extends CartoucheBlockEntity
	{
		public RedSandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.RED_SANDSTONE_CARTOUCHE.get(), pos, state);
		}
	}

}
