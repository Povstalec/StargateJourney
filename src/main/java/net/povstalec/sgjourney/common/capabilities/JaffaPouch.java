package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.entities.goals.EvacuateHostGoal;
import net.povstalec.sgjourney.common.entities.goals.NearestThreatGoal;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.items.GoauldItem;

import javax.annotation.Nullable;

public class JaffaPouch
{
	public static final String HAS_POUCH = "has_pouch";
	
	public static final int HEAL_DURATION = 20 * 5;
	
	private boolean hasPouch = false;
	private Goauld.Info goauldInfo = null;
	
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
	
	public void saveData(CompoundTag tag)
	{
		if(this.hasPouch)
			tag.putBoolean(HAS_POUCH, true);
		
		if(this.goauldInfo != null)
			tag.put(Goauld.Info.GOAULD_INFO, this.goauldInfo.serializeNBT());
	}
	
	public void loadData(CompoundTag tag)
	{
		if(tag.contains(HAS_POUCH, Tag.TAG_BYTE))
			this.hasPouch = tag.getBoolean(HAS_POUCH);
		
		if(tag.contains(Goauld.Info.GOAULD_INFO, CompoundTag.TAG_COMPOUND))
		{
			this.goauldInfo = new Goauld.Info();
			this.goauldInfo.deserializeNBT(tag.getCompound(Goauld.Info.GOAULD_INFO));
		}
	}
}
