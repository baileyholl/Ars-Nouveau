package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.schematic.ItemRequirement;
import com.hollingsworth.arsnouveau.common.block.tile.schematic.SchematicPrinter;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.nuggets.client.overlay.IWorldTooltipProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BuilderGeistTile extends ModdedTile implements ITickable, IWandable, GeoBlockEntity, IWorldTooltipProvider {
    public static final int PLACE_DELAY = 10;
    public static final int SCAN_LIMIT_PER_TICK = 256;

    protected StructureTemplate structureTemplate;
    protected final SchematicPrinter printer = new SchematicPrinter();
    public int progress = 0;
    public BlockPos targetPos;
    private int cooldown;
    private boolean holdTarget;
    public boolean started;

    public BuilderGeistTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BUILDER_GEIST_TILE, pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide || structureTemplate == null || targetPos == null)
            return;
        if (!printer.isLoaded()) {
            holdTarget = false;
            printer.loadSchematic(structureTemplate, targetPos, new StructurePlaceSettings(), level);
            if (printer.isErrored() || printer.isWorldEmpty())
                finishPrinting();
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        for (int scanned = 0; scanned < SCAN_LIMIT_PER_TICK; scanned++) {
            if (!holdTarget && !printer.advanceCurrentPos()) {
                finishPrinting();
                return;
            }
            holdTarget = !printer.isCurrentTargetLoaded(level);
            if (holdTarget)
                return;
            ItemRequirement requirement = printer.getCurrentRequirement();
            if (requirement.isInvalid() || !printer.shouldPlaceCurrent(level, this::shouldPlace))
                continue;
            printer.handleCurrentTarget(this::placeBlock, this::placeEntity);
            progress++;
            cooldown = PLACE_DELAY;
            setChanged();
            return;
        }
    }

    protected boolean shouldPlace(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, BlockState toReplace, @Nullable BlockState toReplaceOther, boolean isNormalCube) {
        return true;
    }

    protected void placeBlock(BlockPos target, BlockState state, @Nullable BlockEntity blockEntity) {
    }

    protected void placeEntity(BlockPos target, Entity entity) {
    }

    protected void finishPrinting() {
        printer.sendBlockUpdates(level);
        printer.resetSchematic();
        structureTemplate = null;
        updateBlock();
    }

    @Override
    public Result onFirstConnection(@Nullable GlobalPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if(storedPos == null || IWandable.getStoredDimension(storedPos, storedEntity) != level.dimension()){
            return Result.FAIL;
        }

        if(storedPos.pos().equals(worldPosition) || BlockUtil.distanceFrom(level, storedPos.pos(), worldPosition) > getRange()){
            return Result.FAIL;
        }

        this.targetPos = storedPos.pos();
        this.progress = 0;
        printer.resetSchematic();

        return Result.SUCCESS;
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if(targetPos != null) {
            list.add(ColorPos.centered(targetPos));
        }
        return IWandable.super.getWandHighlight(list);
    }

    public int getRange() {
        return 40;
    }

    public void setStructure(StructureTemplate structureTemplate) {
        if(!this.level.isClientSide) {
            this.structureTemplate = structureTemplate;
            printer.resetSchematic();
            updateBlock();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (structureTemplate != null) {
            tag.put("structureTemplate", structureTemplate.save(new CompoundTag()));
        }
        tag.putInt("progress", progress);
        NBTUtil.storeNullableBlockPos(tag, "targetPos", targetPos);
        tag.putBoolean("started", started);
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
        started = tag.getBoolean("started");
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(started){
            tooltip.add(Component.translatable("ars_nouveau.builder_geist.started"));
        }
        if(targetPos == null){
            tooltip.add(Component.translatable("ars_nouveau.builder_geist.no_target"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.builder_geist.target", targetPos.getX(), targetPos.getY(), targetPos.getZ()));
        }
        if(structureTemplate == null){
            tooltip.add(Component.translatable("ars_nouveau.builder_geist.no_structure"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.builder_geist.structure", structureTemplate.getSize().getX(), structureTemplate.getSize().getY(), structureTemplate.getSize().getZ()));
        }
    }
}
