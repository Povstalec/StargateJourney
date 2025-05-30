package net.povstalec.sgjourney.common.handlers;


public interface IProtectedItemStackHandler {
    FunctionalItemStackHandler unprotect();
    interface IsProtected {
        boolean isProtected();
    }
}
