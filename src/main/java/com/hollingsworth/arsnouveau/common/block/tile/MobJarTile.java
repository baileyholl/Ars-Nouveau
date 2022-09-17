package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Function;

public class MobJarTile extends ModdedTile implements ITickable {
    @Nullable
    public Entity displayEntity;

    public CompoundTag entityTag;

    public MobJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MOB_JAR_TILE, pos, state);
    }

    @Override
    public void tick() {
        try {
            if (level.isClientSide && this.displayEntity != null) {
                if(displayEntity instanceof Mob mob){
                    mob.getLookControl().tick();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean setEntityData(Entity entity){
        CompoundTag tag = new CompoundTag();
        if(entity.shouldBeSaved() && entity.save(tag)){
            this.entityTag = tag;
            updateBlock();
            return true;
        }
        return false;
    }

    public @Nullable Entity getEntity(){
        if(entityTag == null){
            return null;
        }
        return EntityType.loadEntityRecursive(entityTag, level, Function.identity());
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(entityTag != null){
            tag.put("entityTag", entityTag);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("entityTag")){
            this.entityTag = pTag.getCompound("entityTag");
        }
    }
}
