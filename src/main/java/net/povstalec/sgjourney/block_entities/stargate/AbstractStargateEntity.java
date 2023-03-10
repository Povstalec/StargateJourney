package net.povstalec.sgjourney.block_entities.stargate;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.config.ServerEnergyConfig;
import net.povstalec.sgjourney.config.ServerStargateConfig;
import net.povstalec.sgjourney.data.BlockEntityList;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.data.Universe;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.packets.ServerboundStargateDialingPacket;
import net.povstalec.sgjourney.stargate.Addressing;
import net.povstalec.sgjourney.stargate.Dialing;
import net.povstalec.sgjourney.stargate.PointOfOrigin;
import net.povstalec.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.stargate.Symbols;
import net.povstalec.sgjourney.stargate.Wormhole;

public abstract class AbstractStargateEntity extends SGJourneyBlockEntity
{
	// Basic Info
	private static final boolean REQUIRE_ENERGY = !ServerEnergyConfig.disable_energy_use.get();
	private static final long CONNECTION_ENERGY_COST = ServerStargateConfig.connection_energy_cost.get();
	private static final long INTERSTELLAR_CONNECTION_ENERGY_COST = ServerStargateConfig.interstellar_connection_energy_cost.get();
	private static final long INTERGALACTIC_CONNECTION_ENERGY_COST = ServerStargateConfig.intergalactic_connection_energy_cost.get();
	private static final int maxGateOpenTime = ServerStargateConfig.max_wormhole_open_time.get() * 20;
	private static final boolean END_CONNECTION_FROM_BOTH_ENDS = ServerStargateConfig.end_connection_from_both_ends.get();
	private static Stargate.Gen generation;
	
	// Used during gameplay
	private int tick = 0;
	private boolean isPrimaryChevronEngaged = false;
	private boolean dialingOut = false;
	private boolean hasDHD = false;
	private int openTime = 0;
	private int timesOpened = 0;
	protected String pointOfOrigin = EMPTY;
	protected String symbols = EMPTY;
	
	// Dialing and memory
	private int[] address = new int[0];
	private String connectionID = EMPTY;
	
	//private Stargate.FilterType filter = Stargate.FilterType.NONE;
	//private ListTag whitelist;
	//private ListTag blacklist;

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, Stargate.Gen generation)
	{
		super(blockEntity, pos, state, SGJourneyBlockEntity.Type.STARGATE);
		this.generation = generation;
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		isPrimaryChevronEngaged = nbt.getBoolean("IsPrimaryChevronEngaged");
		dialingOut = nbt.getBoolean("DialingOut");
		openTime = nbt.getInt("OpenTime");
		timesOpened = nbt.getInt("TimesOpened");
		address = nbt.getIntArray("Address");
		
		connectionID = nbt.getString("ConnectionID");
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putBoolean("IsPrimaryChevronEngaged", isPrimaryChevronEngaged);
		nbt.putBoolean("DialingOut", dialingOut);
		nbt.putInt("OpenTime", openTime);
		nbt.putInt("TimesOpened", timesOpened);
		nbt.putIntArray("Address", address);
		
		nbt.putString("ConnectionID", connectionID);
		super.saveAdditional(nbt);
	}
	
	@Override
	public CompoundTag addToBlockEntityList()
	{
		CompoundTag blockEntity = super.addToBlockEntityList();
    	StargateNetwork.get(level).addStargate(level.getServer(), getID(), blockEntity);
		return blockEntity;
	}
	
	@Override
	public CompoundTag addNewToBlockEntityList()
	{
		CompoundTag blockEntity = super.addNewToBlockEntityList();
    	StargateNetwork.get(level).addStargate(level.getServer(), getID(), blockEntity);
		return blockEntity;
	}

	@Override
	public void removeFromBlockEntityList()
	{
		super.removeFromBlockEntityList();
		StargateNetwork.get(level).removeStargate(level, getID());
	}
	
	@Override
	protected String generateID()
	{
		Random random = new Random();
		String address = EMPTY;
		while(true)
		{
			address = Addressing.addressIntArrayToString(Addressing.randomAddress(8, 36, random.nextLong()));
			
			if(!BlockEntityList.get(level).getBlockEntities(SGJourneyBlockEntity.Type.STARGATE.id).contains(address))
				break;
		}
		return address;
	}

	
	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB((this.worldPosition.getX() - 3), (this.worldPosition.getY()), (this.worldPosition.getZ() - 3), (this.worldPosition.getX() + 4), (this.worldPosition.getY() + 9), (this.worldPosition.getZ() + 4));
    }
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	public void inputSymbol(int symbol)
	{
		PacketHandlerInit.INSTANCE.sendToServer(new ServerboundStargateDialingPacket(this.getBlockPos(), symbol));
	}
	
	public void engageSymbol(int symbol)
	{
		if(level.isClientSide())
			return;
		
		if(Addressing.addressContainsSymbol(getAddress(), symbol))
			return;
		
		if(symbol == 0)
			lockPrimaryChevron();
		else
			encodeChevron(symbol);
	}
	
	protected void encodeChevron(int symbol)
	{	
		if(getAddress().length >= 8)
			resetStargate(true);
		else
		{
			growAddress(symbol);
			engageChevron();
		}
		this.setChanged();
	}
	
	protected void lockPrimaryChevron()
	{
		if(level.isClientSide())
			return;
		
		if(getAddress().length == 0)
			resetStargate(false);
		else if(getAddress().length < 6)
			resetStargate(true);
		else if(!isConnected())
		{
			if(canDial())
			{
				setPrimaryChevronEndaged(true);
				engageChevron();
				engageStargate(getAddress());
			}
			else
				resetStargate(true);
		}
		else
		{
			if(!END_CONNECTION_FROM_BOTH_ENDS && !this.isDialingOut())
				StargateJourney.LOGGER.info("Cannot disconnect Stargate from this side!");
			else
			{
				StargateJourney.LOGGER.info("Stargate disconnected due to locking primary chevron");
				disconnectStargate();
			}
		}
		
	}
	
	private void engageChevron()
	{
		level.playSound((Player)null, worldPosition, chevronEngageSound(), SoundSource.BLOCKS, 0.25F, 1F);
	}
	
	public void engageStargate(int[] address)
	{
		AbstractStargateEntity targetStargate = Dialing.dialStargate(this.level, address);
		
		if(targetStargate != null)
		{
			if(!targetStargate.canConnect())
			{
				resetStargate(true);
				return;
			}
			
			dialStargate(targetStargate);
		}
		else
			resetStargate(true);
	}
	
	private void dialStargate(AbstractStargateEntity targetStargate)
	{
		this.connectionID = StargateNetwork.get(level).startConnection(this, targetStargate);
		
		if(REQUIRE_ENERGY)
			this.extractEnergy(100, false);
	}
	
	public void connectStargate(String connectionID, boolean dialingOut, int[] address)
	{
		this.connectionID = connectionID;
		this.setConnected(true);
		this.setAddress(address);
		this.setDialingOut(dialingOut);
		this.timesOpened++;
		this.setChanged();
	}
	
	public void resetStargate(boolean causedByFailure)
	{
		if(isConnected())
		{
			level.playSound((Player)null, worldPosition, SoundInit.WORMHOLE_CLOSE.get(), SoundSource.BLOCKS, 0.25F, 1F);
			setConnected(false);
		}
		
		resetAddress();
		this.openTime = 0;
		this.isPrimaryChevronEngaged = false;
		this.dialingOut = false;
		this.connectionID = EMPTY;
		
		if(causedByFailure)
			level.playSound((Player)null, worldPosition, failSound(), SoundSource.BLOCKS, 0.25F, 1F);
		
		setChanged();
		StargateJourney.LOGGER.info("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ());
	}
	
	public void disconnectStargate()
	{
		StargateNetwork.get(level).terminateConnection(level.getServer(), connectionID);
		resetStargate(false);
	}
	
	protected boolean canConnect()
	{
		if(isConnected())
		{
			StargateJourney.LOGGER.info("Stargate is already connected");
			return false;
		}
		if(isObstructed())
		{
			StargateJourney.LOGGER.info("Stargate is obstructed");
			return false;
		}
		
		return true;
	}
	
	protected boolean canDial()
	{
		if(isConnected())
		{
			StargateJourney.LOGGER.info("Stargate is already connected");
			return false;
		}
		if(isObstructed())
		{
			StargateJourney.LOGGER.info("Stargate is obstructed");
			return false;
		}
		if(REQUIRE_ENERGY && !hasEnergy())
		{
			StargateJourney.LOGGER.info("Stargate does not have enough power to estabilish a stable connection");
			return false;
		}
		
		return true;
	}
	
	protected void growAddress(int symbol)
	{
		this.address = Addressing.growIntArray(this.address, symbol);
	}
	
	protected void resetAddress()
	{
		this.address = new int[0];
	}
	
	//============================================================================================
	//******************************************Symbols*******************************************
	//============================================================================================
	
	/**
	 * Sets the Stargate's point of origin based on the dimension
	 */
	public void setPointOfOrigin(Level level)
	{
		pointOfOrigin = Universe.get(level).getPointOfOrigin(level.dimension().location().toString());
		this.setChanged();
	}
	
	protected boolean isPointOfOriginValid(Level level)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return pointOfOriginRegistry.containsKey(new ResourceLocation(pointOfOrigin));
	}
	
	public void setSymbols(Level level)
	{
		symbols = Universe.get(level).getSymbols(level.dimension().location().toString());
		this.setChanged();
	}
	
	protected boolean areSymbolsValid(Level level)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<Symbols> symbolRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		return symbolRegistry.containsKey(new ResourceLocation(symbols));
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================

	public int getMaxGateOpenTime()
	{
		return maxGateOpenTime;
	}
	
	public Stargate.Gen getGeneration()
	{
		return generation;
	}
	
	public void setTickCount(int tick)
	{
		this.tick = tick;
	}
	
	public int getTickCount()
	{
		return this.tick;
	}
	
	public void setDialingOut(boolean dialingOut)
	{
		this.dialingOut = dialingOut;
	}
	
	public boolean isDialingOut()
	{
		return this.dialingOut;
	}
	
	public boolean hasDHD()
	{
		//TODO Fill this
		return false;
	}
	
	public int getOpenTime()
	{
		return this.openTime;
	}
	
	public int getTimesOpened()
	{
		return this.timesOpened;
	}
	
	public void setPointOfOrigin(String pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin;
	}
	
	public String getPointOfOrigin()
	{
		return this.pointOfOrigin;
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
	
	public int getChevronsEngaged()
	{
		return getAddress().length;
	}
	
	public void setPrimaryChevronEndaged(boolean engaged)
	{
		this.isPrimaryChevronEngaged = engaged;
	}
	
	public boolean isPrimaryChevronEngaged()
	{
		return this.isPrimaryChevronEngaged;
	}
	
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBlock)
			return this.level.getBlockState(gatePos).getValue(AbstractStargateBlock.FACING);

		StargateJourney.LOGGER.info("Couldn't find Stargate Direction");
		return null;
	}
	
	public void setConnected(boolean isConnected)
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBlock)
			level.setBlock(gatePos, gateState.setValue(AbstractStargateBlock.CONNECTED, isConnected), 2);
		else
			StargateJourney.LOGGER.info("Couldn't set Stargate to connected state");
		setChanged();
		
	}
	
	public boolean isConnected()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBlock)
			return this.level.getBlockState(gatePos).getValue(AbstractStargateBlock.CONNECTED);
		
		return false;
	}
	
	public boolean isObstructed()
	{
		Direction direction = getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		BlockPos centerPos = this.worldPosition.above(3);
		int obstructingBlocks = 0;
		
		for(int width = -2; width <= 2; width++)
		{
			for(int height = -2; height <= 2; height++)
			{
				BlockPos pos = centerPos.relative(direction, width).relative(Direction.UP, height);
				BlockState state = level.getBlockState(pos);
				
				if((!state.getMaterial().isReplaceable() && !(state.getBlock() instanceof AbstractStargateRingBlock)) || state.getMaterial() == Material.LAVA)
					obstructingBlocks++;
			}
		}
		System.out.println("Stargate is obstructed by " + obstructingBlocks + " blocks");
		return obstructingBlocks > 12;
	}
	
	public boolean hasEnergy()
	{
		switch(getAddress().length)
		{
		case 6:
			if(this.getEnergyStored() >= INTERSTELLAR_CONNECTION_ENERGY_COST)
				return true;
			break;
		case 7:
			if(this.getEnergyStored() >= INTERGALACTIC_CONNECTION_ENERGY_COST)
				return true;
			break;
		case 8:
			//TODO Change this
			if(this.getEnergyStored() >= INTERSTELLAR_CONNECTION_ENERGY_COST)
				return true;
			break;
		}
		
		return false;
	}
	
	public abstract SoundEvent chevronEngageSound();

	public abstract SoundEvent failSound();
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide)
			return;
		
		super.getStatus(player);
		player.sendSystemMessage(Component.literal("Point of Origin: " + pointOfOrigin).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.literal("Symbols: " + symbols).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.literal("Open Time: " + openTime + "/" + maxGateOpenTime).withStyle(ChatFormatting.DARK_AQUA));
		player.sendSystemMessage(Component.literal("Times Opened: " + timesOpened).withStyle(ChatFormatting.BLUE));
	}
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		return false;
	}

	@Override
	public long capacity()
	{
		return 1000000000000L;
	}

	@Override
	public long maxReceive()
	{
		return 1000000000L;
	}

	@Override
	public long maxExtract()
	{
		return 1000000000L;
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public void wormhole()
	{
		/*if(!level.isClientSide() && !(getTargetStargate() instanceof AbstractStargateEntity))
		{
			StargateJourney.LOGGER.info("Stargate is not present");
			disconnectStargate();
		}*/
		
		if(isDialingOut())
		{
			double x = 0;
			double z = 0;
			
			if(getDirection().getAxis() == Direction.Axis.X)
				z = 2.5;
			else
				x = 2.5;
			
			AABB localBox = new AABB((worldPosition.getX() + 0.5 + x), (worldPosition.getY() + 1), (worldPosition.getZ() + 0.5 + z), 
									(worldPosition.getX() + 0.5 - x), (worldPosition.getY() + 5), (worldPosition.getZ() + 0.5 - z));

			List<Entity> localEntities = level.getEntitiesOfClass(Entity.class, localBox);
			if(!localEntities.isEmpty())
	    		localEntities.stream().forEach((traveler) -> Wormhole.doWormhole(this.connectionID, traveler));
		}
		
		if(level.isClientSide())
			return;
		
		this.openTime++;

		this.setChanged();
		
		/*if(REQUIRE_ENERGY)
			depleteEnergy(this, getTargetStargate());*/
		
		if(openTime >= maxGateOpenTime)
		{
			StargateJourney.LOGGER.info("Stargate exceeded max connection time");
			disconnectStargate();
		}
	}
	
	public static void depleteEnergy(AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		if(dialingStargate.tick % 20 == 0)
		{
			if(dialingStargate.getEnergyStored() == 0 &&
					dialedStargate.getEnergyStored() == 0)
			{
				StargateJourney.LOGGER.info("Stargates ran out of power");
				dialingStargate.disconnectStargate();
			}
			
			if(dialedStargate.getEnergyStored() > dialingStargate.getEnergyStored())
				dialedStargate.extractEnergy(CONNECTION_ENERGY_COST, false);
			else
				dialingStargate.extractEnergy(CONNECTION_ENERGY_COST, false);
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractStargateEntity stargate)
    {
		stargate.tick++;
		
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientboundStargateUpdatePacket(stargate.worldPosition, stargate.address, stargate.dialingOut, stargate.tick, stargate.pointOfOrigin, stargate.symbols));
    }
}
