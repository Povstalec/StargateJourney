package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class DHDCrystalMenu extends InventoryMenu<CrystalDHDEntity>
{
    public DHDCrystalMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, (CrystalDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public DHDCrystalMenu(int containerId, Inventory inventory, CrystalDHDEntity blockEntity)
    {
        super(MenuInit.DHD_CRYSTAL.get(), containerId, inventory, blockEntity);
		
        checkContainerSize(inventory, 9);
        addPlayerInventory(inventory, 8, 84);
        addPlayerHotbar(inventory, 8, 142);
        
        this.blockEntity.getItemHandler().ifPresent(handler -> {
            this.addBlockEntitySlot(new SlotItemHandler(handler, 0, 80, 35));
            
            this.addBlockEntitySlot(new SlotItemHandler(handler, 1, 80, 17));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 2, 98, 17));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 3, 98, 35));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 4, 98, 53));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 5, 80, 53));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 6, 62, 53));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 7, 62, 35));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 8, 62, 17));
        });
        
        this.blockEntity.getEnergyItemHandler().ifPresent(handler -> {
            this.addBlockEntitySlot(new SlotItemHandler(handler, 0, 134, 27));
            this.addBlockEntitySlot(new SlotItemHandler(handler, 1, 134, 53));
        });
    }
    
    public long getStargateEnergy()
    {
        return this.blockEntity.getStargateEnergy();
    }
    
    public int getStargateOpenTime()
    {
        return this.blockEntity.getStargateOpenTime();
    }
    
    public int getStargateTimeSinceLastTraveler()
    {
        return this.blockEntity.getStargateTimeSinceLastTraveler();
    }
    
    public long getEnergy()
    {
        return this.blockEntity.energyStorage.getTrueEnergyStored();
    }
    
    public long getMaxEnergy()
    {
        return this.blockEntity.energyStorage.getTrueMaxEnergyStored();
    }
    
    public boolean enableAdvancedProtocols()
    {
        return this.blockEntity.enableAdvancedProtocols();
    }
    
    public long getEnergyTarget()
    {
        return this.blockEntity.getEnergyTarget();
    }
    
    public long maxEnergyDeplete()
    {
        return this.blockEntity.maxEnergyDeplete();
    }
    
    public int getMaxDistance()
    {
        return this.blockEntity.getMaxDistance();
    }
	
    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.MILKY_WAY_DHD.get()) ||
        		stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.PEGASUS_DHD.get()) ||
        		stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CLASSIC_DHD.get());
    }
}
