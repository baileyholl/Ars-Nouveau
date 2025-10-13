package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.util.LevelPosMap;
import com.hollingsworth.arsnouveau.api.util.MobJarPosMap;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class WitherBehavior extends JarBehavior<WitherBoss> {
    public static LevelPosMap WITHER_MAP = new MobJarPosMap<>(WitherBoss.class);

    @Override
    public void tick(MobJarTile tile) {
        Level level = tile.getLevel();
        if (level == null) return;

        if (level.getGameTime() % 20 == 0) {
            WITHER_MAP.addPosition(level, tile.getBlockPos());
        }
    }

    @Override
    public void onRedstonePower(MobJarTile tile) {
        destroyBlocks(tile);
    }

    private void destroyBlocks(MobJarTile tile) {
        BlockPos pos = tile.getBlockPos();
        WitherBoss entity = entityFromJar(tile);
        if (!EventHooks.canEntityGrief(tile.getLevel(), entity)) return;
        for (BlockPos block : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (block.equals(pos)) continue;
            BlockState bs = tile.getLevel().getBlockState(block);
            if (bs.canEntityDestroy(tile.getLevel(), block, entity) && EventHooks.onEntityDestroyBlock(entity, block, bs)) {
                tile.getLevel().destroyBlock(block, true, entity);
            }
        }
    }

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide() || entity instanceof IDispellable || entity instanceof ISummon)
            return;

        WITHER_MAP.applyForRange(level, entity.blockPosition(), 4, (pos) -> {
            if (level.getBlockEntity(pos) instanceof MobJarTile tile && tile.getEntity() instanceof WitherBoss) {
                ItemStack rose = new ItemStack(Items.WITHER_ROSE);
                BlockPos blockPos = entity.blockPosition();
                level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), rose));
                return true;
            }
            return false;
        });
    }
}
