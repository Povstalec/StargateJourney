package net.povstalec.sgjourney.common.block_entities.dhd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public abstract class AbstractDHDEntity extends EnergyBlockEntity
{
	private AbstractStargateEntity stargate = null;
	
	protected boolean enableAdvancedProtocols = false;
	
	protected int desiredEnergyLevel = 150000;
	protected int maxEnergyTransfer = 2500;
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
	}
	
	public int getMaxDistance()
	{
		return 16;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		return false;
	}

	@Override
	protected long capacity()
	{
		return 0;
	}

	@Override
	protected long maxReceive()
	{
		return 0;
	}

	@Override
	protected long maxExtract()
	{
		return 0;
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		if(stargate.getEnergyStored() < this.desiredEnergyLevel)
		{
			long needed = this.desiredEnergyLevel - stargate.getEnergyStored();
			
			long energySent = needed > this.maxEnergyTransfer ? this.maxEnergyTransfer : needed;
			
			stargate.receiveEnergy(energySent, false);
		}
	}
	
	/*
	 * Searches for the nearest Stargate to the DHD
	 */
	public AbstractStargateEntity getNearestStargate(int maxDistance)
	{
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = this.level.getChunk(this.getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(this.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
						stargates.add(stargate);
				});
			}
		}
		
		Iterator<AbstractStargateEntity> iterator = stargates.iterator();
		double bestDistance = (double) maxDistance;
		AbstractStargateEntity bestStargate = null;
		
		while(iterator.hasNext())
		{
			AbstractStargateEntity stargate = iterator.next();
			
			double distance = distance(this.getBlockPos(), stargate.getCenterPos());
			
			if(distance <= bestDistance)
			{
				bestDistance = distance;
				bestStargate = stargate;
			}
			
		}
		
		return bestStargate;
	}
	
	private double distance(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		double stargateDistance = Math.sqrt(x*x + y*y + z*z);
		
		return stargateDistance;
	}
	
	/*
	 * Engages the next Stargate chevron
	 */
	public void engageChevron(int symbol)
	{
		if(stargate != null)
		{
			if(symbol == 0)
				level.playSound((Player)null, this.getBlockPos(), SoundInit.MILKY_WAY_DHD_ENTER.get(), SoundSource.BLOCKS, 0.25F, 1F);
			Stargate.Feedback feedback = stargate.engageSymbol(symbol);
			
			if(feedback.isError())
			{
				Component message = feedback.getFeedbackMessage();
				AABB localBox = new AABB((getBlockPos().getX() - 4), (getBlockPos().getY() - 4), (getBlockPos().getZ() - 4), 
						(getBlockPos().getX() + 5), (getBlockPos().getY() + 5), (getBlockPos().getZ() + 5));
				level.getEntitiesOfClass(Player.class, localBox).stream().forEach((player) -> player.displayClientMessage(message, true));
			}
		}
		else
			System.out.println("Stargate not found");
	}
	
	public void disconnectFromStargate()
	{
		if(this.stargate != null)
			setStargateConnection(this.stargate, false);
	}
	
	private void setStargateConnection(AbstractStargateEntity stargate, boolean hasDHD)
	{
		stargate.setDHD(hasDHD, this.enableAdvancedProtocols);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
    {
		if(level.isClientSide())
			return;

		AbstractStargateEntity nearbyStargate = dhd.getNearestStargate(dhd.getMaxDistance());
		
		if(dhd.stargate != null && nearbyStargate != null && dhd.stargate != nearbyStargate)
			dhd.setStargateConnection(dhd.stargate, false);
		
		dhd.stargate = nearbyStargate;
		
		if(dhd.stargate != null)
		{
			dhd.setStargateConnection(dhd.stargate, true);
			
			dhd.outputEnergy(null);
		}
    }
}
