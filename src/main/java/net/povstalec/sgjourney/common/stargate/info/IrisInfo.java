package net.povstalec.sgjourney.common.stargate.info;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.StargateIrisItem;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;

import javax.annotation.Nonnull;
import java.util.Optional;

public class IrisInfo
{
	protected AbstractStargateEntity stargate;
	
	protected short irisProgress;
	protected short oldIrisProgress;
	protected final ItemStackHandler irisItemHandler;
	
	public IrisInfo(AbstractStargateEntity stargate)
	{
		this.stargate = stargate;
		
		this.irisProgress = 0;
		this.oldIrisProgress = 0;
		this.irisItemHandler = createIrisHandler();
	}
	
	public void decreaseIrisDurability()
	{
		if(irisItemHandler.getStackInSlot(0).isEmpty())
			return;
		
		if(this.stargate.getBlockState().getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			boolean shouldDestroyIris = !StargateIrisItem.decreaseDurability(irisItemHandler.getStackInSlot(0));
			
			if(shouldDestroyIris)
				AbstractShieldingBlock.destroyShielding(this.stargate.getLevel(), this.stargate.getBlockPos(), stargateBlock.getShieldingParts(), this.stargate.getDirection(), this.stargate.getOrientation());
		}
	}
	
	public boolean isIrisClosed()
	{
		return hasIris() && this.irisProgress == ShieldingState.MAX_PROGRESS;
	}
	
	public int getIrisDurability()
	{
		return hasIris() ? StargateIrisItem.getDurability(getIris()) : 0;
	}
	
	public int getIrisMaxDurability()
	{
		if(getIris().getItem() instanceof StargateIrisItem iris)
			return iris.getMaxDurability();
		
		return 0;
	}
	
	public ShieldingState getShieldingState()
	{
		return ShieldingState.fromProgress(irisProgress);
	}
	
	protected void setIrisState()
	{
		if(irisProgress == ShieldingState.CLOSED.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_4);
			else if(oldIrisProgress < irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.CLOSED);
			return;
		}
		
		if(irisProgress == ShieldingState.MOVING_4.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_3);
			else if(oldIrisProgress < irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_4);
			return;
		}
		
		if(irisProgress == ShieldingState.MOVING_3.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_2);
			else if(oldIrisProgress < irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_3);
			return;
		}
		
		if(irisProgress == ShieldingState.MOVING_2.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_1);
			else if(oldIrisProgress < irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_2);
			return;
		}
		
		if(irisProgress == ShieldingState.MOVING_1.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.OPEN);
			else if(oldIrisProgress < irisProgress)
				this.stargate.setStargateState(true, true, ShieldingState.MOVING_1);
			return;
		}
		
		if(irisProgress == 0 && oldIrisProgress > irisProgress)
			this.stargate.setStargateState(true, true, ShieldingState.OPEN);
	}
	
	public boolean addIris(ItemStack stack)
	{
		if(setIris(stack))
		{
			irisProgress = ShieldingState.MAX_PROGRESS;
			oldIrisProgress = ShieldingState.MAX_PROGRESS;
			
			this.stargate.setStargateState(true, true, ShieldingState.CLOSED);
			
			return true;
		}
		else
			return false;
	}
	
	public void removeIris()
	{
		if(unsetIris())
		{
			ShieldingState shieldingState = ShieldingState.OPEN;
			
			irisProgress = shieldingState.getProgress();
			oldIrisProgress = shieldingState.getProgress();
			
			this.stargate.setStargateState(true, true, ShieldingState.OPEN);
		}
	}
	
	public short increaseIrisProgress()
	{
		oldIrisProgress = irisProgress;
		
		if(hasIris() && irisProgress < ShieldingState.MAX_PROGRESS)
		{
			irisProgress++;
			
			setIrisState();
		}
		
		return irisProgress;
	}
	
	public short decreaseIrisProgress()
	{
		oldIrisProgress = irisProgress;
		
		if(hasIris() && irisProgress > 0)
		{
			irisProgress--;
			
			setIrisState();
		}
		
		return irisProgress;
	}
	
	public float checkIrisState()
	{
		return irisProgress * 100F / ShieldingState.MAX_PROGRESS;
	}
	
	protected ItemStackHandler createIrisHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				stargate.setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return stack.getItem() instanceof StargateIrisItem;
			}
			
			// Limits the number of items per slot
			public int getSlotLimit(int slot)
			{
				return 1;
			}
			
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
			{
				if(!isItemValid(slot, stack))
					return stack;
				
				return super.insertItem(slot, stack, simulate);
				
			}
		};
	}
	
	public void playIrisThudSound()
	{
		if(!this.stargate.getLevel().isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.stargate.getLevel().getChunkAt(this.stargate.getBlockPos())), new ClientBoundSoundPackets.IrisThud(this.stargate.getBlockPos()));
	}
	
	public boolean hasIris()
	{
		return irisItemHandler.getStackInSlot(0).getItem() instanceof StargateIrisItem;
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public void setIrisProgress(short oldIrisProgress, short irisProgress)
	{
		this.oldIrisProgress = oldIrisProgress;
		this.irisProgress = irisProgress;
	}
	
	public void setIrisProgress(short irisProgress)
	{
		setIrisProgress(this.irisProgress, irisProgress);
	}
	
	public short getIrisProgress()
	{
		return hasIris() ? this.irisProgress : 0;
	}
	
	public float getIrisProgress(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getIrisProgress() : Mth.lerp(partialTick, this.oldIrisProgress, this.irisProgress);
	}
	
	public Optional<ResourceLocation> getIrisTexture()
	{
		if(!hasIris())
			return Optional.empty();
		
		return Optional.ofNullable(StargateIrisItem.getIrisTexture(irisItemHandler.getStackInSlot(0)));
	}
	
	public boolean setIris(ItemStack stack)
	{
		if(irisItemHandler.getStackInSlot(0).isEmpty())
		{
			irisItemHandler.setStackInSlot(0, stack.copy());
			return true;
		}
		
		return false;
	}
	
	@Nonnull
	public ItemStack getIris()
	{
		return irisItemHandler.getStackInSlot(0).copy();
	}
	
	/**
	 * Removes iris from the Stargate
	 * @return true if the Iris has been removed, false if there was no Iris to remove
	 */
	public boolean unsetIris()
	{
		if(!irisItemHandler.getStackInSlot(0).isEmpty())
		{
			irisItemHandler.setStackInSlot(0, ItemStack.EMPTY);
			return true;
		}
		
		return false;
	}
	
	public CompoundTag serializeIrisInventory()
	{
		return irisItemHandler.serializeNBT();
	}
	
	public void deserializeIrisInventory(CompoundTag tag)
	{
		irisItemHandler.deserializeNBT(tag);
	}
	
	
	
	public interface Interface
	{
		String IRIS_PROGRESS = "IrisProgress";
		String IRIS_INVENTORY = "IrisInventory";
		
		IrisInfo irisInfo();
	}
}
