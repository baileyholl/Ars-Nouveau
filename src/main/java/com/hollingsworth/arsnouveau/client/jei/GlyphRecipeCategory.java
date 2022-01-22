package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hollingsworth.arsnouveau.client.jei.EnchantingApparatusRecipeCategory.rotatePointAbout;

public class GlyphRecipeCategory implements IRecipeCategory<GlyphRecipe> {

    public IDrawable background;
    public IDrawable icon;
    IGuiHelper helper;
    public final static ResourceLocation UID = new ResourceLocation(ArsNouveau.MODID, "glyph_recipe");
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public GlyphRecipeCategory(IGuiHelper helper){
        this.helper = helper;
        background = helper.createBlankDrawable(60,30);
        icon = helper.createDrawableIngredient(new ItemStack(BlockRegistry.SCRIBES_BLOCK));
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return helper.drawableBuilder(JEIConstants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends GlyphRecipe> getRecipeClass() {
        return GlyphRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("block.ars_nouveau.scribes_table");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(80,30);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(GlyphRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        renderer.draw(matrixStack, new TranslatableComponent("ars_nouveau.exp", recipe.exp), 0.0f,100f, 10);
    }

    @Override
    public void setIngredients(GlyphRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> itemStacks = new ArrayList<>();
        for(Ingredient i : o.inputs){
            itemStacks.add(Arrays.asList(i.getItems()));
        }
        iIngredients.setInputLists(VanillaTypes.ITEM, itemStacks);
        iIngredients.setOutput(VanillaTypes.ITEM, o.output);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GlyphRecipe apparatusRecipe, IIngredients ingredients) {
        int index = 0;
        double angleBetweenEach = 360.0 / ingredients.getInputs(VanillaTypes.ITEM).size();
        Vec2 point = new Vec2(48, 13), center = new Vec2(48, 45);

        for (List<ItemStack> o : ingredients.getInputs(VanillaTypes.ITEM)) {
            recipeLayout.getItemStacks().init(index, true, (int) point.x, (int) point.y);
            recipeLayout.getItemStacks().set(index, o);
            index += 1;
            point = rotatePointAbout(point, center, angleBetweenEach);
        }
        recipeLayout.getItemStacks().init(index, false, 86, 10);
        recipeLayout.getItemStacks().set(index, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
