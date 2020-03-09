package com.hollingsworth.craftedmagic.api.util;

import com.hollingsworth.craftedmagic.api.ISpellBonus;
import com.hollingsworth.craftedmagic.api.item.AbstractAugmentItem;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.spell.AbstractSpellPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SpellRecipeUtil {

    public static ArrayList<AbstractAugment> getAugments(ArrayList<AbstractSpellPart> spell_recipe, int startPosition, @Nullable LivingEntity caster){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        for(int j = startPosition + 1; j < spell_recipe.size(); j++){
            AbstractSpellPart next_spell = spell_recipe.get(j);
            if(next_spell instanceof AbstractAugment){
                augments.add((AbstractAugment) next_spell);
            }else{
                break;
            }
        }
        // Add augment bonuses from equipment
        if(caster != null)
            augments.addAll(getEquippedAugments(caster));
        return augments;
    }

    /**
     * Returns the list of augments that come from equipment
     */
    public static List<AbstractAugment> getEquippedAugments(@Nonnull LivingEntity caster){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        CuriosUtil.getAllWornItems(caster).ifPresent(e ->{
            for(int i = 0; i < e.getSlots(); i++){
                Item item = e.getStackInSlot(i).getItem();
                if(item instanceof ISpellBonus)
                    augments.addAll(((ISpellBonus) item).getList());
            }

        });
        caster.getArmorInventoryList().forEach(itemStack -> {
            if(itemStack.getItem() instanceof ISpellBonus)
                augments.addAll(((ISpellBonus) itemStack.getItem()).getList());
        });
        return augments;
    }
}
