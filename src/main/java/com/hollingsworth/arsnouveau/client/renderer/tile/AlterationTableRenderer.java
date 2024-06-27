package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.perk.StackPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.util.RenderUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AlterationTableRenderer extends GeoBlockRenderer<AlterationTile> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

    public final ArmorStandArmorModel innerModel;
    public final ArmorStandArmorModel outerModel;

    public AlterationTableRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(new GenericModel<>("alteration_table").withEmptyAnim());
        innerModel = new ArmorStandArmorModel(p_i226006_1_.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR));
        outerModel = new ArmorStandArmorModel(p_i226006_1_.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
    }

    public void renderArmorStack(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float partialTicks, int colour) {
        matrixStack.pushPose();
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (!(state.getBlock() instanceof AlterationTable))
            return;
        if (tile.armorStack.getItem() instanceof ArmorItem armorItem) {
            // to rotate around a point: scale, point translate, rotate, object translate
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.translate(-2.1, 3.3, 0);
            double yOffset = Mth.smoothstepDerivative((Math.sin((ClientInfo.ticksInGame + ticks) / 20f) + 1f) / 2f) * 0.0625;
            if (tile.newPerkTimer >= 0) {
                // need zero it out or else it fights the translation we're doing below
                yOffset = 0;
                float percentage = Mth.abs(tile.newPerkTimer - 20) / 20f;
                double smooooooooth = Mth.smoothstep(percentage);
                double perkYOffset = 0.625 - (smooooooooth * 0.625);
                matrixStack.mulPose(Axis.YP.rotationDegrees((float) (Mth.smoothstep(tile.newPerkTimer / 40f) * 360)));
                matrixStack.translate(0, perkYOffset, 0);
            }
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180F));
            matrixStack.translate(0, yOffset + rotForSlot(armorItem.getEquipmentSlot()), 0);

            this.renderArmorPiece(tile, tile.armorStack, matrixStack, iRenderTypeBuffer, packedLightIn, getArmorModel(armorItem.getEquipmentSlot()), colour);
        } else {
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.armorStack, ItemDisplayContext.FIXED, packedLightIn, packedOverlayIn, matrixStack, iRenderTypeBuffer, tile.getLevel(), (int) tile.getBlockPos().asLong());
        }
        matrixStack.popPose();
    }

    public void renderPerks(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float partialTicks) {
        if (tile.perkList.isEmpty()) {
            return;
        }
        for (int i = 0; i < Math.min(3, tile.perkList.size()); i++) {
            ItemStack perkStack = tile.perkList.get(i);
            if (perkStack.isEmpty()) {
                continue;
            }
            matrixStack.pushPose();
            matrixStack.translate(-0.25, 0.74 - (0.175 * i), -0.3 - (0.175 * i));
            GeoBone bone = model.getBone("display").get();
            if (bone.getRotZ() != 0.0F) {
                matrixStack.mulPose(Axis.ZP.rotation(-bone.getRotZ()));
            }

            if (bone.getRotY() != 0.0F) {
                matrixStack.mulPose(Axis.YP.rotation(-bone.getRotY()));
            }

            if (bone.getRotX() != 0.0F) {
                matrixStack.mulPose(Axis.XP.rotation(-bone.getRotX()));
            }
            GeoBone locBone = model.getBone("top_" + (i + 1)).get();
            RenderUtil.translateToPivotPoint(matrixStack, locBone);
            matrixStack.scale(0.18f, 0.18f, 0.18f);
            Minecraft.getInstance().getItemRenderer().renderStatic(perkStack, ItemDisplayContext.FIXED, packedLightIn, packedOverlayIn, matrixStack, iRenderTypeBuffer, tile.getLevel(), (int) tile.getBlockPos().asLong());
            matrixStack.popPose();
        }
    }

    public float rotForSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0.3f;
            case LEGS -> -0.2f;
            case FEET -> -0.6f;
            default -> 0;
        };
    }

    private ArmorStandArmorModel getArmorModel(EquipmentSlot pSlot) {
        return (this.usesInnerModel(pSlot) ? this.innerModel : this.outerModel);
    }

    private void renderArmorPiece(AlterationTile tile, ItemStack itemstack, PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLightIn, ArmorStandArmorModel armorModel, int defaultColor) {
        if (!(itemstack.getItem() instanceof ArmorItem armoritem))
            return;

        EquipmentSlot pSlot = armoritem.getEquipmentSlot();
        Model model = getArmorModelHook(itemstack, pSlot, armorModel);
        boolean flag1 = itemstack.hasFoil();
        DyedItemColor color = itemstack.get(DataComponents.DYED_COLOR);
        if (color != null) {
            int i = color.rgb();
            float f = (i >> 16 & 255) / 255.0F;
            float f1 = (i >> 8 & 255) / 255.0F;
            float f2 = (i & 255) / 255.0F;
            this.renderModel(pPoseStack, pBuffer, packedLightIn, flag1, model, i, this.getArmorResource(itemstack, pSlot, null));
            this.renderModel(pPoseStack, pBuffer, packedLightIn, flag1, model, i, this.getArmorResource(itemstack, pSlot, "overlay"));
        } else {
            this.renderModel(pPoseStack, pBuffer, packedLightIn, flag1, model, defaultColor, this.getArmorResource(itemstack, pSlot, null));
        }
    }

    protected net.minecraft.client.model.Model getArmorModelHook(ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
        return net.neoforged.neoforge.client.ClientHooks.getArmorModel(Minecraft.getInstance().player, itemStack, slot, model);
    }

    private void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, boolean p_117111_, net.minecraft.client.model.Model pModel, int color, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(pBuffer, RenderType.armorCutoutNoCull(armorResource), false);
        pModel.renderToBuffer(pPoseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    public ResourceLocation getArmorResource(ItemStack stack, EquipmentSlot slot, @Nullable String type) {
//        ArmorItem item = (ArmorItem) stack.getItem();
//        String texture = item.getMaterial().getName();
//        String domain = "minecraft";
//        int idx = texture.indexOf(':');
//        if (idx != -1) {
//            domain = texture.substring(0, idx);
//            texture = texture.substring(idx + 1);
//        }
//        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));
//
//        s1 = ClientHooks.getArmorTexture(Minecraft.getInstance().player, stack, s1, slot, type);
//        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);
//
//        if (resourcelocation == null) {
//            resourcelocation = ResourceLocation.tryParse(s1);
//            ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
//        }

//        return resourcelocation;
    return null; //TODO: reenable armor rendering
    }

    private boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }


    @Override
    public void actuallyRender(PoseStack stack, AlterationTile tile, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.ALTERATION_TABLE.get())
            return;
        if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.PART) != ThreePartBlock.HEAD)
            return;
        Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.FACING);
        stack.pushPose();

        if (direction == Direction.NORTH) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(1, 0, -1);
        }

        if (direction == Direction.SOUTH) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(-1, 0, -1);
        }

        if (direction == Direction.WEST) {
            stack.mulPose(Axis.YP.rotationDegrees(270));

            stack.translate(0, 0, -2);
        }

        if (direction == Direction.EAST) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(0, 0, 0);

        }
        renderSlate(model, animatable);
        super.actuallyRender(stack, tile, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        stack.popPose();
    }

    @Override
    public void renderFinal(PoseStack stack, AlterationTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderFinal(stack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getBlock() != BlockRegistry.ALTERATION_TABLE.get())
            return;
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getValue(AlterationTable.PART) != ThreePartBlock.HEAD)
            return;
        Direction direction = animatable.getLevel().getBlockState(animatable.getBlockPos()).getValue(AlterationTable.FACING);
        Vector3d perkTranslate = new Vector3d(0, 0, 0);
        Quaternionf perkQuat = Axis.YP.rotationDegrees(-90);
        Vector3d armorTranslate = new Vector3d(0, 0, 0);
        Quaternionf armorQuat = Axis.YP.rotationDegrees(-90);
        if (direction == Direction.NORTH) {
            perkQuat = Axis.YP.rotationDegrees(-90);
            perkTranslate = new Vector3d(1.55, 0.00, -.5);
            armorQuat = Axis.YP.rotationDegrees(90);
            armorTranslate = new Vector3d(0.6, 0.2, .5);
        }

        if (direction == Direction.SOUTH) {
            perkQuat = Axis.YP.rotationDegrees(90);
            perkTranslate = new Vector3d(.5, 0, .5);
            armorQuat = Axis.YP.rotationDegrees(-90);
            armorTranslate = new Vector3d(1.6, 0.2, -0.5);
        }

        if (direction == Direction.WEST) {
            perkQuat = Axis.YP.rotationDegrees(0);
            perkTranslate = new Vector3d(1.5, 0, 0.5);
            armorQuat = Axis.YP.rotationDegrees(180);
            armorTranslate = new Vector3d(0.6, 0.2, -0.5);
        }

        if (direction == Direction.EAST) {
            perkQuat = Axis.YP.rotationDegrees(180);
            perkTranslate = new Vector3d(.5, 0, -.5);
            armorQuat = Axis.YP.rotationDegrees(0);
            armorTranslate = new Vector3d(1.6, 0.2, 0.5);
        }
        double ticks = animatable.getTick(animatable);
        stack.pushPose();
        stack.mulPose(armorQuat);
        stack.translate(armorTranslate.x, armorTranslate.y, armorTranslate.z);

        if (!(animatable.armorStack.getItem() instanceof ArmorItem)) {
            stack.scale(0.75f, 0.75f, 0.75f);
            stack.translate(-1.5, 1.95, 0);
        }

        this.renderArmorStack(animatable, stack, (float) ticks, bufferSource, buffer, packedLight, packedOverlay, partialTick, colour);
        stack.popPose();


        stack.pushPose();
        stack.mulPose(perkQuat);
        stack.translate(perkTranslate.x, perkTranslate.y, perkTranslate.z);
        this.renderPerks(animatable, stack, (float) ticks, bufferSource, buffer, packedLight, packedOverlay, partialTick);
        stack.popPose();
    }

    public void renderSlate(BakedGeoModel model, AlterationTile tile) {
        String[] rowNames = new String[]{"top", "mid", "bot"};
        if (tile.armorStack.isEmpty()) {
            for (String s : rowNames) {
                setSlateRow(model, s, 0);
            }
            return;
        }
        if (!(PerkUtil.getPerkHolder(tile.armorStack) instanceof StackPerkHolder armorPerkHolder)) {
            return;
        }
        List<PerkSlot> perks = armorPerkHolder.getSlotsForTier();
        for (int i = 0; i < Math.min(perks.size(), rowNames.length); i++) {
            PerkSlot perkSlot = perks.get(i);
            setSlateRow(model, rowNames[i], perkSlot.value);
        }
        List<String> remainingRows = List.of(rowNames);
        remainingRows.subList(perks.size(), remainingRows.size()).forEach(s -> setSlateRow(model, s, 0));
    }

    public void setSlateRow(BakedGeoModel model, String loc, int tier) {
        for (int i = 0; i < 4; i++) {
            if (tier != i) {
                model.getBone(loc + "_" + i).ifPresent(bone -> bone.setHidden(true));
            } else {
                model.getBone(loc + "_" + i).ifPresent(bone -> bone.setHidden(false));
            }
        }
    }


    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new GenericModel<>("alteration_table").withEmptyAnim());
    }

    @Override
    public RenderType getRenderType(AlterationTile animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public boolean shouldRenderOffScreen(AlterationTile pBlockEntity) {
        return false;
    }
}