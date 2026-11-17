package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.util.PaintingUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Painting.class)
public abstract class PaintingMixin extends Entity {
    public PaintingMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract Holder<PaintingVariant> getVariant();

    @Inject(method = "dropItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/Painting;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;", shift = At.Shift.BEFORE), cancellable = true)
    private void an$dropItem(CallbackInfo ci) {
        @Nullable ResourceKey<PaintingVariant> key = this.getVariant().getKey();
        if (key != null && key.location().getNamespace().equals("ars_nouveau")) {
            ItemStack painting = PaintingUtil.getPainting(this.level.registryAccess(), key);
            this.spawnAtLocation(painting);
            ci.cancel();
        }
    }
}
