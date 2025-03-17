package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class ChickenBehavior extends JarBehavior<Chicken> {
    @Override
    public void tick(MobJarTile tile) {
        if(tile.getLevel().isClientSide || isPowered(tile))
            return;
        Chicken chicken = this.entityFromJar(tile);
        if(isEntityBaby(chicken)){
            return;
        }
        chicken.eggTime -= 1;
        if(chicken.eggTime <= 0){
            chicken.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (chicken.getRandom().nextFloat() - chicken.getRandom().nextFloat()) * 0.2F + 1.0F);
            chicken.spawnAtLocation(Items.EGG);
            chicken.gameEvent(GameEvent.ENTITY_PLACE);
            chicken.eggTime = chicken.getRandom().nextInt(6000) + 6000;
        }
    }
}
