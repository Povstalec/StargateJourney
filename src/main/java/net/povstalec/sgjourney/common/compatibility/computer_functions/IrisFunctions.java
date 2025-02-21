package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class IrisFunctions
{
	public static String getIris(IrisStargateEntity stargate)
	{
		if(stargate.irisInfo().getIris().isEmpty())
			return null;
		
		return ForgeRegistries.ITEMS.getKey(stargate.irisInfo().getIris().getItem()).toString();
	}
	
	public static boolean closeIris(AbstractInterfaceEntity interfaceEntity)
	{
		return interfaceEntity.setIrisMotion(Stargate.IrisMotion.CLOSING_COMPUTER);
	}
	
	public static boolean openIris(AbstractInterfaceEntity interfaceEntity)
	{
		return interfaceEntity.setIrisMotion(Stargate.IrisMotion.OPENING_COMPUTER);
	}
	
	public static boolean stopIris(AbstractInterfaceEntity interfaceEntity)
	{
		return interfaceEntity.setIrisMotion(Stargate.IrisMotion.IDLE);
	}
	
	public static short getIrisProgress(IrisStargateEntity stargate)
	{
		return stargate.irisInfo().getIrisProgress();
	}
	
	public static float getIrisProgressPercentage(IrisStargateEntity stargate)
	{
		return stargate.irisInfo().checkIrisState();
	}
	
	public static int getIrisDurability(IrisStargateEntity stargate)
	{
		return stargate.irisInfo().getIrisDurability();
	}
	
	public static int getIrisMaxDurability(IrisStargateEntity stargate)
	{
		return stargate.irisInfo().getIrisMaxDurability();
	}
}
