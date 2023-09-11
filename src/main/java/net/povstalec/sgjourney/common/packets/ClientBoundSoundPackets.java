package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.sound.SoundAccess;

public abstract class ClientBoundSoundPackets
{
    public final BlockPos pos;
    public final boolean bool;

    public ClientBoundSoundPackets(BlockPos pos, boolean stop)
    {
        this.pos = pos;
        this.bool = stop;
    }

    public ClientBoundSoundPackets(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeBoolean(this.bool);
    }

    public abstract boolean handle(Supplier<NetworkEvent.Context> ctx);
    
    
    
    public static class OpenWormhole extends ClientBoundSoundPackets
    {
    	public OpenWormhole(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public OpenWormhole(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playWormholeOpenSound(pos);
            });
            return true;
        }
    }
    
    public static class IdleWormhole extends ClientBoundSoundPackets
    {
    	public IdleWormhole(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public IdleWormhole(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playWormholeIdleSound(pos);
            });
            return true;
        }
    }

    public static class Chevron extends ClientBoundSoundPackets
    {
    	public Chevron(BlockPos pos, boolean raise)
    	{
    		super(pos, raise);
    	}
    	public Chevron(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playChevronSound(pos, bool);
            });
            return true;
        }
    }

    public static class Fail extends ClientBoundSoundPackets
    {
    	public Fail(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public Fail(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playFailSound(pos);
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
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playRotationSound(pos, bool);
            });
            return true;
        }
    }
    
    public static class MilkyWayBuildup extends ClientBoundSoundPackets
    {
    	public MilkyWayBuildup(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public MilkyWayBuildup(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playMilkyWayBuildupSound(pos);
            });
            return true;
        }
    }
}


