package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ISpellBonus;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpellRecipeUtil {

    /**
     * Returns the list of augments that come from equipment
     */
    public static List<AbstractAugment> getEquippedAugments(@Nonnull LivingEntity caster){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        CuriosUtil.getAllWornItems(caster).ifPresent(e ->{
            for(int i = 0; i < e.getSlots(); i++){
                Item item = e.getStackInSlot(i).getItem();
                if(item instanceof ISpellBonus)
                    augments.addAll(((ISpellBonus) item).getList( e.getStackInSlot(i)));
            }

        });
        caster.getArmorSlots().forEach(itemStack -> {
            if(itemStack.getItem() instanceof ISpellBonus)
                augments.addAll(((ISpellBonus) itemStack.getItem()).getList( itemStack));
        });
        return augments;
    }

    @Deprecated // Marked for removal for Spell object methods.
    public static ArrayList<AbstractSpellPart> getSpellsFromString(String spellString){
        List<String> spellStrings = Arrays.asList(spellString.split(","));
        ArrayList<AbstractSpellPart> spells = new ArrayList<>();

        spellStrings.forEach(s->{
            Optional<AbstractSpellPart> spell =  ArsNouveauAPI.getInstance().getSpell_map().values().stream().filter(sp -> sp.getTag().equals(s.trim())).findFirst();
            spell.ifPresent(spells::add);
        });
        return spells;
    }

    /**
     * Parses the NBT stored string, which is stored as an array of spell IDs. ex: [touch, harm, , , ,]
     */
    @Deprecated // Marked for removal for Spell object methods.
    public static List<AbstractSpellPart> getSpellsFromTagString(String recipeStr){
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        if (recipeStr.length() <= 3) // Account for empty strings and '[,]'
            return recipe;
        String[] recipeList = recipeStr.substring(1, recipeStr.length() - 1).split(",");
        for(String id : recipeList){
            if (ArsNouveauAPI.getInstance().getSpell_map().containsKey(id.trim()))
                recipe.add(ArsNouveauAPI.getInstance().getSpell_map().get(id.trim()));
        }
        return recipe;
    }

    @Deprecated // Marked for removal for Spell object methods.
    public static String serializeForNBT(List<AbstractSpellPart> abstractSpellPart){
        List<String> tags = new ArrayList<>();
        for(AbstractSpellPart slot : abstractSpellPart){
            tags.add(slot.tag);
        }
        return tags.toString();
    }
    @Deprecated // Marked for removal
    public static String getDisplayString(List<AbstractSpellPart> abstractSpellPart){
        return new Spell(abstractSpellPart).getDisplayString();
    }
}
