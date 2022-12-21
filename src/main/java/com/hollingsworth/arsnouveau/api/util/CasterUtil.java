package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import net.minecraft.world.item.ItemStack;

public class CasterUtil {

    public static ISpellCaster getCaster(ItemStack stack) {
        if (stack.getItem() instanceof ICasterTool casterTool) {
            return casterTool.getSpellCaster(stack);
        }
        return new SpellCaster(stack);
    }
}
