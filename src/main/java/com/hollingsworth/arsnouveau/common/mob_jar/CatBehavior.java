package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.util.LevelPosMap;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class CatBehavior extends JarBehavior<Cat> {
    public static LevelPosMap CAT_MAP = new LevelPosMap(
            (level, pos) -> !(level.getBlockEntity(pos) instanceof MobJarTile mobJarTile) || !(mobJarTile.getEntity() instanceof Cat)
    );

    @Override
    public void tick(MobJarTile tile) {
        Level level = tile.getLevel();
        if (level == null) {
            return;
        }

        if (level.getGameTime() % 20 == 0) {
            CAT_MAP.addPosition(level, tile.getBlockPos());
        }
    }

    @Override
    public int getAnalogPower(MobJarTile tile) {
        var level = tile.getLevel();

        if (!(level instanceof ServerLevel)) {
            return super.getAnalogPower(tile);
        }

        Cat cat = this.entityFromJar(tile);
        return cat.getOwner() == null ? 0 : 15;
    }

    @SubscribeEvent
    public static void sleepEvent(SleepFinishedTimeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        var key = level.dimension().location().toString();
        if (!CAT_MAP.posMap.containsKey(key)) {
            return;
        }

        var positions = CAT_MAP.posMap.getOrDefault(key, new HashSet<>());
        List<BlockPos> stale = new ArrayList<>();
        for (BlockPos p : positions) {
            if (!level.isLoaded(p)) {
                continue;
            }

            if (CAT_MAP.removeFunction.apply(level, p)) {
                stale.add(p);
                continue;
            }

            var tile = (MobJarTile) level.getBlockEntity(p);
            var cat = (Cat) tile.getEntity();

            if (!(cat.getOwner() instanceof Player owner) || cat.getRandom().nextFloat() > 0.7F) {
                return;
            }

            if (owner.distanceToSqr(cat) <= 10 * 10 && owner.getSleepTimer() >= 100) {
                LootTable loottable = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.CAT_MORNING_GIFT);
                LootParams lootparams = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, cat.position())
                        .withParameter(LootContextParams.THIS_ENTITY, cat)
                        .create(LootContextParamSets.GIFT);

                for (ItemStack itemstack : loottable.getRandomItems(lootparams)) {
                    JarBehavior.insertOrCreateItem(tile, itemstack);
                }
            }
        }

        for (BlockPos pos : stale) {
            CAT_MAP.posMap.get(key).remove(pos);
        }
    }
}
