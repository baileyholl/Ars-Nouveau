package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.client.ForgeHooksClient;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AlterationTableRenderer extends GeoBlockRenderer<AlterationTile> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

    public final ArmorStandArmorModel innerModel;
    public final ArmorStandArmorModel outerModel;

    public AlterationTableRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, new GenericModel<>("alteration_table").withEmptyAnim());
        innerModel = new ArmorStandArmorModel(p_i226006_1_.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR));
        outerModel =  new ArmorStandArmorModel(p_i226006_1_.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
    }

    @Override
    public void renderEarly(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        try {
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.ALTERATION_APPARATUS)
                return;
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.PART) != BedPart.HEAD)
                return;

            this.renderArmorStack(tile, matrixStack, ticks, iRenderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, partialTicks);
            this.renderPerks(tile, matrixStack, ticks, iRenderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, partialTicks);
        } catch (Throwable t) {
            t.printStackTrace();
            // Mercy for HORRIBLE RENDER CHANGING MODS
        }
    }

    public void renderArmorStack(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float partialTicks) {
        matrixStack.pushPose();
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (!(state.getBlock() instanceof AlterationTable))
            return;
        if(tile.armorStack.getItem() instanceof ArmorItem armorItem) {
            double yOffset = Math.pow(Math.cos((ClientInfo.ticksInGame + ticks)  /20f)/4, 2);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));
            matrixStack.translate(1.05, -1.65 + yOffset + rotForSlot(armorItem.getSlot()), 0);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            this.renderArmorPiece(tile.armorStack, matrixStack, iRenderTypeBuffer, packedLightIn, getArmorModel(armorItem.getSlot()));
        }else {
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.armorStack, ItemTransforms.TransformType.FIXED, packedLightIn, packedOverlayIn, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
        }
        matrixStack.popPose();
    }

    public void renderPerks(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float partialTicks){
        if(tile.perkList.isEmpty()){
            return;
        }
        for(int i = 0; i < Math.min(3, tile.perkList.size()); i++){
            ItemStack perkStack = tile.perkList.get(i);
            if(perkStack.isEmpty()){
                continue;
            }
            matrixStack.pushPose();
            matrixStack.translate(-0.25, 0.74 - (0.175 * i),-0.3 - (0.175 * i));
            GeoBone bone = (GeoBone) getGeoModelProvider().getBone("display");
            if (bone.getRotationZ() != 0.0F) {
                matrixStack.mulPose(Vector3f.ZP.rotation(-bone.getRotationZ()));
            }

            if (bone.getRotationY() != 0.0F) {
                matrixStack.mulPose(Vector3f.YP.rotation(-bone.getRotationY()));
            }

            if (bone.getRotationX() != 0.0F) {
                matrixStack.mulPose(Vector3f.XP.rotation(-bone.getRotationX()));
            }
            GeoBone locBone = (GeoBone) getGeoModelProvider().getBone("top_" + (i + 1));
            RenderUtils.moveToPivot(locBone, matrixStack);
            matrixStack.scale(0.18f, 0.18f, 0.18f);
            Minecraft.getInstance().getItemRenderer().renderStatic(perkStack, ItemTransforms.TransformType.FIXED, packedLightIn, packedOverlayIn, matrixStack, iRenderTypeBuffer, (int) tile.getBlockPos().asLong());
            matrixStack.popPose();
        }
    }

    public float rotForSlot(EquipmentSlot slot){
        switch (slot){
            case HEAD:
                return 0;
            case CHEST:
                return 0;
            case LEGS:
                return -0.2f;
            case FEET:
                return -0.3f;
            default:
                return 0;
        }
    }

    private ArmorStandArmorModel getArmorModel(EquipmentSlot pSlot) {
        return (this.usesInnerModel(pSlot) ? this.innerModel : this.outerModel);
    }

    private void renderArmorPiece(ItemStack itemstack, PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLightIn, ArmorStandArmorModel armorModel) {
        if(!(itemstack.getItem() instanceof ArmorItem armoritem))
            return;

        EquipmentSlot pSlot = armoritem.getSlot();
        Model model = getArmorModelHook(itemstack, pSlot, armorModel);
        boolean flag1 = itemstack.hasFoil();
        if (armoritem instanceof DyeableLeatherItem dyeableLeatherItem) {
            int i = dyeableLeatherItem.getColor(itemstack);
            float f = (i >> 16 & 255) / 255.0F;
            float f1 = (i >> 8 & 255) / 255.0F;
            float f2 = (i & 255) / 255.0F;
            this.renderModel(pPoseStack, pBuffer, packedLightIn, flag1, model, f, f1, f2, this.getArmorResource(itemstack, pSlot, null));
            this.renderModel(pPoseStack, pBuffer, packedLightIn, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(itemstack, pSlot, "overlay"));
        } else {
            this.renderModel(pPoseStack, pBuffer, packedLightIn, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(itemstack, pSlot, null));
        }

    }

    protected net.minecraft.client.model.Model getArmorModelHook(ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(Minecraft.getInstance().player, itemStack, slot, model);
    }

    private void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, boolean p_117111_, net.minecraft.client.model.Model pModel, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(pBuffer, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
        pModel.renderToBuffer(pPoseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, p_117114_, p_117115_, p_117116_, 1.0F);
    }

    public ResourceLocation getArmorResource(ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        s1 = ForgeHooksClient.getArmorTexture(Minecraft.getInstance().player, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    private boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }

    @Override
    public void render(AlterationTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        try {
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.ALTERATION_APPARATUS)
                return;
            if (tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.PART) != BedPart.HEAD)
                return;
            Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(AlterationTable.FACING);
            stack.pushPose();

            if (direction == Direction.NORTH) {
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                stack.translate(1, 0, -1);
            }

            if (direction == Direction.SOUTH) {
                stack.mulPose(Vector3f.YP.rotationDegrees(270));
                stack.translate(-1, 0, -1);
            }

            if (direction == Direction.WEST) {
                stack.mulPose(Vector3f.YP.rotationDegrees(270));

                stack.translate(0, 0, -2);
            }

            if (direction == Direction.EAST) {
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                stack.translate(0, 0, 0);

            }

            super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
            stack.popPose();
        } catch (Throwable t) {
            t.printStackTrace();
            // why must people change the rendering order of tesrs
        }
    }

    @Override
    public void render(GeoModel model, AlterationTile animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        renderSlate(model, animatable, partialTicks, matrixStackIn, renderTypeBuffer, packedLightIn);
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderSlate(GeoModel model, AlterationTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn){
        String[] rowNames = new String[]{"top", "mid", "bot"};
        if(tile.armorStack.isEmpty()){
            for(String s : rowNames){
                setSlateRow(model, s, 0);
            }
            return;
        }
        if(!(PerkUtil.getPerkHolder(tile.armorStack) instanceof ArmorPerkHolder armorPerkHolder)){
          return;
        }
        List<PerkSlot> perks = armorPerkHolder.getSlotsForTier();
        for(int i = 0; i < Math.min(perks.size(), rowNames.length); i++){
            PerkSlot perkSlot = perks.get(i);
            setSlateRow(model, rowNames[i], perkSlot.value);
        }
        List<String> remainingRows = List.of(rowNames);
        remainingRows.subList(perks.size(), remainingRows.size()).forEach(s -> setSlateRow(model, s, 0));
    }

    public void setSlateRow(GeoModel model, String loc, int tier){
        for(int i = 0; i < 4; i++){
            if(tier != i){
                model.getBone(loc + "_" + i).ifPresent(bone -> bone.setHidden(true));
            }else{
                model.getBone(loc + "_" + i).ifPresent(bone -> bone.setHidden(false));
            }
        }
    }


    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new GenericModel<>("alteration_table").withEmptyAnim());
    }

    @Override
    public RenderType getRenderType(AlterationTile animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntity p_188185_1_) {
        return false;
    }
}