package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.common.items.data.StackPerkHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.Map;

// GeckoLib 5.4.2 migration:
// - GeoBlockRenderer now requires 2 type params <T, R extends BlockEntityRenderState & GeoRenderState>
// - actuallyRender() REMOVED - direction rotation ported to adjustRenderPose(RenderPassInfo)
// - getRenderType(T, Identifier, MultiBufferSource, float) signature changed to (R, Identifier)
// - ArmorStandArmorModel moved to net.minecraft.client.model.object.armorstand
// - ArmorMaterial moved to net.minecraft.world.item.equipment.ArmorMaterial
// - ArmorTrim moved to net.minecraft.world.item.equipment.trim.ArmorTrim
// - FastColor.ABGR32/ARGB32 replaced by ARGB class
// TODO: Port render() override (item/perk rendering) to preRenderPass or a separate hook in GeckoLib 5.
// The render() method no longer exists; use captureDefaultRenderState + preRenderPass for complex rendering.
public class AlterationTableRenderer extends GeoBlockRenderer<AlterationTile, ArsBlockEntityRenderState> {
    private static final Map<String, Identifier> ARMOR_LOCATION_CACHE = Maps.newHashMap();

    public final ArmorStandArmorModel innerModel;
    public final ArmorStandArmorModel outerModel;
    private final TextureAtlas armorTrimAtlas;

    public AlterationTableRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(new GenericModel<>("alteration_table").withEmptyAnim());
        // 1.21.11: ARMOR_STAND_INNER/OUTER_ARMOR removed; replaced by ARMOR_STAND_ARMOR (ArmorModelSet)
        // Stub with null for now; TODO: port to new ArmorModelSet.bake() API
        innerModel = null;
        outerModel = null;
        // 1.21.11: ModelManager.getAtlas() removed; TODO: port armor trim rendering
        this.armorTrimAtlas = null;
    }

    @Override
    public ArsBlockEntityRenderState createRenderState() {
        return new ArsBlockEntityRenderState();
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ArsBlockEntityRenderState> renderPassInfo) {
        super.adjustRenderPose(renderPassInfo);
        BlockState state = renderPassInfo.renderState().blockState;
        if (state.getValue(AlterationTable.PART) != ThreePartBlock.HEAD) return;
        Direction direction = state.getValue(AlterationTable.FACING);
        PoseStack stack = renderPassInfo.poseStack();
        if (direction == Direction.NORTH) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(1, 0, 0);
        } else if (direction == Direction.SOUTH) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(-1, 0, 0);
        } else if (direction == Direction.WEST) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(0, 0, -1);
        } else if (direction == Direction.EAST) {
            stack.mulPose(Axis.YP.rotationDegrees(-90));
            stack.translate(0, 0, 1);
        }
    }

    @Override
    public RenderType getRenderType(ArsBlockEntityRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    // TODO: GeckoLib 5 + 1.21.11: Port item/perk/armor rendering.
    // Removed APIs: model.getBone(), bone.getRotX/Y/Z(), RenderUtil.translateToPivotPoint(),
    // ItemRenderer.renderStatic(7-arg), animatable.getTick(), bone.setHidden().
    // All rendering here is stubbed until the GeckoLib 5 migration is complete.
    public void renderForTile(AlterationTile animatable, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Stub - see class-level TODO
    }

    public void renderArmorStack(AlterationTile tile, PoseStack matrixStack, float ticks, MultiBufferSource iRenderTypeBuffer, int packedLightIn, int packedOverlayIn) {
        // Stub - see class-level TODO
    }

    public void renderPerks(AlterationTile tile, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int packedLightIn, int packedOverlayIn) {
        // Stub - see class-level TODO
    }

    public void renderSlate(AlterationTile tile, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight) {
        // Stub - see class-level TODO
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

    // TODO: Port armor rendering to 1.21.11 EquipmentClientInfo / EquipmentClientInfo.Layer API.
    // ArmorItem was removed; equipment slot comes from DataComponents.EQUIPPABLE.
    // ArmorMaterial.layers() no longer exists; use EquipmentClientInfo.getLayers(LayerType) instead.
    // ClientHooks.getArmorModel / getArmorTexture signatures have changed.
    private void renderArmorPiece(AlterationTile tile, ItemStack itemstack, PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLightIn, ArmorStandArmorModel armorModel) {
        var equippable = itemstack.getComponents().get(DataComponents.EQUIPPABLE);
        if (equippable == null)
            return;

        EquipmentSlot pSlot = equippable.slot();
        setPartVisibility(armorModel, pSlot);
        // Full armor layer rendering requires EquipmentClientInfo — stubbed until ported.
    }

    private void renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_, Model p_289658_, int p_350798_, Identifier p_324344_) {
        VertexConsumer vertexconsumer = p_289689_.getBuffer(RenderTypes.armorCutoutNoCull(p_324344_));
        p_289658_.renderToBuffer(p_289664_, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, p_350798_);
    }

    private boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }

    private void renderTrim(
            Holder<ArmorMaterial> p_323506_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, Model p_289663_, boolean p_289651_
    ) {
        // TODO: 1.21.11: ArmorTrim.innerTexture/outerTexture removed; use new EquipmentClientInfo-based API
    }

    private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, Model p_289659_) {
        p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderTypes.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new GenericModel<>("alteration_table").withEmptyAnim());
    }

    protected void setPartVisibility(HumanoidModel<?> pModel, EquipmentSlot pSlot) {
        pModel.setAllVisible(false);
        switch (pSlot) {
            case HEAD:
                pModel.head.visible = true;
                pModel.hat.visible = true;
                break;
            case CHEST:
                pModel.body.visible = true;
                pModel.rightArm.visible = true;
                pModel.leftArm.visible = true;
                break;
            case LEGS:
                pModel.body.visible = true;
                pModel.rightLeg.visible = true;
                pModel.leftLeg.visible = true;
                break;
            case FEET:
                pModel.rightLeg.visible = true;
                pModel.leftLeg.visible = true;
        }
    }

    // 1.21.11: shouldRenderOffScreen() takes no parameter anymore
    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull AlterationTile blockEntity) {
        return AABB.INFINITE;
    }
}
