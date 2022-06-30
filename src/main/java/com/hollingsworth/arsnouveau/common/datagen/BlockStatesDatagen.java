package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStatesDatagen extends BlockStateProvider {

    public BlockStatesDatagen(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        simpleBlock(BlockRegistry.ARCANE_STONE, new ModelFile.UncheckedModelFile("ars_nouveau:block/arcane_ore"));
        registerNormalCube(BlockRegistry.ARCANE_STONE, LibBlockNames.ARCANE_STONE);
        registerNormalCube(BlockRegistry.ARCANE_BRICKS, LibBlockNames.ARCANE_BRICKS);
        registerNormalCube(BlockRegistry.AB_ALTERNATE, LibBlockNames.AB_ALTERNATE);
        registerNormalCube(BlockRegistry.AB_BASKET, LibBlockNames.AB_BASKET);
        registerNormalCube(BlockRegistry.AB_HERRING, LibBlockNames.AB_HERRING);
        registerNormalCube(BlockRegistry.AB_MOSAIC, LibBlockNames.AB_MOSAIC);

        registerNormalCube(BlockRegistry.AB_CLOVER, LibBlockNames.AB_CLOVER);
        registerNormalCube(BlockRegistry.AB_SMOOTH, LibBlockNames.AB_SMOOTH);
        registerNormalCube(BlockRegistry.AB_SMOOTH_SLAB, LibBlockNames.AB_SMOOTH_SLAB);
        registerNormalCube(BlockRegistry.SOURCE_GEM_BLOCK, LibBlockNames.SOURCE_GEM_BLOCK);

        registerNormalCube(BlockRegistry.AB_SMOOTH_BASKET, LibBlockNames.AB_SMOOTH_BASKET);
        registerNormalCube(BlockRegistry.AB_SMOOTH_CLOVER, LibBlockNames.AB_SMOOTH_CLOVER);
        registerNormalCube(BlockRegistry.AB_SMOOTH_HERRING, LibBlockNames.AB_SMOOTH_HERRING);
        registerNormalCube(BlockRegistry.AB_SMOOTH_MOSAIC, LibBlockNames.AB_SMOOTH_MOSAIC);
        registerNormalCube(BlockRegistry.AB_SMOOTH_ALTERNATING, LibBlockNames.AB_SMOOTH_ALTERNATING);
        registerNormalCube(BlockRegistry.AB_SMOOTH_ASHLAR, LibBlockNames.AB_SMOOTH_ASHLAR);

        registerNormalCube(BlockRegistry.AS_GOLD_ALT, LibBlockNames.AS_GOLD_ALT);
        registerNormalCube(BlockRegistry.AS_GOLD_ASHLAR, LibBlockNames.AS_GOLD_ASHLAR);
        registerNormalCube(BlockRegistry.AS_GOLD_BASKET, LibBlockNames.AS_GOLD_BASKET);
        registerNormalCube(BlockRegistry.AS_GOLD_CLOVER, LibBlockNames.AS_GOLD_CLOVER);
        registerNormalCube(BlockRegistry.AS_GOLD_HERRING, LibBlockNames.AS_GOLD_HERRING);
        registerNormalCube(BlockRegistry.AS_GOLD_MOSAIC, LibBlockNames.AS_GOLD_MOSAIC);
        registerNormalCube(BlockRegistry.AS_GOLD_SLAB, LibBlockNames.AS_GOLD_SLAB);
        registerNormalCube(BlockRegistry.AS_GOLD_STONE, LibBlockNames.AS_GOLD_STONE);

        registerNormalCube(BlockRegistry.RED_SBED, LibBlockNames.RED_SBED);
        registerNormalCube(BlockRegistry.BLUE_SBED, LibBlockNames.BLUE_SBED);
        registerNormalCube(BlockRegistry.GREEN_SBED, LibBlockNames.GREEN_SBED);
        registerNormalCube(BlockRegistry.YELLOW_SBED, LibBlockNames.YELLOW_SBED);
        registerNormalCube(BlockRegistry.ORANGE_SBED, LibBlockNames.ORANGE_SBED);
        registerNormalCube(BlockRegistry.PURPLE_SBED, LibBlockNames.PURPLE_SBED);

     //   registerDoor(BlockRegistry.ARCHWOOD_DOOR, LibBlockNames.ARCHWOOD_DOOR);
    }

    private void registerDoor(DoorBlock door, String reg) {
        doorBlock(door, reg, getBlockLoc(reg + "_bottom"), getBlockLoc(reg + "_top"));
    }

    //will it work? idk
    public void signBlock(Block sign, String reg) {
        ModelFile signModel = models().withExistingParent(reg, new ResourceLocation("block/air")).texture("particle", new ResourceLocation(ArsNouveau.MODID, "block/" + reg));
        getVariantBuilder(sign).forAllStates(s -> ConfiguredModel.builder().modelFile(signModel).build());
    }

    public void registerNormalCube(Block block, String registry) {
        buildNormalCube(registry);
        simpleBlock(block, getUncheckedModel(registry));
    }

    public static ModelFile getUncheckedModel(String registry) {
        return new ModelFile.UncheckedModelFile("ars_nouveau:block/" + registry);
    }

    public void buildNormalCube(String registryName){
        this.models().getBuilder(registryName).parent(new ModelFile.UncheckedModelFile("block/cube_all")).texture("all",getBlockLoc(registryName));
    }

    public ResourceLocation getBlockLoc(String registryName){
        return new ResourceLocation(ArsNouveau.MODID, "blocks" + "/" +registryName);
    }
}
