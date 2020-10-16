package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.common.items.Glyph;
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
                getBuilder("glyph_" + ((Glyph) i).spellPart.getTag()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", spellTexture(i));
            }
        });
        getBuilder(LibBlockNames.ARCANE_STONE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.ARCANE_STONE));
        getBuilder(LibBlockNames.AB_ALTERNATE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_ALTERNATE));
        getBuilder(LibBlockNames.AB_BASKET).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_BASKET));
        getBuilder(LibBlockNames.AB_HERRING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_HERRING));
        getBuilder(LibBlockNames.AB_MOSAIC).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_MOSAIC));
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
}
