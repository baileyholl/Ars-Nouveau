package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.*;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.Calendar;

import static net.minecraft.client.renderer.Atlases.CHEST_SHEET;

public class ArchwoodChestRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {

    private final ModelRenderer lid;
    private final ModelRenderer bottom;
    private final ModelRenderer lock;
    private final ModelRenderer doubleLeftLid;
    private final ModelRenderer doubleLeftBottom;
    private final ModelRenderer doubleLeftLock;
    private final ModelRenderer doubleRightLid;
    private final ModelRenderer doubleRightBottom;
    private final ModelRenderer doubleRightLock;
    private boolean xmasTextures;
    public static Block invBlock = null;
    public ArchwoodChestRenderer(TileEntityRendererDispatcher p_i226008_1_) {
        super(p_i226008_1_);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.xmasTextures = true;
        }

        this.bottom = new ModelRenderer(64, 64, 0, 19);
        this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelRenderer(64, 64, 0, 0);
        this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.y = 9.0F;
        this.lid.z = 1.0F;
        this.lock = new ModelRenderer(64, 64, 0, 0);
        this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.lock.y = 8.0F;
        this.doubleLeftBottom = new ModelRenderer(64, 64, 0, 19);
        this.doubleLeftBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.doubleLeftLid = new ModelRenderer(64, 64, 0, 0);
        this.doubleLeftLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.doubleLeftLid.y = 9.0F;
        this.doubleLeftLid.z = 1.0F;
        this.doubleLeftLock = new ModelRenderer(64, 64, 0, 0);
        this.doubleLeftLock.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.doubleLeftLock.y = 8.0F;
        this.doubleRightBottom = new ModelRenderer(64, 64, 0, 19);
        this.doubleRightBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.doubleRightLid = new ModelRenderer(64, 64, 0, 0);
        this.doubleRightLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.doubleRightLid.y = 9.0F;
        this.doubleRightLid.z = 1.0F;
        this.doubleRightLock = new ModelRenderer(64, 64, 0, 0);
        this.doubleRightLock.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.doubleRightLock.y = 8.0F;
    }

    public void render(T tileEntity, float p_225616_2_, MatrixStack ms, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
        World world = tileEntity.getLevel();
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
            TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
            if (flag) {
                icallbackwrapper = abstractchestblock.combine(blockstate, world, tileEntity.getBlockPos(), true);
            } else {
                icallbackwrapper = TileEntityMerger.ICallback::acceptNone;
            }

            float f1 = icallbackwrapper.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(tileEntity)).get(p_225616_2_);
            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            int i = icallbackwrapper.<Int2IntFunction>apply(new DualBrightnessCallback<>()).applyAsInt(p_225616_5_);
            RenderMaterial rendermaterial = this.getMaterial(tileEntity, chesttype);
            IVertexBuilder ivertexbuilder = rendermaterial.buffer(buffer, RenderType::entityCutout);
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

    private void render(MatrixStack p_228871_1_, IVertexBuilder p_228871_2_, ModelRenderer p_228871_3_, ModelRenderer p_228871_4_, ModelRenderer p_228871_5_, float p_228871_6_, int p_228871_7_, int p_228871_8_) {

        p_228871_3_.xRot = -(p_228871_6_ * ((float)Math.PI / 2F));
        p_228871_4_.xRot = p_228871_3_.xRot;
        p_228871_3_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
        p_228871_4_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
        p_228871_5_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
    }

    protected RenderMaterial getMaterial(T tileEntity, ChestType chestType) {
        switch(chestType) {
            case LEFT:
                return new RenderMaterial(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/archwood_chest_left"));
            case RIGHT:
                return new RenderMaterial(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/archwood_chest_right"));
            case SINGLE:
            default:
                return new RenderMaterial(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/archwood_chest"));
        }
      //  return new RenderMaterial(CHEST_SHEET, new ResourceLocation("ars_nouveau","entity/chest/archwood"));
    }

    public static ItemStackTileEntityRenderer getRenderer(){
        return new ItemStackTileEntityRenderer() {
            private TileEntity tile = null;
            //render
            @Override
            public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrix, IRenderTypeBuffer buffer, int x, int y) {
                if(tile == null) {
                    tile =  BlockRegistry.ARCHWOOD_CHEST_TILE.create();
                }
                if(tile == null)
                    return;
                TileEntityRendererDispatcher.instance.renderItem(tile, matrix, buffer, x, y);
            }

        };
    }
}
