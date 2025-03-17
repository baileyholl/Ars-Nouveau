package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ScryEvents {
    @SubscribeEvent
    public static void playerLoginEvent(final PlayerLoggedInEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity().hasEffect(ModPotions.SCRYING_EFFECT)) {
            CompoundTag tag = event.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            Networking.sendToPlayerClient((CustomPacketPayload) new PacketGetPersistentData(tag), (ServerPlayer) event.getEntity());
        }
    }

    public static IScryer getScryer() {
        if (!ArsNouveau.proxy.getPlayer().hasEffect(ModPotions.SCRYING_EFFECT)) return null;

        CompoundTag tag = ClientInfo.persistentData;
        if (!tag.contains("an_scryer")) return null;
        CompoundTag scryerTag = tag.getCompound("an_scryer");

        ResourceLocation scryerLocation = ResourceLocation.tryParse(scryerTag.getString("id"));
        if (scryerLocation == null) return null;

        IScryer scryer = ArsNouveauAPI.getInstance().getScryer(scryerLocation);
        if (scryer == null) return null;

        return scryer.fromTag(scryerTag);
    }

    @SubscribeEvent
    public static void playerTickEvent(final PlayerTickEvent.Post event) {
        if (event.getEntity().level.isClientSide && ClientInfo.ticksInGame % 30 == 0) {
            IScryer scryer = getScryer();
            if (scryer == null) return;
            if (!scryer.revealsBlocks()) return;

            Player playerEntity = event.getEntity();
            Level world = playerEntity.level;
            Vec3i scrySize = scryer.getScryingSize();
            List<BlockPos> scryingPos = new ArrayList<>();
            for (BlockPos p : BlockPos.withinManhattan(playerEntity.blockPosition(), scrySize.getX(), scrySize.getY(), scrySize.getZ())) {
                if (world.isOutsideBuildHeight(p) || world.getBlockState(p).isAir())
                    continue;
                if (scryingPos.size() >= scryer.getScryMax())
                    break;

                if (scryer.shouldRevealBlock(world.getBlockState(p), p, playerEntity)) {
                    scryingPos.add(new BlockPos(p));
                }
            }
            ClientInfo.scryingPositions = scryingPos;
        }
    }

    @SubscribeEvent
    public static void onRenderHighlights(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
        ClientLevel world = Minecraft.getInstance().level;

        if(ClientInfo.highlightTicks > 0){
            ClientInfo.highlightTicks--;
            for(ColorPos pos : ClientInfo.highlightPositions){
                double xzOffset = 0.15;
                double xOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4) + 0.5;
                double zOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4) + 0.5;
                double centerX = pos.pos.x + xOffset;
                double centerZ = pos.pos.x + zOffset;

                double xSpeedOffset = 0;
                double ySpeedOffset = ParticleUtil.inRange(0.0, 0.03f);
                double zSpeedOffset = 0;

                xSpeedOffset = ParticleUtil.inRange(-0.01f, 0.01f);
                zSpeedOffset = ParticleUtil.inRange(-0.01f, 0.01f);
                Vec3 renderPos = pos.pos;
                ParticleColor color = pos.color;
                world.addParticle(
                        GlowParticleData.createData(color, true),
                        renderPos.x, renderPos.y  + ParticleUtil.inRange(-0.00, 0.1), renderPos.z,
                        xSpeedOffset, ySpeedOffset, zSpeedOffset);
            }
        }
    }

    @SubscribeEvent
    public static void renderScry(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
        ClientLevel world = Minecraft.getInstance().level;
        final Player playerEntity = Minecraft.getInstance().player;
        if (playerEntity == null || playerEntity.getEffect(ModPotions.SCRYING_EFFECT) == null || world == null) return;

        if (Minecraft.getInstance().isPaused())
            return;

        IScryer scryer = getScryer();
        if (scryer == null) return;

        Vec3i size = scryer.getScryingSize();
        int horizontal = Math.max(size.getX(), size.getZ()) / 2;
        int vertical = size.getY() / 2;

        for (BlockPos p : ClientInfo.scryingPositions) {
            double x = p.getX() - playerEntity.getX();
            double y = p.getY() - playerEntity.getY();
            double z = p.getZ() - playerEntity.getZ();
            boolean overHalf = Mth.lengthSquared(x, z) < Mth.square(horizontal) && Mth.square(y) < Mth.square(vertical);

            ParticleColor color = scryer.getParticleColor();
            if (!overHalf) {
                color = color.transitionTowards(ParticleColor.BLACK);
            } else {
                color = color.transitionTowards(ParticleColor.WHITE);
            }
            BlockPos renderPos = new BlockPos(p);
//            ParticleColor color = new ParticleColor(
//                    rand.nextInt(255),
//                    rand.nextInt(255),
//                    rand.nextInt(255));
//
//            color.transition()
//            BlockPos renderPos = new BlockPos(p);
//            if (Math.abs(yView - p.getY()) >= 30) {
//                renderPos = new BlockPos(p.getX(), (int) (p.getY() > yView ? yView + 20 : yView - 20), p.getZ());
//                color = new ParticleColor(
//                        rand.nextInt(30),
//                        rand.nextInt(255),
//                        rand.nextInt(50));
//            }
//
//            if (Math.abs(yView - p.getY()) >= 60) {
//                renderPos = new BlockPos(p.getX(), (int) (p.getY() > yView ? yView + 20 : yView - 20), p.getZ());
//                color = new ParticleColor(
//                        rand.nextInt(50),
//                        rand.nextInt(50),
//                        rand.nextInt(255));
//            }

            world.addParticle(
                    GlowParticleData.createData(color, true),
                    renderPos.getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), renderPos.getY() + 0.2 + ParticleUtil.inRange(-0.1, 0.1), renderPos.getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                    0, 0.03f, 0);
        }
    }
}
