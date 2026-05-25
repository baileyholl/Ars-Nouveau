package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;

public class RitualDig extends AbstractRitual {

    public static float HORIZONTAL_RADIUS = 2.0f;
    public static float VERTICAL_RADIUS = 2.0f;
    public static int MAX_STEPS = 256;
    public static int TICK_RATE = 5;
    public static float MAX_PITCH = (float) Math.toRadians(40);
    public static float YAW_JITTER = (float) Math.toRadians(20);
    public static float DESCEND_BIAS_PITCH = (float) Math.toRadians(-20);

    protected Carver carver;

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        carver = createPrimaryCarver(getPos());
    }

    public boolean canBlockBeHarvested(BlockState state) {
        return !state.isAir() && state.is(BlockTagProvider.RITUAL_DIG_BREAKABLE);
    }

    public void breakBlock(BlockPos pos) {
        if (!(getWorld() instanceof ServerLevel serverLevel) || serverLevel.isOutsideBuildHeight(pos.getY()))
            return;
        BlockState state = serverLevel.getBlockState(pos);
        if (!canBlockBeHarvested(state))
            return;
        FakePlayer fakePlayer = ANFakePlayer.getPlayer(serverLevel, playerUUID);
        if (!BlockUtil.destroyRespectsClaim(fakePlayer, serverLevel, pos))
            return;
        destroyBlockSafely(serverLevel, pos, false, fakePlayer);
    }

    @Override
    public void tick() {
        Level world = getWorld();
        if (world.isClientSide)
            return;

        int tickRate = Math.max(1, TICK_RATE);
        if (world.getGameTime() % tickRate != 0)
            return;


        if (!advanceCarver(carver, world)) {
            onEnd();
            return;
        }
        getContext().progress++;
    }

    private boolean advanceCarver(Carver c, Level world) {
        if (c.step >= c.maxSteps)
            return false;

        c.yaw += (rand.nextFloat() - 0.5f) * 2f * YAW_JITTER;
        c.pitch += (rand.nextFloat() - 0.5f) * YAW_JITTER;
        c.pitch += (DESCEND_BIAS_PITCH - c.pitch) * 0.1f;
        c.pitch = Mth.clamp(c.pitch, -MAX_PITCH, MAX_PITCH);

        double cosPitch = Math.cos(c.pitch);
        c.x += cosPitch * Math.cos(c.yaw);
        if (c.step <= 4) {
            c.y += Math.sin(Math.toRadians(-90));
        } else {
            c.y += Math.sin(c.pitch);
        }
        c.z += cosPitch * Math.sin(c.yaw);

        BlockPos center = BlockPos.containing(c.x, c.y, c.z);
        if (world.isOutsideBuildHeight(center))
            return false;

        carveEllipsoid(world, c.x, c.y, c.z, c.horizontalRadius, c.verticalRadius);
        c.step++;
        return true;
    }

    private void carveEllipsoid(Level world, double centerX, double centerY, double centerZ, float horizontalRadius, float verticalRadius) {
        int horizontalBound = (int) Math.ceil(horizontalRadius);
        int verticalBound = (int) Math.ceil(verticalRadius);
        BlockPos center = BlockPos.containing(centerX, centerY, centerZ);
        for (int offsetX = -horizontalBound; offsetX <= horizontalBound; offsetX++) {
            for (int offsetY = -verticalBound; offsetY <= verticalBound; offsetY++) {
                for (int offsetZ = -horizontalBound; offsetZ <= horizontalBound; offsetZ++) {
                    double normalizedX = offsetX / (double) horizontalRadius;
                    double normalizedY = offsetY / (double) verticalRadius;
                    double normalizedZ = offsetZ / (double) horizontalRadius;
                    // Axis aligned ellipsoid: (x/a)^2 + (y/b)^2 + (z/c)^2 <= 1
                    // https://en.wikipedia.org/wiki/Ellipsoid#Standard_equation
                    if (normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ > 1.0)
                        continue;
                    BlockPos pos = center.offset(offsetX, offsetY, offsetZ);
                    if (world.isOutsideBuildHeight(pos))
                        continue;
                    breakBlock(pos);
                }
            }
        }
    }

    private Carver createPrimaryCarver(BlockPos start) {
        Carver primary = new Carver();
        primary.x = start.getX() + 0.5;
        primary.y = start.getY() - 1;
        primary.z = start.getZ() + 0.5;
        primary.yaw = rand.nextFloat() * Mth.TWO_PI;
        primary.pitch = Mth.clamp(DESCEND_BIAS_PITCH, -MAX_PITCH, MAX_PITCH);
        primary.maxSteps = MAX_STEPS;
        primary.horizontalRadius = HORIZONTAL_RADIUS;
        primary.verticalRadius = VERTICAL_RADIUS;
        return primary;
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        if (carver != null) {
            tag.put("carver", carver.write());
        }
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        if (tag.contains("carver")) {
            carver = Carver.read(tag.getCompound("carver"));
        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(50),
                rand.nextInt(255),
                rand.nextInt(20));
    }

    @Override
    public String getLangDescription() {
        return "Digs four adjacent holes to bedrock, dropping any blocks.";
    }

    @Override
    public String getLangName() {
        return "Burrowing";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.DIG);
    }

    public static class Carver {
        public double x, y, z;
        public float yaw, pitch;
        public int step;
        public int maxSteps;
        public int branchesRemaining;
        public float horizontalRadius;
        public float verticalRadius;

        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("x", x);
            tag.putDouble("y", y);
            tag.putDouble("z", z);
            tag.putFloat("yaw", yaw);
            tag.putFloat("pitch", pitch);
            tag.putInt("step", step);
            tag.putInt("max", maxSteps);
            tag.putInt("branches", branchesRemaining);
            tag.putFloat("rH", horizontalRadius);
            tag.putFloat("rV", verticalRadius);
            return tag;
        }

        public static Carver read(CompoundTag tag) {
            Carver c = new Carver();
            c.x = tag.getDouble("x");
            c.y = tag.getDouble("y");
            c.z = tag.getDouble("z");
            c.yaw = tag.getFloat("yaw");
            c.pitch = tag.getFloat("pitch");
            c.step = tag.getInt("step");
            c.maxSteps = tag.getInt("max");
            c.branchesRemaining = tag.getInt("branches");
            c.horizontalRadius = tag.getFloat("rH");
            c.verticalRadius = tag.getFloat("rV");
            return c;
        }
    }
}
