package com.hollingsworth.arsnouveau.api.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;

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
    // Final mana cost
    public int getAdjustedManaCost(List<AbstractAugment> augmentTypes){
        int cost = getConfigCost();
        for(AbstractAugment a: augmentTypes){
            if(a instanceof AugmentDampen && !dampenIsAllowed()){
                continue;
            }
            cost += a.getConfigCost();
        }
        return Math.max(cost, 0);
    }

    public int getConfigCost(){
        return COST == null ? getManaCost() : COST.get();
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

    public static int getBuffCount(List<AbstractAugment> augments, Class<? extends AbstractSpellPart> spellClass){
        return (int) augments.stream().filter(spellClass::isInstance).count();
    }

    public boolean hasBuff(List<AbstractAugment> augments, Class spellClass){
        return getBuffCount(augments, spellClass) > 0;
    }

    public int getAmplificationBonus(List<AbstractAugment> augmentTypes){
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
        jsonobject.addProperty("category", "spells_"+(getTier().ordinal() + 1));
        jsonobject.addProperty("sortnum", this instanceof AbstractCastMethod ? 1 : this instanceof AbstractEffect ? 2 : 3);
        JsonArray jsonArray = new JsonArray();
        JsonObject descPage = new JsonObject();
        descPage.addProperty("type", "text");
        descPage.addProperty("text","ars_nouveau.glyph_desc." + tag);

        JsonObject infoPage = new JsonObject();
        infoPage.addProperty("type", "glyph_recipe");
        infoPage.addProperty("recipe", ArsNouveau.MODID + ":" + "glyph_" + this.tag);
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
    public ForgeConfigSpec CONFIG;
    public ForgeConfigSpec.IntValue COST;
    public ForgeConfigSpec.BooleanValue ENABLED;
    public ForgeConfigSpec.BooleanValue STARTER_SPELL;

    public void buildConfig(ForgeConfigSpec.Builder builder){
        builder.comment("General settings").push("general");
        ENABLED = builder.comment("Is Enabled?").define("enabled", true);
        COST = builder.comment("Cost").defineInRange("cost", getManaCost(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        STARTER_SPELL = builder.comment("Is Starter Glyph?").define("starter", defaultedStarterGlyph());
    }

    // Default value for the starter spell config
    public boolean defaultedStarterGlyph(){
        return false;
    }

    public String getItemID(){
        return "glyph_" + this.getTag();
    }

    public String getBookDescription(){
        return "";
    }

    public String getLocalizationKey() {
        return "ars_nouveau.glyph_name." + tag;
    }

    public String getLocaleName(){
        return new TranslationTextComponent(getLocalizationKey()).getString();
    }
}
