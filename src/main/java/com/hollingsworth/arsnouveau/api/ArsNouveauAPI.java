package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.mixin.PotionRecipeMixin;
import com.hollingsworth.arsnouveau.common.spell.validation.StandardSpellValidator;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.config.ANModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Main class of the Ars Nouveau API.
 * <p>
 * Obtain an instance with {@link ArsNouveauAPI#getInstance()}.
 * FOR ADDON AUTHORS: All registration should occur from YOUR MOD CONSTRUCTOR. BEFORE ITEMS REGISTER.
 * If you need to access data from other addons, use the FMLLoadingComplete event.
 */
public class ArsNouveauAPI {
    public static final ResourceLocation EMPTY_KEY = new ResourceLocation(ArsNouveau.MODID, "empty");

    /**
     * Map of all spells to be registered in the spell system
     * <p>
     * key: Unique spell ID. Please make this snake_case!
     * value: Associated glyph
     */
    private ConcurrentHashMap<ResourceLocation, AbstractSpellPart> spellpartMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<ResourceLocation, AbstractRitual> ritualMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<ResourceLocation, AbstractFamiliarHolder> familiarHolderMap = new ConcurrentHashMap<>();

    /**
     * Contains the list of glyph item instances.
     */
    private ConcurrentHashMap<ResourceLocation, Supplier<Glyph>> glyphItemMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<ResourceLocation, FamiliarScript> familiarScriptMap = new ConcurrentHashMap<>();

    /**
     * Contains the list of parchment item instances created during registration
     */
    private ConcurrentHashMap<ResourceLocation, RitualTablet> ritualParchmentMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<ResourceLocation, IScryer> scryerMap = new ConcurrentHashMap<>();

    private Set<RecipeType<? extends IEnchantingRecipe>> enchantingRecipeTypes = ConcurrentHashMap.newKeySet();

    private ConcurrentHashMap<ResourceLocation, SpellSound> spellSoundsRegistry = new ConcurrentHashMap<>();

    private ConcurrentHashMap<ResourceLocation, IPerk> perkMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<ResourceLocation, PerkItem> perkItemMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Item, IPerkProvider<ItemStack>> itemPerkProviderMap = new ConcurrentHashMap<>();
    /**
     * Validator to use when crafting a spell in the spell book.
     */
    private ISpellValidator craftingSpellValidator;
    /**
     * Validator to use when casting a spell.
     */
    private ISpellValidator castingSpellValidator;

    private List<IEnchantingRecipe> enchantingApparatusRecipes = new ArrayList<>();

    private List<BrewingRecipe> brewingRecipes = new ArrayList<>();

    public List<AbstractSpellPart> getDefaultStartingSpells() {
        return spellpartMap.values().stream().filter(Config::isStarterEnabled).collect(Collectors.toList());
    }

    public Item getGlyphItem(AbstractSpellPart spell) {
        return spell.glyphItem;
    }

    public Item getFamiliarItem(ResourceLocation id) {
        return familiarScriptMap.get(id);
    }

    public AbstractSpellPart registerSpell(AbstractSpellPart part) {
        glyphItemMap.put(part.getRegistryName(), part::getGlyph);

        //register the spell part's config in
        ForgeConfigSpec spec;
        ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
        part.buildConfig(spellBuilder);
        spec = spellBuilder.build();
        part.CONFIG = spec;
        ANModConfig anModConfig = new ANModConfig(ModConfig.Type.SERVER, part.CONFIG, ModLoadingContext.get().getActiveContainer(), part.getRegistryName().getNamespace() + "/" + part.getRegistryName().getPath());
        ModLoadingContext.get().getActiveContainer().addConfig(anModConfig);
        return spellpartMap.put(part.getRegistryName(), part);
    }

    public AbstractRitual registerRitual(AbstractRitual ritual) {
        return ritualMap.put(ritual.getRegistryName(), ritual);
    }

    public AbstractFamiliarHolder registerFamiliar(AbstractFamiliarHolder familiar) {
        return familiarHolderMap.put(familiar.getRegistryName(), familiar);
    }

    public @Nullable AbstractRitual getRitual(ResourceLocation id) {
        if (!ritualMap.containsKey(id))
            return null;
        try {
            return ritualMap.get(id).getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public @Nullable AbstractSpellPart getSpellPart(ResourceLocation id) {
        return spellpartMap.get(id);
    }

    public Map<ResourceLocation, AbstractSpellPart> getSpellpartMap() {
        return spellpartMap;
    }

    public Map<ResourceLocation, Supplier<Glyph>> getGlyphItemMap() {
        return glyphItemMap;
    }

    public Map<ResourceLocation, AbstractRitual> getRitualMap() {
        return ritualMap;
    }

    public Map<ResourceLocation, RitualTablet> getRitualItemMap() {
        return ritualParchmentMap;
    }

    public Map<ResourceLocation, AbstractFamiliarHolder> getFamiliarHolderMap() {
        return this.familiarHolderMap;
    }

    public Map<ResourceLocation, FamiliarScript> getFamiliarScriptMap() {
        return this.familiarScriptMap;
    }

    public Set<RecipeType<? extends IEnchantingRecipe>> getEnchantingRecipeTypes() {
        return enchantingRecipeTypes;
    }

    public Map<ResourceLocation, IPerk> getPerkMap() {
        return perkMap;
    }

    public Map<ResourceLocation, PerkItem> getPerkItemMap() {
        return perkItemMap;
    }

    public Map<Item, IPerkProvider<ItemStack>> getItemPerkProviderMap() {
        return itemPerkProviderMap;
    }

    public List<IEnchantingRecipe> getEnchantingApparatusRecipes(Level world) {
        List<IEnchantingRecipe> recipes = new ArrayList<>(enchantingApparatusRecipes);
        RecipeManager manager = world.getRecipeManager();
        List<IEnchantingRecipe> recipesByType = new ArrayList<>(); // todo lazy init enchanting types
        for (RecipeType<? extends IEnchantingRecipe> type : enchantingRecipeTypes) {
            recipesByType.addAll(manager.getAllRecipesFor(type));
        }
        recipes.addAll(recipesByType);
        return recipes;
    }

    public List<BrewingRecipe> getAllPotionRecipes() {
        if (brewingRecipes.isEmpty()) {
            BrewingRecipeRegistry.getRecipes().forEach(ib -> {
                if (ib instanceof BrewingRecipe brewingRecipe)
                    brewingRecipes.add(brewingRecipe);
            });
            for(PotionBrewing.Mix<Potion> mix : PotionRecipeMixin.mixList()){
                brewingRecipes.add(new BrewingRecipe(
                        PotionIngredient.fromPotion(mix.from.get()),
                        mix.ingredient,
                        PotionIngredient.fromPotion(mix.to.get()).getStack()
                ));
            }
        }
        return brewingRecipes;
    }

    /**
     * Returns the {@link ISpellValidator} that enforces the standard rules for spell crafting.
     * This validator relaxes the rule about starting with a cast method, to allow for spells that will be imprinted
     * onto caster items, which generally have a built-in cast method.
     */
    public ISpellValidator getSpellCraftingSpellValidator() {
        if (craftingSpellValidator == null)
            craftingSpellValidator = new StandardSpellValidator(false);
        return craftingSpellValidator;
    }

    /**
     * Returns the {@link ISpellValidator} that enforces the standard rules for spells at cast time.
     * This validator enforces all rules, asserting that a spell can be cast.
     */
    public ISpellValidator getSpellCastingSpellValidator() {
        if (castingSpellValidator == null) // Lazy init this because we need configs to load.
            castingSpellValidator = new StandardSpellValidator(true);
        return castingSpellValidator;
    }

    public @Nullable IScryer getScryer(ResourceLocation id) {
        return this.scryerMap.get(id);
    }

    public boolean registerScryer(IScryer scryer) {
        this.scryerMap.put(scryer.getRegistryName(), scryer);
        return true;
    }

    public boolean registerPerk(IPerk perk){
        perkMap.put(perk.getRegistryName(), perk);
        return true;
    }

    public boolean registerPerkProvider(ItemLike item, IPerkProvider<ItemStack> provider){
        itemPerkProviderMap.put(item.asItem(), provider);
        return true;
    }

    public @Nullable IPerkProvider<ItemStack> getPerkProvider(Item item){
        return itemPerkProviderMap.get(item);
    }

    public @NotNull List<Item> getPerkProviderItems(){
        List<Item> list = new ArrayList<>();
        for (Iterator<Item> it = itemPerkProviderMap.keys().asIterator(); it.hasNext(); ) {
            Item i = it.next();
            list.add(i);
        }
        return list;
    }

    public ConcurrentHashMap<ResourceLocation, SpellSound> getSpellSoundsRegistry() {
        return this.spellSoundsRegistry;
    }

    public SpellSound registerSpellSound(SpellSound sound) {
        return this.spellSoundsRegistry.put(sound.getId(), sound);
    }

    public void onResourceReload(){
        this.brewingRecipes = new ArrayList<>();
    }

    private ArsNouveauAPI() {
    }

    /**
     * Retrieves a handle to the singleton instance.
     */
    public static ArsNouveauAPI getInstance() {
        return INSTANCE;
    }

    // This is needed internally by the mod, so just make it eagerly.
    private static final ArsNouveauAPI INSTANCE = new ArsNouveauAPI();

    static {
        //think we just gotta ensure the path exists once
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(ArsNouveau.MODID), ArsNouveau.MODID);
    }
}
