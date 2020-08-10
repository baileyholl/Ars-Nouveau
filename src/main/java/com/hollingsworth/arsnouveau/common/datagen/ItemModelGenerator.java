package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class ItemModelGenerator extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //getBuilder("testitem").parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", mcLoc("block/stone"));
        getBuilder("glyph").texture("layer0",itemTexture(ItemsRegistry.noviceSpellBook));
        //System.out.println(itemTexture(ItemsRegistry.spellBook));

//        ArsNouveauAPI.getInstance().getSpell_map().values().forEach(p ->{
//            System.out.println(spellTexture(p));
//            getBuilder("glyph_"+p.tag.toLowerCase()).texture("layer0",spellTexture(p));
//        });
        ItemsRegistry.RegistrationHandler.ITEMS.forEach(i ->{
            if(i instanceof Glyph){
                getBuilder("glyph_" + ((Glyph) i).spellPart.getTag()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", spellTexture(i));
            }
        });
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
