package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        getBuilder("glyph").texture("layer0",itemTexture(ItemsRegistry.noviceSpellBook));
        ItemsRegistry.RegistrationHandler.ITEMS.forEach(i ->{
            if(i instanceof Glyph){
                try {
                    getBuilder("glyph_" + ((Glyph) i).spellPart.getTag()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", spellTexture(i));
                }catch (Exception e){
                    System.out.println("No texture for " + i.toString());
                }
            }else if(i instanceof RitualTablet){
                try {
                    getBuilder("ritual_" + ((RitualTablet) i).ritual.getID()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", ritualTexture(i));
                }catch (Exception e){
                    System.out.println("No texture for " + i.toString());
                }
            }else{
                try {
                    getBuilder(i.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(i));
                }catch (Exception e){
                    System.out.println("No texture for " + i.toString());
                }
            }


        });
        getBuilder(LibBlockNames.ARCANE_STONE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.ARCANE_STONE));
        getBuilder(LibBlockNames.AB_ALTERNATE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_ALTERNATE));
        getBuilder(LibBlockNames.AB_BASKET).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_BASKET));
        getBuilder(LibBlockNames.AB_HERRING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_HERRING));
        getBuilder(LibBlockNames.AB_MOSAIC).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_MOSAIC));
        getBuilder(LibBlockNames.AB_SMOOTH).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH));
        getBuilder(LibBlockNames.AB_SMOOTH_SLAB).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_SLAB));
        getBuilder(LibBlockNames.AB_CLOVER).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_CLOVER));

        getBuilder(LibBlockNames.STRIPPED_AWLOG_BLUE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_BLUE));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_BLUE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_BLUE));
        getBuilder(LibBlockNames.STRIPPED_AWLOG_GREEN).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_GREEN));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_GREEN).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_GREEN));
        getBuilder(LibBlockNames.STRIPPED_AWLOG_RED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_RED));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_RED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_RED));
        getBuilder(LibBlockNames.STRIPPED_AWLOG_PURPLE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_PURPLE));
        getBuilder(LibBlockNames.STRIPPED_AWWOOD_PURPLE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWWOOD_PURPLE));
        getBuilder(LibBlockNames.MANA_GEM_BLOCK).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.MANA_GEM_BLOCK));

        getBuilder(LibBlockNames.AB_SMOOTH_BASKET).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_BASKET));
        getBuilder(LibBlockNames.AB_SMOOTH_CLOVER).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_CLOVER));
        getBuilder(LibBlockNames.AB_SMOOTH_HERRING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_HERRING));
        getBuilder(LibBlockNames.AB_SMOOTH_MOSAIC).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_MOSAIC));
        getBuilder(LibBlockNames.AB_SMOOTH_ALTERNATING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_ALTERNATING));
        getBuilder(LibBlockNames.AB_SMOOTH_ASHLAR).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_ASHLAR));

        getBuilder(ItemsRegistry.EXPERIENCE_GEM.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(ItemsRegistry.EXPERIENCE_GEM));
        getBuilder(ItemsRegistry.GREATER_EXPERIENCE_GEM.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(ItemsRegistry.GREATER_EXPERIENCE_GEM));

    }



    @Override
    public String getName() {
        return "Ars Nouveau Item Models";
    }
    private ResourceLocation registryName(final Item item) {
        return Preconditions.checkNotNull(item.getRegistryName(), "Item %s has a null registry name", item);
    }
    private ResourceLocation itemTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath());
    }

    private ResourceLocation spellTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath().replace("glyph_", ""));
    }

    private ResourceLocation ritualTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath());
    }
}
