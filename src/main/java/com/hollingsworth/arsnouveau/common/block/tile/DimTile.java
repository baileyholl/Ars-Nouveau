package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class DimTile extends ModdedTile implements ITickable {
    public StructureTemplate template;
    public ResourceKey<Level> key;

    public DimTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public DimTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.DIM_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if(key == null)
            return;
        if(level instanceof ServerLevel serverLevel && template == null){
            ServerLevel dimLevel = serverLevel.getServer().getLevel(key);
            if(dimLevel != null){
                SectionPos chunkPos = SectionPos.of(BlockPos.ZERO);
                int chunkLoadingDistance = 5;
                forceLoad(chunkPos, chunkLoadingDistance, dimLevel,true);

                BlockPos pos = BlockPos.ZERO;
                Vec3i size = new Vec3i(16, 16, 16);
                template = new StructureTemplate();
                template.fillFromWorld(dimLevel, pos, size, true, null);
                updateBlock();
                forceLoad(chunkPos, chunkLoadingDistance, dimLevel,false);
            }
        }
    }

    private void forceLoad(SectionPos chunkPos, int chunkLoadingDistance, ServerLevel dimLevel, boolean load) {
        for (int x = chunkPos.getX() - chunkLoadingDistance; x <= chunkPos.getX() + chunkLoadingDistance; x++) {
            for (int z = chunkPos.getZ() - chunkLoadingDistance; z <= chunkPos.getZ() + chunkLoadingDistance; z++) {
                ArsNouveau.ticketController.forceChunk(dimLevel, worldPosition, x, z, load, load);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(key != null) {
            tag.putString("key", key.location().toString());
        }
        if(template != null){
            CompoundTag templateTag = new CompoundTag();
            template.save(templateTag);
            tag.put("template", templateTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("key"))
            key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("key")));
        if(tag.contains("template")) {
            template = new StructureTemplate();
            template.load(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("template"));
        }
    }
}
