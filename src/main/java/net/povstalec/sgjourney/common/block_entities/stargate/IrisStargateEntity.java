package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.StargateIrisItem;
import net.povstalec.sgjourney.common.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.info.IrisInfo;

public abstract class IrisStargateEntity extends AbstractStargateEntity implements IrisInfo.Interface
{
	protected IrisInfo irisInfo;
	
	public IrisStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
							  int totalSymbols, StargateInfo.Gen gen, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, verticalCenterHeight, horizontalCenterHeight);
		
		this.irisInfo = new IrisInfo(this);
	}
	
	public IrisStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
							  int totalSymbols, StargateInfo.Gen gen, int defaultNetwork)
	{
		this(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
	}
	
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		super.deserializeStargateInfo(tag, isUpgraded);
		
		short irisProgress = tag.getShort(IRIS_PROGRESS);
		irisInfo().setIrisProgress(irisProgress, irisProgress);
		irisInfo().deserializeIrisInventory(tag.getCompound(IRIS_INVENTORY));
	}
	
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		tag.putShort(IRIS_PROGRESS, irisInfo().getIrisProgress());
		tag.put(IRIS_INVENTORY, irisInfo().serializeIrisInventory());
		
		return tag;
	}
	
	//============================================================================================
	//********************************************Info********************************************
	//============================================================================================
	
	@Override
	public IrisInfo irisInfo()
	{
		return this.irisInfo;
	}
	
	//============================================================================================
	//*****************************************Overrides******************************************
	//============================================================================================
	
	@Override
	public void doKawoosh()
	{
		if(!irisInfo().isIrisClosed())
			super.doKawoosh();
	}
	
	@Override
	public void setStargateState(boolean updateInterfaces)
	{
		setStargateState(updateInterfaces, false, irisInfo().getShieldingState());
		updateClientState();
	}
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.iris").append(Component.literal(": ").append((!irisInfo().getIris().isEmpty() ? irisInfo().getIris().getDisplayName() : Component.literal("-")))).withStyle(ChatFormatting.GRAY));
		player.sendSystemMessage(Component.translatable("info.sgjourney.iris_durability").append(Component.literal(": " + (!irisInfo().getIris().isEmpty() ? StargateIrisItem.getDurability(irisInfo().getIris()) : "-"))).withStyle(ChatFormatting.GRAY));
		if(!irisInfo().getIris().isEmpty() && StargateIrisItem.hasCustomTexture(irisInfo().getIris()))
			player.sendSystemMessage(Component.translatable("info.sgjourney.iris_texture").append(Component.literal(": " + StargateIrisItem.getIrisTexture(irisInfo().getIris()))).withStyle(ChatFormatting.DARK_PURPLE));
		
		super.getStatus(player);
	}
	
	public boolean updateClient()
	{
		if(level.isClientSide())
			return false;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(this.worldPosition, this.getEnergyStored(), this.openTime, this.timeSinceLastTraveler, this.address.getArray(), this.engagedChevrons, this.kawooshTick, this.animationTick, irisInfo().getIrisProgress(), symbolInfo().pointOfOrigin(), symbolInfo().symbols(), this.variant, irisInfo().getIris()));
		return true;
	}
}
