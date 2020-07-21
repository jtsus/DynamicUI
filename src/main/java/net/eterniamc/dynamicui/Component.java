package net.eterniamc.dynamicui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eterniamc.dynamicui.implementation.InventoryUI;

@RequiredArgsConstructor
@Getter
public abstract class Component {

    private final int origin;

    public void loadListeners(DynamicUI ui) {

    }

    public abstract void render(InventoryUI inventory);

}
