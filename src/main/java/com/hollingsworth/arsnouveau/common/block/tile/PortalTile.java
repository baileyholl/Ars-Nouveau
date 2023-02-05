package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hollingsworth.arsnouveau.setup.BlockRegistry.PORTAL_TILE_TYPE;

public class PortalTile extends ModdedTile implements ITickable, ITooltipProvider {
    public BlockPos warpPos;
    public String dimID;
    public Vec2 rotationVec;
    public String displayName;
    public boolean isHorizontal;
    public Set<Entity> entityQueue = new HashSet<>();

    public PortalTile(BlockPos pos, BlockState state) {
        super(PORTAL_TILE_TYPE, pos, state);
    }

    public void warp(Entity e) {
        if ((level instanceof ServerLevel serverLevel) && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            e.moveTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5,
                    rotationVec != null ? rotationVec.y : e.getYRot(), rotationVec != null ? rotationVec.x : e.getXRot());
            e.setXRot(rotationVec != null ? rotationVec.x : e.getXRot());
            e.setYRot(rotationVec != null ? rotationVec.y : e.getYRot());
            Networking.sendToNearby(level, e, new PacketWarpPosition(e.getId(), e.getX() + 0.5, e.getY(), e.getZ() + 0.5, e.getXRot(), e.getYRot()));
            serverLevel.sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                    4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        }
    }

    public void setFromScroll(WarpScroll.WarpScrollData scrollData){
        this.warpPos = scrollData.getPos();
        this.dimID = scrollData.getDimension();
        this.rotationVec = scrollData.getRotation();
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.dimID = compound.getString("dim");
        this.warpPos = NBTUtil.getBlockPos(compound, "warp");
        this.rotationVec = new Vec2(compound.getFloat("xRot"), compound.getFloat("yRot"));
        this.displayName = compound.getString("display");
        this.isHorizontal = compound.getBoolean("horizontal");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if (this.warpPos != null) {
            NBTUtil.storeBlockPos(compound, "warp", this.warpPos);
        }
        if(this.dimID != null) {
            compound.putString("dim", this.dimID);
        }
        if (rotationVec != null) {
            compound.putFloat("xRot", rotationVec.x);
            compound.putFloat("yRot", rotationVec.y);
        }
        if (displayName != null) {
            compound.putString("display", displayName);
        }
        compound.putBoolean("horizontal", isHorizontal);
    }

    @Override
    public void tick() {
        if (level != null && level instanceof ServerLevel serverLevel && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            Set<Entity> entities = entityQueue;
            if (!entities.isEmpty()) {
                for (Entity e : entities) {
                    if (e instanceof EntityFollowProjectile || BlockUtil.distanceFrom(e.blockPosition(), worldPosition) > 2)
                        continue;
                    if(teleport(serverLevel, e)) {
                        level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0f, 1.0f);
                       serverLevel.sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                                4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                        if (rotationVec != null) {
                            e.setXRot(rotationVec.x);
                            e.setYRot(rotationVec.y);
                            Networking.sendToNearby(e.level, e, new PacketWarpPosition(e.getId(), warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5, e.getXRot(), e.getYRot()));
                        }
                    }
                }
                entityQueue.clear();
            }
        }
    }

    public boolean teleport(ServerLevel serverLevel, Entity e){
        if(dimID != null && !dimID.equals(level.dimension().location().toString())){
            if(e.canChangeDimensions()){
                DimensionType type = BuiltinRegistries.DIMENSION_TYPE.get(new ResourceLocation(dimID));
                if(type != null) {
                    ResourceKey<Level> resourcekey = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimID));
                    ServerLevel destination = serverLevel.getServer().getLevel(resourcekey);
                    if(destination != null) {
                        e.changeDimension(destination);
                        e.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
                        return true;
                    }
                }
            }
            return false;
        }
        e.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
        return true;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.displayName != null) {
            tooltip.add(Component.literal(this.displayName));
        }
    }
}
