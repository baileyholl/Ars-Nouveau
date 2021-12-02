package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import java.util.Calendar;

import static net.minecraft.client.renderer.Atlases.CHEST_SHEET;

import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

public clasnet.minecraft.client.renderer.SheetsckEntity & LidBlockEntity> extends BlockEntityRenderer<T> {

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;
    private boolean xmasTextures;
    public static Block invBlock = null;
    public ArchwoodChestRenderer(BlockEntityRenderDispatcher p_i226008_1_) {
        super(p_i226008_1_);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26) {
            this.xmasTextures = true;
        }

        this.bottom = new ModelPart(64, 64, 0, 19);
        this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelPart(64, 64, 0, 0);
        this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.y = 9.0F;
        this.lid.z = 1.0F;
        this.lock = new ModelPart(64, 64, 0, 0);
        this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.lock.y = 8.0F;
        this.doubleLeftBottom = new ModelPart(64, 64, 0, 19);
        this.doubleLeftBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.doubleLeftLid = new ModelPart(64, 64, 0, 0);
        this.doubleLeftLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.doubleLeftLid.y = 9.0F;
        this.doubleLeftLid.z = 1.0F;
        this.doubleLeftLock = new ModelPart(64, 64, 0, 0);
        this.doubleLeftLock.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.doubleLeftLock.y = 8.0F;
        this.doubleRightBottom = new ModelPart(64, 64, 0, 19);
        this.doubleRightBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.doubleRightLid = new ModelPart(64, 64, 0, 0);
        this.doubleRightLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.doubleRightLid.y = 9.0F;
        this.doubleRightLid.z = 1.0F;
        this.doubleRightLock = new ModelPart(64, 64, 0, 0);
        this.doubleRightLock.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.doubleRightLock.y = 8.0F;
    }

    public void render(T tileEntity, float p_225616_2_, PoseStack ms, MultiBufferSource buffer, int p_225616_5_, int p_225616_6_) {
        Level world = tileEntity.getLevel();
        boolean flag = world != null;
        BlockState blockstate = flag ? tileEntity.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Block block = blockstate.getBlock();
        if (block instanceof AbstractChestBlock) {
            AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock)block;
            boolean flag1 = chesttype != ChestType.SINGLE;
            ms.pushPose();
            float f = blockstate.getValue(ChestBlock.FACING).toYRot();
            ms.translate(0.5D, 0.5D, 0.5D);
            ms.mulPose(Vector3f.YP.rotationDegrees(-f));
            ms.translate(-0.5D, -0.5D, -0.5D);
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> icallbackwrapper;
            if (flag) {
                icallbackwrapper = abstractchestblock.combine(blockstate, world, tileEntity.getBlockPos(), true);
            } else {
                icallbackwrapper = DoubleBlockCombiner.Combiner::acceptNone;
            }

            float f1 = icallbackwrapper.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(tileEntity)).get(p_225616_2_);
            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            int i = icallbackwrapper.<Int2IntFunction>apply(new BrightnessCombiner<>()).applyAsInt(p_225616_5_);
            Material rendermaterial = this.getMaterial(tileEntity, chesttype);
            VertexConsumer ivertexbuilder = rendermaterial.buffer(buffer, RenderType::entityCutout);
            if (flag1) {
                if (chesttype == ChestType.LEFT) {
                    this.render(ms, ivertexbuilder, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, p_225616_6_);
                } else {
                    this.render(ms, ivertexbuilder, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, p_225616_6_);
                }
            } else {
                this.render(ms, ivertexbuilder, this.lid, this.lock, this.bottom, f1, i, p_225616_6_);
            }

            ms.popPose();
        }
    }

    private void render(PoseStack p_228871_1_, VertexConsumer p_228871_2_, ModelPart p_228871_3_, ModelPart p_228871_4_, ModelPart p_228871_5_, float p_228871_6_, int p_228871_7_, int p_228871_8_) {

        p_228871_3_.xRot = -(p_228871_6_ * ((float)Math.PI / 2F));
        p_228871_4_.xRot = p_228871_3_.xRot;
        p_228871_3_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
        p_228871_4_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
        p_228871_5_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
    }

    protected Material getMaterial(T tileEntity, ChestType chestType) {
        switch(chestType) {
            case LEFT:
                return new Material(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/archwood_chest_left"));
            case RIGHT:
                return new Material(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/archwood_chest_right"));
            case SINGLE:
            default:
                return new Material(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/archwood_chest"));
        }
      //  return new RenderMaterial(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/chest/archwood"));
    }

    public static BlockEntityWithoutLevelRenderer getRenderer(){
        return new BlockEntityWithoutLevelRenderer() {
            private BlockEntity tile = null;
            //render
            @Override
            public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrix, MultiBufferSource buffer, int x, int y) {
                if(tile == null) {
                    tile =  BlockRegistry.ARCHWOOD_CHEST_TILE.create();
                }
                if(tile == null)
                    return;
                BlockEntityRenderDispatcher.instance.renderItem(tile, matrix, buffer, x, y);
            }

        };
    }
}
