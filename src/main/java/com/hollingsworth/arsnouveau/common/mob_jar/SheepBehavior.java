package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SheepBehavior extends JarBehavior<Sheep> {
    @Override
    public void tick(MobJarTile tile) {
        Sheep sheep = entityFromJar(tile);
        if (!sheep.level.isClientSide && sheep.isSheared()) {
            if (sheep.getRandom().nextInt(sheep.isBaby() ? 50 : 1000) != 0) {
                return;
            }
            BlockPos pos1 = tile.getBlockPos().below();
            if (sheep.level.getBlockState(pos1).is(Blocks.GRASS_BLOCK)) {
                sheep.level.broadcastEntityEvent(sheep, (byte) 10);
                sheep.level.levelEvent(2001, pos1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                sheep.level.setBlock(pos1, Blocks.DIRT.defaultBlockState(), 2);
                sheep.ate();
                syncClient(tile);
            }
        }
    }
}
