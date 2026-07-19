package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

import javax.annotation.Nullable;

public class JaffaPouch
{
	public static final String JAFFA_POUCH = "jaffa_pouch";
	public static final String HAS_POUCH = "has_pouch";
	
	public static final EntityCapability<JaffaPouch, Void> JAFFA_POUCH_CAPABILITY = EntityCapability.createVoid(
			StargateJourney.sgjourneyLocation(JAFFA_POUCH), JaffaPouch.class);
	
	public static final int HEAL_DURATION = 20 * 5;
	
	private final LivingEntity entity;
	
	private boolean hasPouch = false;
	private Goauld.Info goauldInfo = null;
	
	public JaffaPouch(LivingEntity entity)
	{
		this.entity = entity;
		//TODO
		CompoundTag jaffaPouch = this.entity.getData(AttachmentTypeInit.JAFFA_POUCH);
		
		this.hasPouch = jaffaPouch.getBoolean(HAS_POUCH);
		
		if(jaffaPouch.contains(Goauld.Info.GOAULD_INFO, CompoundTag.TAG_COMPOUND))
		{
			this.goauldInfo = new Goauld.Info();
			this.goauldInfo.deserializeNBT(entity.getServer().registryAccess(), jaffaPouch.getCompound(Goauld.Info.GOAULD_INFO));
		}
	}
	
	public boolean hasPouch()
	{
		return hasPouch;
	}
	
	public boolean hasGoauld()
	{
		return goauldInfo != null;
	}
	
	public void setPouch(boolean hasPouch)
	{
		this.hasPouch = hasPouch;
	}
	
	public void setGoauldInfo(@Nullable Goauld.Info goauldInfo)
	{
		this.goauldInfo = goauldInfo;
	}
	
	//============================================================================================
	//***************************************Goa'uld stuff****************************************
	//============================================================================================
	
	public void tick(LivingEntity jaffa)
	{
		if(!hasGoauld())
			return;
		
		//TODO Add a cooldown for these
		/*if(jaffa.hasEffect(MobEffects.POISON))
			jaffa.removeEffect(MobEffects.POISON);
		if(jaffa.hasEffect(MobEffects.WITHER))
			jaffa.removeEffect(MobEffects.WITHER);
		
		if(jaffa.getHealth() < jaffa.getMaxHealth() / 2F)
			jaffa.addEffect(new MobEffectInstance(MobEffects.REGENERATION, HEAL_DURATION, 1));*/
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public void copyFrom(JaffaPouch source)
	{
		this.hasPouch = source.hasPouch;
		
		if(source.goauldInfo != null)
			this.goauldInfo = source.goauldInfo.copy();
	}
	
	public void update(MinecraftServer server)
	{
		CompoundTag jaffaPouch = new CompoundTag();
		
		jaffaPouch.putBoolean(HAS_POUCH, this.hasPouch);
		
		if(this.goauldInfo != null)
			jaffaPouch.put(Goauld.Info.GOAULD_INFO, this.goauldInfo.serializeNBT(server.registryAccess()));
		
		this.entity.setData(AttachmentTypeInit.JAFFA_POUCH, jaffaPouch);
	}
}
