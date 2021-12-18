package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatchouliProvider implements DataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public static ResourceLocation AUTOMATION = new ResourceLocation(ArsNouveau.MODID, "automation");
    public static ResourceLocation ENCHANTMENTS = new ResourceLocation(ArsNouveau.MODID, "enchantments");
    public static ResourceLocation EQUIPMENT = new ResourceLocation(ArsNouveau.MODID, "equipment");
    public static ResourceLocation FAMILIARS = new ResourceLocation(ArsNouveau.MODID, "familiars");
    public static ResourceLocation GETTING_STARTED = new ResourceLocation(ArsNouveau.MODID, "getting_started");

    public static ResourceLocation MACHINES = new ResourceLocation(ArsNouveau.MODID, "machines");
    public static ResourceLocation RESOURCES = new ResourceLocation(ArsNouveau.MODID, "resources");
    public static ResourceLocation RITUALS = new ResourceLocation(ArsNouveau.MODID, "rituals");
    public static ResourceLocation SOURCE = new ResourceLocation(ArsNouveau.MODID, "source");
    public static ResourceLocation GLYPHS_1 = new ResourceLocation(ArsNouveau.MODID, "glyphs_1");
    public static ResourceLocation GLYPHS_2 = new ResourceLocation(ArsNouveau.MODID, "glyphs_2");
    public static ResourceLocation GLYPHS_3 = new ResourceLocation(ArsNouveau.MODID, "glyphs_3");

    public static PatchouliBuilder.RecipeProvider APPARATUS = new PatchouliBuilder.RecipeProvider() {
        @Override
        ResourceLocation getType(ItemLike item) {
            return new ResourceLocation(ArsNouveau.MODID, "apparatus_recipe");
        }
    };

    public List<PatchouliPage> pages = new ArrayList<>();

    public PatchouliProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    public void addEntries(){
        for(Enchantment g : enchants){
            addEnchantmentPage(g);
        }
        for(AbstractRitual r : ArsNouveauAPI.getInstance().getRitualMap().values()){
            addRitualPage(r);
        }

        for(AbstractFamiliarHolder r : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()){
            addFamiliarPage(r);
        }
        addPage(new PatchouliBuilder(GETTING_STARTED, "spell_casting")
                .withLocalizedText()
                .withLocalizedText()
                .withLocalizedText()
                .withCraftingPage(ItemsRegistry.NOVICE_SPELLBOOK), getPath(GETTING_STARTED, "spell_casting"));
        addPage(new PatchouliBuilder(GETTING_STARTED, "spell_mana")
                .withLocalizedText()
                .withLocalizedText(), getPath(GETTING_STARTED, "spell_mana"));

        addBasicItem(ItemsRegistry.AMULET_OF_MANA_BOOST, EQUIPMENT, APPARATUS);
        addBasicItem(ItemsRegistry.AMULET_OF_MANA_REGEN, EQUIPMENT, APPARATUS);
        addBasicItem(ItemsRegistry.BELT_OF_LEVITATION, EQUIPMENT, APPARATUS);
        addBasicItem(ItemsRegistry.BELT_OF_UNSTABLE_GIFTS, EQUIPMENT, APPARATUS);
        addBasicItem(ItemsRegistry.JAR_OF_LIGHT, EQUIPMENT, APPARATUS);
        addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.STARBUNCLE_CHARM)
                .withLocalizedText()
                .withRecipePage(APPARATUS.getType(ItemsRegistry.STARBUNCLE_CHARM), ItemsRegistry.STARBUNCLE_CHARM)
                .withEntityTextPage(ModEntities.STARBUNCLE_TYPE.getRegistryName(), "starbuncle_charm"), getPath(AUTOMATION, "starbuncle_charm"));
    }

    public String getLangPath(String name){
        return "ars_nouveau.page." + name;
    }

    public void addPage(PatchouliBuilder builder, Path path){
        this.pages.add(new PatchouliPage(builder, path));
    }

    public void addBasicItem(ItemLike item, ResourceLocation category, PatchouliBuilder.RecipeProvider recipeProvider){
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withTextPage("ars_nouveau.page." + item.asItem().getRegistryName().getPath())
                .withRecipePage(recipeProvider.getType(item), recipeProvider.getPath(item).toString());
        this.pages.add(new PatchouliPage(builder, getPath(category, item.asItem().getRegistryName())));
    }

    public Path getPath(ResourceLocation category, ResourceLocation fileName){
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/" + category.getPath() +"/" + fileName.getPath() + ".json");
    }

    public Path getPath(ResourceLocation category, String fileName){
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/" + category.getPath() +"/" + fileName + ".json");
    }
    @Override
    public void run(HashCache cache) throws IOException {
        addEntries();
        for(PatchouliPage patchouliPage : pages){
            DataProvider.save(GSON, cache, patchouliPage.builder.build(), patchouliPage.path);
        }
    }

    public static record PatchouliPage(PatchouliBuilder builder, Path path) { }

    public void addFamiliarPage(AbstractFamiliarHolder familiarHolder){
        PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity.ars_nouveau." + familiarHolder.getId())
                .withIcon("ars_nouveau:familiar_" + familiarHolder.getId())
                .withTextPage("ars_nouveau.familiar_desc." + familiarHolder.getId())
                .withEntityPage(new ResourceLocation(ArsNouveau.MODID, familiarHolder.getEntityKey()));

        this.pages.add(new PatchouliPage(builder, this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/familiars/" + familiarHolder.getId() + ".json")));
    }

    public void addRitualPage(AbstractRitual ritual){
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item.ars_nouveau.ritual_" + ritual.getID())
                .withIcon("ars_nouveau:ritual_" + ritual.getID())
                .withTextPage("ars_nouveau.ritual_desc." + ritual.getID())
                .withCraftingPage("ars_nouveau:ritual_" + ritual.getID());

        this.pages.add(new PatchouliPage(builder,this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/rituals/" + ritual.getID() + ".json")));
    }

    public void addEnchantmentPage(Enchantment enchantment){
        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, enchantment.getDescriptionId())
                .withIcon(Items.ENCHANTED_BOOK.getRegistryName().toString());
        for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++){
            builder.withRecipePage(new ResourceLocation("ars_nouveau:enchanting_recipe"),"ars_nouveau:" + enchantment.getRegistryName().getPath() + "_" + i);
        }
        this.pages.add(new PatchouliPage(builder,  this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli/enchantments/" +  enchantment.getRegistryName().getPath() + ".json")));
    }

    List<Enchantment> enchants = Arrays.asList(
            Enchantments.AQUA_AFFINITY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.BLAST_PROTECTION,
            Enchantments.DEPTH_STRIDER,
            Enchantments.BLOCK_EFFICIENCY,
            Enchantments.FALL_PROTECTION,
            Enchantments.FIRE_ASPECT,
            Enchantments.FIRE_PROTECTION,
            Enchantments.FLAMING_ARROWS,
            Enchantments.BLOCK_FORTUNE,
            Enchantments.INFINITY_ARROWS,
            Enchantments.KNOCKBACK,
            Enchantments.MOB_LOOTING,
            Enchantments.MULTISHOT,
            Enchantments.PIERCING,
            Enchantments.POWER_ARROWS,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.ALL_DAMAGE_PROTECTION,
            Enchantments.PUNCH_ARROWS,
            Enchantments.QUICK_CHARGE,
            Enchantments.RESPIRATION,
            Enchantments.SHARPNESS,
            Enchantments.SILK_TOUCH,
            Enchantments.SMITE,
            Enchantments.SWEEPING_EDGE,
            Enchantments.THORNS,
            Enchantments.UNBREAKING,
            EnchantmentRegistry.MANA_BOOST_ENCHANTMENT,
            EnchantmentRegistry.MANA_REGEN_ENCHANTMENT
    );

    @Override
    public String getName() {
        return "Patchouli";
    }
}
