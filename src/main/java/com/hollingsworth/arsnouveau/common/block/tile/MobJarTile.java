package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.items.data.MobJarData;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobJarTile extends ModdedTile implements ITickable, IDispellable, ITooltipProvider {
    @Nullable
    public Entity cachedEntity;

    private CompoundTag entityTag;

    private CompoundTag extraDataTag;

    public boolean isVisible = true;

    public MobJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MOB_JAR_TILE, pos, state);
    }

    @Override
    public void tick() {
        try {
            if (level.isClientSide && this.cachedEntity != null) {
                if (this.cachedEntity.isRemoved()) {
                    this.removeEntity();
                    return;
                }

                if (cachedEntity instanceof Mob mob && !(mob instanceof Bee)) {
                    mob.getLookControl().tick();
                }

                this.isVisible = false;
                var entity = this.getEntity();
                if (entity != null) {
                    var camera = Minecraft.getInstance().gameRenderer.getMainCamera();

                    var startPos = camera.getPosition();
                    if (startPos.distanceToSqr(this.getBlockPos().getCenter()) <= 64 * 64) {
                        var aabb = entity.getBoundingBoxForCulling();
                        for (var passenger : entity.getPassengers()) {
                            aabb = aabb.minmax(passenger.getBoundingBoxForCulling());
                        }

                        if (Double.isFinite(aabb.minX) && Double.isFinite(aabb.minY) && Double.isFinite(aabb.minZ)
                                && Double.isFinite(aabb.maxX) && Double.isFinite(aabb.maxY) && Double.isFinite(aabb.maxZ)) {
                            for (var endPos : new Vec3[]{
                                    new Vec3(aabb.minX, aabb.minY, aabb.minZ),
                                    new Vec3(aabb.minX, aabb.minY, aabb.maxZ),
                                    new Vec3(aabb.minX, aabb.maxY, aabb.minZ),
                                    new Vec3(aabb.minX, aabb.maxY, aabb.maxZ),
                                    new Vec3(aabb.maxX, aabb.minY, aabb.minZ),
                                    new Vec3(aabb.maxX, aabb.minY, aabb.maxZ),
                                    new Vec3(aabb.maxX, aabb.maxY, aabb.minZ),
                                    new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ),
                            }) {
                                var result = level.clip(new ClipContext(startPos, endPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
                                if (result.getType() == HitResult.Type.MISS || this.getBlockPos() == result.getBlockPos()) {
                                    this.isVisible = true;
                                    break;
                                }
                            }
                        } else {
                            this.isVisible = true;
                        }
                    }
                }
            }
            dispatchBehavior((behavior) -> {
                behavior.tick(this);
            });
            setChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean setEntityData(@NotNull Entity entity) {
        CompoundTag tag = new CompoundTag();
        if (entity.shouldBeSaved() && entity.save(tag)) {
            this.cachedEntity = EntityType.loadEntityRecursive(tag, level, Function.identity());
            this.cachedEntity.setBoundingBox(new AABB(0, 0, 0, 0, 0, 0));
            this.cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
            this.extraDataTag = null;
            this.entityTag = tag;
            if (!level.isClientSide) {
                this.invalidateCapabilities();
                level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(MobJar.LIGHT_LEVEL, calculateLight()));
                updateBlock();
            }
            return true;
        } else {
            try {
                writeSimple(entity);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.entityTag = this.saveEntityToTag(this.getEntity());
        if (this.getEntity() instanceof Entity e) {
            e.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }

    public void writeSimple(Entity e) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(e.getType()).toString());
        if (level == null) return;
        this.cachedEntity = e.getType().create(level);
        assert cachedEntity != null;
        this.cachedEntity.setBoundingBox(new AABB(0, 0, 0, 0, 0, 0));
        this.cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        this.extraDataTag = null;
        this.entityTag = tag;
        if (!level.isClientSide) {
            level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(MobJar.LIGHT_LEVEL, calculateLight()));
            updateBlock();
        }
    }

    public int calculateLight() {
        if (getEntity() == null)
            return 0;

        if (getEntity().isOnFire()) {
            return 15;
        }
        if (getEntity() instanceof LightningBolt) {
            return 15;
        }
        AtomicInteger light = new AtomicInteger();
        JarBehaviorRegistry.forEach(getEntity(), (behavior) -> {
            light.set(Math.max(light.get(), behavior.lightLevel(this)));
        });
        return light.get();
    }

    public @Nullable Entity getEntity() {
        if (entityTag != null && cachedEntity == null) {
            cachedEntity = loadEntityFromTag(level, entityTag);
            if (cachedEntity == null) {
                return null;
            }
            cachedEntity.setBoundingBox(new AABB(0, 0, 0, 0, 0, 0));
            cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        }
        return cachedEntity;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        if (entityTag == null || level == null)
            return false;
        Entity entity = loadEntityFromTag(level, entityTag);
        if (entity == null || entity.getType().is(EntityTags.JAR_RELEASE_BLACKLIST))
            return false;
        entity.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5);
        level.addFreshEntity(entity);
        removeEntity();
        return true;
    }

    public void removeEntity() {
        if (this.getEntity() instanceof Entity e) {
            e.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
        this.entityTag = null;
        this.cachedEntity = null;
        this.extraDataTag = null;
        this.invalidateCapabilities();
        updateBlock();
    }

    public CompoundTag getExtraDataTag() {
        return extraDataTag == null ? new CompoundTag() : extraDataTag;
    }

    public void setExtraDataTag(CompoundTag tag) {
        this.extraDataTag = tag;
        setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);

        // Check both conditions because the entity may have never been loaded on the server side.
        if (entityTag != null || cachedEntity != null) {
            cachedEntity = getEntity();
            if (cachedEntity != null) {
                try {
                    tag.put("entityTag", saveEntityToTag(cachedEntity));
                    if (tag.getCompound("entityTag").contains("id")) {
                        tag.putString("entityId", tag.getCompound("entityTag").getString("id"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (extraDataTag != null) {
            tag.put("extraMobData", extraDataTag);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("entityTag")) {
            this.entityTag = pTag.getCompound("entityTag");
            this.cachedEntity = null;
        }
        this.extraDataTag = pTag.getCompound("extraMobData");
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
        this.cachedEntity = null;
        this.entityTag = null;
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public void dispatchBehavior(Consumer<JarBehavior<? extends Entity>> consumer) {
        Entity entity = getEntity();
        if (entity == null) {
            return;
        }
        JarBehaviorRegistry.forEach(entity, consumer);
    }

    public static Entity loadEntityFromTag(Level level, CompoundTag tag) {
        Entity entity = EntityType.loadEntityRecursive(tag, level, Function.identity());
        if (entity == null) {
            String id = tag.getString("id");
            Optional<EntityType<?>> type = EntityType.byString(id);
            if (type.isPresent()) {
                entity = type.get().create(level);
            }
        }
        return entity;
    }

    public CompoundTag saveEntityToTag(Entity entity) {
        CompoundTag tag = new CompoundTag();
        if (entity != null) {
            entity.save(tag);
            if (tag.isEmpty()) {
                tag.putString("id", EntityType.getKey(entity.getType()).toString());
            }
        }
        return tag;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        Entity entity = getEntity();
        if (entity != null) {
            JarBehaviorRegistry.forEach(entity, (behavior) -> {
                behavior.getTooltip(this, tooltip);
            });
        }
    }


    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        var jar = pComponentInput.getOrDefault(DataComponentRegistry.MOB_JAR, new MobJarData(Optional.empty(), Optional.empty()));
        this.entityTag = jar.entityTag().orElse(null);
        this.extraDataTag = jar.extraDataTag().orElse(null);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        if ((this.entityTag != null && !this.entityTag.isEmpty()) || (this.extraDataTag != null && !this.extraDataTag.isEmpty())) {
            pComponents.set(DataComponentRegistry.MOB_JAR, new MobJarData(this.entityTag, this.extraDataTag));
        }
    }

    public <T, C> T getEntityCapability(EntityCapability<T, C> type, C context) {
        var entity = this.getEntity();
        if (entity == null) {
            return null;
        }

        return entity.getCapability(type, context);
    }

    public static class SavingItemHandler implements IItemHandler {
        MobJarTile tile;
        IItemHandler inner;

        private SavingItemHandler(MobJarTile tile, IItemHandler inner) {
            this.tile = tile;
            this.inner = inner;
        }

        public static SavingItemHandler of(MobJarTile tile, IItemHandler inner) {
            return inner == null ? null : new SavingItemHandler(tile, inner);
        }

        @Override
        public int getSlots() {
            return inner.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inner.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            var result = inner.insertItem(slot, stack, simulate);
            if (!simulate) {
                var entity = tile.getEntity();
                if (entity != null) {
                    tile.entityTag = tile.saveEntityToTag(entity);
                }
            }
            return result;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            var result = inner.extractItem(slot, amount, simulate);
            if (!simulate) {
                var entity = tile.getEntity();
                if (entity != null) {
                    tile.entityTag = tile.saveEntityToTag(entity);
                }
            }
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return inner.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return inner.isItemValid(slot, stack);
        }
    }
}
