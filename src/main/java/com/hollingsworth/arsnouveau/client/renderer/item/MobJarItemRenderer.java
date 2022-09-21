package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class MobJarItemRenderer extends FixedGeoItemRenderer<MobJarItem> {

    public MobJarItemRenderer() {
        super(new GenericModel("mob_jar"));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        super.renderByItem(stack, pTransformType, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);

        Entity entity = MobJarItem.fromItem(stack);
        if(entity == null)
            return;
        pPoseStack.pushPose();
        float f = 0.43125F;
        float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());

        if ((double)f1 > 1.0d) {
            f /= f1 * 1.0;
        }
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        pPoseStack.scale(f,f,f);
        pPoseStack.translate(-1.5, 1.5, 1);

        Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.popPose();
    }
}
