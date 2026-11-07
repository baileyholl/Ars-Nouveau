package com.hollingsworth.arsnouveau.common.block.tile.schematic;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ItemRequirement {
    public static final ItemRequirement NONE = new ItemRequirement(List.of());
    public static final ItemRequirement INVALID = new ItemRequirement(List.of());

    private final List<StackRequirement> requiredItems;

    public ItemRequirement(List<StackRequirement> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public ItemRequirement(ItemStack stack) {
        this(List.of(new StackRequirement(stack, false)));
    }

    public static ItemRequirement of(BlockState state, @Nullable BlockEntity blockEntity) {
        if (state.is(Blocks.AIR))
            return NONE;
        Block block = state.getBlock();
        Item item = block.asItem();
        if (item == Items.AIR)
            return INVALID;
        if (state.hasProperty(BlockStateProperties.SLAB_TYPE) && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE)
            return new ItemRequirement(new ItemStack(item, 2));
        if (block instanceof TurtleEggBlock)
            return new ItemRequirement(new ItemStack(item, state.getValue(TurtleEggBlock.EGGS)));
        if (block instanceof SeaPickleBlock)
            return new ItemRequirement(new ItemStack(item, state.getValue(SeaPickleBlock.PICKLES)));
        if (block instanceof SnowLayerBlock)
            return new ItemRequirement(new ItemStack(item, state.getValue(SnowLayerBlock.LAYERS)));
        if (block instanceof FarmBlock || block instanceof DirtPathBlock)
            return new ItemRequirement(new ItemStack(Items.DIRT));
        if (block instanceof AbstractBannerBlock && blockEntity instanceof BannerBlockEntity banner)
            return new ItemRequirement(List.of(new StackRequirement(banner.getItem(), true)));
        if (block == Blocks.TALL_GRASS)
            return new ItemRequirement(new ItemStack(Items.SHORT_GRASS, 2));
        if (block == Blocks.LARGE_FERN)
            return new ItemRequirement(new ItemStack(Items.FERN, 2));
        return new ItemRequirement(new ItemStack(item));
    }

    public static ItemRequirement of(Entity entity) {
        if (entity instanceof ItemFrame itemFrame) {
            ItemStack frame = new ItemStack(itemFrame instanceof GlowItemFrame ? Items.GLOW_ITEM_FRAME : Items.ITEM_FRAME);
            ItemStack displayed = itemFrame.getItem();
            if (displayed.isEmpty())
                return new ItemRequirement(frame);
            return new ItemRequirement(List.of(new StackRequirement(frame, false), new StackRequirement(displayed, true)));
        }
        if (entity instanceof ArmorStand armorStand) {
            List<StackRequirement> requirements = new ArrayList<>();
            requirements.add(new StackRequirement(new ItemStack(Items.ARMOR_STAND), false));
            for (ItemStack stack : armorStand.getAllSlots()) {
                if (!stack.isEmpty())
                    requirements.add(new StackRequirement(stack, true));
            }
            return new ItemRequirement(requirements);
        }
        return INVALID;
    }

    public boolean isEmpty() {
        return this == NONE;
    }

    public boolean isInvalid() {
        return this == INVALID;
    }

    public List<StackRequirement> getRequiredItems() {
        return requiredItems;
    }

    public ItemRequirement union(ItemRequirement other) {
        if (isInvalid() || other.isInvalid())
            return INVALID;
        if (isEmpty())
            return other;
        if (other.isEmpty())
            return this;
        return new ItemRequirement(Stream.concat(requiredItems.stream(), other.requiredItems.stream()).toList());
    }

    public record StackRequirement(ItemStack stack, boolean strictComponents) {
        public boolean matches(ItemStack other) {
            return strictComponents ? ItemStack.isSameItemSameComponents(stack, other) : ItemStack.isSameItem(stack, other);
        }
    }
}
