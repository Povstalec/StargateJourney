package net.povstalec.sgjourney.common.block_entities.transporter;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class TransporterControllerEntity extends EnergyBlockEntity implements StructureGenEntity, ProtectedBlockEntity, AutoCache.IController<TransporterControllerEntity, AbstractTransporterEntity<?>>
{
	public static final String TRANSPORTER_POS = "transporter_pos";
	
	public static final int CONTROLLER_INFO_DISTANCE = 3;
	public static final double DEFAULT_MAX_DISCOVERY_DISTANCE = 1024;
	
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Direction direction;
	
	protected Set<Integer> networks = new HashSet<>();
	
	protected int maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE; // Max distance from which it can connect to a Transporter and control it
	protected double maxDiscoveryDistance = DEFAULT_MAX_DISCOVERY_DISTANCE; // Max distance for discovering nearby Transporters for the purposes of establishing a connection
	
	@Nullable
	protected Vec3i transporterRelativePos = null;
	public final AutoCache.Receiver<TransporterControllerEntity, AbstractTransporterEntity<?>> transporterCache = new AutoCache.Receiver<>(this);
	
	protected boolean isProtected = false;
	
	public TransporterControllerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		if(getLevel().isClientSide())
		{
			// Revalidation
			transporterCache.setRevalidate(() ->
			{
				if(transporterRelativePos == null)
					return false;
				
				BlockPos transporterPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), transporterRelativePos);
				if(transporterPos != null && level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?> transporter)
					return transporterCache.getCached() == transporter; // Check if the Transporter at the saved pos is the same Transporter
				
				return false;
			});
			// Client will only ever attempt to fetch Transporter from the relative pos provided by syncing
			transporterCache.setFetch(() ->
			{
				if(transporterRelativePos == null)
					return null;
				
				BlockPos transporterPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), transporterRelativePos);
				if(transporterPos != null && level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?> transporter)
					return transporter;
				
				return null;
			});
		}
		else
		{
			// Revalidation - check if it's not too far
			transporterCache.setRevalidate(() ->
			{
				if(transporterRelativePos == null)
					return false;
				
				BlockPos transporterPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), transporterRelativePos);
				if(transporterPos != null && level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?> transporter)
					return transporterCache.getCached() == transporter && CoordinateHelper.Relative.distanceSqr(transporterPos, getBlockPos()) <= getMaxConnectionDistanceSqr(); // Check if the Transporter at the saved pos is the same Transporter
				
				return false;
			});
			// Find nearest Transporter that isn't connected to a Controller
			transporterCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(AbstractTransporterEntity.class, level, worldPosition, maxConnectionDistance,
					transporter -> !transporter.controllerCache.isCached()));
			
			transporterCache.setOnChanged((oldTransporter, newTransporter) ->
			{
				if(newTransporter != null)
					transporterRelativePos = CoordinateHelper.Relative.getRelativeOffset(getDirection(), getBlockPos(), newTransporter.getBlockPos());
				else
					transporterRelativePos = null;
				
				updateClient();
			});
			
			if(generationStep == StructureGenEntity.Step.READY)
				generate();
			
			updateClient();
		}
		
		super.onLoad();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
		
		if(tag.contains(TRANSPORTER_POS, Tag.TAG_INT_ARRAY))
			transporterRelativePos = Conversion.intArrayToVec(tag.getIntArray(TRANSPORTER_POS));
		else
			transporterRelativePos = null;
		
		super.load(tag);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(transporterRelativePos != null)
			tag.putIntArray(TRANSPORTER_POS, Conversion.vecToIntArray(transporterRelativePos));
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putLong(ENERGY, energyStorage.getTrueEnergyStored());
		
		if(transporterRelativePos != null)
			tag.putIntArray(TRANSPORTER_POS, Conversion.vecToIntArray(transporterRelativePos));
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		energyStorage.setEnergy(tag.getLong(ENERGY));
		
		if(tag.contains(TRANSPORTER_POS, Tag.TAG_INT_ARRAY))
			transporterRelativePos = Conversion.intArrayToVec(tag.getIntArray(TRANSPORTER_POS));
		else
			transporterRelativePos = null;
		transporterCache.markDirty();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
			handleUpdateTag(tag);
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
	
	public abstract Direction getDirection();
	
	
	
	public int getMaxConnectionDistance()
	{
		return maxConnectionDistance;
	}
	
	public long getMaxConnectionDistanceSqr()
	{
		return (long) maxConnectionDistance * maxConnectionDistance;
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
		if(generationStep == Step.SETUP)
			generationStep = Step.READY; // Marks the Controller as ready for generation
	}
	
	public void generate()
	{
		generateEnergyItem();
		generateAdditional(Step.READY);
		
		generationStep = Step.GENERATED;
	}
	
	public void generateAdditional(StructureGenEntity.Step generationStep) {}
	
	protected abstract void generateEnergyItem();
	
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
