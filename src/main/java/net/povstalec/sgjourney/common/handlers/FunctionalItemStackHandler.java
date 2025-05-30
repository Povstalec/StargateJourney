package net.povstalec.sgjourney.common.handlers;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An extension of {@link ItemStackHandler} that allows every major public or protected
 * operation of the parent class to be replaced by user supplied {@code java.util.function.*}
 * style callbacks.
 * <p>
 * This is especially useful when you need a mutable / script�driven implementation
 * that can dynamically change its behavior at runtime (for example, delegating to
 * another inventory, becoming read-only, logging, etc.).
 * <p>
 * <h2>How it works</h2>
 * <ol>
 *     <li>Every vanilla-like method is overridden.</li>
 *     <li>If a replacement callback is defined it is invoked, otherwise the
 *     original {@code super.*} implementation is used.</li>
 *     <li>Convenience {@code super_*} methods are exposed which directly call the original
 *     implementation.  These are helpful when the callback
 *     still wants to call the base implementation after doing its own work.</li>
 * </ol>
 * <p>
 * <b>Thread-safety:</b> identical to {@link ItemStackHandler}.  No additional
 * synchronisation is introduced.
 */
@SuppressWarnings("unused")
public class FunctionalItemStackHandler extends ItemStackHandler
        implements IFunctionalItemStackHandler,
                   IItemHandler,
                   IItemHandlerModifiable,
                   INBTSerializable<CompoundTag>
{
    /* ======================================================================
     * Constructors
     * ====================================================================== */

    /**
     * Creates a handler with {@code 1} empty slot (mirrors {@link ItemStackHandler#ItemStackHandler()}).
     */
    public FunctionalItemStackHandler() {
        super();
    }

    /**
     * Creates a handler with the specified number of empty slots
     * (mirrors {@link ItemStackHandler#ItemStackHandler(int)}).
     *
     * @param slots number of slots to create
     */
    public FunctionalItemStackHandler(int slots) {
        super(slots);
    }

    /**
     * Creates a handler that is backed by the provided {@link NonNullList}.
     *
     * @param stacks pre-populated list of stacks to back this handler
     */
    public FunctionalItemStackHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    /* ======================================================================
     * Behaviour-replacing function fields
     * ====================================================================== */

    /**
     * Callback for {@link #setSize(int)}.
     */
    @Nullable private SetSizeFunction            setSizeFunction;
    /**
     * Callback for {@link #setStackInSlot(int, ItemStack)}.
     */
    @Nullable private SetStackInSlotFunction     setStackInSlotFunction;
    /**
     * Callback for {@link #getSlots()}.
     */
    @Nullable private GetSlotsFunction           getSlotsFunction;
    /**
     * Callback for {@link #getStackInSlot(int)}.
     */
    @Nullable private GetStackInSlotFunction     getStackInSlotFunction;
    /**
     * Callback for {@link #insertItem(int, ItemStack, boolean)}.
     */
    @Nullable private InsertItemFunction         insertItemFunction;
    /**
     * Callback for {@link #extractItem(int, int, boolean)}.
     */
    @Nullable private ExtractItemFunction        extractItemFunction;
    /**
     * Callback for {@link #getSlotLimit(int)}.
     */
    @Nullable private GetSlotLimitFunction       getSlotLimitFunction;
    /**
     * Callback for {@link #isItemValid(int, ItemStack)}.
     */
    @Nullable private IsItemValidFunction        isItemValidFunction;
    /**
     * Callback for {@link #serializeNBT()}.
     */
    @Nullable private SerializeNBTFunction       serializeNBTFunction;
    /**
     * Callback for {@link #deserializeNBT(CompoundTag)}.
     */
    @Nullable private DeserializeNBTFunction     deserializeNBTFunction;
    /**
     * Callback for {@link #onLoad()} (called once when the game world loads).
     */
    @Nullable private OnLoadFunction             onLoadFunction;
    /**
     * Callback for {@link #onContentsChanged(int)}.
     */
    @Nullable private OnContentsChangedFunction  onContentsChangedFunction;
    /**
     * Callback for {@link #getStackLimit(int, ItemStack)} (protected helper).
     */
    @Nullable private GetStackLimitFunction      getStackLimitFunction;



    /* ======================================================================
     * Fluent setter helpers
     * ====================================================================== */

    /**
     * Registers a replacement for {@link #setSize(int)}.
     * <p>
     * When setting this function, it completely replaces the original implementation.
     * If you need to call the original method within your custom implementation,
     * use {@link #super_setSize(int)}.
     *
     * @param function user supplied lambda
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_setSize(SetSizeFunction function) {
        return set_setSize(function, false);
    }

    /**
     * Registers a replacement for {@link #setSize(int)} with override option.
     * <p>
     * When setting this function, it completely replaces the original implementation.
     * If you need to call the original method within your custom implementation,
     * use {@link #super_setSize(int)}.
     *
     * @param function user supplied lambda
     * @param override if true, allows replacing an already set function. If false and a function is already set, throws an error.
     *                When override is true and no function is set, an IllegalStateException will be thrown.
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_setSize(SetSizeFunction function, boolean override) {
        if (!override && this.setSizeFunction != null) {
            throw new IllegalStateException("SetSizeFunction is already set and override is false");
        }
        if (override && this.setSizeFunction == null) {
            throw new IllegalStateException("Cannot override SetSizeFunction because it is not set");
        }
        this.setSizeFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #setStackInSlot(int, ItemStack)}.
     * <p>
     * When setting this function, it completely replaces the original implementation.
     * If you need to call the original method within your custom implementation,
     * use {@link #super_setStackInSlot(int, ItemStack)}.
     *
     * @param function user supplied lambda
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_setStackInSlot(SetStackInSlotFunction function) {
        return set_setStackInSlot(function, false);
    }

    /**
     * Registers a replacement for {@link #setStackInSlot(int, ItemStack)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_setStackInSlot(SetStackInSlotFunction function, boolean override) {
        if (!override && this.setStackInSlotFunction != null) {
            throw new IllegalStateException("SetStackInSlotFunction is already set and override is false");
        }
        if (override && this.setStackInSlotFunction == null) {
            throw new IllegalStateException("Cannot override SetStackInSlotFunction because it is not set");
        }
        this.setStackInSlotFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #getSlots()}.
     * <p>
     * When setting this function, it completely replaces the original implementation.
     * If you need to call the original method within your custom implementation,
     * use {@link #super_getSlots()}.
     *
     * @param function user supplied lambda
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_getSlots(GetSlotsFunction function) {
        return set_getSlots(function, false);
    }

    /**
     * Registers a replacement for {@link #getSlots()} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_getSlots(GetSlotsFunction function, boolean override) {
        if (!override && this.getSlotsFunction != null) {
            throw new IllegalStateException("GetSlotsFunction is already set and override is false");
        }
        if (override && this.getSlotsFunction == null) {
            throw new IllegalStateException("Cannot override GetSlotsFunction because it is not set");
        }
        this.getSlotsFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #getStackInSlot(int)}.
     */
    public final FunctionalItemStackHandler set_getStackInSlot(GetStackInSlotFunction function) {
        return set_getStackInSlot(function, false);
    }

    /**
     * Registers a replacement for {@link #getStackInSlot(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_getStackInSlot(GetStackInSlotFunction function, boolean override) {
        if (!override && this.getStackInSlotFunction != null) {
            throw new IllegalStateException("GetStackInSlotFunction is already set and override is false");
        }
        if (override && this.getStackInSlotFunction == null) {
            throw new IllegalStateException("Cannot override GetStackInSlotFunction because it is not set");
        }
        this.getStackInSlotFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #insertItem(int, ItemStack, boolean)}.
     */
    public final FunctionalItemStackHandler set_insertItem(InsertItemFunction function) {
        return set_insertItem(function, false);
    }

    /**
     * Registers a replacement for {@link #insertItem(int, ItemStack, boolean)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_insertItem(InsertItemFunction function, boolean override) {
        if (!override && this.insertItemFunction != null) {
            throw new IllegalStateException("InsertItemFunction is already set and override is false");
        }
        if (override && this.insertItemFunction == null) {
            throw new IllegalStateException("Cannot override InsertItemFunction because it is not set");
        }
        this.insertItemFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #extractItem(int, int, boolean)}.
     */
    public final FunctionalItemStackHandler set_extractItem(ExtractItemFunction function) {
        return set_extractItem(function, false);
    }

    /**
     * Registers a replacement for {@link #extractItem(int, int, boolean)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_extractItem(ExtractItemFunction function, boolean override) {
        if (!override && this.extractItemFunction != null) {
            throw new IllegalStateException("ExtractItemFunction is already set and override is false");
        }
        if (override && this.extractItemFunction == null) {
            throw new IllegalStateException("Cannot override ExtractItemFunction because it is not set");
        }
        this.extractItemFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #getSlotLimit(int)}.
     */
    public final FunctionalItemStackHandler set_getSlotLimit(GetSlotLimitFunction function) {
        return set_getSlotLimit(function, false);
    }

    /**
     * Registers a replacement for {@link #getSlotLimit(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_getSlotLimit(GetSlotLimitFunction function, boolean override) {
        if (!override && this.getSlotLimitFunction != null) {
            throw new IllegalStateException("GetSlotLimitFunction is already set and override is false");
        }
        if (override && this.getSlotLimitFunction == null) {
            throw new IllegalStateException("Cannot override GetSlotLimitFunction because it is not set");
        }
        this.getSlotLimitFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #isItemValid(int, ItemStack)}.
     */
    public final FunctionalItemStackHandler set_isItemValid(IsItemValidFunction function) {
        return set_isItemValid(function, false);
    }

    /**
     * Registers a replacement for {@link #isItemValid(int, ItemStack)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_isItemValid(IsItemValidFunction function, boolean override) {
        if (!override && this.isItemValidFunction != null) {
            throw new IllegalStateException("IsItemValidFunction is already set and override is false");
        }
        if (override && this.isItemValidFunction == null) {
            throw new IllegalStateException("Cannot override IsItemValidFunction because it is not set");
        }
        this.isItemValidFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #serializeNBT()}.
     */
    public final FunctionalItemStackHandler set_serializeNBT(SerializeNBTFunction function) {
        return set_serializeNBT(function, false);
    }

    /**
     * Registers a replacement for {@link #serializeNBT()} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_serializeNBT(SerializeNBTFunction function, boolean override) {
        if (!override && this.serializeNBTFunction != null) {
            throw new IllegalStateException("SerializeNBTFunction is already set and override is false");
        }
        if (override && this.serializeNBTFunction == null) {
            throw new IllegalStateException("Cannot override SerializeNBTFunction because it is not set");
        }
        this.serializeNBTFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #deserializeNBT(CompoundTag)}.
     */
    public final FunctionalItemStackHandler set_deserializeNBT(DeserializeNBTFunction function) {
        return set_deserializeNBT(function, false);
    }

    /**
     * Registers a replacement for {@link #deserializeNBT(CompoundTag)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_deserializeNBT(DeserializeNBTFunction function, boolean override) {
        if (!override && this.deserializeNBTFunction != null) {
            throw new IllegalStateException("DeserializeNBTFunction is already set and override is false");
        }
        if (override && this.deserializeNBTFunction == null) {
            throw new IllegalStateException("Cannot override DeserializeNBTFunction because it is not set");
        }
        this.deserializeNBTFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #onLoad()}.
     */
    public final FunctionalItemStackHandler set_onLoad(OnLoadFunction function) {
        return set_onLoad(function, false);
    }

    /**
     * Registers a replacement for {@link #onLoad()} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_onLoad(OnLoadFunction function, boolean override) {
        if (!override && this.onLoadFunction != null) {
            throw new IllegalStateException("OnLoadFunction is already set and override is false");
        }
        if (override && this.onLoadFunction == null) {
            throw new IllegalStateException("Cannot override OnLoadFunction because it is not set");
        }
        this.onLoadFunction = function;
        return this;
    }

    /**
     * Registers a replacement for {@link #onContentsChanged(int)}.
     */
    public final FunctionalItemStackHandler set_onContentsChanged(OnContentsChangedFunction function) {
        return set_onContentsChanged(function, false);
    }

    /**
     * Registers a replacement for {@link #onContentsChanged(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_onContentsChanged(OnContentsChangedFunction function, boolean override) {
        if (!override && this.onContentsChangedFunction != null) {
            throw new IllegalStateException("OnContentsChangedFunction is already set and override is false");
        }
        if (override && this.onContentsChangedFunction == null) {
            throw new IllegalStateException("Cannot override OnContentsChangedFunction because it is not set");
        }
        this.onContentsChangedFunction = function;
        return this;
    }

    /**
     * Registers a replacement for protected {@link #getStackLimit(int, ItemStack)}.
     */
    public final FunctionalItemStackHandler set_getStackLimit(GetStackLimitFunction function) {
        return set_getStackLimit(function, false);
    }

    /**
     * Registers a replacement for protected {@link #getStackLimit(int, ItemStack)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public final FunctionalItemStackHandler set_getStackLimit(GetStackLimitFunction function, boolean override) {
        if (!override && this.getStackLimitFunction != null) {
            throw new IllegalStateException("GetStackLimitFunction is already set and override is false");
        }
        if (override && this.getStackLimitFunction == null) {
            throw new IllegalStateException("Cannot override GetStackLimitFunction because it is not set");
        }
        this.getStackLimitFunction = function;
        return this;
    }

    /* ======================================================================
     * Convenience wrappers exposing the original behaviour
     *
     * NOTE: These methods are intended for use within set_* functions only.
     * They allow custom implementations to call through to the super method
     * when overriding behavior. They are not meant to be called directly
     * from outside code.
     * ====================================================================== */

    /**
     * Calls the parent implementation of {@link #setSize(int)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_setSize(SetSizeFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param size the new size for the inventory
     */
    public final void super_setSize(int size) { super.setSize(size); }

    /**
     * Calls the parent implementation of {@link #setStackInSlot(int, ItemStack)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_setStackInSlot(SetStackInSlotFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to modify
     * @param stack the stack to set
     */
    public final void super_setStackInSlot(int slot, @NotNull ItemStack stack) { super.setStackInSlot(slot, stack); }

    /**
     * Calls the parent implementation of {@link #getSlots()}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_getSlots(GetSlotsFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @return the number of slots in the inventory
     */
    public final int super_getSlots() { return super.getSlots(); }

    /**
     * Calls the parent implementation of {@link #getStackInSlot(int)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_getStackInSlot(GetStackInSlotFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to get the stack from
     * @return the stack in the given slot
     */
    public final @NotNull ItemStack super_getStackInSlot(int slot) { return super.getStackInSlot(slot); }

    /**
     * Calls the parent implementation of {@link #insertItem(int, ItemStack, boolean)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_insertItem(InsertItemFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to insert into
     * @param stack the stack to insert
     * @param simulate if true, the insertion is only simulated
     * @return the remaining stack that was not inserted
     */
    public final @NotNull ItemStack super_insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return super.insertItem(slot, stack, simulate); }

    /**
     * Calls the parent implementation of {@link #extractItem(int, int, boolean)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_extractItem(ExtractItemFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to extract from
     * @param amount the max amount to extract
     * @param simulate if true, the extraction is only simulated
     * @return the extracted stack
     */
    public final @NotNull ItemStack super_extractItem(int slot, int amount, boolean simulate) { return super.extractItem(slot, amount, simulate); }

    /**
     * Calls the parent implementation of {@link #getSlotLimit(int)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_getSlotLimit(GetSlotLimitFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to get the limit for
     * @return the slot's stack limit
     */
    public final int super_getSlotLimit(int slot) { return super.getSlotLimit(slot); }

    /**
     * Calls the parent implementation of {@link #isItemValid(int, ItemStack)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_isItemValid(IsItemValidFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to check
     * @param stack the stack to check
     * @return true if the stack is valid for the slot
     */
    public final boolean super_isItemValid(int slot, @NotNull ItemStack stack) { return super.isItemValid(slot, stack); }

    /**
     * Calls the parent implementation of {@link #serializeNBT()}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_serializeNBT(SerializeNBTFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @return the serialized NBT
     */
    public final CompoundTag super_serializeNBT() { return super.serializeNBT(); }

    /**
     * Calls the parent implementation of {@link #deserializeNBT(CompoundTag)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_deserializeNBT(DeserializeNBTFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param nbt the NBT to deserialize
     */
    public final void super_deserializeNBT(@NotNull CompoundTag nbt) { super.deserializeNBT(nbt); }

    /**
     * Calls the parent implementation of {@link #onLoad()}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_onLoad(OnLoadFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     */
    public final void super_onLoad() { super.onLoad(); }

    /**
     * Calls the parent implementation of {@link #onContentsChanged(int)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_onContentsChanged(OnContentsChangedFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot that changed
     */
    public final void super_onContentsChanged(int slot) { super.onContentsChanged(slot); }

    /**
     * Calls the parent implementation of {@link #getStackLimit(int, ItemStack)}.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * through {@link #set_getStackLimit(GetStackLimitFunction)}. When you override a function, you can use this
     * to call through to the super implementation if needed.
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @param slot the slot to check
     * @param stack the stack to check
     * @return the stack limit for the slot and stack
     */
    public final int super_getStackLimit(int slot, @NotNull ItemStack stack) { return super.getStackLimit(slot, stack); }

    /* ======================================================================
     * Function status checker methods
     * ====================================================================== */

    /**
     * Checks if {@link #setSizeFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_setSize() {
        return this.setSizeFunction != null;
    }

    /**
     * Checks if {@link #setStackInSlotFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_setStackInSlot() {
        return this.setStackInSlotFunction != null;
    }

    /**
     * Checks if {@link #getSlotsFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_getSlots() {
        return this.getSlotsFunction != null;
    }

    /**
     * Checks if {@link #getStackInSlotFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_getStackInSlot() {
        return this.getStackInSlotFunction != null;
    }

    /**
     * Checks if {@link #insertItemFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_insertItem() {
        return this.insertItemFunction != null;
    }

    /**
     * Checks if {@link #extractItemFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_extractItem() {
        return this.extractItemFunction != null;
    }

    /**
     * Checks if {@link #getSlotLimitFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_getSlotLimit() {
        return this.getSlotLimitFunction != null;
    }

    /**
     * Checks if {@link #isItemValidFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_isItemValid() {
        return this.isItemValidFunction != null;
    }

    /**
     * Checks if {@link #serializeNBTFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_serializeNBT() {
        return this.serializeNBTFunction != null;
    }

    /**
     * Checks if {@link #deserializeNBTFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_deserializeNBT() {
        return this.deserializeNBTFunction != null;
    }

    /**
     * Checks if {@link #onLoadFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_onLoad() {
        return this.onLoadFunction != null;
    }

    /**
     * Checks if {@link #onContentsChangedFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_onContentsChanged() {
        return this.onContentsChangedFunction != null;
    }

    /**
     * Checks if {@link #getStackLimitFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public final boolean isSet_getStackLimit() {
        return this.getStackLimitFunction != null;
    }

    /* ======================================================================
     * Overridden delegating to function field when available
     * ====================================================================== */

    /**
     * {@inheritDoc}
     *
     * <p>The call is forwarded to {@link #setSizeFunction} if one is defined,
     * otherwise the superclass implementation is used.</p>
     */
    @Override
    public final void setSize(int size) {
        if (setSizeFunction == null) {
            super.setSize(size);
            return;
        }
        setSizeFunction.setSize(size);
    }

    /**
     * Gets underlying {@link #stacks} object.
     * <p>
     * <b>Note:</b> This method is intended to be used when setting a custom function
     * When you override a function, you can use this to fetch the underlying {@link #stacks} object
     * <br/><b><u>This method is not intended to be used outside the context of a set_* method.</u></b>
     *
     * @return NonNullList<ItemStack> which is {@link #stacks}
     */
    @Override
    public NonNullList<ItemStack> get_stacks() {
        return stacks;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #setStackInSlotFunction} when present.</p>
     */
    @Override
    public final void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (setStackInSlotFunction == null) {
            super.setStackInSlot(slot, stack);
            return;
        }
        setStackInSlotFunction.setStackInSlot(slot, stack);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #getSlotsFunction} when present.</p>
     */
    @Override
    public final int getSlots() {
        return (getSlotsFunction == null) ? super.getSlots()
                                          : getSlotsFunction.getSlots();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #getStackInSlotFunction} when present.</p>
     */
    @Override
    @NotNull
    public final ItemStack getStackInSlot(int slot) {
        return (getStackInSlotFunction == null) ? super.getStackInSlot(slot)
                                                : getStackInSlotFunction.getStackInSlot(slot);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #insertItemFunction} when present.</p>
     */
    @Override
    @NotNull
    public final ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return (insertItemFunction == null) ? super.insertItem(slot, stack, simulate)
                                            : insertItemFunction.insertItem(slot, stack, simulate);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #extractItemFunction} when present.</p>
     */
    @Override
    @NotNull
    public final ItemStack extractItem(int slot, int amount, boolean simulate) {
        return (extractItemFunction == null) ? super.extractItem(slot, amount, simulate)
                                             : extractItemFunction.extractItem(slot, amount, simulate);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #getSlotLimitFunction} when present.</p>
     */
    @Override
    public final int getSlotLimit(int slot) {
        return (getSlotLimitFunction == null) ? super.getSlotLimit(slot)
                                              : getSlotLimitFunction.getSlotLimit(slot);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #getStackLimitFunction} when present.</p>
     */
    @Override
    protected final int getStackLimit(int slot, @NotNull ItemStack stack) {
        return (getStackLimitFunction == null) ? super.getStackLimit(slot, stack)
                                               : getStackLimitFunction.getStackLimit(slot, stack);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #isItemValidFunction} when present.</p>
     */
    @Override
    public final boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return (isItemValidFunction == null) ? super.isItemValid(slot, stack)
                                             : isItemValidFunction.isItemValid(slot, stack);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #serializeNBTFunction} when present.</p>
     */
    @Override
    @NotNull
    public final CompoundTag serializeNBT() {
        return (serializeNBTFunction == null) ? super.serializeNBT()
                                              : serializeNBTFunction.serializeNBT();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #deserializeNBTFunction} when present.</p>
     */
    @Override
    public final void deserializeNBT(CompoundTag nbt) {
        if (deserializeNBTFunction == null) {
            super.deserializeNBT(nbt);
            return;
        }
        deserializeNBTFunction.deserializeNBT(nbt);
    }


    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #onLoadFunction} when present.</p>
     */
    @Override
    protected final void onLoad() {
        if (onLoadFunction == null) {
            super.onLoad();
            return;
        }
        onLoadFunction.onLoad();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #onContentsChangedFunction} when present.</p>
     */
    @Override
    protected final void onContentsChanged(int slot) {
        if (onContentsChangedFunction == null) {
            super.onContentsChanged(slot);
            return;
        }
        onContentsChangedFunction.onContentsChanged(slot);
    }
}
