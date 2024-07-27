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
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobJarTile extends ModdedTile implements ITickable, IDispellable, ITooltipProvider {
    @Nullable
    public Entity cachedEntity;

    private CompoundTag entityTag;

    private CompoundTag extraDataTag ;

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

    public boolean setEntityData(@NotNull Entity entity){
        CompoundTag tag = new CompoundTag();
        if(entity.shouldBeSaved() && entity.save(tag)){
            this.cachedEntity = EntityType.loadEntityRecursive(tag, level, Function.identity());
            this.cachedEntity.setBoundingBox(new AABB(0,0,0,0,0,0));
            this.cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
            this.extraDataTag = null;
            this.entityTag = tag;
            if(!level.isClientSide) {
                level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(MobJar.LIGHT_LEVEL, calculateLight()));
                updateBlock();
            }
            return true;
        }else{
            try{
                writeSimple(entity);
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public void writeSimple(Entity e){
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(e.getType()).toString());
        this.cachedEntity = e.getType().create(level);
        this.cachedEntity.setBoundingBox(new AABB(0,0,0,0,0,0));
        this.cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        this.extraDataTag = null;
        this.entityTag = tag;
        if(!level.isClientSide) {
            level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(MobJar.LIGHT_LEVEL, calculateLight()));
            updateBlock();
        }
    }

    public int calculateLight(){
        if(getEntity() == null)
            return 0;

        if(getEntity().isOnFire()){
            return 15;
        }
        if(getEntity() instanceof LightningBolt){
            return 15;
        }
        AtomicInteger light = new AtomicInteger();
        JarBehaviorRegistry.forEach(getEntity(), (behavior) -> {
            light.set(Math.max(light.get(), behavior.lightLevel(this)));
        });
        return light.get();
    }

    public @Nullable Entity getEntity(){
       if(entityTag != null && cachedEntity == null){
           cachedEntity = loadEntityFromTag(level, entityTag);
           if(cachedEntity == null){
               return null;
           }
           cachedEntity.setBoundingBox(new AABB(0,0,0,0,0,0));
           cachedEntity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        }
        return cachedEntity;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        if(entityTag == null)
            return false;
        Entity entity = loadEntityFromTag(level, entityTag);
        if(entity == null || entity.getType().is(EntityTags.JAR_RELEASE_BLACKLIST))
            return false;
        entity.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5);
        level.addFreshEntity(entity);
        removeEntity();
        return true;
    }

    public void removeEntity(){
        this.entityTag = null;
        this.cachedEntity = null;
        this.extraDataTag = null;
        updateBlock();
    }

    public CompoundTag getExtraDataTag(){
        return extraDataTag == null ? new CompoundTag() : extraDataTag;
    }

    public void setExtraDataTag(CompoundTag tag){
        this.extraDataTag = tag;
        setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);

        // Check both conditions because the entity may have never been loaded on the server side.
        if(entityTag != null || cachedEntity != null){
            cachedEntity = getEntity();
            if(cachedEntity != null) {
                tag.put("entityTag", saveEntityToTag(cachedEntity));
                if(tag.getCompound("entityTag").contains("id")) {
                    tag.putString("entityId", tag.getCompound("entityTag").getString("id"));
                }
            }
        }
        if(extraDataTag != null) {
            tag.put("extraMobData", extraDataTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if(pTag.contains("entityTag")){
            this.entityTag = pTag.getCompound("entityTag");
            this.cachedEntity = null;
        }
        this.extraDataTag = pTag.getCompound("extraMobData");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        this.cachedEntity = null;
        this.entityTag = null;
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public void dispatchBehavior(Consumer<JarBehavior<? extends Entity>> consumer){
        Entity entity = getEntity();
        if(entity == null){
            return;
        }
        JarBehaviorRegistry.forEach(entity, consumer);
    }

    public static Entity loadEntityFromTag(Level level, CompoundTag tag){
        Entity entity = EntityType.loadEntityRecursive(tag, level, Function.identity());
        if(entity == null){
            String id = tag.getString("id");
            Optional<EntityType<?>> type = EntityType.byString(id);
            if(type.isPresent()){
                entity = type.get().create(level);
            }
        }
        return entity;
    }

    public CompoundTag saveEntityToTag(Entity entity){
        CompoundTag tag = new CompoundTag();
        if(entity != null){
            entity.save(tag);
            if(tag.isEmpty()){
                tag.putString("id", EntityType.getKey(entity.getType()).toString());
            }
        }
        return tag;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        Entity entity = getEntity();
        if(entity != null){
            JarBehaviorRegistry.forEach(entity, (behavior) -> {
                behavior.getTooltip(this, tooltip);
            });
        }
    }


    @Override
    protected void applyImplicitComponents(DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        var jar = pComponentInput.getOrDefault(DataComponentRegistry.MOB_JAR, new MobJarData(Optional.empty(), Optional.empty()));
        this.entityTag = jar.entityTag().orElse(null);
        this.extraDataTag = jar.extraDataTag().orElse(null);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        pComponents.set(DataComponentRegistry.MOB_JAR, new MobJarData(this.entityTag, this.extraDataTag));
    }
}
