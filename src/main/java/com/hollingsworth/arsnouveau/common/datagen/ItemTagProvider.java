package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(DataGenerator p_126530_, BlockTagsProvider p_126531_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126530_, p_126531_, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {


        this.tag(ItemTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_food")))
                .add(ItemsRegistry.SOURCE_BERRY_PIE,
                        ItemsRegistry.SOURCE_BERRY_ROLL);
        this.tag(ItemTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "whirlisprig/denied_drop")))
                .add(Items.DIRT).addTags(Tags.Items.SEEDS);

        this.tag(Tags.Items.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(Tags.Items.FENCES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(Tags.Items.FENCE_GATES).add(BlockRegistry.ARCHWOOD_FENCE_GATE.asItem());
        this.tag(Tags.Items.FENCE_GATES_WOODEN).add(BlockRegistry.ARCHWOOD_FENCE_GATE.asItem());
        this.tag(ItemTags.createOptional(new ResourceLocation("forge", "gems/source")))
                .add(ItemsRegistry.SOURCE_GEM);

        this.tag(ItemTags.createOptional(new ResourceLocation("forge", "logs/archwood")))
                .add(BlockRegistry.BLAZING_LOG.asItem(),
                        BlockRegistry.CASCADING_LOG.asItem(),
                        BlockRegistry.VEXING_LOG.asItem(),
                        BlockRegistry.FLOURISHING_LOG.asItem(),
                        BlockRegistry.BLAZING_WOOD.asItem(),
                        BlockRegistry.CASCADING_WOOD.asItem(),
                        BlockRegistry.FLOURISHING_WOOD.asItem(),
                        BlockRegistry.VEXING_WOOD.asItem());
        this.tag(ItemTags.LOGS).add(
                BlockRegistry.BLAZING_LOG.asItem(),
                BlockRegistry.CASCADING_LOG.asItem(),
                BlockRegistry.VEXING_LOG.asItem(),
                BlockRegistry.FLOURISHING_LOG.asItem(),
                BlockRegistry.BLAZING_WOOD.asItem(),
                BlockRegistry.CASCADING_WOOD.asItem(),
                BlockRegistry.FLOURISHING_WOOD.asItem(),
                BlockRegistry.VEXING_WOOD.asItem()
        );
        this.tag(ItemTags.LOGS_THAT_BURN).add(
                BlockRegistry.BLAZING_LOG.asItem(),
                BlockRegistry.CASCADING_LOG.asItem(),
                BlockRegistry.VEXING_LOG.asItem(),
                BlockRegistry.FLOURISHING_LOG.asItem(),
                BlockRegistry.BLAZING_WOOD.asItem(),
                BlockRegistry.CASCADING_WOOD.asItem(),
                BlockRegistry.FLOURISHING_WOOD.asItem(),
                BlockRegistry.VEXING_WOOD.asItem()
        );
        this.tag(ItemTags.createOptional(new ResourceLocation("forge", "planks/archwood")))
                .add(BlockRegistry.ARCHWOOD_PLANK.asItem());
        this.tag(Tags.Items.SEEDS)
                .add(BlockRegistry.MAGE_BLOOM_CROP.asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS).add(BlockRegistry.SOURCE_GEM_BLOCK.asItem());
        this.tag(ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/source")))
                .add(BlockRegistry.SOURCE_GEM_BLOCK.asItem());
        this.tag(Tags.Items.GEMS).add(ItemsRegistry.SOURCE_GEM);

        this.tag(ItemTags.PLANKS).add(BlockRegistry.ARCHWOOD_PLANK.asItem());
        this.tag(ItemTags.FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(ItemTags.WOODEN_FENCES).add(BlockRegistry.ARCHWOOD_FENCE.asItem());
        this.tag(ItemTags.BEACON_PAYMENT_ITEMS).add(ItemsRegistry.SOURCE_GEM);
        this.tag(ItemTags.BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON.asItem());
        this.tag(ItemTags.WOODEN_BUTTONS).add(BlockRegistry.ARCHWOOD_BUTTON.asItem());
        this.tag(ItemTags.DOORS).add(BlockRegistry.ARCHWOOD_DOOR.asItem());
        this.tag(ItemTags.WOODEN_DOORS).add(BlockRegistry.ARCHWOOD_DOOR.asItem());
        this.tag(ItemTags.SAPLINGS).add( BlockRegistry.BLAZING_SAPLING.asItem(),
                BlockRegistry.CASCADING_SAPLING.asItem(),
                BlockRegistry.FLOURISHING_SAPLING.asItem(),
                BlockRegistry.VEXING_SAPLING.asItem());
        this.tag(ItemTags.SLABS).add(BlockRegistry.ARCHWOOD_SLABS.asItem());
        this.tag(ItemTags.WOODEN_SLABS).add(BlockRegistry.ARCHWOOD_SLABS.asItem());
        this.tag(ItemTags.STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS.asItem());
        this.tag(ItemTags.WOODEN_STAIRS).add(BlockRegistry.ARCHWOOD_STAIRS.asItem());
        this.tag(ItemTags.TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR.asItem());
        this.tag(ItemTags.WOODEN_TRAPDOORS).add(BlockRegistry.ARCHWOOD_TRAPDOOR.asItem());
        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(BlockRegistry.ARCHWOOD_PPlate.asItem());
    }
}
