package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.entities.goals.EvacuateHostGoal;
import net.povstalec.sgjourney.common.entities.goals.NearestThreatGoal;
import net.povstalec.sgjourney.common.init.AttachmentTypeInit;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.items.GoauldItem;

public class GoauldHost
{
	public static final String GOAULD_HOST = "goauld_host";
	
	public static final EntityCapability<GoauldHost, Void> GOAULD_HOST_CAPABILITY = EntityCapability.createVoid(
			StargateJourney.sgjourneyLocation(GOAULD_HOST), GoauldHost.class);
	
	public static final String HOST_DATA = "host_data";
	public static final String CUSTOM_NAME = "CustomName"; //TODO This needs to change on 1.21.1
	
	private LivingEntity entity;
	private CompoundTag hostData = null;
	private Goauld.Info goauldInfo = null;
	
	public GoauldHost(LivingEntity entity)
	{
		this.entity = entity;
		CompoundTag goauldHost = this.entity.getData(AttachmentTypeInit.GOAULD_HOST);
		
		if(goauldHost.contains(Goauld.Info.GOAULD_INFO, CompoundTag.TAG_COMPOUND))
		{
			this.goauldInfo = new Goauld.Info();
			this.goauldInfo.deserializeNBT(entity.getServer().registryAccess(), goauldHost.getCompound(Goauld.Info.GOAULD_INFO));
		}
		if(goauldHost.contains(HOST_DATA))
			this.hostData = goauldHost.getCompound(HOST_DATA);
	}
	
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
		BloodstreamNaquadah cap = host.getCapability(BloodstreamNaquadah.BLOODSTREAM_NAQUADAH_CAPABILITY);
		if(cap != null)
			cap.addNaquadahToBloodstream();
		
		if(goauldInfo.name() != null)
			host.setCustomName(goauldInfo.name());
		
		this.goauldInfo = goauldInfo;
		
		update(host.getServer());
		return true;
	}
	
	public boolean takeOverHost(Goauld goauld, Mob host)
	{
		if(goauld == null)
			return false;
		
		boolean res = takeOverHost(goauld.goauldInfo(), host);
		if(res)
			goauld.remove(Entity.RemovalReason.DISCARDED);
		
		update(goauld.getServer());
		return res;
	}
	
	public boolean takeOverHost(ItemStack stack, Mob host)
	{
		if(stack == null || !(stack.getItem() instanceof GoauldItem))
			return false;
		
		return takeOverHost(Goauld.Info.fromItemStack(host.getServer(), stack), host);
	}
	
	public void leaveHost(Mob host)
	{
		if(host == null || this.goauldInfo == null)
			return;
		
		Goauld goauld = EntityInit.GOAULD.get().create(host.level());
		goauld.moveTo(host.getX(), host.getY(), host.getZ(), host.getYRot(), 0.0F);
		goauld.setFromInfo(this.goauldInfo);
		
		host.level().addFreshEntity(goauld);
		
		removeGoauldHostGoals(host);
		restoreHostData(host);
		goauldInfo = null;
		
		update(host.getServer());
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
			hostData.putString(CUSTOM_NAME, Component.Serializer.toJson(host.getCustomName(), host.getServer().registryAccess()));
	}
	
	public void restoreHostData(Mob host)
	{
		if(hostData != null)
		{
			if(hostData.contains(CUSTOM_NAME, CompoundTag.OBJECT_HEADER))
				host.setCustomName(Component.Serializer.fromJson(hostData.getString(CUSTOM_NAME), host.getServer().registryAccess()));
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
	
	public void update(MinecraftServer server)
	{
		CompoundTag goauldHost = new CompoundTag();
		
		if(this.goauldInfo != null)
			goauldHost.put(Goauld.Info.GOAULD_INFO, this.goauldInfo.serializeNBT(server.registryAccess()));
		
		if(this.hostData != null)
			goauldHost.put(HOST_DATA, this.hostData);
		
		this.entity.setData(AttachmentTypeInit.GOAULD_HOST, goauldHost);
	}
}
