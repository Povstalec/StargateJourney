package net.povstalec.sgjourney.common.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * An inventory wrapper that protects.
 * {@link IsProtected} predicate returns {@code true}.
 * When unprotected, every call is delegated to the underlying
 * {@link #backed} handler.
 */
public class ProtectedItemStackHandler extends ItemStackHandler implements IProtectedItemStackHandler
{
    private final FunctionalItemStackHandler backed;
    private final IsProtected isProtected;

    public ProtectedItemStackHandler(int slots, IsProtected isProtected) {
        super(slots);
        this.backed       = new FunctionalItemStackHandler(slots);
        this.isProtected  = isProtected;
    }

    protected boolean isProtected() {
        return isProtected.isProtected();
    }

    @Override
    public FunctionalItemStackHandler unprotect() {
        return backed;
    }

    /* ------------------------------------------------------------------ */
    /* Persistence                                                        */
    /* ------------------------------------------------------------------ */

    @Override
    public @NotNull CompoundTag serializeNBT() {
        return backed.serializeNBT();
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        backed.deserializeNBT(nbt);
    }

    /* ------------------------------------------------------------------ */
    /* Mutating operations                                                */
    /* ------------------------------------------------------------------ */

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (isProtected()) {
            return;
        }
        backed.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (isProtected() || !super.isItemValid(slot, stack)) {
            return stack;
        }
        return backed.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return isProtected() ? ItemStack.EMPTY
                             : backed.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlots() {
        return isProtected() ? super.getSlots()
                             : backed.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return isProtected() ? ItemStack.EMPTY
                             : backed.getStackInSlot(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        return backed.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isProtected() ? false
                             : backed.isItemValid(slot, stack);
    }
}
