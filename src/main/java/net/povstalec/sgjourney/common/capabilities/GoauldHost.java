package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.entities.goals.EvacuateHostGoal;
import net.povstalec.sgjourney.common.entities.goals.NearestThreatGoal;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.items.GoauldItem;

public class GoauldHost
{
	public static final String HOST_DATA = "host_data";
	public static final String CUSTOM_NAME = "CustomName"; //TODO This needs to change on 1.21.1
	
	private CompoundTag hostData = null;
	private Goauld.Info goauldInfo = null;
	
	public boolean isHost()
	{
		return goauldInfo != null;
	}
	
	//============================================================================================
	//***************************************Host takeover****************************************
	//============================================================================================
	
	public boolean takeOverHost(Goauld.Info goauldInfo, Mob host)
	{
		if(this.goauldInfo != null || goauldInfo == null || host == null)
			return false;
		
		saveHostData(host);
		addGoauldHostGoals(host);
		host.getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH)
				.ifPresent(cap ->cap.addNaquadahToBloodstream());
		
		if(goauldInfo.name() != null)
			host.setCustomName(goauldInfo.name());
		
		this.goauldInfo = goauldInfo;
		
		return true;
	}
	
	public boolean takeOverHost(Goauld goauld, Mob host)
	{
		if(goauld == null)
			return false;
		
		boolean res = takeOverHost(goauld.goauldInfo(), host);
		if(res)
			goauld.remove(Entity.RemovalReason.DISCARDED);
		
		return res;
	}
	
	public boolean takeOverHost(ItemStack stack, Mob host)
	{
		if(stack == null || !(stack.getItem() instanceof GoauldItem))
			return false;
		
		return takeOverHost(Goauld.Info.fromItemStack(stack), host);
	}
	
	public void leaveHost(Mob host)
	{
		if(host == null || this.goauldInfo == null)
			return;
		
		Goauld goauld = EntityInit.GOAULD.get().create(host.getLevel());
		goauld.moveTo(host.getX(), host.getY(), host.getZ(), host.getYRot(), 0.0F);
		goauld.setFromInfo(this.goauldInfo);
		
		host.getLevel().addFreshEntity(goauld);
		
		removeGoauldHostGoals(host);
		restoreHostData(host);
		goauldInfo = null;
	}
	
	//============================================================================================
	//***************************************Takeover data****************************************
	//============================================================================================
	
	public static void addGoauldHostGoals(Mob host)
	{
		//host.goalSelector.addGoal(0, new EvacuateHostGoal(host)); //TODO Goa'uld should only evacuate a host rarely, maybe have an arrogance meter that decides this?
		
		host.targetSelector.addGoal(1, new NearestThreatGoal<>(host, Player.class, true));
		host.targetSelector.addGoal(2, new NearestThreatGoal<>(host, Human.class, true));
	}
	
	public static void removeGoauldHostGoals(Mob host)
	{
		host.goalSelector.removeAllGoals(goal -> goal instanceof EvacuateHostGoal || goal instanceof NearestThreatGoal);
		
		host.setLastHurtByPlayer(null);
		host.setLastHurtByMob(null);
		host.setTarget(null);
		host.setAggressive(false);
	}
	
	public void saveHostData(Mob host)
	{
		hostData = new CompoundTag();
		
		if(host.getCustomName() != null)
			hostData.putString(CUSTOM_NAME, Component.Serializer.toJson(host.getCustomName()));
	}
	
	public void restoreHostData(Mob host)
	{
		if(hostData != null)
		{
			if(hostData.contains(CUSTOM_NAME, CompoundTag.OBJECT_HEADER))
				host.setCustomName(Component.Serializer.fromJson(hostData.getString(CUSTOM_NAME)));
			else
				host.setCustomName(null);
		}
		
		hostData = null;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public void copyFrom(GoauldHost source)
	{
		if(this.goauldInfo != null)
			this.goauldInfo = source.goauldInfo.copy();
	}
	
	public void saveData(CompoundTag tag)
	{
		if(this.goauldInfo != null)
			tag.put(Goauld.Info.GOAULD_INFO, this.goauldInfo.serializeNBT());
		
		if(this.hostData != null)
			tag.put(HOST_DATA, this.hostData);
	}
	
	public void loadData(CompoundTag tag)
	{
		if(tag.contains(Goauld.Info.GOAULD_INFO, CompoundTag.TAG_COMPOUND))
		{
			this.goauldInfo = new Goauld.Info();
			this.goauldInfo.deserializeNBT(tag.getCompound(Goauld.Info.GOAULD_INFO));
		}
		
		if(tag.contains(HOST_DATA, CompoundTag.TAG_COMPOUND))
			this.hostData = tag.getCompound(HOST_DATA);
	}
}
