package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.packets.ClientboundCartoucheUpdatePacket;
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
		super.onLoad();
		
		if(level.isClientSide())
			return;
		
		if(generationStep == StructureGenEntity.Step.READY)
			generate();
		
		generateAddress();
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
			if(tag.contains(GALAXY))
				address = new Address.Dimension(Conversion.stringToDimension(tag.getString(DIMENSION)), Optional.of(Conversion.stringToGalaxyKey(tag.getString(GALAXY))));
			else
				address = new Address.Dimension(Conversion.stringToDimension(tag.getString(DIMENSION)), Optional.empty());
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
		else if(address != null)
			tag.putIntArray(ADDRESS, address.toArray());
		
		super.saveAdditional(tag);
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
	
	public void setAddress(Address address)
	{
		this.address = address;
	}
	
	public Address getAddress()
	{
		if(this.address == null)
			return new Address.Immutable();
		
		return this.address;
	}
	
	public ResourceLocation getAddressTable()
	{
		return this.addressTable;
	}
	
	//============================================================================================
	//****************************************Functionality***************************************
	//============================================================================================
	
	public void setAddressFromAddressTable()
	{
		AddressTable addressTable = AddressTable.getAddressTable(level, this.addressTable);
		Address address = AddressTable.randomAddress(level.getServer(), addressTable);
		
		if(address != null)
			setAddress(address);
		
		this.addressTable = null;
		
		this.setChanged();
	}
	
	public void generateAddress()
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
	
	protected void updateClient()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)),
				new ClientboundCartoucheUpdatePacket(worldPosition, symbols == null ? StargateJourney.EMPTY_LOCATION : symbols, address != null ? this.address.toArray() : new int[0]));
	}
	
	@Override
	public AABB getRenderBoundingBox()
	{
		return new AABB(getBlockPos().getX() - 1, getBlockPos().getY(), getBlockPos().getZ() - 1,
				getBlockPos().getX() + 2, getBlockPos().getY() + 2, getBlockPos().getZ() + 2);
	}
	
	public void tick(Level level, BlockPos pos, BlockState state)
	{
		if(state.getValue(CartoucheBlock.HALF) == DoubleBlockHalf.LOWER)
			updateClient();
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		if(generationStep == Step.SETUP)
			generationStep = Step.READY;
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
