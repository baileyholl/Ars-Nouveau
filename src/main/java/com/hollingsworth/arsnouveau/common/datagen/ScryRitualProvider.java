package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe.BlockHighlight;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe.EntityHighlight;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
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
        recipes.add(
                ScryRecipeWrapper.forEntity(
                        ArsNouveau.prefix("minecart"), ItemTagProvider.MINECARTS, EntityTags.MINECARTS, ParticleColor.CYAN
                )
        );
    }

    private void addForgeOreRecipe(String ore) {
        recipes.add(
                ScryRecipeWrapper.forBlock(ArsNouveau.prefix(ore + "_ores"), forgeItemTag("ores/" + ore), forgeBlockTag("ores/" + ore))
        );
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

    public record ScryRecipeWrapper(ResourceLocation id, TagKey<Item> augment, Either<BlockHighlight, EntityHighlight> either){
        public static ScryRecipeWrapper forBlock(ResourceLocation id, TagKey<Item> augment, TagKey<Block> block) {
            return new ScryRecipeWrapper(id, augment, Either.left(new BlockHighlight(block)));
        }

        public static ScryRecipeWrapper forEntity(ResourceLocation id, TagKey<Item> augment, TagKey<EntityType<?>> entity) {
            return forEntity(id, augment, entity, ParticleColor.DEFAULT);
        }

        public static ScryRecipeWrapper forEntity(ResourceLocation id, TagKey<Item> augment, TagKey<EntityType<?>> entity, ParticleColor color) {
            return new ScryRecipeWrapper(id, augment, Either.right(new EntityHighlight(entity, color)));
        }

        public ScryRitualRecipe recipe(){
            return new ScryRitualRecipe(augment, either);
        }
    }
}
