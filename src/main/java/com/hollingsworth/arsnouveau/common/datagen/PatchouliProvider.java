package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
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
            DataProvider.save(GSON, cache, getEnchantmentPage(g), path);
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
        PatchouliBuilder builder = new PatchouliBuilder();
        builder.withName("entity.ars_nouveau." + familiarHolder.getId())
                .withIcon("ars_nouveau:familiar_" + familiarHolder.getId())
                .withCategory(PatchouliBuilder.FAMILIARS)
                .withTextPage("ars_nouveau.familiar_desc." + familiarHolder.getId())
                .withEntityPage(new ResourceLocation(ArsNouveau.MODID, familiarHolder.getEntityKey()));

        return builder.build();
    }

    public JsonObject getRitualPage(AbstractRitual ritual){
        PatchouliBuilder builder = new PatchouliBuilder();
        builder.withName("item.ars_nouveau.ritual_" + ritual.getID())
                .withIcon("ars_nouveau:ritual_" + ritual.getID())
                .withCategory(PatchouliBuilder.RITUALS)
                .withTextPage("ars_nouveau.ritual_desc." + ritual.getID())
                .withCraftingPage("ars_nouveau:ritual_" + ritual.getID());

        return builder.build();
    }

    public JsonObject getEnchantmentPage(Enchantment enchantment){
        PatchouliBuilder builder = new PatchouliBuilder();

        builder.withName(enchantment.getDescriptionId())
                .withIcon(Items.ENCHANTED_BOOK.getRegistryName().toString())
                .withCategory(PatchouliBuilder.ENCHANTMENTS);

        for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++){
            builder.withRecipePage(new ResourceLocation("ars_nouveau:enchanting_recipe"),"ars_nouveau:" + enchantment.getRegistryName().getPath() + "_" + i);
        }
        return builder.build();
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
