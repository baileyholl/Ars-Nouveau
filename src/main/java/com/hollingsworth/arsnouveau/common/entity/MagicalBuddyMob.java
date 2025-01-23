package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class MagicalBuddyMob extends PathfinderMob implements GeoEntity, IDispellable {


    public int tamingTime;
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    protected MagicalBuddyMob(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @OnlyIn(Dist.CLIENT)
    /*
     * The matrixStack is already ready to be transformed, no need of push/pop.
     *
     */
    public void adjustShoulderPosition(PoseStack pMatrixStack, boolean pLeftShoulder, Player pLivingEntity) {
        pMatrixStack.translate(pLeftShoulder ? 0.4F : -0.4F, pLivingEntity.isCrouching() ? +.2F : 0F, 0.0F);
        pMatrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        this.yHeadRot = pLivingEntity.yHeadRot;
        this.yHeadRotO = pLivingEntity.yHeadRot;

    }

    public void setEntityOnShoulder(ServerPlayer pPlayer) {
        CompoundTag compoundtag = new CompoundTag();
        var id = this.getEncodeId();
        if (id == null) return;
        compoundtag.putString("id", id);
        this.saveWithoutId(compoundtag);
        if (pPlayer.setEntityOnShoulder(compoundtag)) {
            this.discard();
        }
    }


    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer instanceof ServerPlayer sp && sp.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && sp.isShiftKeyDown()) {
            this.setEntityOnShoulder(sp);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }
}
