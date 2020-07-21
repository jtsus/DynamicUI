package net.eterniamc.dynamicui;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.eterniamc.dynamicui.implementation.ContainerUI;
import net.eterniamc.dynamicui.implementation.InventoryUI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.Map;

@Getter
public abstract class DynamicUI {
    /** Sponge inventory reference */
    protected InventoryUI inventory;

    /** Sponge players viewing the UI */
    protected EntityPlayerMP player;

    /** Custom slot listeners */
    private final Map<Integer, ClickListener> listeners = Maps.newHashMap();

    /**
     * Listener for when player clicks there own inventory
     * if null no listeners will be called
     */
    @Setter
    private ClickListener playerInventoryListener;

    /** List of other DynamicUIs that should update as this one does */
    private final List<DynamicUI> responders = Lists.newArrayList();

    private final List<Component> components = Lists.newArrayList();

    @Setter
    private boolean intractable = true;

    private boolean built = false;

    @Setter
    private boolean closeSilently = false;

    public void build() {
        generateInventory();
        render();
        InterfaceController.INSTANCE.getActiveInterfaces().add(this);
        built = true;
    }

    public void open(EntityPlayerMP player) {
        open(player, !built);
    }

    public void openInventory(EntityPlayerMP player) {
        closeSilently = true;
        Container container = new ContainerUI(player, inventory, this);
        container.windowId = 1;
        // Sends packet to player to open the container window.
        SPacketOpenWindow openWindow = new SPacketOpenWindow(
                container.windowId,
                "minecraft:container",
                inventory.getDisplayName(),
                inventory.getSizeInventory()
        );
        player.connection.sendPacket(openWindow);
        player.openContainer = container;
        player.currentWindowId = container.windowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, container));
        closeSilently = false;
    }

    public void open(EntityPlayerMP player, boolean build) {
        try {
            Preconditions.checkArgument(FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread(), "Inventories must be opened on the main thread!");
            player.closeScreen();

            InterfaceController.INSTANCE.getActiveInterfaces().add(this);

            if (this.player != null) {
                close();
            }

            this.player = player;

            initialize();

            if (build) {
                build();
            }

            openInventory(player);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Something went very wrong and the UI was unable to be opened"));
            InterfaceController.INSTANCE.getActiveInterfaces().remove(this);
            player.closeScreen();
            onClose();
        }
    }

    public InventoryUI createInventory(String title, int rows) {
        return new InventoryUI(title, true, rows * 9);
    }

    public void addListener(int slot, ClickListener listener) {
        listeners.put(slot, listener);
    }

    public void addResponder(DynamicUI ui) {
        responders.add(ui);
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void initialize() {

    }

    public abstract void generateInventory();

    public void loadListeners() {
        getListeners().clear();

        for (Component component : components) {
            component.loadListeners(this);
        }
    }

    /**
     * This is required if there are two UIs that listen to each other's updates.
     */
    public boolean shouldUpdate() {
        return true;
    }

    public void render() {
        loadListeners();

        for (Component component : components) {
            component.render(inventory);
        }

        for (DynamicUI responder : responders) {
            if (responder.isValid()) {
                if (responder.shouldUpdate()) {
                    responder.render();
                }
            }
        }
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    public void setTitle(String title) {
        inventory.setCustomName(title);

        SPacketOpenWindow openWindow = new SPacketOpenWindow(
                player.currentWindowId,
                "minecraft:container",
                inventory.getDisplayName(),
                inventory.getSizeInventory()
        );

        player.connection.sendPacket(openWindow);
    }

    public void close() {
        if (player != null) {
            player.closeScreen();
        }
    }

    public boolean onClose() {
        for (DynamicUI responder : responders) {
            responder.close();
        }
        player = null;
        InterfaceController.INSTANCE.getActiveInterfaces().remove(this);

        return true;
    }

    public void afterClose(EntityPlayerMP player) {

    }

    public boolean isValid() {
        return player != null;
    }
}
