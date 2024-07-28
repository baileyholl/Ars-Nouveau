package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.common.items.data.StackPerkHolder;
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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.Map;

public class AlterationTableRenderer extends GeoBlockRenderer<AlterationTile> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

    public final ArmorStandArmorModel innerModel;
    public final ArmorStandArmorModel outerModel;
    private final TextureAtlas armorTrimAtlas;
    public AlterationTableRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(new GenericModel<>("alteration_table").withEmptyAnim());
        innerModel = new ArmorStandArmorModel(p_i226006_1_.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR));
        outerModel = new ArmorStandArmorModel(p_i226006_1_.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
        this.armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
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
        boolean innerModel = this.usesInnerModel(pSlot);
        var dyeColor = itemstack.get(DataComponents.DYED_COLOR);
        int color = dyeColor != null ? FastColor.ABGR32.opaque(dyeColor.rgb()) : -1;
        ArmorMaterial armormaterial = armoritem.getMaterial().value();
        for (ArmorMaterial.Layer armormaterial$layer : armormaterial.layers()) {
            int j = armormaterial$layer.dyeable() ? color : -1;
            var texture = net.neoforged.neoforge.client.ClientHooks.getArmorTexture(Minecraft.getInstance().player, itemstack, armormaterial$layer, innerModel, pSlot);
            this.renderModel(pPoseStack, pBuffer, packedLightIn, model, j, texture);
        }

        ArmorTrim armortrim = itemstack.get(DataComponents.TRIM);
        if (armortrim != null) {
            this.renderTrim(armoritem.getMaterial(), pPoseStack, pBuffer, packedLightIn, armortrim, model, innerModel);
        }

        if (itemstack.hasFoil()) {
            this.renderGlint(pPoseStack, pBuffer, packedLightIn, model);
        }
    }

    protected net.minecraft.client.model.Model getArmorModelHook(ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
        return net.neoforged.neoforge.client.ClientHooks.getArmorModel(Minecraft.getInstance().player, itemStack, slot, model);
    }

    private void renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_, net.minecraft.client.model.Model p_289658_, int p_350798_, ResourceLocation p_324344_) {
        VertexConsumer vertexconsumer = p_289689_.getBuffer(RenderType.armorCutoutNoCull(p_324344_));
        p_289658_.renderToBuffer(p_289664_, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, p_350798_);
    }

    private boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }


    private void renderTrim(
            Holder<ArmorMaterial> p_323506_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, net.minecraft.client.model.Model p_289663_, boolean p_289651_
    ) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas
                .getSprite(p_289651_ ? p_289692_.innerTexture(p_323506_) : p_289692_.outerTexture(p_323506_));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet(p_289692_.pattern().value().decal())));
        p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY);
    }


    private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, net.minecraft.client.model.Model p_289659_) {
        p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY);
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
            stack.translate(1, 0, 0);
        }

        if (direction == Direction.SOUTH) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(-1, 0, 0);
        }

        if (direction == Direction.WEST) {
            stack.mulPose(Axis.YP.rotationDegrees(270));

            stack.translate(0, 0, -1);
        }

        if (direction == Direction.EAST) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(0, 0, 1);

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
        if (direction == Direction.NORTH) {
            perkQuat = Axis.YP.rotationDegrees(-90);
            perkTranslate = new Vector3d(1.55, 0.00, -.5);
        }

        if (direction == Direction.SOUTH) {
            perkQuat = Axis.YP.rotationDegrees(90);
            perkTranslate = new Vector3d(.5, 0, .5);
        }

        if (direction == Direction.WEST) {
            perkQuat = Axis.YP.rotationDegrees(0);
            perkTranslate = new Vector3d(1.5, 0, 0.5);
        }

        if (direction == Direction.EAST) {
            perkQuat = Axis.YP.rotationDegrees(180);
            perkTranslate = new Vector3d(.5, 0, -.5);
        }
        double ticks = animatable.getTick(animatable);
        stack.pushPose();
        stack.mulPose(perkQuat);
        stack.translate(perkTranslate.x, perkTranslate.y + 0.2, perkTranslate.z);

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
        List<PerkSlot> perks = armorPerkHolder.getSlotsForTier(tile.armorStack);

        for (int i = 0; i < Math.min(perks.size(), rowNames.length); i++) {
            PerkSlot perkSlot = perks.get(i);
            setSlateRow(model, rowNames[i], perkSlot.value());
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
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(AlterationTile blockEntity) {
        return AABB.INFINITE;
    }
}