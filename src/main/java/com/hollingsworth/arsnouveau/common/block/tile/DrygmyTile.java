package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.DrygmyEntity;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class DrygmyTile extends SummoningTile {
    public boolean converted;

    public int entityID;
    public DrygmyTile() {
        super(BlockRegistry.DRYGMY_TILE);
    }

    @Override
    public void tick() {
        super.tick();
    }
    public void convertedEffect() {
        super.convertedEffect();
        if (tickCounter >= 120 && !level.isClientSide) {
            converted = true;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(SummoningTile.CONVERTED, true));
            DrygmyEntity drygmyEntity = new DrygmyEntity(level);
            drygmyEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5);
            level.addFreshEntity(drygmyEntity);
            ParticleUtil.spawnPoof((ServerWorld) level, worldPosition.above());
            entityID = drygmyEntity.getId();
            tickCounter = 0;
            return;
        }
        if (tickCounter % 10 == 0 && !level.isClientSide) {
            Random r = level.random;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(level, worldPosition.offset(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), worldPosition, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            level.addFreshEntity(proj1);
        }
    }

}
