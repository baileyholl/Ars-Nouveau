package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.DropDistribution.rand;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ScryEvents {
    @SubscribeEvent
    public static void playerLoginEvent(final net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity().hasEffect(ModPotions.SCRYING_EFFECT.get())) {
            CompoundTag tag = event.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new PacketGetPersistentData(tag));
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(final TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END && event.player.getEffect(ModPotions.SCRYING_EFFECT.get()) != null && ClientInfo.ticksInGame % 30 == 0) {

            List<BlockPos> scryingPos = new ArrayList<>();
            CompoundTag tag = ClientInfo.persistentData;
            if (!tag.contains("an_scryer"))
                return;
            IScryer scryer = ArsNouveauAPI.getInstance().getScryer(new ResourceLocation(tag.getCompound("an_scryer").getString("id"))).fromTag(tag.getCompound("an_scryer"));
            if (scryer == null)
                return;
            Player playerEntity = event.player;
            Level world = playerEntity.level;
            Vec3i scrySize = scryer.getScryingSize();
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
    public static void onRenderWorldLast(final RenderLevelLastEvent event) {
        final Player playerEntity = Minecraft.getInstance().player;

        if (playerEntity == null || playerEntity.getEffect(ModPotions.SCRYING_EFFECT.get()) == null)
            return;
        Vec3 vector3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        ClientLevel world = Minecraft.getInstance().level;

        double yView = vector3d.y();
        if (Minecraft.getInstance().isPaused())
            return;
        for (BlockPos p : ClientInfo.scryingPositions) {
            ParticleColor color = new ParticleColor(
                    rand.nextInt(255),
                    rand.nextInt(255),
                    rand.nextInt(255));
            BlockPos renderPos = new BlockPos(p);
            if (Math.abs(yView - p.getY()) >= 30) {
                renderPos = new BlockPos(p.getX(), p.getY() > yView ? yView + 20 : yView - 20, p.getZ());
                color = new ParticleColor(
                        rand.nextInt(30),
                        rand.nextInt(255),
                        rand.nextInt(50));
            }

            if (Math.abs(yView - p.getY()) >= 60) {
                renderPos = new BlockPos(p.getX(), p.getY() > yView ? yView + 20 : yView - 20, p.getZ());
                color = new ParticleColor(
                        rand.nextInt(50),
                        rand.nextInt(50),
                        rand.nextInt(255));
            }

            world.addParticle(
                    GlowParticleData.createData(color, true),
                    renderPos.getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), renderPos.getY() + 0.2 + ParticleUtil.inRange(-0.1, 0.1), renderPos.getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                    0, 0.03f, 0);
        }
    }
}
