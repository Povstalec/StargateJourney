package net.povstalec.sgjourney.common.handlers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface IFunctionalItemStackHandler{

    void setSize(int size);


    NonNullList<ItemStack> get_stacks();

    @FunctionalInterface
    interface SetSizeFunction {
        void setSize(int size);
    }
    @FunctionalInterface
    interface SetStackInSlotFunction {
        void setStackInSlot(int slot, @NotNull ItemStack stack);
    }
    @FunctionalInterface
    interface GetSlotsFunction {
        int getSlots();
    }
    @FunctionalInterface
    interface GetStackInSlotFunction {
        @NotNull ItemStack getStackInSlot(int slot);
    }
    @FunctionalInterface
    interface InsertItemFunction {
        @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);
    }
    @FunctionalInterface
    interface ExtractItemFunction {
        @NotNull ItemStack extractItem(int slot, int amount, boolean simulate);
    }
    @FunctionalInterface
    interface GetSlotLimitFunction {
        int getSlotLimit(int slot);
    }
    @FunctionalInterface
    interface IsItemValidFunction {
        boolean isItemValid(int slot, @NotNull ItemStack stack);
    }
    @FunctionalInterface
    interface SerializeNBTFunction {
        @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider);
    }
    @FunctionalInterface
    interface DeserializeNBTFunction {
        void deserializeNBT(HolderLookup.Provider provider, @NotNull CompoundTag nbt);
    }
    @FunctionalInterface
    interface OnLoadFunction {
        void onLoad();
    }
    @FunctionalInterface
    interface OnContentsChangedFunction {
        void onContentsChanged(int slot);
    }
    @FunctionalInterface
    interface GetStackLimitFunction {
        int getStackLimit(int slot, @NotNull ItemStack stack);
    }

    IFunctionalItemStackHandler set_setSize(SetSizeFunction function);
    IFunctionalItemStackHandler set_setStackInSlot(SetStackInSlotFunction function);
    IFunctionalItemStackHandler set_getSlots(GetSlotsFunction function);
    IFunctionalItemStackHandler set_getStackInSlot(GetStackInSlotFunction function);
    IFunctionalItemStackHandler set_insertItem(InsertItemFunction function);
    IFunctionalItemStackHandler set_extractItem(ExtractItemFunction function);
    IFunctionalItemStackHandler set_getSlotLimit(GetSlotLimitFunction function);
    IFunctionalItemStackHandler set_isItemValid(IsItemValidFunction function);
    IFunctionalItemStackHandler set_serializeNBT(SerializeNBTFunction function);
    IFunctionalItemStackHandler set_deserializeNBT(DeserializeNBTFunction function);
    IFunctionalItemStackHandler set_onLoad(OnLoadFunction function);
    IFunctionalItemStackHandler set_onContentsChanged(OnContentsChangedFunction function);
    IFunctionalItemStackHandler set_getStackLimit(GetStackLimitFunction function);
    
    boolean isSet_setSize();
    boolean isSet_setStackInSlot();
    boolean isSet_getSlots();
    boolean isSet_getStackInSlot();
    boolean isSet_insertItem();
    boolean isSet_extractItem();
    boolean isSet_getSlotLimit();
    boolean isSet_isItemValid();
    boolean isSet_serializeNBT();
    boolean isSet_deserializeNBT();
    boolean isSet_onLoad();
    boolean isSet_onContentsChanged();
    boolean isSet_getStackLimit();

}
