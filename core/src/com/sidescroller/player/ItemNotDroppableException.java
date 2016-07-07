package com.sidescroller.player;

/**
 * Throw this when the item in question cannot be dropped.
 */
public class ItemNotDroppableException extends Exception {
    public ItemNotDroppableException(String message) {
        super(message);
    }
}
