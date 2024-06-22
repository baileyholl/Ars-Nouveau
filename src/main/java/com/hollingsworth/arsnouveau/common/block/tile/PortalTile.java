package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.ITeleporter;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.PORTAL_TILE_TYPE;

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
        if ((level instanceof ServerLevel serverLevel)
                && warpPos != null
                && dimID != null
                && PortalTile.teleportEntityTo(e, getServerLevel(dimID, serverLevel), this.warpPos, rotationVec) != null) {
            ServerLevel serverWorld = getServerLevel(dimID, serverLevel);
            if(serverWorld == null){
                return;
            }
            Networking.sendToNearby(serverWorld, e, new PacketWarpPosition(e.getId(), e.getX() + 0.5, e.getY(), e.getZ() + 0.5, rotationVec.x, rotationVec.y));
            serverLevel.sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                    4, (serverWorld.random.nextDouble() - 0.5D) * 2.0D, -serverWorld.random.nextDouble(), (serverWorld.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        }
    }

    public void setFromScroll(WarpScroll.WarpScrollData scrollData){
        this.warpPos = scrollData.getPos();
        this.dimID = scrollData.getDimension();
        this.rotationVec = scrollData.getRotation();
    }


    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.dimID = compound.getString("dim");
        this.warpPos = NBTUtil.getBlockPos(compound, "warp");
        this.rotationVec = new Vec2(compound.getFloat("xRot"), compound.getFloat("yRot"));
        this.displayName = compound.getString("display");
        this.isHorizontal = compound.getBoolean("horizontal");
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
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
        if (level != null && level instanceof ServerLevel serverLevel && warpPos != null) {
            Set<Entity> entities = entityQueue;
            if (!entities.isEmpty()) {
                for (Entity e : entities) {
                    if (e instanceof EntityFollowProjectile)
                        continue;
                    if(dimID != null && PortalTile.teleportEntityTo(e, getServerLevel(dimID, serverLevel), this.warpPos, rotationVec) != null){
                        level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0f, 1.0f);
                        serverLevel.sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                                4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                    }
                }
                entityQueue.clear();
            }
        }
    }

    public static @Nullable ServerLevel getServerLevel(String dimID, ServerLevel level){
        if(dimID != null && level != null){
            ResourceKey<Level> resourcekey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(dimID));
            return level.getServer().getLevel(resourcekey);
        }
        return null;
    }

    @Nullable
    public static Entity teleportEntityTo(Entity entity, @Nullable Level targetWorld, BlockPos target, Vec2 rotationVec) {
        if(targetWorld == null){
            return entity;
        }
        if (entity.getCommandSenderWorld().dimension() == targetWorld.dimension()) {
            // Check if the target block is a portal, if so, don't teleport
            if((targetWorld.getBlockState(target).getBlock() instanceof PortalBlock)){
                return entity;
            }
            entity.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
            var rotX = rotationVec != null ? rotationVec.x : entity.getXRot();
            var rotY = rotationVec != null ? rotationVec.y : entity.getYRot();
            entity.setXRot(rotX);
            entity.setYRot(rotY);
            Networking.sendToNearby(targetWorld, entity, new PacketWarpPosition(entity.getId(),target.getX() + 0.5, target.getY(), target.getZ() + 0.5, rotX, rotY));
            if (!entity.getPassengers().isEmpty()) {
                //Force re-apply any passengers so that players don't get "stuck" outside what they may be riding
                ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, new ClientboundSetPassengersPacket(entity));
                Entity controller = entity.getControllingPassenger();
                if (controller != entity && controller instanceof ServerPlayer player && !(controller instanceof FakePlayer)) {
                    if (player.connection != null) {
                        //Force sync the fact that the vehicle moved to the client that is controlling it
                        // so that it makes sure to use the correct positions when sending move packets
                        // back to the server instead of running into moved wrongly issues
                        player.connection.send(new ClientboundMoveVehiclePacket(entity));
                    }
                }
            }
            return entity;
        }
        Vec3 destination = new Vec3(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        //Note: We grab the passengers here instead of in placeEntity as changeDimension starts by removing any passengers
        List<Entity> passengers = entity.getPassengers();
        return entity.changeDimension((ServerLevel) targetWorld, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                Entity repositionedEntity = repositionEntity.apply(false);
                if (repositionedEntity != null) {
                    //Teleport all passengers to the other dimension and then make them start riding the entity again
                    for (Entity passenger : passengers) {
                        teleportPassenger(destWorld, destination, repositionedEntity, passenger);
                    }
                }
                return repositionedEntity;
            }

            @Override
            public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                return new PortalInfo(destination, entity.getDeltaMovement(), rotationVec.y, rotationVec.x);
            }

            @Override
            public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                return false;
            }
        });
    }

    private static void teleportPassenger(ServerLevel destWorld, Vec3 destination, Entity repositionedEntity, Entity passenger) {
        if (!passenger.canChangeDimensions()) {
            //If the passenger can't change dimensions just let it peacefully stay after dismounting rather than trying to teleport it
            return;
        }
        //Note: We grab the passengers here instead of in placeEntity as changeDimension starts by removing any passengers
        List<Entity> passengers = passenger.getPassengers();
        passenger.changeDimension(destWorld, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                boolean invulnerable = entity.isInvulnerable();
                //Make the entity invulnerable so that when we teleport it, it doesn't take damage
                // we revert this state to the previous state after teleporting
                entity.setInvulnerable(true);
                Entity repositionedPassenger = repositionEntity.apply(false);
                if (repositionedPassenger != null) {
                    //Force our passenger to start riding the new entity again
                    repositionedPassenger.startRiding(repositionedEntity, true);
                    //Teleport "nested" passengers
                    for (Entity passenger : passengers) {
                        teleportPassenger(destWorld, destination, repositionedPassenger, passenger);
                    }
                    repositionedPassenger.setInvulnerable(invulnerable);
                }
                entity.setInvulnerable(invulnerable);
                return repositionedPassenger;
            }

            @Override
            public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                //This is needed to ensure the passenger starts getting tracked after teleporting
                return new PortalInfo(destination, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
            }

            @Override
            public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                return false;
            }
        });
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.displayName != null) {
            tooltip.add(Component.literal(this.displayName));
        }
    }
}
