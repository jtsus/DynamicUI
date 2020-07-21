package net.eterniamc.dynamicui;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Set;

public enum InterfaceController {
    INSTANCE;

    @Getter
    private final Set<DynamicUI> activeInterfaces = Sets.newConcurrentHashSet();

    public void initialize() {
    }

    public void onInventoryClick(ClickAction event, EntityPlayerMP player) {
        for (DynamicUI ui : activeInterfaces) {
            EntityPlayerMP entity = ui.getPlayer();
            if (entity.getUniqueID().equals(player.getUniqueID())) {
                // We found the interface, now cancel the event and find the button they clicked.
                event.setCancelled(true);

                if (!ui.isIntractable()) {
                    continue;
                }

                event.setCancelled(true);

                //They clicked in the player inventory
                if (event.getSlotId() > ui.getInventory().getSizeInventory()) {
                    if (ui.getPlayerInventoryListener() != null) {
                        ui.getPlayerInventoryListener().onClick(player, event);
                        return;
                    }
                } else {
                    //They clicked on one of the specific slot listeners
                    ClickListener listener = ui.getListeners().get(event.getSlotId());
                    if (listener != null) {
                        listener.onClick(player, event);
                        return;
                    }
                }

                return;
            }
        }
    }

    public void onInventoryClose(EntityPlayerMP player) {
        for (DynamicUI ui : getActiveInterfaces()) {
            if (ui.isValid() && ui.getPlayer().equals(player)) {
                getActiveInterfaces().remove(ui);
                if (!ui.isCloseSilently() && !ui.onClose()) {
                    ui.open(player);
                } else {
                    FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> ui.afterClose(player));
                }
                return;
            }
        }
    }
}
