package net.povstalec.sgjourney.common.sgjourney.info;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;

public class DHDInfo
{
	public static final int DHD_INFO_DISTANCE = 5;
	
	protected AbstractStargateEntity stargate;
	
	@Nullable
	protected AbstractDHDEntity dhd;
	@Nullable
	protected Vec3i dhdRelativePos;
	protected int autoclose;
	
	public DHDInfo(AbstractStargateEntity stargate)
	{
		this.stargate = stargate;
		
		this.dhd = null;
		this.dhdRelativePos = null;
		this.autoclose = 0;
	}
	
	public void setDHD(AbstractDHDEntity dhd, int autoclose)
	{
		Direction direction = this.stargate.getDirection();
		
		if(dhd != null && direction != null)
		{
			if(!hasDHD() || this.dhd == dhd)
			{
				Vec3i relativeOffset = CoordinateHelper.Relative.getRelativeOffset(direction, this.stargate.getBlockPos(), dhd.getBlockPos());
				
				this.dhdRelativePos = relativeOffset;
				this.dhd = dhd;
				updateDHD();
			}
			
			this.autoclose = autoclose;
		}
		
		this.stargate.updateStargate(this.stargate.getLevel(), false);
		this.stargate.setChanged();
	}
	
	public void unsetDHD(boolean notifyDHD)
	{
		if(notifyDHD && hasDHD())
			this.dhd.unsetStargate();
		
		this.dhd = null;
		this.dhdRelativePos = null;
		this.autoclose = 0;
		
		this.stargate.updateStargate(this.stargate.getLevel(), false);
		updateDHD();
		
		this.stargate.setChanged();
	}
	
	@Nullable
	public BlockPos getDHDPos()
	{
		if(this.dhdRelativePos == null)
			return null;
		
		BlockPos dhdPos = CoordinateHelper.Relative.getOffsetPos(this.stargate.getDirection(), this.stargate.getBlockPos(), this.dhdRelativePos);
		
		if(dhdPos != null)
			return dhdPos;
		
		return null;
	}
	
	public void loadDHD()
	{
		BlockPos dhdPos = getDHDPos();
		
		if(dhdPos == null)
			return;
		
		if(this.stargate.getLevel().getBlockEntity(dhdPos) instanceof AbstractDHDEntity dhd)
			this.dhd = dhd;
		
		updateDHD();
		
		this.stargate.setChanged();
	}
	
	public void updateDHD()
	{
		if(hasDHD())
			this.dhd.updateDHD(!this.stargate.isConnected() || (this.stargate.isConnected() && this.stargate.isDialingOut()) ?
					this.stargate.getAddress() : new Address.Mutable(), this.stargate.isConnected());
	}
	
	public void sendDHDFeedback(StargateInfo.Feedback feedback)
	{
		if(hasDHD() && feedback.isError())
			this.dhd.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), DHD_INFO_DISTANCE);
	}
	
	public boolean shouldCallForward()
	{
		return hasDHD() && this.dhd.callForwardingEnabled();
	}
	
	public void revalidateDHD()
	{
		BlockPos dhdPos = getDHDPos();
		
		if(dhdPos == null)
			return;
		
		if(this.stargate.getLevel().getBlockEntity(dhdPos) instanceof AbstractDHDEntity dhd)
		{
			if(this.dhd != dhd)
			{
				if(this.dhd != null)
					unsetDHD(true);
				
				this.dhd = dhd;
			}
		}
		else if(this.dhd != null)
			unsetDHD(true);
			
	}
	
	public void onDialAttempt(StargateInfo.Feedback feedback, Address address)
	{
		if(this.dhd == null)
			return;
		
		this.dhd.onDialAttempt(feedback, address);
	}
	
	public boolean hasDHD()
	{
		return this.dhd != null;
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public void setRelativePos(Vec3i dhdRelativePos)
	{
		this.dhdRelativePos = dhdRelativePos;
	}
	
	@Nullable
	public Vec3i relativePos()
	{
		return this.dhdRelativePos;
	}
	
	public void setAutoclose(int autoclose)
	{
		this.autoclose = autoclose;
	}
	
	public int autoclose()
	{
		return this.autoclose;
	}
	
	
	
	public interface Interface
	{
		String DHD_POS = "DHDPos";
		
		DHDInfo dhdInfo();
	}
}
