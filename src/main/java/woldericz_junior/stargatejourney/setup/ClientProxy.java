package woldericz_junior.stargatejourney.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy implements IProxy 
{
	static 
	{
		
	}

	@Override
	public World getClientWorld() 
	{
		return Minecraft.getInstance().world;
	}

	@Override
	public void init() 
	{
		
	}

	@Override
	public PlayerEntity getClientPlayer() 
	{
		return Minecraft.getInstance().player;
	}

}
