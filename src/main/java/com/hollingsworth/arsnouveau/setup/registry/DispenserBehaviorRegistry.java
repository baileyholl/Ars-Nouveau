package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.DispenserRitualBehavior;
import com.hollingsworth.arsnouveau.common.block.CreativeSourceJar;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

public class DispenserBehaviorRegistry {
    public static void register() {
        for (RitualTablet tablet : RitualRegistry.getRitualItemMap().values()) {
            DispenserBlock.registerBehavior(tablet, new DispenserRitualBehavior());
        }

        DispenserBlock.registerBehavior(BlockRegistry.SOURCE_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                SourceJar jar = BlockRegistry.SOURCE_JAR.get();
                BlockItem blockItem = (BlockItem) jar.asItem();

                try {
                    this.setSuccess(
                            blockItem.place(new UnbiasedDirectionalPlaceContext(level, pos, Direction.DOWN, item, Direction.UP)).consumesAction()
                    );
                } catch (Exception exception) {
                    Log.getLogger().error("Error trying to place source jar at {}", pos, exception);
                }

                return item;
            }
        });

        DispenserBlock.registerBehavior(BlockRegistry.CREATIVE_SOURCE_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                CreativeSourceJar jar = BlockRegistry.CREATIVE_SOURCE_JAR.get();
                BlockItem blockItem = (BlockItem) jar.asItem();

                try {
                    this.setSuccess(
                            blockItem.place(new UnbiasedDirectionalPlaceContext(level, pos, Direction.DOWN, item, Direction.UP)).consumesAction()
                    );
                } catch (Exception exception) {
                    Log.getLogger().error("Error trying to place creative source jar at {}", pos, exception);
                }
                return item;
            }
        });

        DispenserBlock.registerBehavior(BlockRegistry.MOB_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING).getOpposite();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                MobJar jar = BlockRegistry.MOB_JAR.get();
                BlockItem blockItem = (BlockItem) jar.asItem();

                try {
                    this.setSuccess(
                            blockItem.place(new UnbiasedDirectionalPlaceContext(level, pos, direction, item, direction)).consumesAction()
                    );
                } catch (Exception exception) {
                    Log.getLogger().error("Error trying to place containment jar at {}", pos, exception);
                }
                return item;
            }
        });
    }

    static class UnbiasedDirectionalPlaceContext extends DirectionalPlaceContext {
        private final Direction direction;

        public UnbiasedDirectionalPlaceContext(Level level, BlockPos pos, Direction direction, ItemStack itemStack, Direction face) {
            super(level, pos, direction, itemStack, face);
            this.direction = direction;
        }

        @Override
        public @NotNull Direction getNearestLookingDirection() {
            return this.direction;
        }

        @Override
        public Direction @NotNull [] getNearestLookingDirections() {
            return switch (this.direction) {
                default ->
                        new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
                case UP ->
                        new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
                case NORTH ->
                        new Direction[]{Direction.NORTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
                case SOUTH ->
                        new Direction[]{Direction.SOUTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
                case WEST ->
                        new Direction[]{Direction.WEST, Direction.DOWN, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
                case EAST ->
                        new Direction[]{Direction.EAST, Direction.DOWN, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
            };
        }
    }
}
