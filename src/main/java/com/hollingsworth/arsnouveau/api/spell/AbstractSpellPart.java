package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.util.SpellPartConfigUtil;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractSpellPart implements Comparable<AbstractSpellPart> {

    private String id;
    public String name;
    public Glyph glyphItem;
    /*ID for NBT data and SpellManager#spellList*/
    public String getId(){
        return this.id;
    }

    public String getIcon(){return this.id + ".png";}

    /**
     * The list of schools that apply to this spell.
     * Addons should add and access this list directly.
     */
    public List<SpellSchool> spellSchools = new CopyOnWriteArrayList<>();
    /**
     * The list of augments that apply to a form or effect.
     * Addons should add and access this set directly.
     */
    public Set<AbstractAugment> compatibleAugments = ConcurrentHashMap.newKeySet();

    public AbstractSpellPart(String id, String name){
        this.id = id;
        this.name = name;
        for(SpellSchool spellSchool : getSchools()){
            spellSchool.addSpellPart(this);
            spellSchools.add(spellSchool);
        }
        compatibleAugments.addAll(getCompatibleAugments());
    }

    public abstract int getDefaultManaCost();

    public int getConfigCost(){
        return COST == null ? getDefaultManaCost() : COST.get();
    }

    public String getName(){return this.name;}

    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    public Glyph getGlyph() {
        if(glyphItem == null){
            glyphItem = new Glyph(ArsNouveauAPI.getInstance().getSpellRegistryName(this.getId()), this);
        }
        return this.glyphItem;
    }

    /**
     * Returns the set of augments that this spell part can be enhanced by.
     * Mods should use {@link AbstractSpellPart#compatibleAugments} for addon-supported augments.
     * @see AbstractSpellPart#augmentSetOf(AbstractAugment...) for easy syntax to make the Set.
     * @deprecated This will be set to protected in a future update.
     * This should not be accessed directly, but can be overriden.
     */
    @Deprecated
    public abstract @Nonnull Set<AbstractAugment> getCompatibleAugments();

    /**
     * Syntax support to easily make a set for {@link AbstractSpellPart#getCompatibleAugments()}
     */
    protected Set<AbstractAugment> augmentSetOf(AbstractAugment... augments) {
        return setOf(augments);
    }

    /**
     * A helper for mods to add schools.
     * @deprecated This will be set to protected in the future.
     * Mods should use {@link AbstractSpellPart#spellSchools} to get the addon-supported list.
     */
    @Deprecated
    public @Nonnull Set<SpellSchool> getSchools(){
        return setOf();
    }

    protected <T> Set<T> setOf(T... list) {
        return Set.of(list);
    }

    @Override
    public int compareTo(AbstractSpellPart o) {
        return this.getTier().value - o.getTier().value;
    }

    public Component getBookDescLang(){
        return Component.translatable("ars_nouveau.glyph_desc." + getId());
    }

    // Can be null if addons do not create a config. PLEASE REGISTER THESE IN A CONFIG. See RegistryHelper
    public @Nullable ForgeConfigSpec CONFIG;
    public @Nullable ForgeConfigSpec.IntValue COST;
    public @Nullable ForgeConfigSpec.BooleanValue ENABLED;
    public @Nullable ForgeConfigSpec.BooleanValue STARTER_SPELL;
    public @Nullable ForgeConfigSpec.IntValue PER_SPELL_LIMIT;

    public void buildConfig(ForgeConfigSpec.Builder builder){
        builder.comment("General settings").push("general");
        ENABLED = builder.comment("Is Enabled?").define("enabled", true);
        COST = builder.comment("Cost").defineInRange("cost", getDefaultManaCost(), Integer.MIN_VALUE, Integer.MAX_VALUE);
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
    public SpellPartConfigUtil.AugmentLimits augmentLimits;

    /** Registers the glyph_limits configuration entry for augmentation limits. */
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<String, Integer> defaults) {
        this.augmentLimits = SpellPartConfigUtil.buildAugmentLimitsConfig(builder, defaults);
    }
    // TODO: Pass the map to the method param
    /** Override this method to provide defaults for the augmentation limits configuration. */
    protected Map<String, Integer> getDefaultAugmentLimits() {
        return new HashMap<>();
    }

    // Default value for the starter spell config
    public boolean defaultedStarterGlyph(){
        return false;
    }

    public boolean isRenderAsIcon() {
        return true;
    }

    public String getItemID(){
        return "glyph_" + this.getId();
    }

    public String getBookDescription(){
        return "";
    }

    public String getLocalizationKey() {
        return "ars_nouveau.glyph_name." + id;
    }

    public String getLocaleName(){
        return Component.translatable(getLocalizationKey()).getString();
    }
}
