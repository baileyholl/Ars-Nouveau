package com.hollingsworth.arsnouveau.common.mixin.scrying;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.event.ScryEvents;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean shouldEntityAppearGlowing(Minecraft instance, Entity entity, Operation<Boolean> original) {
        Player player = instance.player;
        if (player == null) return original.call(instance, entity);

        IScryer scryer = ScryEvents.getScryer();
        if (scryer == null || !scryer.revealsEntities()) return original.call(instance, entity);
        Vec3i size = scryer.getScryingSize();
        int horizontal = Math.max(size.getX(), size.getZ());
        int vertical = size.getY();
        if (!player.position().closerThan(entity.position(), horizontal, vertical)) return original.call(instance, entity);

        return scryer.shouldRevealEntity(entity, player);
    }

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
    private int getTeamColor(Entity entity, Operation<Integer> original) {
        IScryer scryer = ScryEvents.getScryer();
        if (scryer == null || !scryer.revealsEntities()) return original.call(entity);

        int color = original.call(entity);
        if (color != 16777215) return color;

        if (scryer.shouldRevealEntity(entity, minecraft.player)) {
            return scryer.getParticleColor().getColor();
        }

        return color;
    }
}
