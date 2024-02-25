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

    /**
     * Used for item tab ordering
     */
    public abstract Integer getTypeIndex();

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
     * A wrapper for the list of glyphs that cannot be used with this glyph. Parsed from configs.
     */
    public SpellPartConfigUtil.ComboLimits invalidCombinations = new SpellPartConfigUtil.ComboLimits(null);

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

    /**
     * A callback when the spell is canceled before this part had a chance to resolve.
     * Can be changed and uncanceled by modifying this context directly.
     * If isCanceled is not false, no more effects will resolve.
     * Use the currentIndex to determine where a spell was canceled.
     */
    public void onContextCanceled(SpellContext context) {
    }

    /**
     * The default cost generated for the config.
     * This should not be used directly for calculations, but as a helper for a recommended value.
     */
    protected abstract int getDefaultManaCost();

    /**
     * The actual cost of the spell part after config is applied.
     */
    public int getCastingCost() {
        return COST == null ? getDefaultManaCost() : COST.get();
    }

    public String getName() {
        return this.name;
    }

    public SpellTier getConfigTier(){
        return GLYPH_TIER == null ? defaultTier() : SpellTier.SPELL_TIER_MAP.get(GLYPH_TIER.get());
    }

    public SpellTier defaultTier() {
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
        GLYPH_TIER = builder.comment("The tier of the glyph").defineInRange("glyph_tier", defaultTier().value, 1, 99);
    }

    public boolean shouldShowInUnlock() {
        return isEnabled();
    }

    public boolean shouldShowInSpellBook() {
        return isEnabled();
    }

    public boolean isEnabled() {
        return ENABLED != null && ENABLED.get();
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

    // Augment limits only apply to cast forms and effects, but not augments.
    public SpellPartConfigUtil.AugmentCosts augmentCosts;

    /**
     * Registers the glyph_limits configuration entry for augmentation limits.
     */
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        this.augmentLimits = SpellPartConfigUtil.buildAugmentLimitsConfig(builder, defaults);
    }

    protected void buildAugmentCostOverrideConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        this.augmentCosts = SpellPartConfigUtil.buildAugmentCosts(builder, defaults);
    }
    /**
     * Registers the glyph_limits configuration entry for combo limits.
     */
    protected void buildInvalidCombosConfig(ForgeConfigSpec.Builder builder, Set<ResourceLocation> defaults) {
        this.invalidCombinations = SpellPartConfigUtil.buildInvalidCombosConfig(builder, defaults);
    }

    /**
     * Override this method to provide defaults for the augmentation limits configuration.
     */
    protected Map<ResourceLocation, Integer> getDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        addDefaultAugmentLimits(defaults);
        return defaults;
    }

    /**
     * Adds default augment limits to the given map, used to generate the config.
     */
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {}

    protected void addAugmentCostOverrides(Map<ResourceLocation, Integer> defaults) {}

    protected Set<ResourceLocation> getDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        addDefaultInvalidCombos(defaults);
        return defaults;
    }

    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
    }

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
