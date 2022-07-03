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
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStatesDatagen extends BlockStateProvider {

    public BlockStatesDatagen(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerNormalCube(BlockRegistry.SOURCE_GEM_BLOCK, LibBlockNames.SOURCE_GEM_BLOCK);
        registerNormalCube(BlockRegistry.RED_SBED, LibBlockNames.RED_SBED);
        registerNormalCube(BlockRegistry.BLUE_SBED, LibBlockNames.BLUE_SBED);
        registerNormalCube(BlockRegistry.GREEN_SBED, LibBlockNames.GREEN_SBED);
        registerNormalCube(BlockRegistry.YELLOW_SBED, LibBlockNames.YELLOW_SBED);
        registerNormalCube(BlockRegistry.ORANGE_SBED, LibBlockNames.ORANGE_SBED);
        registerNormalCube(BlockRegistry.PURPLE_SBED, LibBlockNames.PURPLE_SBED);

        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            registerNormalCube(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s)), s);
        }

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

    public void buildNormalCube(String registryName) {
        this.models().getBuilder(registryName).parent(new ModelFile.UncheckedModelFile("block/cube_all")).texture("all", getBlockLoc(registryName));
    }

    public ResourceLocation getBlockLoc(String registryName) {
        return new ResourceLocation(ArsNouveau.MODID, "blocks" + "/" + registryName);
    }
}
