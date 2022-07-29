package com.hollingsworth.arsnouveau.api.client;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.util.RenderUtils;

public class CosmeticRenderUtil {

    public static <T extends Entity & IDecoratable> void renderCosmetic(GeoBone bone, PoseStack matrix, MultiBufferSource buffer, T entity, int packedLightIn) {
        ItemStack stack = entity.getCosmeticItem();
        if (!(stack.getItem() instanceof ICosmeticItem cosmetic)) return;
        //checks should have already been made, but pattern variables ftw
        matrix.pushPose();

        RenderUtils.moveToPivot(bone, matrix);
        RenderUtils.rotate(bone, matrix);
        RenderUtils.translate(bone, matrix);
        Vec3 translations = cosmetic.getTranslations();
        Vec3 scaling = cosmetic.getScaling();
        matrix.translate(translations.x, translations.y, translations.z);
        matrix.scale((float) scaling.x, (float) scaling.y, (float) scaling.z);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, cosmetic.getTransformType(), packedLightIn, OverlayTexture.NO_OVERLAY, matrix, buffer, (int) entity.getOnPos().asLong());

        matrix.popPose();
    }

}
