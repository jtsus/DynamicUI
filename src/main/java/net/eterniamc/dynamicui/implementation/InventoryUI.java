package net.eterniamc.dynamicui.implementation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Getter
@Setter
@ToString
public class InventoryUI extends InventoryBasic {
    private final NonNullList<ItemStack> content;

    public InventoryUI(String title, boolean customName, int slotCount) {
        super(title, customName, slotCount);

        content = ObfuscationReflectionHelper.getPrivateValue(InventoryBasic.class, this, "field_70482_c");
    }

}
