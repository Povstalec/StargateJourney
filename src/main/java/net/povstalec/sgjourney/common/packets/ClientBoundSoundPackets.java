package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.sound.SoundAccess;

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
    
    public static class CloseWormhole extends ClientBoundSoundPackets
    {
    	public CloseWormhole(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public CloseWormhole(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playWormholeCloseSound(pos);
            });
            return true;
        }
    }

    public static class Chevron
    {
	    public final BlockPos pos;
    	public final boolean primary;
    	public final boolean incoming;
    	public final boolean raise;
    	public final boolean encode;
    	
    	public Chevron(BlockPos pos, boolean primary, boolean incoming, boolean raise, boolean encode)
    	{
    		this.pos = pos;
    		this.primary = primary;
    		this.incoming = incoming;
    		this.raise = raise;
    		this.encode = encode;
    	}
    	public Chevron(FriendlyByteBuf buffer)
    	{
    		 this(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    	}

        public void encode(FriendlyByteBuf buffer)
        {
            buffer.writeBlockPos(this.pos);
            buffer.writeBoolean(this.primary);
            buffer.writeBoolean(this.incoming);
            buffer.writeBoolean(this.raise);
            buffer.writeBoolean(this.encode);
        }
    	
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playChevronSound(pos, primary, incoming, raise, encode);
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
            	SoundAccess.playRotationSound(pos, stop);
            });
            return true;
        }
    }
    
    public static class UniverseStart extends ClientBoundSoundPackets
    {
    	public UniverseStart(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public UniverseStart(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playUniverseStartSound(pos);
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
    
    public static class MilkyWayStop extends ClientBoundSoundPackets
    {
    	public MilkyWayStop(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public MilkyWayStop(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playMilkyWayStopSound(pos);
            });
            return true;
        }
    }
}


