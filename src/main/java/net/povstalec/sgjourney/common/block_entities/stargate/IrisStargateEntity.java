package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.items.StargateIrisItem;
import net.povstalec.sgjourney.common.sgjourney.info.IrisInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class IrisStargateEntity<SG extends BlockEntityStargate<?>> extends AbstractStargateEntity<SG> implements IrisInfo.Interface
{
	protected IrisInfo irisInfo;
	
	public IrisStargateEntity(BlockEntityType<?> blockEntityType, StargateType<SG> stargateType, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
							  int totalSymbols, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntityType, stargateType, defaultVariant, pos, state, totalSymbols, defaultNetwork, verticalCenterHeight, horizontalCenterHeight);
		
		this.irisInfo = new IrisInfo(this);
	}
	
	public IrisStargateEntity(BlockEntityType<?> blockEntityType, StargateType<SG> stargateType, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
							  int totalSymbols, int defaultNetwork)
	{
		this(blockEntityType, stargateType, defaultVariant, pos, state, totalSymbols, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
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
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		tag.putShort(IRIS_PROGRESS, irisInfo().getIrisProgress());
		tag.putShort(OLD_IRIS_PROGRESS, irisInfo().getIrisProgress());
		tag.put(IRIS_INVENTORY, irisInfo().serializeIrisInventory());
		tag.putByte(IRIS_MOTION, (byte) irisInfo().getIrisMotion().ordinal());
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(net, packet);
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			short progress = tag.getShort(IRIS_PROGRESS);
			short oldProgress = tag.getShort(OLD_IRIS_PROGRESS);
			
			if(progress == oldProgress && progress != irisInfo().getIrisProgress())
				irisInfo().setIrisProgress(progress, oldProgress);
			
			irisInfo().deserializeIrisInventory(tag.getCompound(IRIS_INVENTORY));
			irisInfo().setIrisMotion(IrisInfo.IrisMotion.fromByte(tag.getByte(IRIS_MOTION)));
		}
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
	public void setStargateState()
	{
		setStargateState(false, irisInfo().getShieldingState());
		updateClient();
	}
	
	@Override
	public List<Component> getStatus()
	{
		List<Component> status = new ArrayList<>();
		
		status.add(new TranslatableComponent("info.sgjourney.iris").append(new TextComponent(": ").append((!irisInfo().getIris().isEmpty() ? irisInfo().getIris().getDisplayName() : new TextComponent("-")))).withStyle(ChatFormatting.GRAY));
		status.add(new TranslatableComponent("info.sgjourney.iris_durability").append(new TextComponent(": " + (!irisInfo().getIris().isEmpty() ? StargateIrisItem.getDurability(irisInfo().getIris()) : "-"))).withStyle(ChatFormatting.GRAY));
		if(!irisInfo().getIris().isEmpty() && StargateIrisItem.hasCustomTexture(irisInfo().getIris()))
			status.add(new TranslatableComponent("info.sgjourney.iris_texture").append(new TextComponent(": " + StargateIrisItem.getIrisTexture(irisInfo().getIris()))).withStyle(ChatFormatting.DARK_PURPLE));
		
		status.addAll(super.getStatus());
		return status;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, IrisStargateEntity<?> stargate)
	{
		stargate.irisInfo().tickIris();
		
		AbstractStargateEntity.tick(level, pos, state, stargate);
	}
}
