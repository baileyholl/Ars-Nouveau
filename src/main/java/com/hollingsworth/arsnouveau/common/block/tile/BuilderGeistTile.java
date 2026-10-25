package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BuilderGeistTile extends ModdedTile implements ITickable {
    StructureTemplate structureTemplate;
    public int progress = 0;
    public BlockPos targetPos;

    public BuilderGeistTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BUILDER_GEIST_TILE, pos, state);
    }

    @Override
    public void tick() {
        System.out.println("hi");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (structureTemplate != null) {
            tag.put("structureTemplate", structureTemplate.save(new CompoundTag()));
        }
        tag.putInt("progress", progress);
        NBTUtil.storeNullableBlockPos(tag, "targetPos", targetPos);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("structureTemplate")) {
            structureTemplate = new StructureTemplate();
            structureTemplate.load(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound("structureTemplate"));
        }
        progress = tag.getInt("progress");
        targetPos = NBTUtil.getNullablePos(tag, "targetPos");
    }
}
