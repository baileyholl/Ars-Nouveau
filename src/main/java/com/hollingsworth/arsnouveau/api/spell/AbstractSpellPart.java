package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.util.SpellPartConfigUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractSpellPart implements Comparable<AbstractSpellPart> {

    private final ResourceLocation registryName;
    public String name;
    public Glyph glyphItem;

    /*ID for NBT data and SpellManager#spellList*/
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

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

    /**
     * A list of glyphs that cannot be used with this glyph.
     */
    public Set<ResourceLocation> invalidCombinations = ConcurrentHashMap.newKeySet();

    public AbstractSpellPart(String registryName, String name) {
        this(new ResourceLocation(ArsNouveau.MODID, registryName), name);
    }

    public AbstractSpellPart(ResourceLocation registryName, String name) {
        this.registryName = registryName;
        this.name = name;
        for (SpellSchool spellSchool : getSchools()) {
            spellSchool.addSpellPart(this);
            spellSchools.add(spellSchool);
        }
        compatibleAugments.addAll(getCompatibleAugments());
    }

    public abstract int getDefaultManaCost();

    public int getCastingCost() {
        return COST == null ? getDefaultManaCost() : COST.get();
    }

    public String getName() {
        return this.name;
    }

    public SpellTier getConfigTier(){
        return GLYPH_TIER == null ? SpellTier.ONE : SpellTier.SPELL_TIER_MAP.get(GLYPH_TIER.get());
    }

    @Deprecated() // TODO: 1.20 Rename to default tier
    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    /**
     * Cache the glyph item here because of registry freezing.
     */
    public Glyph getGlyph() {
        if (glyphItem == null) {
            glyphItem = new Glyph(this);
        }
        return this.glyphItem;
    }

    /**
     * Returns the set of augments that this spell part can be enhanced by.
     * Mods should use {@link AbstractSpellPart#compatibleAugments} for addon-supported augments.
     *
     * @see AbstractSpellPart#augmentSetOf(AbstractAugment...) for easy syntax to make the Set.
     * This should not be accessed directly, but can be overridden.
     */
    protected abstract@NotNull Set<AbstractAugment> getCompatibleAugments();

    /**
     * Syntax support to easily make a set for {@link AbstractSpellPart#getCompatibleAugments()}
     */
    protected Set<AbstractAugment> augmentSetOf(AbstractAugment... augments) {
        return setOf(augments);
    }

    /**
     * A helper for mods to add schools.
     * Mods should use {@link AbstractSpellPart#spellSchools} to get the addon-supported list.
     */
    protected@NotNull Set<SpellSchool> getSchools() {
        return setOf();
    }

    protected <T> Set<T> setOf(T... list) {
        return Set.of(list);
    }

    @Override
    public int compareTo(AbstractSpellPart o) {
        return this.getConfigTier().value - o.getConfigTier().value;
    }

    public Component getBookDescLang() {
        return Component.translatable(getRegistryName().getNamespace() + ".glyph_desc." + getRegistryName().getPath());
    }

    public @Nullable ForgeConfigSpec CONFIG;
    public @Nullable ForgeConfigSpec.IntValue COST;
    public @Nullable ForgeConfigSpec.BooleanValue ENABLED;
    public @Nullable ForgeConfigSpec.BooleanValue STARTER_SPELL;
    public @Nullable ForgeConfigSpec.IntValue PER_SPELL_LIMIT;
    public @Nullable ForgeConfigSpec.IntValue GLYPH_TIER;


    public void buildConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("General settings").push("general");
        ENABLED = builder.comment("Is Enabled?").define("enabled", true);
        COST = builder.comment("Cost").defineInRange("cost", getDefaultManaCost(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        STARTER_SPELL = builder.comment("Is Starter Glyph?").define("starter", defaultedStarterGlyph());
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        GLYPH_TIER = builder.comment("The tier of the glyph").defineInRange("glyph_tier", getTier().value, 1, 99);
    }

    /**
     * Returns the number of times that this glyph may be modified by the given augment.
     */
    public int getAugmentLimit(ResourceLocation augmentTag) {
        if (augmentLimits == null) {
            return Integer.MAX_VALUE;
        } else {
            return augmentLimits.getAugmentLimit(augmentTag);
        }
    }

    // Augment limits only apply to cast forms and effects, but not augments.
    public SpellPartConfigUtil.AugmentLimits augmentLimits;

    /**
     * Registers the glyph_limits configuration entry for augmentation limits.
     */
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        this.augmentLimits = SpellPartConfigUtil.buildAugmentLimitsConfig(builder, defaults);
    }

    /**
     * Override this method to provide defaults for the augmentation limits configuration.
     */
    protected Map<ResourceLocation, Integer> getDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        addDefaultAugmentLimits(defaults);
        return defaults;
    }

    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {}

    // Default value for the starter spell config
    public boolean defaultedStarterGlyph() {
        return false;
    }

    /**
     * Used for datagen lang ONLY.
     */
    public String getBookDescription() {
        return "";
    }

    public String getLocalizationKey() {
        return registryName.getNamespace() + ".glyph_name." + registryName.getPath();
    }

    public String getLocaleName() {
        return Component.translatable(getLocalizationKey()).getString();
    }
}
