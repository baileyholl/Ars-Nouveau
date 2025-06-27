package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;

public class ArmadilloBehavior extends JarBehavior<Armadillo> {
    @Override
    public void tick(MobJarTile tile) {
        if (tile.getLevel().isClientSide) {
            return;
        }

        Armadillo armadillo = this.entityFromJar(tile);
        if (!isEntityBaby(armadillo) && --armadillo.scuteTime <= 0) {
            armadillo.playSound(SoundEvents.ARMADILLO_SCUTE_DROP, 1.0F, (tile.getLevel().random.nextFloat() - tile.getLevel().random.nextFloat()) * 0.2F + 1.0F);
            JarBehavior.insertOrCreateItem(tile, Items.ARMADILLO_SCUTE.getDefaultInstance());
            armadillo.gameEvent(GameEvent.ENTITY_PLACE);
            armadillo.scuteTime = armadillo.pickNextScuteDropTime();
        }
    }
}
