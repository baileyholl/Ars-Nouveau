package com.hollingsworth.arsnouveau.client.particle.engine;


public class EmbersParticles  {
//    ArrayDeque<Particle> normalParticles = new ArrayDeque<>();
//    ArrayDeque<Particle> additiveParticles = new ArrayDeque<>();
//    ArrayDeque<Particle> throughParticles = new ArrayDeque<>();
//    ArrayDeque<Particle> additiveThroughParticles = new ArrayDeque<>();
//    Queue<Particle> queue = Queues.<Particle>newArrayDeque();
//
//    public void updateParticles() {
//        updateParticles(normalParticles);
//        updateParticles(additiveParticles);
//        updateParticles(throughParticles);
//        updateParticles(additiveThroughParticles);
//
//        if (!this.queue.isEmpty()) {
//            for (Particle particle = this.queue.poll(); particle != null; particle = this.queue.poll()) {
//                IEmberParticle emberParticle = (IEmberParticle) particle;
//                boolean additive = emberParticle.isAdditive();
//                boolean rendersThrough = emberParticle.renderThroughBlocks();
//
//                getParticleCollection(additive, rendersThrough).add(particle);
//            }
//        }
//    }
//
//    public void updateParticles(ArrayDeque<Particle> particles) {
//        Iterator<Particle> iterator = particles.iterator();
//        while (iterator.hasNext()) {
//            Particle particle = iterator.next();
//            if (((IEmberParticle) particle).alive())
//                particle.tick();
//            else {
//                iterator.remove();
//            }
//        }
//    }
//
//    public void renderParticles(PlayerEntity dumbplayer, float partialTicks) {
//
//        float f = ActiveRenderInfo.getRotationX();
//        float f1 = ActiveRenderInfo.getRotationZ();
//        float f2 = ActiveRenderInfo.getRotationYZ();
//        float f3 = ActiveRenderInfo.getRotationXY();
//        float f4 = ActiveRenderInfo.getRotationXZ();
//        PlayerEntity player = Minecraft.getInstance().player;
//        if (player != null) {
//            Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
//            Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
//            Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
//            Particle.cameraViewDir = player.getLook(partialTicks);
//            GlStateManager.enableAlphaTest();
//            GlStateManager.enableBlend();
//            GlStateManager.alphaFunc(516, 0.003921569F);
//            GlStateManager.disableCull();
//
//            GlStateManager.depthMask(false);
//
////            Minecraft.getInstance().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//            Tessellator tess = Tessellator.getInstance();
//            BufferBuilder buffer = tess.getBuffer();
//
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//            for (Particle particle : normalParticles) {
//                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
//            }
//            tess.draw();
//
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//            for (Particle particle : additiveParticles) {
//                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
//            }
//            tess.draw();
//
//            GlStateManager.disableDepth();
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//            for (Particle particle : throughParticles) {
//                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
//            }
//            tess.draw();
//
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
//            for (Particle particle : additiveThroughParticles) {
//                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
//            }
//            tess.draw();
//            GlStateManager.enableDepthTest();
//
//            GlStateManager.enableCull();
//            GlStateManager.depthMask(true);
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            GlStateManager.disableBlend();
//            GlStateManager.alphaFunc(516, 0.1F);
//        }
//    }
//
//    public void addParticle(Particle particle) {
//        if (particle instanceof IEmberParticle) {
//            queue.add(particle);
//        }
//    }
//
//    private Collection<Particle> getParticleCollection(boolean additive, boolean rendersThroughBlocks) {
//        if (!rendersThroughBlocks && !additive)
//            return normalParticles;
//        else if (!rendersThroughBlocks)
//            return additiveParticles;
//        else if (!additive)
//            return throughParticles;
//        else
//            return additiveThroughParticles;
//    }
}