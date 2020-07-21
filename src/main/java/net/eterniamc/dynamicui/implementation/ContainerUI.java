package net.eterniamc.dynamicui.implementation;


import com.google.common.collect.Sets;
import lombok.Getter;
import net.eterniamc.dynamicui.ClickAction;
import net.eterniamc.dynamicui.DynamicUI;
import net.eterniamc.dynamicui.InterfaceController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import java.util.Set;

@Getter
public class ContainerUI extends ContainerChest {
    private static final Set<ClickType> refresh = Sets.newHashSet(ClickType.CLONE, ClickType.QUICK_MOVE, ClickType.SWAP, ClickType.THROW);

    private final DynamicUI represented;

    private final InventoryUI internalInventory;

    public ContainerUI(EntityPlayerMP player, InventoryUI internalInventory, DynamicUI ui) {
        super(player.inventory, internalInventory, player);
        this.represented = ui;
        this.internalInventory = internalInventory;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        playerIn.openContainer = playerIn.inventoryContainer;
        ((EntityPlayerMP) playerIn).currentWindowId = playerIn.openContainer.windowId;
        InterfaceController.INSTANCE.onInventoryClose((EntityPlayerMP) playerIn);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId < 0) {
            return ItemStack.EMPTY;
        }

        Slot slot = getSlot(slotId);

        ClickAction action = new ClickAction(slot, slotId, dragType, clickType);

        InterfaceController.INSTANCE.onInventoryClick(action, (EntityPlayerMP) player);

        if (action.isCancelled()) {
            if (refresh.contains(clickType) && represented.isValid()) {
                represented.open(represented.getPlayer());
            }
            return ItemStack.EMPTY;
        }

        return super.slotClick(slotId, dragType, clickType, player);
    }
}
