package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
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
        getBuilder(LibBlockNames.SOURCE_GEM_BLOCK).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.SOURCE_GEM_BLOCK));

        getBuilder(LibBlockNames.AB_SMOOTH_BASKET).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_BASKET));
        getBuilder(LibBlockNames.AB_SMOOTH_CLOVER).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_CLOVER));
        getBuilder(LibBlockNames.AB_SMOOTH_HERRING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_HERRING));
        getBuilder(LibBlockNames.AB_SMOOTH_MOSAIC).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_MOSAIC));
        getBuilder(LibBlockNames.AB_SMOOTH_ALTERNATING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_ALTERNATING));
        getBuilder(LibBlockNames.AB_SMOOTH_ASHLAR).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AB_SMOOTH_ASHLAR));

        getBuilder(ItemsRegistry.EXPERIENCE_GEM.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(ItemsRegistry.EXPERIENCE_GEM.get()));
        getBuilder(ItemsRegistry.GREATER_EXPERIENCE_GEM.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(ItemsRegistry.GREATER_EXPERIENCE_GEM.get()));

        getBuilder(LibBlockNames.AS_GOLD_ALT).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_ALT));
        getBuilder(LibBlockNames.AS_GOLD_ASHLAR).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_ASHLAR));
        getBuilder(LibBlockNames.AS_GOLD_BASKET).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_BASKET));
        getBuilder(LibBlockNames.AS_GOLD_CLOVER).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_CLOVER));
        getBuilder(LibBlockNames.AS_GOLD_HERRING).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_HERRING));
        getBuilder(LibBlockNames.AS_GOLD_MOSAIC).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_MOSAIC));
        getBuilder(LibBlockNames.AS_GOLD_SLAB).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_SLAB));
        getBuilder(LibBlockNames.AS_GOLD_STONE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.AS_GOLD_STONE));

        getBuilder(LibBlockNames.RED_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.RED_SBED));
        getBuilder(LibBlockNames.BLUE_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.BLUE_SBED));
        getBuilder(LibBlockNames.GREEN_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.GREEN_SBED));
        getBuilder(LibBlockNames.YELLOW_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.YELLOW_SBED));
        getBuilder(LibBlockNames.ORANGE_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.ORANGE_SBED));
        getBuilder(LibBlockNames.PURPLE_SBED).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.PURPLE_SBED));
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
