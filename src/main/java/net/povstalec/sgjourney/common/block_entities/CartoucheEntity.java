package net.povstalec.sgjourney.common.block_entities;

import java.util.Iterator;
import java.util.Set;

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
import net.povstalec.sgjourney.common.packets.ClientboundCartoucheUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.AddressTable;

public abstract class CartoucheEntity extends BlockEntity
{
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public static final String ADDRESS_TABLE = "AddressTable";
	public static final String DIMENSION = "Dimension";
	public static final String SYMBOLS = "Symbols";
	public static final String ADDRESS = "Address";

	private String addressTable = EMPTY;
	private String dimension;
	
	private String symbols;
	private int[] address = new int[0];
	
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
		
		if(addressTable != null && !addressTable.equals(EMPTY))
			setDimensionFromAddressTable();
		else if(dimension == null)
			setDimension(level);
		
		if(symbols == null)
			setSymbols(level);
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);

    	if(tag.contains(ADDRESS_TABLE))
    		addressTable = tag.getString(ADDRESS_TABLE);
    	if(tag.contains(DIMENSION))
    		dimension = tag.getString(DIMENSION);
    	if(tag.contains(SYMBOLS))
    		symbols = tag.getString(SYMBOLS);
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(addressTable != null)
			tag.putString(ADDRESS_TABLE, addressTable);
		if(dimension != null)
			tag.putString(DIMENSION, dimension);
		if(symbols != null)
			tag.putString(SYMBOLS, symbols);
		
		super.saveAdditional(tag);
	}
	
	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getBlockPos().getX() - 1, getBlockPos().getY(), getBlockPos().getZ() - 1,
        		getBlockPos().getX() + 2, getBlockPos().getY() + 2, getBlockPos().getZ() + 2);
    }
	
	public void setDimension(Level level)
	{
		if(level.isClientSide())
			return;
		
		dimension = level.dimension().location().toString();
	}
	
	public void setDimensionFromAddressTable()
	{
		if(level.isClientSide())
			return;
		
		AddressTable addressTable = AddressTable.getAddressTable(level, ResourceLocation.tryParse(this.addressTable));
		String dimension = AddressTable.getRandomDimension(level, addressTable);
		if(dimension != null && !dimension.equals(EMPTY))
			this.dimension = dimension;
		this.addressTable = EMPTY;
	}
	
	public void setSymbols(Level level)
	{
		if(level.isClientSide())
			return;
		
		symbols = Universe.get(level).getSymbols(level.dimension().location().toString());
	}
	
	public void setSymbols(String symbols)
	{
		this.symbols = symbols;
	}
	
	public String getSymbols()
	{
		return this.symbols;
	}
	
	public void setAddress(int[] address)
	{
		this.address = address;
	}
	
	public int[] getAddress()
	{
		return this.address;
	}
	
	public String getAddressFromDimension()
	{
		String galaxy = EMPTY;
		Set<String> galaxies = Universe.get(level).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys();
		
		Iterator<String> iterator = galaxies.iterator();
		if(iterator.hasNext())
			galaxy = iterator.next();
			
		
		return Universe.get(level).getAddressInGalaxyFromDimension(galaxy, dimension);
	}
	
	protected void updateClient()
	{
		if(level.isClientSide())
			return;
		//System.out.println(this.dimension);
		
		int[] address = addressTable.equals(EMPTY) ? this.address : new int[0];
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundCartoucheUpdatePacket(worldPosition, symbols, address));
	}
	
	public void tick(Level level, BlockPos pos, BlockState state)
	{
		if(this.address.length == 0)
			setAddress(Address.addressStringToIntArray(getAddressFromDimension()));
		
		if(state.getValue(CartoucheBlock.HALF) == DoubleBlockHalf.LOWER)
			updateClient();
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

}
