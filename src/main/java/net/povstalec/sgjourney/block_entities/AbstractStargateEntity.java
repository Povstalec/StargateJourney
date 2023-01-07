package net.povstalec.sgjourney.block_entities;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.config.ServerStargateConfig;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.network.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.stargate.Dialing;

public abstract class AbstractStargateEntity extends SGJourneyBlockEntity
{
	private Random random = new Random();

	public int tick = 0;
	public int currentSymbol = 0;
	public int chevronsActive = 0;
	
	private int gateOpenTime = 0;
	private static final int maxGateOpenTime = ServerStargateConfig.max_wormhole_open_time.get() * 20;
	
	public String pointOfOrigin = "sgjourney:error";
	public String symbols = "sgjourney:error";
	public int symbolCount = 39;
	
	public int[] inputAddress = new int[0];
	private CompoundTag targetStargate = new CompoundTag();
	
	public int signalStrength;
	public boolean isPowered;
	private boolean isPrimary = false;
	
	public AbstractStargateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, SGJourneyBlockEntity.Type.STARGATE);
	}
	
	@Override
    public void load(CompoundTag nbt)
	{
        super.load(nbt);
        targetStargate = nbt.getCompound("TargetStargate");
        inputAddress = nbt.getIntArray("InputAddress");
        chevronsActive = nbt.getInt("ChevronsActive");
        isPrimary = nbt.getBoolean("IsPrimary");
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put("TargetStargate", targetStargate);
		nbt.putIntArray("InputAddress", inputAddress);
		nbt.putInt("ChevronsActive", chevronsActive);
		nbt.putBoolean("IsPrimary", isPrimary);
		super.saveAdditional(nbt);
	}
	
	@Override
	protected String generateID()
	{
		int symbol[] = new int[0];
		
		while(symbol.length < 8)
		{
			int glyph = randomSymbol();
			boolean isValid = true;
			
			for(int i = 0; i < symbol.length; i++)
			{
				if(glyph == symbol[i])
				{
					isValid = false;
				}
			}
			if(isValid)
			{
				symbol = growIntArray(symbol, glyph);
			}
		}
		String address = "-" + symbol[0] + "-" + symbol[1] + "-" + symbol[2] + "-" + symbol[3] + "-" + symbol[4] + "-" + symbol[5] + "-" + symbol[6] + "-" + symbol[7] + "-";
		
		StargateJourney.LOGGER.info("Generated address: " + address);
		
		return address;
	}
	
	@Override
	public void addToBlockEntityList()
	{
		super.addToBlockEntityList();
    	StargateNetwork.get(level).addToNetwork(id, blockEntity);
		
	}
	
	@Override
	public void addNewToBlockEntityList()
	{
		super.addNewToBlockEntityList();
    	StargateNetwork.get(level).addToNetwork(id, blockEntity);
		
	}

	@Override
	public void removeFromBlockEntityList()
	{
		super.removeFromBlockEntityList();
		StargateNetwork.get(level).removeFromNetwork(level, id);
	}
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide)
			return;
		player.sendSystemMessage(Component.literal("Point of Origin: " + pointOfOrigin).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.literal("Symbols: " + symbols).withStyle(ChatFormatting.LIGHT_PURPLE));
		super.getStatus(player);
	}
	
	public boolean canConnect()
	{
		if(isBusy())
		{
			StargateJourney.LOGGER.info("Stargate is busy.");
			return false;
		}
		if(isObstructed())
		{
			StargateJourney.LOGGER.info("Stargate is obstructed.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns true if the Stargate is connected to another Stargate, false if it isn't
	 */
	public boolean isBusy()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		if(gateState.getBlock() instanceof AbstractStargateBlock)
		{
			return this.level.getBlockState(gatePos).getValue(AbstractStargateBlock.CONNECTED);
		}
		return false;
	}
	
	public  boolean isObstructed()
	{
		return false;
	}
	
	public boolean hasEnergy()
	{
		return true;
	}
	
	public abstract SoundEvent chevronEngageSound();

	public abstract SoundEvent failSound();
	
	/**
	 * Sets the connection state of the Stargate
	 */
	public void setConnected(boolean isConnected)
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		if(gateState.getBlock() instanceof AbstractStargateBlock)
		{
			level.setBlock(gatePos, gateState.setValue(AbstractStargateBlock.CONNECTED, isConnected), 2);
		}
		
	}
	
	/**
	 * Gets the direction of the Stargate
	 */
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		if(gateState.getBlock() instanceof AbstractStargateBlock)
		{
			return this.level.getBlockState(gatePos).getValue(AbstractStargateBlock.FACING);
		}
		return null;
	}
	
	/**
	 * Sets the Stargate's point of origin based on the dimension
	 */
	public void setPointOfOrigin(Level level)
	{
		pointOfOrigin = StargateNetwork.get(level).getPointOfOrigin(level.dimension().location().toString());
	}
	
	public void setSymbols(Level level)
	{
		symbols = StargateNetwork.get(level).getSymbols(level.dimension().location().toString());
		//TODO symbolCount
	}
	
	/**
	 * Sets the number of active chevrons to be the same as the length of the dialed address
	 */
	public void updateAddressAndChevrons(int[] inputAddress, int chenvronsActive)
	{
		this.inputAddress = inputAddress;
		this.chevronsActive = chenvronsActive;
	}
	
	private void setPrimary(boolean isPrimary)
	{
		this.isPrimary = isPrimary;
	}
	
	protected boolean isPrimary()
	{
		return isPrimary;
	}
	
	protected boolean symbolInAddress(int symbol)
	{
		for(int i = 0; i < inputAddress.length; i++)
		{
			if(symbol == inputAddress[i])
				return true;
		}
		return false;
	}
	
	public void encodeChevron(int symbol)
	{
		if(symbolInAddress(symbol))
			return;
		
		if(symbol == 0)
			lockChevron();
		else
			engageChevron(symbol);
			
	}
	
	protected void engageChevron(int symbol)
	{
		if(inputAddress.length >= 8)
		{
			resetGate();
			level.playSound((Player)null, worldPosition, failSound(), SoundSource.BLOCKS, 0.5F, 1F);
			return;
		}
		
		inputAddress = growIntArray(inputAddress, symbol);
		level.playSound((Player)null, worldPosition, chevronEngageSound(), SoundSource.BLOCKS, 0.5F, 1F);
		updateAddressAndChevrons(inputAddress, inputAddress.length);
	}
	
	protected void lockChevron()
	{
		if(inputAddress.length < 6)
		{
			resetGate();
			level.playSound((Player)null, worldPosition, failSound(), SoundSource.BLOCKS, 0.5F, 1F);
			return;
		}
		
		if(!isBusy())
		{
			tryConnect();
			level.playSound((Player)null, worldPosition, chevronEngageSound(), SoundSource.BLOCKS, 0.5F, 1F);
		}
		else
			disconnectGate();
	}
	
	/**
	 * Connects the Stargate to another Stargate depending on the address.
	 */
	public void tryConnect()
	{
		AbstractStargateEntity target = Dialing.dialStargate(level, inputAddress);
		
		if(target != null)
		{
			if(!target.canConnect())
			{
				level.playSound((Player)null, worldPosition, failSound(), SoundSource.BLOCKS, 0.25F, 1F);
				resetGate();
				return;
			}
			
			ResourceKey<Level> dimension = target.getLevel().dimension();
			this.setTargetStargate(target);
			connectGate(dimension);
		}
		else
		{
			StargateJourney.LOGGER.info("Could not reach target Stargate");
			level.playSound((Player)null, worldPosition, failSound(), SoundSource.BLOCKS, 0.25F, 1F);
			resetGate();
		}
	}
	
	/**
	 * Connects the Stargate to another Stargate.
	 */
	private void connectGate(ResourceKey<Level> dimension)
	{
		setConnected(true);
		setPrimary(true);
		
		ServerLevel targetLevel = level.getServer().getLevel(dimension);
		ServerLevel localLevel = level.getServer().getLevel(level.dimension());
		ForgeChunkManager.forceChunk(localLevel, StargateJourney.MODID, worldPosition, level.getChunk(this.worldPosition).getPos().x, level.getChunk(this.worldPosition).getPos().z, true, true);
		ForgeChunkManager.forceChunk(targetLevel, StargateJourney.MODID, getTarget().worldPosition, level.getChunk(getTarget().worldPosition).getPos().x, level.getChunk(getTarget().worldPosition).getPos().z, true, true);
		
		AbstractStargateEntity target = getTarget();
		target.setPrimary(false);
		target.updateAddressAndChevrons(addressStringToArray(getID()), this.chevronsActive);
		target.setConnected(true);
		target.setTargetStargate(this);
	}
	
	private void setTargetStargate(AbstractStargateEntity stargate)
	{
		targetStargate.putString("Address", stargate.getID());
		targetStargate.putString("Dimension", stargate.level.dimension().location().toString());
		targetStargate.putIntArray("Coordinates", new int[] {stargate.getBlockPos().getX(), stargate.getBlockPos().getY(), stargate.getBlockPos().getZ()});
	}
	
	/**
	 * Disconnects the Stargate from the Stargate it's connected to.
	 */
	public void disconnectGate()
	{
		if(getTarget() != null)
		{
			getTarget().resetGate();
		}
		resetGate();
	}
	
	public void resetGate()
	{
		setPrimary(false);
		updateAddressAndChevrons(new int[0], 0);
		if(isBusy())
		{
			setConnected(false);
			level.playSound((Player)null, worldPosition, SoundInit.WORMHOLE_CLOSE.get(), SoundSource.BLOCKS, 0.25F, 1F);
		}
		gateOpenTime = 0;
		
		ServerLevel localLevel = level.getServer().getLevel(level.dimension());
		ForgeChunkManager.forceChunk(localLevel, StargateJourney.MODID, this.worldPosition, level.getChunk(this.worldPosition).getPos().x, level.getChunk(this.worldPosition).getPos().z, false, true);
	}
	
	public void wormhole(Entity traveler)
	{
		Level level = traveler.getLevel();
		if(level.isClientSide())
			return;
		
		int[] targetCoords = targetStargate.getIntArray("Coordinates");
		ServerLevel serverlevel = level.getServer().getLevel(getTargetDimension());
        
        if (serverlevel == null)
        {
        	System.out.println("Dimension is null");
            return;
        }
    	
    	if(traveler instanceof ServerPlayer player)
        	player.teleportTo(serverlevel, targetCoords[0] + 0.5, targetCoords[1] + 2, targetCoords[2] + 0.5, preserveYRot(player.getYRot()), player.getXRot());
    	/*else TODO Fix this for Entities
    	{
    		if((ServerLevel)level != serverlevel)
    		{
            	System.out.println("(ServerLevel)level != serverlevel");
        		traveler.changeDimension(serverlevel, new WormholeTeleporter((ServerLevel) traveler.getLevel()));
    		}
    		traveler.moveTo(targetCoords[0] + 0.5, targetCoords[1] + 2, targetCoords[2] + 0.5, preserveYRot(traveler.getYRot()), traveler.getXRot());
    	}*/
    	level.playSound((Player)null, traveler.blockPosition(), SoundInit.WORMHOLE_ENTER.get(), SoundSource.BLOCKS, 0.25F, 1F);
    	System.out.println("Wormholing to " + traveler.getX() + " " + traveler.getY() + " " + traveler.getZ());
	}
	
	private float preserveYRot(float yRot)
	{
		float stargate1Direction = Mth.wrapDegrees(this.getDirection().toYRot());
    	float stargate2Direction = Mth.wrapDegrees(getTarget().getDirection().toYRot());
    	
    	float relativeRot = yRot - stargate1Direction;
    	
    	yRot = relativeRot + stargate2Direction + 180;
    	
    	return yRot;
	}
	
	/*private Vec3 preserveMomentum(Entity traveler)
	{
		Vec3 vec3 = traveler.getDeltaMovement();
		double xSpeed = vec3.x;
		double ySpeed = vec3.y;
		double zSpeed = vec3.z;
		
		
		
		return vec3;
	}*/
	
	public void tick(Level level, BlockPos pos, BlockState state)
    {
		if(isBusy() && isPrimary())
		{
			if(!(getTarget() instanceof AbstractStargateEntity))
				disconnectGate();

			double x = 0;
			
			double z = 0;
			
			if(getDirection().getAxis() == Direction.Axis.X)
				z = 2.5;
			else
				x = 2.5;
			
			AABB localBox = new AABB((this.worldPosition.getX() + 0.5 + x), (this.worldPosition.getY() + 1), (this.worldPosition.getZ() + 0.5 + z), 
									(this.worldPosition.getX() + 0.5 - x), (this.worldPosition.getY() + 5), (this.worldPosition.getZ() + 0.5 - z));
			
			List<Entity> localEntities = this.level.getEntitiesOfClass(Entity.class, localBox);
			if(!localEntities.isEmpty())
	    	{
	    		localEntities.stream().forEach(this::wormhole);
	    	}
			
			gateOpenTime++;
			
			if(gateOpenTime >= maxGateOpenTime)
			{
				disconnectGate();
			}
		}
		tick++;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(pos, chevronsActive, isBusy(), tick, pointOfOrigin, symbols, currentSymbol));
    }
	
	@Override
	public AABB getRenderBoundingBox()
    {
        AABB bb = new AABB((this.worldPosition.getX() - 3), (this.worldPosition.getY()), (this.worldPosition.getZ() - 3), (this.worldPosition.getX() + 4), (this.worldPosition.getY() + 9), (this.worldPosition.getZ() + 4));
        
        
        return bb;
    }
	
	/**
     * Used for growing the array.
     *
     * @param array The original array.
     * @param x Number added to the array.
     */
	protected int[] growIntArray(int array[], int x)
	{
		int[] newarray = new int[array.length + 1];
		
		for (int i = 0; i < array.length; i++)
		{
			newarray[i] = array[i];
		}
		
		newarray[array.length] = x;
		
		return newarray;
	}
    
	/**
	 * Used for generating a random symbol for the 9 Chevron address.
	 */
	public int randomSymbol()
	{
		int glyph = random.nextInt(1, 36);
		
		return glyph;
	}
	
	private ResourceKey<Level> getTargetDimension()
	{
		if(!targetStargate.contains("Dimension"))
			return null;
		
		String dimension = targetStargate.getString("Dimension");
		return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(dimension.split(":")[0], dimension.split(":")[1]));
	}
	
	private AbstractStargateEntity getTarget()
	{
		if(!targetStargate.contains("Coordinates"))
			return null;
		int[] coords = targetStargate.getIntArray("Coordinates");
		
		if(level.getServer().getLevel(getTargetDimension()).getBlockEntity(new BlockPos(coords[0], coords[1], coords[2])) instanceof AbstractStargateEntity stargate)
			return stargate;
		return null;
	}
	
	private int[] addressStringToArray(String addressString)
	{
		String[] stringArray = addressString.split("-");
		int[] intArray = new int[0];
		
		for(int i = 1; i < stringArray.length; i++)
		{
			int number = Character.getNumericValue(stringArray[i].charAt(0));
			intArray = growIntArray(intArray, number);
		}
		
		return intArray;
	}
}
