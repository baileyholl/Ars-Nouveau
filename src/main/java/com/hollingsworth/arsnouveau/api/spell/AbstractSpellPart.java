package com.hollingsworth.arsnouveau.api.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.util.SpellPartConfigUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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

    /**
     * Returns the set of augments that this spell part can be enhanced by.
     *
     * @see AbstractSpellPart#augmentSetOf(AbstractAugment...) for easy syntax to make the Set.
     */
    public abstract @Nonnull Set<AbstractAugment> getCompatibleAugments();

    /**
     * Syntax support to easily make a set for {@link AbstractSpellPart#getCompatibleAugments()}
     */
    protected Set<AbstractAugment> augmentSetOf(AbstractAugment... augments) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(augments)));
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
    public ForgeConfigSpec.IntValue PER_SPELL_LIMIT;

    public void buildConfig(ForgeConfigSpec.Builder builder){
        builder.comment("General settings").push("general");
        ENABLED = builder.comment("Is Enabled?").define("enabled", true);
        COST = builder.comment("Cost").defineInRange("cost", getManaCost(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        STARTER_SPELL = builder.comment("Is Starter Glyph?").define("starter", defaultedStarterGlyph());
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
    }

    /** Returns the number of times that this glyph may be modified by the given augment. */
    public int getAugmentLimit(String augmentTag) {
        if (augmentLimits == null) {
            return Integer.MAX_VALUE;
        } else {
            return augmentLimits.getAugmentLimit(augmentTag);
        }
    }

    // Augment limits only apply to cast forms and effects, but not augments.
    private SpellPartConfigUtil.AugmentLimits augmentLimits;

    /** Registers the glyph_limits configuration entry for augmentation limits. */
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<String, Integer> defaults) {
        this.augmentLimits = SpellPartConfigUtil.buildAugmentLimitsConfig(builder, defaults);
    }

    /** Override this method to provide defaults for the augmentation limits configuration. */
    protected Map<String, Integer> getDefaultAugmentLimits() {
        return new HashMap<>();
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
