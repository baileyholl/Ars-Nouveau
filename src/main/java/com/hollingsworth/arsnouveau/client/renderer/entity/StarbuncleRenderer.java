package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StarbuncleRenderer extends GeoEntityRenderer<Starbuncle> {
    public static MultiBufferSource.BufferSource cosmeticBuffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    public static Map<String, Function<ResourceLocation, RenderType>> specialShaders = new HashMap<>();

    static {
        // Jared's special shader, because adopter details aren't synced.
        specialShaders.put("Splonk", (texture) -> ShaderRegistry.blamed(texture, true));
        specialShaders.put("Bailey", (texture) -> ShaderRegistry.rainbowEntity(texture, ArsNouveau.prefix("textures/entity/starbuncle_mask.png"), true));
        specialShaders.put("Gootastic", RenderType::entityTranslucent);
    }

    public StarbuncleRenderer(EntityRendererProvider.Context manager) {
        super(manager, new StarbuncleModel());
    }

    @Override
    public void renderRecursively(PoseStack stack, Starbuncle animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        if (bone.getName().equals("item")) {
            stack.pushPose();
            RenderUtil.translateToPivotPoint(stack, bone);
            stack.translate(0, -0.10, 0);
            stack.scale(0.75f, 0.75f, 0.75f);
            ItemStack itemstack = animatable.getHeldStack();
            if (animatable.dynamicBehavior != null) {
                itemstack = animatable.dynamicBehavior.getStackForRender();
            }
            if (!itemstack.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, stack, bufferSource, animatable.level, (int) animatable.getOnPos().asLong());
            }
            stack.popPose();
        }
        if (animatable.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone(animatable).equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, stack, cosmeticBuffer, animatable, packedLight);
            cosmeticBuffer.endBatch();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Starbuncle entity) {
        return entity.getTexture();
    }

    @Override
    public RenderType getRenderType(Starbuncle animatable, ResourceLocation textureLocation, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return specialShaders.getOrDefault(animatable.getName().getString(), RenderType::entityCutoutNoCull).apply(textureLocation);
    }
}