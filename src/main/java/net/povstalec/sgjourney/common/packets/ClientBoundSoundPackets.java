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
    
    
    
    public abstract static class WormholeSound extends ClientBoundSoundPackets
    {
    	public final boolean incoming;
    	
    	public WormholeSound(BlockPos pos, boolean stop, boolean incoming)
    	{
    		super(pos, false);
    		
    		this.incoming = incoming;
    	}
    	public WormholeSound(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    		this.incoming = buffer.readBoolean();
    	}
    	
    	@Override
        public void encode(FriendlyByteBuf buffer)
        {
            super.encode(buffer);
            buffer.writeBoolean(this.incoming);
        }
    }
    
    public static class OpenWormhole extends WormholeSound
    {
    	public OpenWormhole(BlockPos pos, boolean incoming)
    	{
    		super(pos, false, incoming);
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
            	SoundAccess.playWormholeOpenSound(pos, incoming);
            });
            return true;
        }
    }
    
    public static class IdleWormhole extends WormholeSound
    {
    	public IdleWormhole(BlockPos pos, boolean incoming)
    	{
    		super(pos, false, incoming);
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
            	SoundAccess.playWormholeIdleSound(pos, incoming);
            });
            return true;
        }
    }
    
    public static class CloseWormhole extends WormholeSound
    {
    	public CloseWormhole(BlockPos pos, boolean incoming)
    	{
    		super(pos, false, incoming);
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
            	SoundAccess.playWormholeCloseSound(pos, incoming);
            });
            return true;
        }
    }
    
    public static class IrisThud extends ClientBoundSoundPackets
    {
    	public IrisThud(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public IrisThud(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
        public void encode(FriendlyByteBuf buffer)
        {
            super.encode(buffer);
        }
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playIrisThudSound(pos);
            });
            return true;
        }
    }

    public static class Chevron
    {
	    public final BlockPos pos;
    	public final short chevron;
    	public final boolean incoming;
    	public final boolean open;
    	public final boolean encode;
    	
    	public Chevron(BlockPos pos, short chevron, boolean incoming, boolean open, boolean encode)
    	{
    		this.pos = pos;
    		this.chevron = chevron;
    		this.incoming = incoming;
    		this.open = open;
    		this.encode = encode;
    	}
    	public Chevron(FriendlyByteBuf buffer)
    	{
    		 this(buffer.readBlockPos(), buffer.readShort(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    	}

        public void encode(FriendlyByteBuf buffer)
        {
            buffer.writeBlockPos(this.pos);
            buffer.writeShort(this.chevron);
            buffer.writeBoolean(this.incoming);
            buffer.writeBoolean(this.open);
            buffer.writeBoolean(this.encode);
        }
    	
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playChevronSound(pos, chevron, incoming, open, encode);
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
            	SoundAccess.playUniverseDialStartSound(pos);
            });
            return true;
        }
    }
    
    public static class RotationStartup extends ClientBoundSoundPackets
    {
    	public RotationStartup(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public RotationStartup(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playRotationStartupSound(pos);
            });
            return true;
        }
    }
    
    public static class RotationStop extends ClientBoundSoundPackets
    {
    	public RotationStop(BlockPos pos)
    	{
    		super(pos, false);
    	}
    	public RotationStop(FriendlyByteBuf buffer)
    	{
    		super(buffer);
    	}
    	
    	@Override
    	public boolean handle(Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
            	SoundAccess.playRotationStopSound(pos);
            });
            return true;
        }
    }
}


