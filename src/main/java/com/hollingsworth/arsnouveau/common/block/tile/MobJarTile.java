package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobJarTile extends ModdedTile implements ITickable, IDispellable {
    @Nullable
    public Entity cachedEntity;

    private CompoundTag entityTag;

    private CompoundTag extraDataTag;

    public MobJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MOB_JAR_TILE, pos, state);
    }

    @Override
    public void tick() {
        try {
            if (level.isClientSide && this.cachedEntity != null) {
                if(cachedEntity instanceof Mob mob && !(mob instanceof Bee)){
                    mob.getLookControl().tick();
                }
            }
            dispatchBehavior((behavior) -> {
                behavior.tick(this);
            });
            setChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean setEntityData(@Nonnull Entity entity){
        CompoundTag tag = new CompoundTag();
        if(entity.shouldBeSaved() && entity.save(tag)){
            this.cachedEntity = EntityType.loadEntityRecursive(tag, level, Function.identity());
            this.extraDataTag = null;
            this.entityTag = tag;
            if(!level.isClientSide) {
                level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(MobJar.LIGHT_LEVEL, calculateLight()));
                updateBlock();
            }
            return true;
        }
        return false;
    }

    public int calculateLight(){
        if(getEntity() == null)
            return 0;
        AtomicInteger light = new AtomicInteger();
        JarBehaviorRegistry.forEach(getEntity(), (behavior) -> {
            light.set(Math.max(light.get(), behavior.lightLevel(this)));
        });
        return light.get();
    }

    public @Nullable Entity getEntity(){
       if(entityTag != null && cachedEntity == null){
           cachedEntity = loadEntityFromTag(entityTag);
           if(cachedEntity == null){
               return null;
           }
           cachedEntity.setBoundingBox(new AABB(0,0,0,0,0,0));
           cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
//           cachedEntity.setCustomNameVisible(false);
//           cachedEntity.setNoGravity(true);
//           cachedEntity.noPhysics = true;
//           if(level.isClientSide){
//               cachedEntity.level = ClientJarWorld.of(cachedEntity.level);
//           }
        }
        return cachedEntity;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        if(entityTag == null)
            return false;
        Entity entity = loadEntityFromTag(entityTag);
        if(entity == null)
            return false;
        entity.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5);
        level.addFreshEntity(entity);
        this.entityTag = null;
        this.cachedEntity = null;
        this.extraDataTag = null;
        updateBlock();
        return true;
    }

    public CompoundTag getExtraDataTag(){
        return extraDataTag == null ? new CompoundTag() : extraDataTag;
    }

    public void setExtraDataTag(CompoundTag tag){
        this.extraDataTag = tag;
        setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag cacheTag = new CompoundTag();
        // Check both conditions because the entity may have never been loaded on the server side.
        if(entityTag != null || cachedEntity != null){
            cachedEntity = getEntity();
            if(cachedEntity != null) {
                cachedEntity.save(cacheTag);
                tag.put("entityTag", cacheTag);
            }
        }
        if(extraDataTag != null) {
            tag.put("extraMobData", extraDataTag);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("entityTag")){
            this.entityTag = pTag.getCompound("entityTag");
            this.cachedEntity = null;
        }
        this.extraDataTag = pTag.getCompound("extraMobData");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.cachedEntity = null;
        this.entityTag = null;
        super.onDataPacket(net, pkt);
    }

    public void dispatchBehavior(Consumer<JarBehavior<? extends Entity>> consumer){
        Entity entity = getEntity();
        if(entity == null){
            return;
        }
        JarBehaviorRegistry.forEach(entity, consumer);
    }

    private Entity loadEntityFromTag(CompoundTag tag){
        return EntityType.loadEntityRecursive(tag, level, Function.identity());
    }
}
