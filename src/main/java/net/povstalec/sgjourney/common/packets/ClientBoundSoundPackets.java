package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.SoundAccess;

public abstract class ClientBoundSoundPackets
{
    public final BlockPos pos;
    public final boolean stop;

    public ClientBoundSoundPackets(BlockPos pos, boolean stop)
    {
        this.pos = pos;
        this.stop = stop;
    }

    public ClientBoundSoundPackets(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeBoolean(this.stop);
    }

    public abstract boolean handle(Supplier<NetworkEvent.Context> ctx);
    
    
    public static class IdleWormhole extends ClientBoundSoundPackets
    {
    	public IdleWormhole(BlockPos pos, boolean stop)
    	{
    		super(pos, stop);
    	}
    	public IdleWormhole(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
            	SoundAccess.playWormholeIdleSound(pos);
            });
            return true;
        }
    }
    
    public static class StargateRotation extends ClientBoundSoundPackets
    {
    	public StargateRotation(BlockPos pos, boolean stop)
    	{
    		super(pos, stop);
    	}
    	public StargateRotation(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
            	SoundAccess.playRotationSound(pos, stop);
            });
            return true;
        }
    }
    
    public static class MilkyWayBuildup extends ClientBoundSoundPackets
    {
    	public MilkyWayBuildup(BlockPos pos, boolean stop)
    	{
    		super(pos, stop);
    	}
    	public MilkyWayBuildup(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
            	SoundAccess.playMilkyWayBuildupSound(pos);
            });
            return true;
        }
    }
}


