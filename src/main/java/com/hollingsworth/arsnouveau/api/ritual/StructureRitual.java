package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class StructureRitual extends AbstractRitual {
    public ResourceLocation structure;
    public BlockPos offset;
    public List<StructureTemplate.StructureBlockInfo> blocks = new ArrayList<>();
    public List<StructureTemplate.StructureEntityInfo> entityInfoList = new ArrayList<>();
    public int index;
    public int sourceRequired;
    public boolean hasConsumed;
    public ResourceKey<Biome> biome;

    public StructureRitual(ResourceLocation structure, BlockPos offset, int sourceRequired, ResourceKey<Biome> biome){
        this.structure = structure;
        this.offset = offset;
        this.sourceRequired = sourceRequired;
        this.hasConsumed = sourceRequired == 0;
        this.biome = biome;
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if(getWorld().isClientSide)
            return;
        setup();
    }

    public void setup(){
        if(getWorld().isClientSide)
            return;
        StructureTemplateManager manager = getWorld().getServer().getStructureManager();
        StructureTemplate structureTemplate = manager.getOrCreate(structure);
        List<StructureTemplate.StructureBlockInfo> infoList = structureTemplate.palettes.get(0).blocks();
        blocks = new ArrayList<>(infoList.stream().filter(b -> !b.state().isAir()).toList());
        blocks.sort(new StructureComparator(getPos(), offset));
        entityInfoList = new ArrayList<>(((StructureTemplateAccessor) structureTemplate).getEntityInfoList());
    }

    @Override
    protected void tick() {
        if(getWorld().isClientSide)
            return;
        if(!hasConsumed){
            setNeedsSource(true);
            return;
        }
        int placeCount = 0;
        while(placeCount < 5){
            if (index >= blocks.size()) {
                for(StructureTemplate.StructureEntityInfo entityInfo : entityInfoList){
                    BlockPos translatedPos = getPos().offset(entityInfo.blockPos.getX(), entityInfo.blockPos.getY(), entityInfo.blockPos.getZ()).offset(offset);
                    Optional<Entity> entity;
                    try{
                        CompoundTag entityTag = entityInfo.nbt;
                        entityTag.remove("UUID");
                        entity = EntityType.create(entityTag, getWorld());
                    }catch (Exception e){
                        continue;
                    }
                    if(entity.isPresent()){
                        entity.get().moveTo(translatedPos.getX(), translatedPos.getY(), translatedPos.getZ());
                        getWorld().addFreshEntity(entity.get());
                    }
                }
                setFinished();
                return;
            }
            StructureTemplate.StructureBlockInfo blockInfo = blocks.get(index);
            BlockPos translatedPos = getPos().offset(blockInfo.pos().getX(), blockInfo.pos().getY(), blockInfo.pos().getZ()).offset(offset);
            if (getWorld().getBlockState(translatedPos).canBeReplaced()) {
                getWorld().setBlock(translatedPos, blockInfo.state(), 2);
                BlockEntity blockentity1 = getWorld().getBlockEntity(translatedPos);
                if (blockentity1 != null) {
                    if (blockentity1 instanceof RandomizableContainerBlockEntity) {
                        blockInfo.nbt().putLong("LootTableSeed", getWorld().random.nextLong());
                    }

                    if(blockInfo.nbt() != null) {
                        blockentity1.loadWithComponents(blockInfo.nbt(), getWorld().registryAccess());
                    }
                }
                getWorld().playSound(null, translatedPos, blockInfo.state().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                placeCount++;
                if(biome != null){
                    RitualUtil.changeBiome(getWorld(), translatedPos, biome);
                }
            }
            index++;
        }
    }

    @Override
    public void setNeedsSource(boolean needMana) {
        super.setNeedsSource(needMana);
        if(!needMana){
            hasConsumed = true;
        }
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        index = tag.getInt("index");
        hasConsumed = tag.getBoolean("hasConsumed");
        setup();
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        tag.putInt("index", index);
        tag.putBoolean("hasConsumed", hasConsumed);
    }

    @Override
    public int getSourceCost() {
        return sourceRequired;
    }

    @Override
    public abstract ResourceLocation getRegistryName();
}
