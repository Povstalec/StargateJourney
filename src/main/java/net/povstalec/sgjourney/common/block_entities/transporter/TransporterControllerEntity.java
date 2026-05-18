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
import net.povstalec.sgjourney.common.misc.BlockEntityCache;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class TransporterControllerEntity extends EnergyBlockEntity implements StructureGenEntity, ProtectedBlockEntity
{
	public static final double DEFAULT_MAX_DISCOVERY_DISTANCE = 1024;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Set<Integer> networks = new HashSet<>();
	
	protected double maxDiscoveryDistance = DEFAULT_MAX_DISCOVERY_DISTANCE;
	
	/*@Nullable
	protected Transporter transporter;*/
	
	public final BlockEntityCache<AbstractTransporterEntity<?>> transporterCache = new BlockEntityCache<>();
	
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
		
		transporterCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(AbstractTransporterEntity.class, level, worldPosition, 16,
				transporter -> !transporter.controllerCache.isFetching() && !transporter.controllerCache.hasBlockEntity()));
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
	
	public Set<Integer> getNetworks()
	{
		return this.networks;
	}
	
	/*public void connectToTransporter(Transporter transporter)
	{
		this.transporter = transporter;
	}*/
	
	public void setTransporter()
	{
		transporterCache.fetch();
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
