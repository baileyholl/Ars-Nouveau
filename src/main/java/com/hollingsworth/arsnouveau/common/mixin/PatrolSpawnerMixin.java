
package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.world.WildenPatrolSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ServerLevel.class)
public class PatrolSpawnerMixin {

    @Unique
    private static final List<CustomSpawner> ars_Nouveau$customSpawners = new CopyOnWriteArrayList<>();

    static{
        ars_Nouveau$customSpawners.add(new WildenPatrolSpawner());
    }
    @Inject(at = @At("TAIL"), method = "tickCustomSpawners")
    private void ars_Nouveau$tickCustomSpawners(boolean pSpawnEnemies, boolean pSpawnFriendlies, CallbackInfo ci) {
        //call custom spawner
        for (CustomSpawner customSpawner : ars_Nouveau$customSpawners) {
            customSpawner.tick((ServerLevel) (Object) this, pSpawnEnemies, pSpawnFriendlies);
        }
    }


}
