package com.hollingsworth.arsnouveau.api.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class AbstractSpellPart implements ISpellTier, Comparable<AbstractSpellPart> {

    public abstract int getManaCost();
    public String tag;
    public String name;
    /*Tag for NBT data and SpellManager#spellList*/
    public String getTag(){
        return this.tag;
    }

    public String getIcon(){return this.tag + ".png";}

    protected AbstractSpellPart(String tag, String name){
        this.tag = tag;
        this.name = name;
    }

    public int getAdjustedManaCost(ArrayList<AbstractAugment> augmentTypes){
        int cost = getManaCost();
        for(AbstractAugment a: augmentTypes){
            if(a instanceof AugmentDampen && !dampenIsAllowed()){
                continue;
            }
            cost += a.getManaCost();
        }
        return Math.max(cost, 0);
    }

    @Nullable
    public Item getCraftingReagent(){
        return null;
    }

    // Check for mana reduction exploit
    public boolean dampenIsAllowed(){
        return false;
    }

    public String getName(){return this.name;}

    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.ONE;
    }



    public int getBuffCount(ArrayList<AbstractAugment> augments, Class spellClass){
        return (int) augments.stream().filter(spellClass::isInstance).count();
    }

    public boolean hasBuff(ArrayList<AbstractAugment> augments, Class spellClass){
        return getBuffCount(augments, spellClass) > 0;
    }

    public int getAmplificationBonus(ArrayList<AbstractAugment> augmentTypes){
        return getBuffCount(augmentTypes, AugmentAmplify.class) - getBuffCount(augmentTypes, AugmentDampen.class);
    }

    @Override
    public int compareTo(AbstractSpellPart o) {
        return this.getTier().ordinal() - o.getTier().ordinal();
    }

    /**
     * Converts to a patchouli documentation page
     */
    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("name", this.getName());
        jsonobject.addProperty("icon", ArsNouveau.MODID + ":" + getItemID());
        jsonobject.addProperty("category", "spells");
        JsonArray jsonArray = new JsonArray();
        JsonObject descPage = new JsonObject();
        descPage.addProperty("type", "text");
        descPage.addProperty("text",this.getBookDescription());

        JsonObject infoPage = new JsonObject();
        infoPage.addProperty("type", "glyph_recipe");
        infoPage.addProperty("tier",this.getTier().name());

        String manaCost = this.getManaCost() < 20 ? "Low" : "Medium";
        manaCost = this.getManaCost() > 50 ? "High" : manaCost;
        infoPage.addProperty("mana_cost", manaCost);
        if(this.getCraftingReagent() != null){
            String clayType;
            if(this.getTier() == Tier.ONE){
                clayType = ItemsRegistry.magicClay.getRegistryName().toString();
            }else if(this.getTier() == Tier.TWO){
                clayType = ItemsRegistry.marvelousClay.getRegistryName().toString();
            }else{
                clayType = ItemsRegistry.mythicalClay.getRegistryName().toString();
            }
            infoPage.addProperty("clay_type", clayType);
            infoPage.addProperty("reagent", this.getCraftingReagent().getRegistryName().toString());
        }


        jsonArray.add(descPage);
        jsonArray.add(infoPage);
        jsonobject.add("pages", jsonArray);
        return jsonobject;
    }

    public String getItemID(){
        return "glyph_" + this.getTag();
    }

    protected String getBookDescription(){
        return "";
    }
}
