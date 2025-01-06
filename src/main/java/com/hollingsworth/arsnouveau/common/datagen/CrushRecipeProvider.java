package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrushRecipeProvider extends SimpleDataProvider {

    public List<CrushWrapper> recipes = new ArrayList<>();

    public CrushRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        recipes.add(new CrushWrapper("stone", Ingredient.of(Tags.Items.STONES))
                .withItems(Items.GRAVEL.getDefaultInstance(), 1.0f));
        recipes.add(new CrushWrapper("gravel", Ingredient.of(Tags.Items.GRAVELS))
                .withItems(Items.SAND.getDefaultInstance(), 1.0f)
                .withItems(Items.FLINT.getDefaultInstance(), 0.02f));
        recipes.add(new CrushWrapper("cobblestone", Ingredient.of(Tags.Items.COBBLESTONES)).withItems(Items.GRAVEL.getDefaultInstance(), 1.0f));
        recipes.add(new CrushWrapper("white_dye", Ingredient.of(Items.LILY_OF_THE_VALLEY)).withItems(new ItemStack(Items.WHITE_DYE, 2)));
        recipes.add(new CrushWrapper("orange_dye", Ingredient.of(Items.ORANGE_TULIP)).withItems(new ItemStack(Items.ORANGE_DYE, 2)));
        recipes.add(new CrushWrapper("magenta_dye", Ingredient.of(Items.ALLIUM)).withItems(new ItemStack(Items.MAGENTA_DYE, 2)));
        recipes.add(new CrushWrapper("light_blue_dye", Ingredient.of(Items.BLUE_ORCHID)).withItems(new ItemStack(Items.LIGHT_BLUE_DYE, 2)));
        recipes.add(new CrushWrapper("yellow_dye", Ingredient.of(Items.DANDELION)).withItems(new ItemStack(Items.YELLOW_DYE, 2)));
        recipes.add(new CrushWrapper("pink_dye", Ingredient.of(Items.PINK_TULIP)).withItems(new ItemStack(Items.PINK_DYE, 2)));
        recipes.add(new CrushWrapper("light_gray_dye_oxeye", Ingredient.of(Items.OXEYE_DAISY)).withItems(new ItemStack(Items.LIGHT_GRAY_DYE, 2)));
        recipes.add(new CrushWrapper("light_gray_dye_azure", Ingredient.of(Items.AZURE_BLUET)).withItems(new ItemStack(Items.LIGHT_GRAY_DYE, 2)));
        recipes.add(new CrushWrapper("light_gray_dye_tulip", Ingredient.of(Items.WHITE_TULIP)).withItems(new ItemStack(Items.LIGHT_GRAY_DYE, 2)));
        recipes.add(new CrushWrapper("blue_dye", Ingredient.of(Items.CORNFLOWER)).withItems(new ItemStack(Items.BLUE_DYE, 2)));
        recipes.add(new CrushWrapper("brown_dye", Ingredient.of(Items.COCOA_BEANS)).withItems(new ItemStack(Items.BROWN_DYE, 2)));
        recipes.add(new CrushWrapper("red_dye_tulip", Ingredient.of(Items.RED_TULIP)).withItems(new ItemStack(Items.RED_DYE, 2)));
        recipes.add(new CrushWrapper("red_dye_beetroot", Ingredient.of(Items.BEETROOT)).withItems(new ItemStack(Items.RED_DYE, 2)));
        recipes.add(new CrushWrapper("red_dye_poppy", Ingredient.of(Items.POPPY)).withItems(new ItemStack(Items.RED_DYE, 2)));
        recipes.add(new CrushWrapper("red_dye_rose_bush", Ingredient.of(Items.ROSE_BUSH)).withItems(new ItemStack(Items.RED_DYE, 4)));
        recipes.add(new CrushWrapper("yellow_dye_sunflower", Ingredient.of(Items.SUNFLOWER)).withItems(new ItemStack(Items.YELLOW_DYE, 4)));
        recipes.add(new CrushWrapper("magenta_dye_lilac", Ingredient.of(Items.LILAC)).withItems(new ItemStack(Items.MAGENTA_DYE, 4)));
        recipes.add(new CrushWrapper("pink_dye_peony", Ingredient.of(Items.PEONY)).withItems(new ItemStack(Items.PINK_DYE, 4)));
        recipes.add(new CrushWrapper("terracotta", Ingredient.of(Items.TERRACOTTA)).withItems(Items.RED_SAND.getDefaultInstance()));
        recipes.add(new CrushWrapper("sugar_cane", Ingredient.of(Items.SUGAR_CANE)).withItems(new ItemStack(Items.SUGAR, 2)));
        recipes.add(new CrushWrapper("sandstone_to_sand", Ingredient.of(Items.SANDSTONE)).withItems(Items.SAND.getDefaultInstance()));
        recipes.add(new CrushWrapper("quartz_block_to_quartz", Ingredient.of(ItemTagProvider.STORAGE_BLOCKS_QUARTZ)).withItems((new ItemStack(Items.QUARTZ, 4))));
        recipes.add(new CrushWrapper("glowstone_block_to_dust", Ingredient.of(Blocks.GLOWSTONE)).withItems((new ItemStack(Items.GLOWSTONE_DUST, 4))));


        for (CrushWrapper g : recipes) {
            Path path = getRecipePath(output, g.path.getPath());
            saveStable(pOutput, CrushRecipe.CODEC.encodeStart(JsonOps.INSTANCE, g.asRecipe()).getOrThrow(), path);
        }
    }

    public static class CrushWrapper{
        public ResourceLocation path;
        public Ingredient ing;
        public CrushWrapper(String string, Ingredient ingredient){
            this.path = ArsNouveau.prefix(string);
            this.ing = ingredient;
        }
        List<CrushRecipe.CrushOutput> outputs = new ArrayList<>();

        public CrushWrapper withItems(ItemStack output, float chance) {
            this.outputs.add(new CrushRecipe.CrushOutput(output, chance));
            return this;
        }

        public CrushWrapper withItems(ItemStack output) {
            this.outputs.add(new CrushRecipe.CrushOutput(output, 1.0f));
            return this;
        }

        public CrushRecipe asRecipe() {
            return new CrushRecipe(this.ing, outputs);
        }
    }

    private static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/" + str + ".json");
    }

    @Override
    public String getName() {
        return "Crush";
    }
}
