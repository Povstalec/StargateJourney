package net.povstalec.sgjourney.common.block_entities.transporter;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class TransporterControllerEntity extends EnergyBlockEntity implements StructureGenEntity, ProtectedBlockEntity, AutoCache.IController<TransporterControllerEntity, AbstractTransporterEntity<?>>
{
	public static final int CONTROLLER_INFO_DISTANCE = 3;
	public static final double DEFAULT_MAX_DISCOVERY_DISTANCE = 1024;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Set<Integer> networks = new HashSet<>();
	
	protected double maxDiscoveryDistance = DEFAULT_MAX_DISCOVERY_DISTANCE;
	
	public final AutoCache.Receiver<TransporterControllerEntity, AbstractTransporterEntity<?>> transporterCache = new AutoCache.Receiver<>(this);
	
	protected boolean isProtected = false;
	
	public TransporterControllerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(this.level.isClientSide())
			return;
		
		//=====Setting up cache logic=====
		transporterCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(AbstractTransporterEntity.class, level, worldPosition, 16,
				transporter -> !transporter.controllerCache.isCached()));
		//==========
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
		
		super.load(tag);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
	}
	
	@Override
	public AutoCache.Receiver<TransporterControllerEntity, AbstractTransporterEntity<?>> receiverCache()
	{
		return transporterCache;
	}
	
	public Set<Integer> getNetworks()
	{
		return this.networks;
	}
	
	public void setTransporter()
	{
		// transporterCache.fetch(); //TODO Probably get rid of this method altogether
	}
	
	// ======= Transporting =======
	
	public TransporterInfo.Feedback startCoordTransport(Vec3 coords)
	{
		return transporterCache.returnOrDefault(transporter -> transporter.dialTransporter(Conversion.vec3ToVec3i(coords)), TransporterInfo.Feedback.NONE);
	}
	
	public TransporterInfo.Feedback startIDTransport(TransporterID transporterID)
	{
		return transporterCache.returnOrDefault(transporter -> transporter.dialTransporter(transporterID), TransporterInfo.Feedback.NONE);
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		//TODO generateEnergyItem();
		generateAdditional(Step.READY);
		
		generationStep = Step.GENERATED;
	}
	
	public void generateAdditional(StructureGenEntity.Step generationStep) {}
	
	@Override
	public void setProtected(boolean isProtected)
	{
		this.isProtected = isProtected;
	}
	
	@Override
	public boolean isProtected()
	{
		return isProtected;
	}
	
	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_transporter_controller_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
