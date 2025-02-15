package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.DispenserRitualBehavior;
import com.hollingsworth.arsnouveau.common.block.CreativeSourceJar;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class DispenserBehaviorRegistry {
    public static void register() {
        for (RitualTablet tablet : RitualRegistry.getRitualItemMap().values()){
            DispenserBlock.registerBehavior(tablet, new DispenserRitualBehavior());
        }

        DispenserBlock.registerBehavior(BlockRegistry.SOURCE_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            @SuppressWarnings("resource")
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                SourceJar jar = BlockRegistry.SOURCE_JAR.get();
                if (level.isEmptyBlock(pos)) {
                    if (!level.isClientSide) {
                        var state = jar.defaultBlockState();
                        level.setBlock(pos, state, 3);
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
                        var be = level.getBlockEntity(pos);
                        if (be instanceof SourceJarTile tile) {
                            tile.setSource(BlockFillContents.get(item));
                        }
                    }

                    item.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(false);
                }
                return item;
            }
        });

        DispenserBlock.registerBehavior(BlockRegistry.CREATIVE_SOURCE_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            @SuppressWarnings("resource")
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                CreativeSourceJar jar = BlockRegistry.CREATIVE_SOURCE_JAR.get();
                if (level.isEmptyBlock(pos)) {
                    if (!level.isClientSide) {
                        var state = jar.defaultBlockState();
                        level.setBlock(pos, state, 3);
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
                    }

                    item.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(false);
                }
                return item;
            }
        });

        DispenserBlock.registerBehavior(BlockRegistry.MOB_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            @SuppressWarnings("resource")
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                MobJar jar = BlockRegistry.MOB_JAR.get();
                if (level.isEmptyBlock(pos)) {
                    if (!level.isClientSide) {
                        var state = jar.defaultBlockState().setValue(MobJar.FACING, direction);
                        level.setBlock(pos, state, 3);
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
                        var be = level.getBlockEntity(pos);
                        if (be instanceof MobJarTile tile) {
                            Entity entity = MobJarItem.fromItem(item, level);
                            if (entity != null) {
                                tile.setEntityData(entity);
                            }
                        }
                    }

                    item.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(false);
                }
                return item;
            }
        });
    }
}
