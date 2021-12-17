package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
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
    public PatchouliProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    List<Enchantment> enchants = new ArrayList<>();
    @Override
    public void run(HashCache cache) throws IOException {
        addEntries();
        Path output = this.generator.getOutputFolder();
        for(Enchantment g : enchants){
            Path path = getPath(output, g.getRegistryName().getPath());
            DataProvider.save(GSON, cache, getPage(g), path);
        }

        for(AbstractRitual r : ArsNouveauAPI.getInstance().getRitualMap().values()){
            Path path = getRitualPath(output, r.getID());
            DataProvider.save(GSON, cache, getRitualPage(r), path);
        }

        for(AbstractFamiliarHolder r : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()){
            Path path = getFamiliarPath(output, r.getId());
            DataProvider.save(GSON, cache, getFamiliarPage(r), path);
        }
    }

    public JsonObject getFamiliarPage(AbstractFamiliarHolder familiarHolder){
        JsonObject object = new JsonObject();
        object.addProperty("name", "entity.ars_nouveau." + familiarHolder.getId());
        object.addProperty("icon", "ars_nouveau:familiar_" + familiarHolder.getId());
        object.addProperty("category", "ars_nouveau:familiars");
        JsonArray pages = new JsonArray();

        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("text", "ars_nouveau.familiar_desc." + familiarHolder.getId());
        pages.add(page);
        JsonObject page2 = new JsonObject();
        page2.addProperty("type", "patchouli:entity");
        page2.addProperty("entity", "ars_nouveau:" + familiarHolder.getEntityKey());
        pages.add(page2);
        object.add("pages", pages);
        return object;
    }

    public JsonObject getRitualPage(AbstractRitual ritual){
        JsonObject object = new JsonObject();
        object.addProperty("name", "item.ars_nouveau.ritual_" + ritual.getID());
        object.addProperty("icon","ars_nouveau:ritual_" + ritual.getID());
        object.addProperty("category", "ars_nouveau:rituals");
        JsonArray pages = new JsonArray();

        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("text", "ars_nouveau.ritual_desc." + ritual.getID());
        pages.add(page);

        JsonObject page2 = new JsonObject();
        page2.addProperty("type", "patchouli:crafting");
        page2.addProperty("recipe", "ars_nouveau:ritual_" + ritual.getID());
        pages.add(page2);

        object.add("pages", pages);
        return object;
    }

    public JsonObject getPage(Enchantment enchantment){
        JsonObject object = new JsonObject();
        object.addProperty("name", enchantment.getDescriptionId());
        object.addProperty("icon", Items.ENCHANTED_BOOK.getRegistryName().toString());
        object.addProperty("category", "ars_nouveau:enchantments");
        JsonArray pages = new JsonArray();
        for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++){
            JsonObject page = new JsonObject();
            page.addProperty("type", "ars_nouveau:enchanting_recipe");
            page.addProperty("recipe", "ars_nouveau:" + enchantment.getRegistryName().getPath() + "_" + i);
            pages.add(page);
        }
        object.add("pages", pages);
        return object;
    }

    public void addEntries(){
        enchants.addAll(Arrays.asList(
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
                ));
    }

    private static Path getPath(Path pathIn, String str){
        return pathIn.resolve("data/ars_nouveau/patchouli/enchantments/" + str + ".json");
    }

    private static Path getRitualPath(Path pathIn, String str){
        return pathIn.resolve("data/ars_nouveau/patchouli/rituals/" + str + ".json");
    }

    private static Path getFamiliarPath(Path pathIn, String str){
        return pathIn.resolve("data/ars_nouveau/patchouli/familiars/" + str + ".json");
    }
    @Override
    public String getName() {
        return "Patchouli";
    }
}
