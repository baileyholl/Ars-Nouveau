package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IProxy {

    void init();

    World getClientWorld();

}
