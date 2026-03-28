package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.storage.TagValueInput;
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
    public Identifier structure;
    public BlockPos offset;
    public List<StructureTemplate.StructureBlockInfo> blocks = new ArrayList<>();
    public List<StructureTemplate.StructureEntityInfo> entityInfoList = new ArrayList<>();
    public int index;
    public int sourceRequired;
    public boolean hasConsumed;
    public ResourceKey<Biome> biome;
    public boolean hasDoneSetup;

    public StructureRitual(Identifier structure, BlockPos offset, int sourceRequired, ResourceKey<Biome> biome) {
        this.structure = structure;
        this.offset = offset;
        this.sourceRequired = sourceRequired;
        this.hasConsumed = sourceRequired == 0;
        this.biome = biome;
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if (getWorld().isClientSide())
            return;
        setup();
    }

    public void setup() {
        if (getWorld().isClientSide() || hasDoneSetup)
            return;
        StructureTemplateManager manager = getWorld().getServer().getStructureManager();
        StructureTemplate structureTemplate = manager.getOrCreate(structure);
        List<StructureTemplate.StructureBlockInfo> infoList = structureTemplate.palettes.get(0).blocks();
        blocks = new ArrayList<>(infoList.stream().filter(b -> !b.state().isAir()).toList());
        blocks.sort(new StructureComparator(getPos(), offset));
        entityInfoList = new ArrayList<>(((StructureTemplateAccessor) structureTemplate).getEntityInfoList());
        hasDoneSetup = true;
    }

    @Override
    protected void tick() {
        if (getWorld().isClientSide())
            return;
        if (!hasDoneSetup) {
            setup();
        }
        if (!hasConsumed) {
            setNeedsSource(true);
            return;
        }
        int placeCount = 0;
        while (placeCount < 5) {
            if (index >= blocks.size()) {
                for (StructureTemplate.StructureEntityInfo entityInfo : entityInfoList) {
                    BlockPos translatedPos = getPos().offset(entityInfo.blockPos.getX(), entityInfo.blockPos.getY(), entityInfo.blockPos.getZ()).offset(offset);
                    Optional<Entity> entity;
                    try {
                        CompoundTag entityTag = entityInfo.nbt;
                        entityTag.remove("UUID");
                        // 1.21.11: EntityType.create(CompoundTag, Level) → create(ValueInput, Level, EntitySpawnReason)
                        var input = TagValueInput.create(ProblemReporter.DISCARDING, getWorld().registryAccess(), entityTag);
                        entity = EntityType.create(input, getWorld(), EntitySpawnReason.LOAD);
                    } catch (Exception e) {
                        continue;
                    }
                    if (entity.isPresent()) {
                        // 1.21.11: moveTo(x,y,z) → snapTo(x,y,z)
                        entity.get().snapTo(translatedPos.getX(), translatedPos.getY(), translatedPos.getZ());
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

                    if (blockInfo.nbt() != null) {
                        // 1.21.11: loadWithComponents(CompoundTag, RegistryAccess) → loadWithComponents(ValueInput)
                        var beInput = TagValueInput.create(ProblemReporter.DISCARDING, getWorld().registryAccess(), blockInfo.nbt());
                        blockentity1.loadWithComponents(beInput);
                    }
                }
                getWorld().playSound(null, translatedPos, blockInfo.state().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                placeCount++;
                if (biome != null) {
                    RitualUtil.changeBiome(getWorld(), translatedPos, biome);
                }
            }
            index++;
        }
    }

    @Override
    public void setNeedsSource(boolean needMana) {
        super.setNeedsSource(needMana);
        if (!needMana) {
            hasConsumed = true;
        }
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        index = tag.getIntOr("index", 0);
        hasConsumed = tag.getBooleanOr("hasConsumed", false);
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
    public abstract Identifier getRegistryName();
}
