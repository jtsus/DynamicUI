package net.eterniamc.dynamicui;

import lombok.*;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

@Data
public class ClickAction {

    @ToString.Exclude
    private final Slot slot;

    private final int slotId;

    private final int dragType;

    private final ClickType clickType;

    private boolean cancelled;

}
