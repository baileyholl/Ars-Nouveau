package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;

public class PandaBehavior extends JarBehavior<Panda> {

    @Override
    public void tick(MobJarTile tile) {
        if(tile.getLevel().isClientSide)
            return;
        Panda panda = entityFromJar(tile);
        if(!panda.isSneezing() && canSneeze(panda)){
            panda.sneeze(true);
        }
        if(panda.isSneezing()){

            panda.setSneezeCounter(panda.getSneezeCounter() + 1);
            if (panda.getSneezeCounter() > 20) {
                panda.sneeze(false);
                afterSneeze(panda, tile);
            } else if (panda.getSneezeCounter() == 1) {
                panda.playSound(SoundEvents.PANDA_PRE_SNEEZE, 1.0F, 1.0F);
            }
        }
    }

    public void afterSneeze(Panda panda, MobJarTile tile){
        panda.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);
        if (!panda.level.isClientSide() && panda.getRandom().nextInt(700) == 0 && panda.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            JarBehavior.insertOrCreateItem(tile, Items.SLIME_BALL.getDefaultInstance());
        }
    }

    public boolean canSneeze(Panda panda){
        if (panda.isBaby()) {
            if (panda.isWeak() && panda.getRandom().nextInt(reducedTickDelay(500)) == 1) {
                return true;
            } else {
                return panda.getRandom().nextInt(reducedTickDelay(6000)) == 1;
            }
        }
        return false;
    }

    public static int reducedTickDelay(int pReduction) {
        return Mth.positiveCeilDiv(pReduction, 2);
    }

}
