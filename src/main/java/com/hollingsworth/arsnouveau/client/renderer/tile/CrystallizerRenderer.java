package com.hollingsworth.arsnouveau.client.renderer.tile;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import com.hollingsworth.arsnouveau.common.block.tile.CrystallizerTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrystallizerRenderer extends TileEntityRenderer<CrystallizerTile> {
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/relay.png");
    public final RelayModel model = new RelayModel();

    public CrystallizerRenderer(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(CrystallizerTile crystallizerTile, float f, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        World world = crystallizerTile.getWorld();
        BlockPos pos  = crystallizerTile.getPos().add(0, 2, 0);
        if(world.rand.nextInt(3) == 0){
            for(int i =0; i< 100; i++){
                Vec3d particlePos = new Vec3d(pos.add(0, 0.5, 0)).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere(null));
                world.addParticle(ParticleLineData.createData(new ParticleColor(120,80,5)),
                        particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                        pos.getX()  , pos.getY()  , pos.getZ());
            }
        }
    }

    public static class ISRender extends ItemStackTileEntityRenderer {
        public final RelayModel model = new RelayModel();

        public ISRender(){ }

        @Override
        public void render(ItemStack p_228364_1_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.push();
            ms.translate(0.75, -0.65, 0.2);
            IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
            model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);
            ms.pop();
        }
    }
}
