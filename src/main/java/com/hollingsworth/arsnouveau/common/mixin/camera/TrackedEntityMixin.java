package com.hollingsworth.arsnouveau.common.mixin.camera;


import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/mixin/camera/TrackedEntityMixin.java
 * Lets entities get sent to the client even though they're not in range of the player
 */
@Mixin(value = ChunkMap.TrackedEntity.class, priority = 1100)
public abstract class TrackedEntityMixin {
    @Shadow
    @Final
    Entity entity;

    /**
     * Enables entities that are in range of a camera as well as security camera entities to be sent to the client
     */
    @ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 2))
    public boolean an$modifyFlag(boolean original, ServerPlayer player, @Local(ordinal = 0) double viewDistance) {
        if (CameraUtil.isPlayerMountedOnCamera(player)) {
            Vec3 relativePosToCamera = player.getCamera().position().subtract(entity.position());

            if (relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance)
                return true;
        }

        return entity instanceof ICameraCallback || original;
    }
}