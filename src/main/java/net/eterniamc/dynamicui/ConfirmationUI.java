package net.eterniamc.dynamicui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class ConfirmationUI extends DynamicUI {

    private static final int DENY = 2;
    private static final int CONFIRM = 6;

    public String getTitle() {
        return "Are you sure you want to do this?";
    }

    @Override
    public void generateInventory() {
        inventory = createInventory(getTitle(), 1);
    }

    @Override
    public void loadListeners() {
        addListener(DENY, (player, event) -> {
            close();
            onReject(player);
        });
        addListener(CONFIRM, (player, event) -> {
            close();
            onAccept(player);
        });
    }

    @Override
    public void render() {
        super.render();

        ItemStack deny = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.RED.getMetadata());
        deny.setStackDisplayName(TextFormatting.RED + "Deny");
        setItem(DENY, deny);

        ItemStack confirm = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.GREEN.getMetadata());
        confirm.setStackDisplayName(TextFormatting.GREEN + "Confirm");
        setItem(CONFIRM, confirm);
    }

    public abstract void onAccept(EntityPlayerMP player);

    public void onReject(EntityPlayerMP player) {

    }
}
