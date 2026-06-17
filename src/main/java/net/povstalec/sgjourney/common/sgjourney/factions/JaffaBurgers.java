package net.povstalec.sgjourney.common.sgjourney.factions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.entities.FactionMember;
import net.povstalec.sgjourney.common.entities.Jaffa;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.stargate.SpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;
import java.util.*;

public class JaffaBurgers extends AbstractFaction
{
	public static final String JAFFA_BURGERS = "jaffa_burgers";
	
	public static final int UPDATE_INTERVAL = 400;
	
	public static final int ATTACKER_MIN_COUNT = 3;
	public static final int ATTACKER_MAX_COUNT = 7;
	public static final int ATTACKER_MIN_INTERVAL = 20;
	public static final int ATTACKER_MAX_INTERVAL = 20 * 3;
	
	@Nullable
	private static JaffaBurgers jaffaBurgers = null;
	
	protected Set<Address.Immutable> visitedAddresses = new HashSet<>();
	protected final SpawnerStargate spawnerStargate;
	
	@Nullable
	protected Address.Immutable visitTarget;
	protected int incursionTime = 0;
	
	public JaffaBurgers(MinecraftServer server)
	{
		this.spawnerStargate = StargateInit.MILKY_WAY_SPAWNER.get().constructStargate(server);
		this.spawnerStargate.loadStargate(Address.Immutable.randomAddress(8, 36, 0),
				ATTACKER_MIN_COUNT, ATTACKER_MAX_COUNT, ATTACKER_MIN_INTERVAL, ATTACKER_MAX_INTERVAL,
				randomSource -> EntityInit.JAFFA.get(), (entity, randomSource) ->
				{
					if(entity instanceof FactionMember factionMember)
						factionMember.setFaction(this);
					
					entity.setItemSlot(EquipmentSlot.MAINHAND, makeJaffaBurgerStack());
				});
	}
	
	public static ItemStack makeJaffaBurgerStack()
	{
		ItemStack stack = new ItemStack(ItemInit.GOAULD_BURGER.get());
		stack.setHoverName(Component.translatable("item.sgjourney.jaffa_burger"));
		return stack;
	}
	
	public void prepareNextVisit(MinecraftServer server, int intervalTicks)
	{
		Stargate stargate = StargateNetwork.get(server).getRandomStargate(server.overworld().getRandom());
		
		if(stargate == null || visitedAddresses.contains(stargate.get9ChevronAddress()))
			return;
		
		incursionTime = server.overworld().getRandom().nextInt(intervalTicks, UPDATE_INTERVAL);
		visitTarget = stargate.get9ChevronAddress();
	}
	
	public boolean startVisit(MinecraftServer server)
	{
		if(visitTarget == null)
			return false;
		
		if(spawnerStargate.isConnected())
			return false;
		
		spawnerStargate.encodeAddress(visitTarget);
		
		return !spawnerStargate.dial().isError();
	}
	
	public void finalizeVisit()
	{
		visitedAddresses.add(visitTarget); // Mark as visited
		visitTarget = null;
	}
	
	@Override
	public void tickFaction(MinecraftServer server, int ticks)
	{
		int intervalTicks = ticks % UPDATE_INTERVAL;
		
		if(intervalTicks == 0)
			prepareNextVisit(server, intervalTicks);
		else if(intervalTicks == incursionTime)
		{
			if(!startVisit(server))
				prepareNextVisit(server, intervalTicks);
			else
				finalizeVisit();
		}
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		int i = 0;
		for(Address.Immutable address : visitedAddresses)
		{
			address.saveToCompoundTag(tag, Integer.toString(i));
			i++;
		}
		
		return tag;
	}
	
	public void deserializeNBT(CompoundTag tag)
	{
		for(String key : tag.getAllKeys())
		{
			visitedAddresses.add(new Address.Immutable(tag.getIntArray(key)));
		}
	}
	
	public static void trySerialize(CompoundTag tag)
	{
		if(jaffaBurgers != null)
			tag.put(JAFFA_BURGERS, jaffaBurgers.serializeNBT());
	}
	
	public static void tryDeserialize(MinecraftServer server, CompoundTag tag)
	{
		if(StargateJourney.isAprilFools())
		{
			if(jaffaBurgers == null)
				jaffaBurgers = new JaffaBurgers(server);
			
			if(tag.contains(JAFFA_BURGERS, CompoundTag.TAG_COMPOUND))
				jaffaBurgers.deserializeNBT(tag.getCompound(JAFFA_BURGERS));
		}
		
	}
	
	public static void handleJaffaBurgers(MinecraftServer server, int ticks)
	{
		if(ticks % 24000 == 0)
		{
			boolean isAprilFools = StargateJourney.isAprilFools();
			if(isAprilFools && jaffaBurgers == null)
				jaffaBurgers = new JaffaBurgers(server);
			else if(!isAprilFools && jaffaBurgers != null)
				jaffaBurgers = null;
		}
		
		if(jaffaBurgers != null)
			jaffaBurgers.tickFaction(server, ticks);
	}
}
