package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScryRitualProvider extends SimpleDataProvider{

    public List<ScryRecipeWrapper> recipes = new ArrayList<>();

    public ScryRitualProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (ScryRecipeWrapper recipe : recipes) {
            Path path = getRecipePath(output, recipe.id().getPath());
            saveStable(pOutput, ScryRitualRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe.recipe()).getOrThrow(), path);
        }
    }

    protected void addEntries() {
        String[] defaultOres = new String[]{"coal", "copper", "diamond", "emerald", "gold", "iron", "lapis", "netherite_scrap", "quartz", "redstone"};
        for (String ore : defaultOres) {
            addForgeOreRecipe(ore);
        }
        recipes.add(new ScryRecipeWrapper(ArsNouveau.prefix( "amethyst_gems"), forgeItemTag("gems/amethyst"), forgeBlockTag("storage_blocks/amethyst")));
    }

    private void addForgeOreRecipe(String ore) {
        recipes.add(new ScryRecipeWrapper(ArsNouveau.prefix( ore + "_ores"), forgeItemTag("ores/" + ore), forgeBlockTag("ores/" + ore)));
    }

    private TagKey<Block> forgeBlockTag(String path) {
        return BlockTags.create(forgeTag(path));
    }

    private TagKey<Item> forgeItemTag(String path) {
        return ItemTags.create(forgeTag(path));
    }

    private ResourceLocation forgeTag(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipe/scry_ritual/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Scry Ritual Datagen";
    }

    public record ScryRecipeWrapper(ResourceLocation id, TagKey<Item> augment, TagKey<Block> highlight){

        public ScryRitualRecipe recipe(){
            return new ScryRitualRecipe(augment, highlight);
        }
    }
}
