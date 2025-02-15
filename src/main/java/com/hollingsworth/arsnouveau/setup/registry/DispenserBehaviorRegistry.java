package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class DispenserBehaviorRegistry {
    public static void register() {
        DispenserBlock.registerBehavior(BlockRegistry.SOURCE_JAR.get(), new OptionalDispenseItemBehavior() {
            @Override
            @SuppressWarnings("resource")
            protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
                Level level = blockSource.level();
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
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
    }
}
