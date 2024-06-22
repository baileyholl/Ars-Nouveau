package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.registry.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class BlockStatesDatagen extends BlockStateProvider {

    public BlockStatesDatagen(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerOnlyState(BlockRegistry.SOURCE_GEM_BLOCK, LibBlockNames.SOURCE_GEM_BLOCK);
        registerOnlyState(BlockRegistry.RED_SBED, LibBlockNames.RED_SBED);
        registerOnlyState(BlockRegistry.BLUE_SBED, LibBlockNames.BLUE_SBED);
        registerOnlyState(BlockRegistry.GREEN_SBED, LibBlockNames.GREEN_SBED);
        registerOnlyState(BlockRegistry.YELLOW_SBED, LibBlockNames.YELLOW_SBED);
        registerOnlyState(BlockRegistry.ORANGE_SBED, LibBlockNames.ORANGE_SBED);
        registerOnlyState(BlockRegistry.PURPLE_SBED, LibBlockNames.PURPLE_SBED);
        registerOnlyState(BlockRegistry.POTION_DIFFUSER, LibBlockNames.POTION_DIFFUSER);
        registerOnlyState(BlockRegistry.AGRONOMIC_SOURCELINK.get(), LibBlockNames.AGRONOMIC_SOURCELINK);
        registerOnlyState(BlockRegistry.ALCHEMICAL_BLOCK.get(), LibBlockNames.ALCHEMICAL_SOURCELINK);
        registerOnlyState(BlockRegistry.MYCELIAL_BLOCK.get(), LibBlockNames.MYCELIAL_SOURCELINK);
        registerOnlyState(BlockRegistry.VITALIC_BLOCK.get(), LibBlockNames.VITALIC_SOURCELINK);
        registerOnlyState(BlockRegistry.VOLCANIC_BLOCK.get(), LibBlockNames.VOLCANIC_SOURCELINK);
        for (var pot : BlockRegistry.flowerPots.entrySet()){
            registerOnlyState(pot.getValue(), "pots/" + LibBlockNames.Pot(pot.getKey().get().getPath()));
        }
        registerDoor(BlockRegistry.ARCHWOOD_DOOR.get(), LibBlockNames.ARCHWOOD_DOOR);

        registerNormalCube(BlockRegistry.VOID_PRISM.get(), LibBlockNames.VOID_PRISM);
        for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
            registerNormalCube(ForgeRegistries.BLOCKS.getValue(ArsNouveau.prefix( s)), s);
        }
        registerNormalCube(BlockRegistry.MAGEBLOOM_BLOCK.get(), LibBlockNames.MAGEBLOOM_BLOCK);
        registerNormalCube(BlockRegistry.FALSE_WEAVE.get(), LibBlockNames.FALSE_WEAVE);
        registerNormalCube(BlockRegistry.GHOST_WEAVE.get(), LibBlockNames.GHOST_WEAVE);
        registerNormalCube(BlockRegistry.MIRROR_WEAVE.get(), LibBlockNames.MIRROR_WEAVE);

        for(String s : LibBlockNames.DECORATIVE_SOURCESTONE){
            ResourceLocation tex = ArsNouveau.prefix( "block/" + s);
            Block block = ForgeRegistries.BLOCKS.getValue(ArsNouveau.prefix( s + "_stairs"));
            stairsBlock((StairBlock) block, tex);

            Block slab = ForgeRegistries.BLOCKS.getValue(ArsNouveau.prefix( s + "_slab"));
            slabBlock((SlabBlock) slab, ArsNouveau.prefix( s), tex);
        }
    }

    private void registerOnlyState(RegistryWrapper<? extends Block> block, String registry) {
        registerOnlyState(block.get(), registry);
    }

    private void registerOnlyState(Block block, String registry) {
        simpleBlock(block, getUncheckedModel(registry));
    }

    //waiting for forge PR to fix the datagen
    private void registerDoor(DoorBlock door, String reg) {
        doorBlock(door, reg, getBlockLoc(reg + "_bottom"), getBlockLoc(reg + "_top"));
    }


//
//    //will it work? idk
//    public void signBlock(Block sign, String reg) {
//        ModelFile signModel = models().withExistingParent(reg, ResourceLocation("block/air")).texture("particle", ArsNouveau.prefix( "block/" + reg));
//        getVariantBuilder(sign).forAllStates(s -> ConfiguredModel.builder().modelFile(signModel).build());
//    }

    public void registerNormalCube(Block block, String registry) {
        buildNormalCube(registry);
        if (LibBlockNames.DIRECTIONAL_SOURCESTONE.contains(registry)) {
            horizontalBlock(block, getUncheckedModel(registry));
        } else {
            simpleBlock(block, getUncheckedModel(registry));
        }
    }

    public static ModelFile getUncheckedModel(String registry) {
        return new ModelFile.UncheckedModelFile("ars_nouveau:block/" + registry);
    }

    public void buildNormalCube(String registryName) {
        this.models().getBuilder(registryName).parent(new ModelFile.UncheckedModelFile("block/cube_all")).texture("all", getBlockLoc(registryName));
    }

    public ResourceLocation getBlockLoc(String registryName) {
        return ArsNouveau.prefix( "block" + "/" + registryName);
    }

}
