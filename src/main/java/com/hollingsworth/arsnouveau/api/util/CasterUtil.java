package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CasterUtil {

    public static ISpellCaster getCaster(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof ICasterTool){
            return ((ICasterTool) item).getSpellCaster(stack);
        }
        return new SpellCaster(stack);
    }
}
