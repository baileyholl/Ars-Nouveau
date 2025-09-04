package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.items.MobJarItem;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MobJarItemRenderer extends GeoItemRenderer<MobJarItem> {
    private static MobJarTile jarTile;

    public MobJarItemRenderer() {
        super(new GenericModel<>("mob_jar"));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (transformType == ItemDisplayContext.GUI) {
            pPackedLight = LightTexture.FULL_BRIGHT;
            pPackedOverlay = LightTexture.FULL_BRIGHT;
        }
        super.renderByItem(stack, transformType, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);

        jarTile = new MobJarTile(Minecraft.getInstance().player.getOnPos().above(), BlockRegistry.MOB_JAR.defaultBlockState());
        Entity entity = MobJarItem.fromItem(stack, Minecraft.getInstance().level);
        if (entity == null)
            return;
        jarTile.setLevel(Minecraft.getInstance().level);
        jarTile.cachedEntity = entity;
        entity.setPos(Minecraft.getInstance().player.getOnPos().getX(), Minecraft.getInstance().player.getOnPos().getY() + 1, Minecraft.getInstance().player.getOnPos().getZ());
        pPoseStack.pushPose();
        pPoseStack.translate(0, .5, 0);
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(jarTile, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }
}
