package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.StargateIrisItem;
import net.povstalec.sgjourney.common.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateConnection;
import net.povstalec.sgjourney.common.stargate.info.IrisInfo;

public abstract class IrisStargateEntity extends AbstractStargateEntity implements IrisInfo.Interface
{
	protected IrisInfo irisInfo;
	
	public IrisStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
							  int totalSymbols, Stargate.Gen gen, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, verticalCenterHeight, horizontalCenterHeight);
		
		this.irisInfo = new IrisInfo(this);
	}
	
	public IrisStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
							  int totalSymbols, Stargate.Gen gen, int defaultNetwork)
	{
		this(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
	}
	
	public void deserializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgraded)
	{
		super.deserializeStargateInfo(tag, registries, isUpgraded);
		
		short irisProgress = tag.getShort(IrisInfo.Interface.IRIS_PROGRESS);
		irisInfo().setIrisProgress(irisProgress, irisProgress);
		irisInfo().deserializeIrisInventory(registries, tag.getCompound(IrisInfo.Interface.IRIS_INVENTORY));
	}
	
	public CompoundTag serializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.serializeStargateInfo(tag, registries);
		
		tag.putShort(IrisInfo.Interface.IRIS_PROGRESS, irisInfo().getIrisProgress());
		tag.put(IrisInfo.Interface.IRIS_INVENTORY, irisInfo().serializeIrisInventory(registries));
		
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
	public void doKawoosh(int kawooshTime)
	{
		setKawooshTickCount(kawooshTime);
		
		if(kawooshTime > StargateConnection.KAWOOSH_TICKS || irisInfo().isIrisClosed())
			return;
		
		Direction axisDirection = getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		Direction direction = Orientation.getEffectiveDirection(getDirection(), getOrientation());
		
		double frontMultiplier = kawooshFunction(kawooshTime);
		
		if(CommonStargateConfig.kawoosh_destroys_blocks.get())
			destroyBlocks(frontMultiplier, axisDirection, direction);
		if(CommonStargateConfig.kawoosh_disintegrates_entities.get())
			disintegrateEntities(frontMultiplier, axisDirection, direction);
	}
	
	@Override
	public void setStargateState(StargateConnection.State connectionState, int chevronsEngaged, boolean updateInterfaces)
	{
		setStargateState(connectionState, chevronsEngaged, updateInterfaces, false, irisInfo().getShieldingState());
		updateClientState();
	}
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + symbolInfo().pointOfOrigin())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbolInfo().symbols())).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.times_opened").append(Component.literal(": " + timesOpened)).withStyle(ChatFormatting.BLUE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.has_dhd").append(Component.literal(": " + dhdInfo().hasDHD())).withStyle(ChatFormatting.GOLD));
		player.sendSystemMessage(Component.translatable("info.sgjourney.autoclose").append(Component.literal(": " + dhdInfo().autoclose())).withStyle(ChatFormatting.RED));
		player.sendSystemMessage(Component.translatable("info.sgjourney.last_traveler_time").append(Component.literal(": " + getTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.encoded_address").append(Component.literal(": ").append(address.toComponent(true))).withStyle(ChatFormatting.GREEN));
		player.sendSystemMessage(Component.translatable("info.sgjourney.recent_feedback").append(Component.literal(": ").append(getRecentFeedback().getFeedbackMessage())).withStyle(ChatFormatting.WHITE));
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.iris").append(Component.literal(": ").append((!irisInfo().getIris().isEmpty() ? irisInfo().getIris().getDisplayName() : Component.literal("-")))).withStyle(ChatFormatting.GRAY));
		player.sendSystemMessage(Component.translatable("info.sgjourney.iris_durability").append(Component.literal(": " + (!irisInfo().getIris().isEmpty() ? StargateIrisItem.getDurability(irisInfo().getIris()) : "-"))).withStyle(ChatFormatting.GRAY));
		if(!irisInfo().getIris().isEmpty() && StargateIrisItem.hasCustomTexture(irisInfo().getIris()))
			player.sendSystemMessage(Component.translatable("info.sgjourney.iris_texture").append(Component.literal(": " + StargateIrisItem.getIrisTexture(irisInfo().getIris()))).withStyle(ChatFormatting.DARK_PURPLE));
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.9_chevron_address").append(": ").withStyle(ChatFormatting.AQUA).append(id9ChevronAddress.toComponent(true)));
		player.sendSystemMessage(Component.translatable("info.sgjourney.add_to_network").append(Component.literal(": " + addToNetwork)).withStyle(ChatFormatting.YELLOW));
		player.sendSystemMessage(Component.translatable("info.sgjourney.open_time").append(Component.literal(": " + getOpenTime() + "/" + getMaxGateOpenTime())).withStyle(ChatFormatting.DARK_AQUA));
		
		player.sendSystemMessage(Component.literal("Energy: " + this.getEnergyStored() + " FE").withStyle(ChatFormatting.DARK_RED));
	}
	
	public boolean updateClient()
	{
		if(level.isClientSide())
			return false;
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientboundStargateUpdatePacket(this.worldPosition, this.address.toArray(), this.engagedChevrons, this.kawooshTick, this.animationTick, irisInfo().getIrisProgress(), symbolInfo().pointOfOrigin(), symbolInfo().symbols(), this.variant, irisInfo().getIris()));
		return true;
	}
}
