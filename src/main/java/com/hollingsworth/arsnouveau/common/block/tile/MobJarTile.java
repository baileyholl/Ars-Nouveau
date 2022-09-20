package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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
                if(cachedEntity instanceof Mob mob){
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

    public boolean setEntityData(Entity entity){
        CompoundTag tag = new CompoundTag();
        if(entity.shouldBeSaved() && entity.save(tag)){
            this.cachedEntity = EntityType.loadEntityRecursive(tag, level, Function.identity());
            this.extraDataTag = null;
            updateBlock();
            return true;
        }
        return false;
    }

    public @Nullable Entity getEntity(){
       if(entityTag != null && cachedEntity == null){
           cachedEntity = loadEntityFromTag(entityTag);
           cachedEntity.setBoundingBox(new AABB(0,0,0,0,0,0));
           cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
           cachedEntity.setCustomNameVisible(false);
           cachedEntity.setNoGravity(true);
           cachedEntity.noPhysics = true;
//           if(level.isClientSide){
//               cachedEntity.level = ClientJarWorld.of(cachedEntity.level);
//           }
        }
        return cachedEntity;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        Entity entity = getEntity();
        if(entity == null)
            return false;
        entity.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5);
        level.addFreshEntity(entity);
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
        // Check both conditions because the entity may never have been loaded on the server side.
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
