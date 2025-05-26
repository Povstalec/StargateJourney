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
 * that can dynamically change its behaviour at runtime (for example delegating to
 * another inventory, becoming read-only, logging, etc.).
 * <p>
 * <h2>How it works</h2>
 * <ol>
 *     <li>Every vanilla-like method is overridden.</li>
 *     <li>If a replacement callback is defined it is invoked, otherwise the
 *     original {@code super.*} implementation is used.</li>
 *     <li>Convenience {@code super_*} methods are exposed which wrap the original
 *     implementation in a {@link Runnable}.  These are helpful when the callback
 *     still wants to call the base implementation after doing its own work.</li>
 * </ol>
 * <p>
 * <b>Thread-safety:</b> identical to {@link ItemStackHandler}.  No additional
 * synchronisation is introduced.
 */
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
    FunctionalItemStackHandler() {
        super();
    }

    /**
     * Creates a handler with the specified number of empty slots
     * (mirrors {@link ItemStackHandler#ItemStackHandler(int)}).
     *
     * @param slots number of slots to create
     */
    FunctionalItemStackHandler(int slots) {
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
    @Nullable public SetSizeFunction            setSizeFunction;
    /**
     * Callback for {@link #setStackInSlot(int, ItemStack)}.
     */
    @Nullable public SetStackInSlotFunction     setStackInSlotFunction;
    /**
     * Callback for {@link #getSlots()}.
     */
    @Nullable public GetSlotsFunction           getSlotsFunction;
    /**
     * Callback for {@link #getStackInSlot(int)}.
     */
    @Nullable public GetStackInSlotFunction     getStackInSlotFunction;
    /**
     * Callback for {@link #insertItem(int, ItemStack, boolean)}.
     */
    @Nullable public InsertItemFunction         insertItemFunction;
    /**
     * Callback for {@link #extractItem(int, int, boolean)}.
     */
    @Nullable public ExtractItemFunction        extractItemFunction;
    /**
     * Callback for {@link #getSlotLimit(int)}.
     */
    @Nullable public GetSlotLimitFunction       getSlotLimitFunction;
    /**
     * Callback for {@link #isItemValid(int, ItemStack)}.
     */
    @Nullable public IsItemValidFunction        isItemValidFunction;
    /**
     * Callback for {@link #serializeNBT()}.
     */
    @Nullable public SerializeNBTFunction       serializeNBTFunction;
    /**
     * Callback for {@link #deserializeNBT(CompoundTag)}.
     */
    @Nullable public DeserializeNBTFunction     deserializeNBTFunction;
    /**
     * Callback for {@link #onLoad()} (called once when the game world loads).
     */
    @Nullable public OnLoadFunction             onLoadFunction;
    /**
     * Callback for {@link #onContentsChanged(int)}.
     */
    @Nullable public OnContentsChangedFunction  onContentsChangedFunction;
    /**
     * Callback for {@link #getStackLimit(int, ItemStack)} (protected helper).
     */
    @Nullable public GetStackLimitFunction      getStackLimitFunction;

    /* ======================================================================
     * Fluent setter helpers
     * ====================================================================== */

    /**
     * Registers a replacement for {@link #setSize(int)}.
     *
     * @param function user supplied lambda
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_setSize(SetSizeFunction function) {
        return set_setSize(function, false);
    }
    
    /**
     * Registers a replacement for {@link #setSize(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_setSize(SetSizeFunction function, boolean override) {
        if (!override && this.setSizeFunction != null) {
            throw new IllegalStateException("SetSizeFunction is already set. To override you must call it with the override flag.");
        }
        this.setSizeFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #setStackInSlot(int, ItemStack)}.
     */
    public FunctionalItemStackHandler set_setStackInSlot(SetStackInSlotFunction function) {
        return set_setStackInSlot(function, false);
    }
    
    /**
     * Registers a replacement for {@link #setStackInSlot(int, ItemStack)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_setStackInSlot(SetStackInSlotFunction function, boolean override) {
        if (!override && this.setStackInSlotFunction != null) {
            throw new IllegalStateException("SetStackInSlotFunction is already set. To override you must call it with the override flag.");
        }
        this.setStackInSlotFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #getSlots()}.
     */
    public FunctionalItemStackHandler set_getSlots(GetSlotsFunction function) {
        return set_getSlots(function, false);
    }
    
    /**
     * Registers a replacement for {@link #getSlots()} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_getSlots(GetSlotsFunction function, boolean override) {
        if (!override && this.getSlotsFunction != null) {
            throw new IllegalStateException("GetSlotsFunction is already set. To override you must call it with the override flag.");
        }
        this.getSlotsFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #getStackInSlot(int)}.
     */
    public FunctionalItemStackHandler set_getStackInSlot(GetStackInSlotFunction function) {
        return set_getStackInSlot(function, false);
    }
    
    /**
     * Registers a replacement for {@link #getStackInSlot(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_getStackInSlot(GetStackInSlotFunction function, boolean override) {
        if (!override && this.getStackInSlotFunction != null) {
            throw new IllegalStateException("GetStackInSlotFunction is already set. To override you must call it with the override flag.");
        }
        this.getStackInSlotFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #insertItem(int, ItemStack, boolean)}.
     */
    public FunctionalItemStackHandler set_insertItem(InsertItemFunction function) {
        return set_insertItem(function, false);
    }
    
    /**
     * Registers a replacement for {@link #insertItem(int, ItemStack, boolean)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_insertItem(InsertItemFunction function, boolean override) {
        if (!override && this.insertItemFunction != null) {
            throw new IllegalStateException("InsertItemFunction is already set. To override you must call it with the override flag.");
        }
        this.insertItemFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #extractItem(int, int, boolean)}.
     */
    public FunctionalItemStackHandler set_extractItem(ExtractItemFunction function) {
        return set_extractItem(function, false);
    }
    
    /**
     * Registers a replacement for {@link #extractItem(int, int, boolean)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_extractItem(ExtractItemFunction function, boolean override) {
        if (!override && this.extractItemFunction != null) {
            throw new IllegalStateException("ExtractItemFunction is already set. To override you must call it with the override flag.");
        }
        this.extractItemFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #getSlotLimit(int)}.
     */
    public FunctionalItemStackHandler set_getSlotLimit(GetSlotLimitFunction function) {
        return set_getSlotLimit(function, false);
    }
    
    /**
     * Registers a replacement for {@link #getSlotLimit(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_getSlotLimit(GetSlotLimitFunction function, boolean override) {
        if (!override && this.getSlotLimitFunction != null) {
            throw new IllegalStateException("GetSlotLimitFunction is already set. To override you must call it with the override flag.");
        }
        this.getSlotLimitFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #isItemValid(int, ItemStack)}.
     */
    public FunctionalItemStackHandler set_isItemValid(IsItemValidFunction function) {
        return set_isItemValid(function, false);
    }
    
    /**
     * Registers a replacement for {@link #isItemValid(int, ItemStack)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_isItemValid(IsItemValidFunction function, boolean override) {
        if (!override && this.isItemValidFunction != null) {
            throw new IllegalStateException("IsItemValidFunction is already set. To override you must call it with the override flag.");
        }
        this.isItemValidFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #serializeNBT()}.
     */
    public FunctionalItemStackHandler set_serializeNBT(SerializeNBTFunction function) {
        return set_serializeNBT(function, false);
    }
    
    /**
     * Registers a replacement for {@link #serializeNBT()} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_serializeNBT(SerializeNBTFunction function, boolean override) {
        if (!override && this.serializeNBTFunction != null) {
            throw new IllegalStateException("SerializeNBTFunction is already set. To override you must call it with the override flag.");
        }
        this.serializeNBTFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #deserializeNBT(CompoundTag)}.
     */
    public FunctionalItemStackHandler set_deserializeNBT(DeserializeNBTFunction function) {
        return set_deserializeNBT(function, false);
    }
    
    /**
     * Registers a replacement for {@link #deserializeNBT(CompoundTag)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_deserializeNBT(DeserializeNBTFunction function, boolean override) {
        if (!override && this.deserializeNBTFunction != null) {
            throw new IllegalStateException("DeserializeNBTFunction is already set. To override you must call it with the override flag.");
        }
        this.deserializeNBTFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #onLoad()}.
     */
    public FunctionalItemStackHandler set_onLoad(OnLoadFunction function) {
        return set_onLoad(function, false);
    }
    
    /**
     * Registers a replacement for {@link #onLoad()} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_onLoad(OnLoadFunction function, boolean override) {
        if (!override && this.onLoadFunction != null) {
            throw new IllegalStateException("OnLoadFunction is already set. To override you must call it with the override flag.");
        }
        this.onLoadFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for {@link #onContentsChanged(int)}.
     */
    public FunctionalItemStackHandler set_onContentsChanged(OnContentsChangedFunction function) {
        return set_onContentsChanged(function, false);
    }
    
    /**
     * Registers a replacement for {@link #onContentsChanged(int)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_onContentsChanged(OnContentsChangedFunction function, boolean override) {
        if (!override && this.onContentsChangedFunction != null) {
            throw new IllegalStateException("OnContentsChangedFunction is already set. To override you must call it with the override flag.");
        }
        this.onContentsChangedFunction = function;
        return this;
    }
    
    /**
     * Registers a replacement for protected {@link #getStackLimit(int, ItemStack)}.
     */
    public FunctionalItemStackHandler set_getStackLimit(GetStackLimitFunction function) {
        return set_getStackLimit(function, false);
    }
    
    /**
     * Registers a replacement for protected {@link #getStackLimit(int, ItemStack)} with override option.
     *
     * @param function user supplied lambda
     * @param override if false and a function is already set, throws an error
     * @return {@code this} for call chaining
     */
    public FunctionalItemStackHandler set_getStackLimit(GetStackLimitFunction function, boolean override) {
        if (!override && this.getStackLimitFunction != null) {
            throw new IllegalStateException("GetStackLimitFunction is already set. To override you must call it with the override flag.");
        }
        this.getStackLimitFunction = function;
        return this;
    }

    /* ======================================================================
     * Convenience wrappers exposing the original behaviour as Runnable
     * ====================================================================== */

    public void super_setSize(int size) { super.setSize(size); }
    public void super_setStackInSlot(int slot, @NotNull ItemStack stack) { super.setStackInSlot(slot, stack); }
    public int super_getSlots() { return super.getSlots(); }
    public @NotNull ItemStack super_getStackInSlot(int slot) { return super.getStackInSlot(slot); }
    public @NotNull ItemStack super_insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return super.insertItem(slot, stack, simulate); }
    public @NotNull ItemStack super_extractItem(int slot, int amount, boolean simulate) { return super.extractItem(slot, amount, simulate); }
    public int super_getSlotLimit(int slot) { return super.getSlotLimit(slot); }
    public boolean super_isItemValid(int slot, @NotNull ItemStack stack) { return super.isItemValid(slot, stack); }
    public CompoundTag super_serializeNBT() { return super.serializeNBT(); }
    public void super_deserializeNBT(@NotNull CompoundTag nbt) { super.deserializeNBT(nbt); }
    public void super_onLoad() { super.onLoad(); }
    public void super_onContentsChanged(int slot) { super.onContentsChanged(slot); }
    public int super_getStackLimit(int slot, @NotNull ItemStack stack) { return super.getStackLimit(slot, stack); }
    
    /* ======================================================================
     * Function status checker methods
     * ====================================================================== */
    
    /**
     * Checks if {@link #setSizeFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_setSize() {
        return this.setSizeFunction != null;
    }
    
    /**
     * Checks if {@link #setStackInSlotFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_setStackInSlot() {
        return this.setStackInSlotFunction != null;
    }
    
    /**
     * Checks if {@link #getSlotsFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_getSlots() {
        return this.getSlotsFunction != null;
    }
    
    /**
     * Checks if {@link #getStackInSlotFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_getStackInSlot() {
        return this.getStackInSlotFunction != null;
    }
    
    /**
     * Checks if {@link #insertItemFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_insertItem() {
        return this.insertItemFunction != null;
    }
    
    /**
     * Checks if {@link #extractItemFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_extractItem() {
        return this.extractItemFunction != null;
    }
    
    /**
     * Checks if {@link #getSlotLimitFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_getSlotLimit() {
        return this.getSlotLimitFunction != null;
    }
    
    /**
     * Checks if {@link #isItemValidFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_isItemValid() {
        return this.isItemValidFunction != null;
    }
    
    /**
     * Checks if {@link #serializeNBTFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_serializeNBT() {
        return this.serializeNBTFunction != null;
    }
    
    /**
     * Checks if {@link #deserializeNBTFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_deserializeNBT() {
        return this.deserializeNBTFunction != null;
    }
    
    /**
     * Checks if {@link #onLoadFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_onLoad() {
        return this.onLoadFunction != null;
    }
    
    /**
     * Checks if {@link #onContentsChangedFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_onContentsChanged() {
        return this.onContentsChangedFunction != null;
    }
    
    /**
     * Checks if {@link #getStackLimitFunction} has been set.
     *
     * @return {@code true} if the function is set, {@code false} otherwise
     */
    public boolean isSet_getStackLimit() {
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
    public void setSize(int size) {
        if (setSizeFunction == null) {
            super.setSize(size);
            return;
        }
        setSizeFunction.setSize(size);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #setStackInSlotFunction} when present.</p>
     */
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
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
    public int getSlots() {
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
    public ItemStack getStackInSlot(int slot) {
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
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
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
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return (extractItemFunction == null) ? super.extractItem(slot, amount, simulate)
                                             : extractItemFunction.extractItem(slot, amount, simulate);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #getSlotLimitFunction} when present.</p>
     */
    @Override
    public int getSlotLimit(int slot) {
        return (getSlotLimitFunction == null) ? super.getSlotLimit(slot)
                                              : getSlotLimitFunction.getSlotLimit(slot);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #getStackLimitFunction} when present.</p>
     */
    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return (getStackLimitFunction == null) ? super.getStackLimit(slot, stack)
                                               : getStackLimitFunction.getStackLimit(slot, stack);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #isItemValidFunction} when present.</p>
     */
    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
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
    public CompoundTag serializeNBT() {
        return (serializeNBTFunction == null) ? super.serializeNBT()
                                              : serializeNBTFunction.serializeNBT();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #deserializeNBTFunction} when present.</p>
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
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
    protected void onLoad() {
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
    protected void onContentsChanged(int slot) {
        if (onContentsChangedFunction == null) {
            super.onContentsChanged(slot);
            return;
        }
        onContentsChangedFunction.onContentsChanged(slot);
    }
}
