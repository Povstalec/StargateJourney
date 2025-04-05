package net.povstalec.sgjourney.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundGDOOpenScreenPacket;
import net.povstalec.sgjourney.common.sgjourney.ITransmissionReceiver;

public class GDOItem extends Item
{
	public static final String IDC = "idc";
	public static final String FREQUENCY = "frequency";
	
	public GDOItem(Properties properties)
	{
		super(properties);
	}
	
	public static void sendTransmission(Level level, Player player, ItemStack stack)
	{
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
						receiver.receiveTransmission(0, getFrequency(stack), getTransmissionMessage(stack));
				});
			}
		}
	}
	
	private static double distance2(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		return x*x + y*y + z*z;
	}
	
	private static void checkShieldingState(Level level, Player player)
	{
		int roundedRadius = (int) Math.ceil(transmissionRadius() / 16);
		BlockPos playerPos = player.getOnPos().above();
		
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -roundedRadius; x <= roundedRadius; x++)
		{
			for(int z = -roundedRadius; z <= roundedRadius; z++)
			{
				ChunkAccess chunk = level.getChunk(player.getOnPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate && distance2(playerPos, stargate.getBlockPos()) <= transmissionRadius2())
					{
						stargates.add(stargate);
					}
				});
			}
		}
		
		if(stargates.size() == 0)
		{
			player.displayClientMessage(Component.translatable("message.sgjourney.gdo.error.no_nearby_stargate").withStyle(ChatFormatting.RED), true);
			return;
		}
		
		stargates.sort((stargateA, stargateB) ->
		Double.valueOf(distance2(playerPos, stargateA.getBlockPos()))
		.compareTo(Double.valueOf(distance2(playerPos, stargateB.getBlockPos()))));
		
		AbstractStargateEntity stargate = stargates.get(0);
		
		if(!stargate.isConnected())
		{
			player.displayClientMessage(Component.translatable("message.sgjourney.gdo.error.stargate_not_connected").withStyle(ChatFormatting.RED), true);
			return;
		}
		
		int shieldingProgress = (int) Math.round(stargate.checkConnectionShieldingState());
		
		ChatFormatting formatting;
		
		if(shieldingProgress == 0)
			formatting = ChatFormatting.DARK_GREEN;
		else if(shieldingProgress < 10)
			formatting = ChatFormatting.GREEN;
		else if(shieldingProgress < 50)
			formatting = ChatFormatting.YELLOW;
		else if(shieldingProgress < 70)
			formatting = ChatFormatting.GOLD;
		else if(shieldingProgress < 90)
			formatting = ChatFormatting.RED;
		else
			formatting = ChatFormatting.DARK_RED;
		
		player.displayClientMessage(Component.translatable("message.sgjourney.gdo.shielded").append(Component.literal(": " + shieldingProgress + "%")).withStyle(formatting), true);
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);

		ItemStack stack = player.getItemInHand(usedHand);
		
		// Open the GDO screen / Send IDC transmission
		if(player.isShiftKeyDown())
		{
			PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundGDOOpenScreenPacket(player.getUUID(), usedHand == InteractionHand.MAIN_HAND, getTransmissionMessage(stack), getFrequency(stack)));
			
	        return super.use(level, player, usedHand);
		}
		// Check Iris / Shield state
		else
		{
			checkShieldingState(level, player);
			
			return InteractionResultHolder.success(stack);
		}
    }
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		int frequency = 0;
		String idc = "";
		
		if(stack.hasTag())
		{
			frequency = getFrequency(stack);
			idc = getTransmissionMessage(stack);
		}

		tooltipComponents.add(Component.translatable("tooltip.sgjourney.gdo.frequency").append(Component.literal(": " + frequency)).withStyle(ChatFormatting.YELLOW));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.gdo.idc").append(Component.literal(": " + idc)).withStyle(ChatFormatting.AQUA));
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.gdo.description.check").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.gdo.description.gui").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public static float transmissionRadius()
	{
		return CommonTransmissionConfig.max_gdo_transmission_distance.get();
	}
	
	public static float transmissionRadius2()
	{
		return transmissionRadius() * transmissionRadius();
	}
	

	
	public static String getTransmissionMessage(ItemStack stack)
	{
		if(stack.getItem() instanceof GDOItem)
		{
			if(stack.getTag() != null && stack.getTag().contains(IDC))
				return stack.getTag().getString(IDC);
		}
		
		return "";
	}
	
	public static int getFrequency(ItemStack stack)
	{
		if(stack.getItem() instanceof GDOItem)
		{
			if(stack.getTag() != null && stack.getTag().contains(FREQUENCY))
				return stack.getTag().getInt(FREQUENCY);
		}
		
		return 0;
	}
	
	public static void setFrequencyAndIDC(ItemStack stack, int frequency, String idc)
	{
		if(stack.getItem() instanceof GDOItem)
		{
			CompoundTag tag = stack.getOrCreateTag();
			tag.putInt(FREQUENCY, frequency);
			tag.putString(IDC, idc);
		}
	}
}
