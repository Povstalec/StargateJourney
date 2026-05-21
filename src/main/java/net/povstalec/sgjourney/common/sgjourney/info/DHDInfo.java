package net.povstalec.sgjourney.common.sgjourney.info;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class DHDInfo
{
	protected AbstractStargateEntity<?> stargate;
	
	@Nullable
	protected AbstractDHDEntity dhd;
	@Nullable
	protected Vec3i dhdRelativePos;
	protected int autoclose;
	
	public DHDInfo(AbstractStargateEntity<?> stargate)
	{
		this.stargate = stargate;
		
		this.dhd = null;
		this.dhdRelativePos = null;
		this.autoclose = 0;
	}
	
	public void setDHD(@NotNull AbstractDHDEntity dhd)
	{
		Direction direction = this.stargate.getDirection();
		
		if(direction != null)
		{
			this.dhdRelativePos = CoordinateHelper.Relative.getRelativeOffset(direction, this.stargate.getBlockPos(), dhd.getBlockPos());
			this.dhd = dhd;
			this.autoclose = dhd.autoclose();
			updateDHD();
		}
		
		this.stargate.updateStargate();
	}
	
	public void unsetDHD(boolean notifyDHD)
	{
		//if(notifyDHD && hasDHD())
		//	this.dhd.unsetStargate();
		
		this.dhd = null;
		this.dhdRelativePos = null;
		this.autoclose = 0;
		
		this.stargate.updateStargate();
		updateDHD();
		this.stargate.setChanged();
	}
	
	@Nullable
	public BlockPos getDHDPos()
	{
		if(this.dhdRelativePos == null)
			return null;
		
		return CoordinateHelper.Relative.getOffsetPos(this.stargate.getDirection(), this.stargate.getBlockPos(), this.dhdRelativePos);
	}
	
	public void loadDHD()
	{
		BlockPos dhdPos = getDHDPos();
		
		if(dhdPos == null)
			return;
		
		if(this.stargate.getLevel().getBlockEntity(dhdPos) instanceof AbstractDHDEntity dhd)
			setDHD(dhd);
		
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
			this.dhd.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), AbstractDHDEntity.DHD_INFO_DISTANCE);
	}
	
	public boolean shouldCallForward()
	{
		return hasDHD() && this.dhd.callForwardingEnabled();
	}
	
	public void revalidateDHD()
	{
		BlockPos dhdPos = getDHDPos();
		
		if(dhdPos == null)
		{
			if(this.dhd != null)
				unsetDHD(true);
			return;
		}
		
		if(this.stargate.getLevel().getBlockEntity(dhdPos) instanceof AbstractDHDEntity dhd) // Found a DHD at specified coords
		{
			if(this.dhd != dhd) // Found DHD is different from cached DHD
			{
				unsetDHD(true);
				setDHD(dhd);
			}
		}
		else if(this.dhd != null) // No DHD found at the specified coords
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
	} //TODO Why does this exist?
	
	public int autoclose()
	{
		return this.autoclose;
	}
	
	public boolean hasNetworkRestrictions()
	{
		if(this.dhd != null)
			return this.dhd.hasNetworkRestrictions();
		
		return false;
	}
	
	public Set<Integer> getNetworks()
	{
		if(this.dhd != null)
			return this.dhd.getNetworks();
		
		return Set.of();
	}
	
	
	
	public interface Interface
	{
		String DHD_POS = "DHDPos";
		
		DHDInfo dhdInfo();
	}
}
