package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.base.Preconditions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class ItemModelGenerator extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        for (Supplier<Glyph> i : ArsNouveauAPI.getInstance().getGlyphItemMap().values()) {
            try {
                if(i.get().spellPart.getRegistryName().getNamespace().equals(ArsNouveau.MODID))
                    getBuilder(i.get().spellPart.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", spellTexture(i.get()));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No texture for " + i.get());
            }
        }
        for (RitualTablet i : ArsNouveauAPI.getInstance().getRitualItemMap().values()) {
            try {
                if(i.ritual.getRegistryName().getNamespace().equals(ArsNouveau.MODID))
                    getBuilder(i.ritual.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(i));
            } catch (Exception e) {
                System.out.println("No texture for " + i);
            }
        }
        for (FamiliarScript i : ArsNouveauAPI.getInstance().getFamiliarScriptMap().values()) {
            try {
                if(i.familiar.getRegistryName().getNamespace().equals(ArsNouveau.MODID))
                    getBuilder(i.familiar.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(i));
            } catch (Exception e) {
                System.out.println("No texture for " + i);
            }
         }

        for (PerkItem i : ArsNouveauAPI.getInstance().getPerkItemMap().values()) {
            try {
                if(i.perk.getRegistryName().getNamespace().equals(ArsNouveau.MODID))
                    getBuilder(i.perk.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(i));
            } catch (Exception e) {
                System.out.println("No texture for " + i);
            }
        }


        getBuilder(LibBlockNames.STRIPPED_AWLOG_BLUE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_BLUE));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_BLUE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_BLUE));
        getBuilder(LibBlockNames.STRIPPED_AWLOG_GREEN).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_GREEN));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_GREEN).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_GREEN));
        getBuilder(LibBlockNames.STRIPPED_AWLOG_RED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_RED));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_RED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_RED));
        getBuilder(LibBlockNames.STRIPPED_AWLOG_PURPLE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_PURPLE));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_PURPLE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_PURPLE));
        getBuilder(LibBlockNames.SOURCE_GEM_BLOCK).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.SOURCE_GEM_BLOCK));
        getBuilder(ItemsRegistry.EXPERIENCE_GEM.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(ItemsRegistry.EXPERIENCE_GEM.get()));
        getBuilder(ItemsRegistry.GREATER_EXPERIENCE_GEM.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(ItemsRegistry.GREATER_EXPERIENCE_GEM.get()));
        getBuilder(LibBlockNames.RED_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.RED_SBED));
        getBuilder(LibBlockNames.BLUE_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.BLUE_SBED));
        getBuilder(LibBlockNames.GREEN_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.GREEN_SBED));
        getBuilder(LibBlockNames.YELLOW_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.YELLOW_SBED));
        getBuilder(LibBlockNames.ORANGE_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.ORANGE_SBED));
        getBuilder(LibBlockNames.PURPLE_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.PURPLE_SBED));
        blockAsItem(LibBlockNames.MENDOSTEEN_POD);
        blockAsItem(LibBlockNames.BASTION_POD);
        blockAsItem(LibBlockNames.FROSTAYA_POD);
        blockAsItem(LibBlockNames.BOMBEGRANATE_POD);
        itemUnchecked(ItemsRegistry.ALCHEMISTS_CROWN);
        stateUnchecked(LibBlockNames.POTION_DIFFUSER);
        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            getBuilder(s).parent(BlockStatesDatagen.getUncheckedModel(s));
        }
        getBuilder(LibBlockNames.VOID_PRISM).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.VOID_PRISM));
        getBuilder(LibBlockNames.FALSE_WEAVE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.FALSE_WEAVE));
        getBuilder(LibBlockNames.MIRROR_WEAVE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.MIRROR_WEAVE));
        getBuilder(LibBlockNames.GHOST_WEAVE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.GHOST_WEAVE));
        getBuilder(LibBlockNames.MAGEBLOOM_BLOCK).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.MAGEBLOOM_BLOCK));

        getBuilder(LibBlockNames.RITUAL_BRAZIER).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.RITUAL_BRAZIER));
        getBuilder(LibBlockNames.ITEM_DETECTOR).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.ITEM_DETECTOR));
    }

    public void blockAsItem(String s){
        getBuilder("ars_nouveau:" + s).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", itemTexture(s));
    }

    public void blockAsItem(RegistryWrapper<? extends Block> block){
        getBuilder(block.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", itemTexture(block.get()));
    }

    public void itemUnchecked(RegistryWrapper<? extends Item> item){
        getBuilder(item.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", itemTexture(item.get()));

    }

    public void stateUnchecked(String name){
        getBuilder(name).parent(BlockStatesDatagen.getUncheckedModel(name));
    }


    @Override
    public String getName() {
        return "Ars Nouveau Item Models";
    }

    private ResourceLocation registryName(final Item item) {
        return Preconditions.checkNotNull(getRegistryName(item), "Item %s has a null registry name", item);
    }

    private ResourceLocation registryName(final Block item) {
        return Preconditions.checkNotNull(getRegistryName(item), "Item %s has a null registry name", item);
    }

    private ResourceLocation itemTexture(String item) {
        return new ResourceLocation(ArsNouveau.MODID, "items" + "/" + item);
    }

    private ResourceLocation itemTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath());
    }

    private ResourceLocation itemTexture(final Block item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath());
    }

    private ResourceLocation spellTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath().replace("glyph_", ""));
    }
}
