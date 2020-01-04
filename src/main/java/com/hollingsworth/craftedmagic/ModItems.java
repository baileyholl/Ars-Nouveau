package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.items.Spell;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    @GameRegistry.ObjectHolder("modtut:spell_book")
    public static Spell spell;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        spell.initModel();

    }
}
