package com.hollingsworth.arsnouveau.api.client;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.util.RenderUtil;


public class CosmeticRenderUtil {

    public static <T extends LivingEntity & IDecoratable> void renderCosmetic(GeoBone bone, PoseStack matrix, MultiBufferSource buffer, T entity, int packedLightIn) {
        ItemStack stack = entity.getCosmeticItem();
        if (!(stack.getItem() instanceof ICosmeticItem cosmetic)) return;
        //checks should have already been made, but pattern variables ftw
        matrix.pushPose();

        // GeckoLib 5.4.5: translateToPivotPoint/rotateMatrixAroundBone/translateMatrixToBone → prepMatrixForBone
        RenderUtil.prepMatrixForBone(matrix, bone);
        Vec3 translations = cosmetic.getTranslations(entity);
        Vec3 scaling = cosmetic.getScaling(entity);
        matrix.translate(translations.x, translations.y, translations.z);
        matrix.scale((float) scaling.x, (float) scaling.y, (float) scaling.z);
        // TODO: 1.21.11 — ItemRenderer.renderStatic() removed. Port to ItemModelResolver + ItemStackRenderState
        // pipeline with SubmitNodeCollector when cosmetic rendering is fully migrated to GeckoLib 5 layers.

        matrix.popPose();
    }

}
