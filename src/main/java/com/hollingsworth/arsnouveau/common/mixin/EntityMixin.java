package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.event.EntityPreRemovalEvent;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.hollingsworth.arsnouveau.common.world.saved_data.AlliesSavedData;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public Level level;

    @ModifyReturnValue(method = "isInWaterOrRain", at = @At("RETURN"))
    private boolean ars_nouveau$isInWaterOrRain(boolean original) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity && ars_nouveau$isWet(livingEntity)) {
            return true;
        }
        return original;
    }

    @Inject(method = "setRemainingFireTicks", at = @At("HEAD"), cancellable = true)
    private void ars_nouveau$setSecondsOnFire(int pRemainingFireTicks, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity && ars_nouveau$isWet(livingEntity) && pRemainingFireTicks > 0) {
            livingEntity.clearFire();
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void onRemoval(Entity.RemovalReason removalReason, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new EntityPreRemovalEvent(this.level, (Entity) (Object) this));
    }

@Unique
    private static boolean ars_nouveau$isWet(LivingEntity livingEntity) {
      return livingEntity.hasEffect(ModPotions.SOAKED_EFFECT) || livingEntity.getVehicle() instanceof BubbleEntity;
    }

    /**
     * This mixin is used to add the ability for players to set other players as allies.
     * This is used to prevent players from damaging each other with spells.
     *
     * @param original the original return value of the method
     * @param entity   the entity that is going to damage the current entity
     */
    @ModifyReturnValue(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private boolean ars_nouveau$isAlliedTo(boolean original, Entity entity) {
        // the check only matters for players server-side
        if (!((Entity) (Object) this instanceof Player target && entity instanceof Player dealer && dealer.level() instanceof ServerLevel serverLevel))
            return original;
        // check if the target is a player and the dealer has the target set as an ally
        return original || AlliesSavedData.getAllies(serverLevel, dealer.getUUID()).contains(target.getUUID());
    }

}
