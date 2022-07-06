package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class ItemModelGenerator extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
// TODO: restore item model datagen

//        ItemsRegistry.RegistrationHandler.ITEMS.forEach(i -> {
//            if(i instanceof Glyph){
//                try {
//                    getBuilder("glyph_" + ((Glyph) i).spellPart.getId()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", spellTexture(i));
//                }catch (Exception e){
//                    System.out.println("No texture for " + i);
//                }
//            }else if(i instanceof RitualTablet){
//                try {
//                    getBuilder("ritual_" + ((RitualTablet) i).ritual.getID()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", ritualTexture(i));
//                }catch (Exception e){
//                    System.out.println("No texture for " + i);
//                }
//            }else if(i instanceof FamiliarScript) {
//                try {
//                    getBuilder("familiar_" + ((FamiliarScript) i).familiar.getId()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", ritualTexture(i));
//                }catch (Exception e){
//                    System.out.println("No texture for " + i);
//                }
//            }else{
//                try {
//                    getBuilder(getRegistryName(i).getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(i));
//                }catch (Exception e){
//                    System.out.println("No texture for " + i.toString());
//                }
//            }
//
//
//        });


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
        stateUnchecked(LibBlockNames.MENDOSTEEN_POD);
        stateUnchecked(LibBlockNames.BASTION_POD);
        stateUnchecked(LibBlockNames.FROSTAYA_POD);
        stateUnchecked(LibBlockNames.BOMBEGRANTE_POD);
        itemUnchecked(ItemsRegistry.ALCHEMISTS_CROWN);
        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            getBuilder(s).parent(BlockStatesDatagen.getUncheckedModel(s));
        }
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
