package net.eterniamc.dynamicui;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ClickListener {

    /** The event is by default cancelled. Can be renewed via the callback */
    void onClick(EntityPlayerMP player, ClickAction event);
}
