package com.hollingsworth.arsnouveau.client.particle;


import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.network.PacketBatchedParticles;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@EventBusSubscriber
public class ParticleUtil {
    public static Random r = new Random();

    private static Object2ObjectOpenHashMap<UUID, List<ClientboundLevelParticlesPacket>> QUEUE = new Object2ObjectOpenHashMap<>();
    private static boolean queueHasItems = false;

    @SubscribeEvent
    public static void processQueue(ServerTickEvent.Post event) {
        if (!queueHasItems) {
            return;
        }

        var players = event.getServer().getPlayerList();
        var iter = Object2ObjectMaps.fastIterator(QUEUE);
        while (iter.hasNext()) {
            var entry = iter.next();
            var player = players.getPlayer(entry.getKey());
            var particles = entry.getValue();
            if (player != null && !particles.isEmpty()) {
                player.connection.send(new PacketBatchedParticles(ImmutableList.copyOf(particles)));
            }

            particles.clear();
        }

        queueHasItems = false;
    }

    public static <T extends ParticleOptions> int sendParticles(
            ServerLevel level, T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed
    ) {
        var packet = new ClientboundLevelParticlesPacket(
                type, false, posX, posY, posZ, (float) xOffset, (float) yOffset, (float) zOffset, (float) speed, particleCount
        );

        int sent = 0;
        var players = level.players();
        for (ServerPlayer player : players) {
            if (sendParticles(level, player, false, posX, posY, posZ, packet)) {
                sent++;
            }
        }

        return sent;
    }

    public static <T extends ParticleOptions> boolean sendParticles(
            ServerPlayer player,
            T type,
            boolean longDistance,
            double posX,
            double posY,
            double posZ,
            int particleCount,
            double xOffset,
            double yOffset,
            double zOffset,
            double speed
    ) {
        var packet = new ClientboundLevelParticlesPacket(
                type, longDistance, posX, posY, posZ, (float)xOffset, (float)yOffset, (float)zOffset, (float)speed, particleCount
        );

        return sendParticles(player.serverLevel(), player, longDistance, posX, posY, posZ, packet);
    }

    private static boolean sendParticles(ServerLevel level, ServerPlayer player, boolean longDistance, double posX, double posY, double posZ, ClientboundLevelParticlesPacket packet) {
        if (player.level != level) {
            return false;
        } else {
            BlockPos blockpos = player.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(posX, posY, posZ), longDistance ? 512.0 : 32.0)) {
                queueHasItems = true;
                QUEUE.compute(player.getUUID(), (a, b) -> {
                    List<ClientboundLevelParticlesPacket> list = b == null ? new ArrayList<>() : b;
                    list.add(packet);
                    return list;
                });

                return true;
            } else {
                return false;
            }
        }
    }

    public static double inRange(double min, double max) {
        if (min == max) {
            return min;
        }

        if (min > max) {
            return 0;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double getCenterOfBlock(double a) {
        return (a + .5);
    }

    // https://karthikkaranth.me/blog/generating-random-points-in-a-sphere/
    public static Vec3 pointInSphere() {
        double u = Math.random();
        double v = Math.random();
        double theta = u * 2.0 * Math.PI;
        double phi = Math.acos(2.0 * v - 1.0);
        double r = Math.cbrt(Math.random());
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double x = r * sinPhi * cosTheta;
        double y = r * sinPhi * sinTheta;
        double z = r * cosPhi;
        return new Vec3(x, y, z);
    }

    public static Vec3 pointInCube() {
        double x = inRange(-1, 1);
        double y = inRange(-1, 1);
        double z = inRange(-1, 1);
        return new Vec3(x, y, z);
    }

    public static void spawnFollowProjectile(Level world, BlockPos from, BlockPos to, ParticleColor color) {
        if (world instanceof ServerLevel serverLevel && world.isLoaded(to) && world.isLoaded(from)) {
            EntityFollowProjectile.spawn(serverLevel, from, to, color.getRedInt(), color.getGreenInt(), color.getBlueInt());
        }
    }

    public static void beam(BlockPos toThisBlock, BlockPos fromThisBlock, Level world) {

        double x2 = getCenterOfBlock(toThisBlock.getX());
        double z2 = getCenterOfBlock(toThisBlock.getZ());
        double y2 = getCenterOfBlock(toThisBlock.getY());
        double x1 = getCenterOfBlock(fromThisBlock.getX());
        double z1 = getCenterOfBlock(fromThisBlock.getZ());
        double y1 = getCenterOfBlock(fromThisBlock.getY());
        double d5 = 1.2;
        double d0 = x2 - x1;
        double d1 = y2 - y1;
        double d2 = z2 - z1;
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;

        double d4 = r.nextDouble();

        while ((d4 + .65) < d3) {
            d4 += 1.8D - d5 + r.nextDouble() * (1.5D - d5);
            if (world.isClientSide)
                world.addAlwaysVisibleParticle(ParticleTypes.ENCHANT, true, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D);
            if (world instanceof ServerLevel serverLevel) {
                sendParticles(serverLevel, ParticleTypes.WITCH, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, r.nextInt(4), 0, 0.0, 0, 0.0);
            }
        }
    }

    public static void spawnPoof(ServerLevel world, BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            double d0 = pos.getX() + 0.5;
            double d1 = pos.getY() + 1.2;
            double d2 = pos.getZ() + .5;
            sendParticles(world, ParticleTypes.END_ROD, d0, d1, d2, 2, (world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3, 0.1f);
        }
    }

    public static void spawnTouch(ClientLevel world, BlockPos loc) {
        spawnTouch(world, loc, ParticleColor.defaultParticleColor());
    }

    public static void spawnTouch(ClientLevel world, BlockPos loc, ParticleColor particleColor) {
        for (int i = 0; i < 5; i++) {
            double d0 = loc.getX() + 0.5;
            double d1 = loc.getY() + 1.0;
            double d2 = loc.getZ() + .5;
            world.addAlwaysVisibleParticle(GlowParticleData.createData(particleColor), true, d0, d1, d2, (world.random.nextFloat() * 1 - 0.5) / 5, (world.random.nextFloat() * 1 - 0.5) / 5, (world.random.nextFloat() * 1 - 0.5) / 5);
        }
    }

    public static void spawnTouchPacket(Level world, BlockPos pos, ParticleColor color) {
        Networking.sendToNearbyClient(world, pos,
                new PacketANEffect(PacketANEffect.EffectType.BURST, pos, color));
    }

    public static void spawnRitualAreaEffect(BlockEntity entity, RandomSource rand, ParticleColor color, int range) {
        spawnRitualAreaEffect(entity.getBlockPos(), entity.getLevel(), rand, color, range);
    }

    public static void spawnRitualAreaEffect(BlockPos pos, Level world, RandomSource rand, ParticleColor color, int range) {
        spawnRitualAreaEffect(pos, world, rand, color, range, 10, 2);
    }

    public static void spawnRitualAreaEffect(BlockPos pos, Level world, RandomSource rand, ParticleColor color, int range, int chance, int numParticles) {
        BlockPos.betweenClosedStream(pos.offset(range, 0, range), pos.offset(-range, 0, -range)).forEach(blockPos -> {
            if (rand.nextInt(chance) == 0) {
                for (int i = 0; i < rand.nextInt(numParticles); i++) {
                    double x = blockPos.getX() + ParticleUtil.inRange(-0.5, 0.5) + 0.5;
                    double y = blockPos.getY() + ParticleUtil.inRange(-0.5, 0.5);
                    double z = blockPos.getZ() + ParticleUtil.inRange(-0.5, 0.5) + 0.5;
                    world.addAlwaysVisibleParticle(ParticleLineData.createData(color),
                            true, x, y, z,
                            x, y + ParticleUtil.inRange(0.5, 5), z);
                }
            }
        });
    }

    public static void spawnRitualSkyEffect(BlockEntity tileEntity, RandomSource rand, ParticleColor.IntWrapper color) {

        int min = -5;
        int max = 5;
        BlockPos nearPos = new BlockPos(tileEntity.getBlockPos().getX() + rand.nextInt(max - min) + min, tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ() + rand.nextInt(max - min) + min);
        BlockPos toPos = nearPos.above(rand.nextInt(3) + 10);
        EntityFollowProjectile proj1 = new EntityFollowProjectile(tileEntity.getLevel(),
                tileEntity.getBlockPos().above(), toPos,
                color);

        proj1.getEntityData().set(EntityFollowProjectile.SPAWN_TOUCH, true);
        proj1.getEntityData().set(EntityFollowProjectile.DESPAWN, 15);

        tileEntity.getLevel().addFreshEntity(proj1);

    }

    public static void spawnRitualSkyEffect(AbstractRitual ritual, BlockEntity tileEntity, RandomSource rand, ParticleColor.IntWrapper color) {
        int scalar = 20;
        if (ritual.getContext().progress >= 5)
            scalar = 10;
        if (ritual.getContext().progress >= 10)
            scalar = 5;
        if (ritual.getContext().progress >= 13)
            scalar = 3;
        if (!ritual.getWorld().isClientSide && ritual.getProgress() <= 15 && (ritual.getWorld().getGameTime() % 20 == 0 || rand.nextInt(scalar) == 0)) {
            ParticleUtil.spawnRitualSkyEffect(tileEntity, rand, color);
        }
    }

    public static void spawnFallingSkyEffect(BlockEntity tileEntity, RandomSource rand, ParticleColor.IntWrapper color) {
        int min = -5;
        int max = 5;
        BlockPos nearPos = new BlockPos(tileEntity.getBlockPos().getX() + rand.nextInt(max - min) + min, tileEntity.getBlockPos().getY() + 8, tileEntity.getBlockPos().getZ() + rand.nextInt(max - min) + min);
        BlockPos toPos = nearPos.below(8);
        EntityFollowProjectile proj1 = new EntityFollowProjectile(tileEntity.getLevel(),
                nearPos, toPos,
                color);

        proj1.getEntityData().set(EntityFollowProjectile.SPAWN_TOUCH, true);
        proj1.getEntityData().set(EntityFollowProjectile.DESPAWN, 20);

        tileEntity.getLevel().addFreshEntity(proj1);

    }

    public static void spawnFallingSkyEffect(AbstractRitual ritual, BlockEntity tileEntity, RandomSource rand, ParticleColor.IntWrapper color) {
        if (ritual == null) {
            return;
        }
        int scalar = 20;
        if (ritual.getContext().progress >= 5)
            scalar = 10;
        if (ritual.getContext().progress >= 10)
            scalar = 5;
        if (ritual.getContext().progress >= 13)
            scalar = 3;
        if (!ritual.getWorld().isClientSide && ritual.getProgress() <= 15 && (ritual.getWorld().getGameTime() % 20 == 0 || rand.nextInt(scalar) == 0)) {
            ParticleUtil.spawnFallingSkyEffect(tileEntity, rand, color);
        }
    }


    public static void spawnLight(Level world, ParticleColor color, Vec3 vec, int intensity) {
        for (int i = 0; i < intensity; i++) {
            world.addAlwaysVisibleParticle(
                    GlowParticleData.createData(color),
                    true, vec.x() + ParticleUtil.inRange(-0.1, 0.1), vec.y() + ParticleUtil.inRange(-0.1, 0.1), vec.z() + ParticleUtil.inRange(-0.1, 0.1),
                    0, 0, 0);
        }
    }

    public static void spawnOrb(Level level, ParticleColor color, BlockPos pos, int lifetime) {
        if (level instanceof ServerLevel server) {
            for (int i = 0; i <= 10; i++)
                sendParticles(
                        server,
                        GlowParticleData.createData(color, 0.4f, 0.5f, lifetime),
                        pos.getX() + ParticleUtil.inRange(0.3, 0.7), pos.getY() + ParticleUtil.inRange(-0.2, 0.2), pos.getZ() + ParticleUtil.inRange(0.3, 0.7), 1,
                        0d, 0d, 0, 0);
        }
    }
}