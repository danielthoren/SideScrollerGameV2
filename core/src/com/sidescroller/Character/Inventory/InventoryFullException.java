package com.sidescroller.Character.Inventory;

/**
 * Exception to be thrown when the inventory is full
 */
public class InventoryFullException extends Exception {
    public InventoryFullException(String message) {
        super(message);
    }
}
