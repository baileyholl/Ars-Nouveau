package com.hollingsworth.arsnouveau.common.compat;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CreateHandler {

    public static void clampGlueBoxes(Level level, AABB area, CompoundTag structure) {
        ListTag entities = structure.getList(StructureTemplate.ENTITIES_TAG, Tag.TAG_COMPOUND);
        entities.removeIf(entity -> entity instanceof CompoundTag compound && compound.getCompound(StructureTemplate.ENTITY_TAG_NBT).getString("id").equals("create:super_glue"));
        for (SuperGlueEntity glue : SuperGlueEntity.collectCropped(level, area)) {
            Vec3 pos = glue.position().subtract(area.minX, area.minY, area.minZ);
            BlockPos blockPos = BlockPos.containing(pos);
            ListTag posTag = new ListTag();
            posTag.addAll(List.of(DoubleTag.valueOf(pos.x), DoubleTag.valueOf(pos.y), DoubleTag.valueOf(pos.z)));
            ListTag blockPosTag = new ListTag();
            blockPosTag.addAll(List.of(IntTag.valueOf(blockPos.getX()), IntTag.valueOf(blockPos.getY()), IntTag.valueOf(blockPos.getZ())));
            CompoundTag glueTag = new CompoundTag();
            glue.save(glueTag);
            CompoundTag entry = new CompoundTag();
            entry.put(StructureTemplate.ENTITY_TAG_POS, posTag);
            entry.put(StructureTemplate.ENTITY_TAG_BLOCKPOS, blockPosTag);
            entry.put(StructureTemplate.ENTITY_TAG_NBT, glueTag);
            entities.add(entry);
        }
        structure.put(StructureTemplate.ENTITIES_TAG, entities);
    }
}
