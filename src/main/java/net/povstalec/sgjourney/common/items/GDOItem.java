package net.povstalec.sgjourney.common.items;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundGDOOpenScreenPacket;
import net.povstalec.sgjourney.common.stargate.ITransmissionReceiver;

public class GDOItem extends Item
{
	public GDOItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);
		
		// Open the GDO screen
		if(player.isShiftKeyDown())
		{
			PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundGDOOpenScreenPacket(player.getUUID()));
			
	        return super.use(level, player, usedHand);
		}
		// Send IDC transmission
		else
		{
			ItemStack stack = player.getItemInHand(usedHand);
			
			int roundedRadius = (int) Math.ceil(transmissionRadius() / 16);
			
			for(int x = -roundedRadius; x <= roundedRadius; x++)
			{
				for(int z = -roundedRadius; z <= roundedRadius; z++)
				{
					ChunkAccess chunk = level.getChunk(player.getOnPos().east(16 * x).south(16 * z));
					Set<BlockPos> positions = chunk.getBlockEntitiesPos();
					
					positions.stream().forEach(pos ->
					{
						BlockEntity blockEntity = level.getBlockEntity(pos);
						
						if(blockEntity instanceof ITransmissionReceiver receiver)
							receiver.receiveTransmission(0, 0, getTransmissionMessage(stack));
					});
				}
			}
			
	        return InteractionResultHolder.success(stack);
		}
    }
	
	public static float transmissionRadius()
	{
		return CommonTransmissionConfig.max_gdo_transmission_distance.get();
	}
	

	
	public static String getTransmissionMessage(ItemStack stack)
	{
		
		
		return "1234";
	}
}
