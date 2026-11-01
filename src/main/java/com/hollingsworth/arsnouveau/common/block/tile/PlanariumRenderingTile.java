package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.PlanariumRenderingWorld;
import com.hollingsworth.arsnouveau.client.renderer.world.CulledStatePos;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.mixin.structure.StructureTemplateAccessor;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientRequestDim;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDimTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.nuggets.common.util.BlockPosHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class PlanariumRenderingTile extends ModdedTile implements ITickable, Nameable {
    public ResourceKey<Level> key;
    public long lastUpdated = 0;
    boolean playersNearby = true;
    public Component name;

    public PlanariumRenderingTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public PlanariumRenderingTile(BlockEntityTypeRegistryWrapper<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void tick() {
        if (key == null)
            return;
        if(!(level instanceof ServerLevel serverLevel)){
            return;
        }
        if (level.getGameTime() % 200 == 0) {
            playersNearby = false;
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (BlockPosHelpers.distanceBetween(worldPosition, serverPlayer.blockPosition()) < 64) {
                    playersNearby = true;
                    break;
                }
            }
        }
        if (!playersNearby) {
            return;
        }
        if (level.getGameTime() % ServerConfig.PLANARIUM_UPDATE_RATE.get() == 0) {
            PlanariumTile.DimManager.Entry entry1 = PlanariumTile.dimManager.getOrCreateTemplate(serverLevel, worldPosition, key);
            if (entry1 != null) {
                // Notifies nearby players if the dimension has changed
                if (entry1.lastUpdated() > lastUpdated) {
                    lastUpdated = entry1.lastUpdated();
                    StructureTemplate template = PlanariumTile.dimManager.getTemplate(key);
                    if (template != null) {
                        Networking.sendToNearbyClient(level, worldPosition, new PacketUpdateDimTile(worldPosition, template));
                    }
                }
            }
        }
    }


    public @Nullable StructureTemplate getTemplate() {
        if (key == null) {
            return null;
        }
        if (level.isClientSide) {
            var entry = PlanariumTile.clientTemplates.get(key);
            return entry == null ? null : entry.template();
        } else {
            return PlanariumTile.dimManager.getTemplate(key);
        }
    }


    public void setTemplateClientSide(StructureTemplate template) {
        if (level.isClientSide && key != null) {
            ArrayList<CulledStatePos> statePos = new ArrayList<>();
            var palette = ((StructureTemplateAccessor) template).getPalettes().get(0);
            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
                statePos.add(new CulledStatePos(blockInfo.state(), blockInfo.pos(), blockInfo.nbt()));
            }
            PlanariumTile.clientTemplates.put(key, new PlanariumTile.ClientDimEntry(template, level.getGameTime()));
            this.lastUpdated = level.getGameTime();
        }
    }



    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        // Checks if the client is up to date with this template compared to
        if (level.isClientSide) {
            if (key != null && (!PlanariumTile.clientTemplates.containsKey(key) || PlanariumTile.clientTemplates.get(key).lastUpdated() < lastUpdated)) {
                Networking.sendToServer(new PacketClientRequestDim(worldPosition));
            }
        }
    }

    @Override
    public @Nullable Component getCustomName() {
        return name;
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        Component customName = componentInput.get(DataComponents.CUSTOM_NAME);
        if (customName == null) {
            this.name = null;
        } else {
            setName(customName);
        }
    }

    public void setName(Component name) {
        this.name = name;
        if (level instanceof ServerLevel serverLevel) {
            if (name == null) {
                this.key = null;
            } else {
                this.setDimension(name.getString(), serverLevel);
            }
        }
    }

    @Override
    public Component getName() {
        return name;
    }

    public abstract @Nullable ServerLevel setDimension(String dimName, ServerLevel serverLevel);

    public IResolveListener onResolve() {
        return new IResolveListener() {
            @Override
            public void onPostResolve(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
                if (world instanceof ServerLevel && resolveEffect instanceof EffectName && spell.name() != null) {
                    setName(Component.literal(spell.name()));
                }
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateBlock();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (key != null) {
            tag.putString("key", key.location().toString());
        }
        if (name != null) {
            tag.putString("name", name.getString());
        }

        tag.putLong("lastUpdated", lastUpdated);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        key = tag.contains("key") ? ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("key"))) : null;
        this.name = tag.contains("name") ? Component.literal(tag.getString("name")) : null;
        lastUpdated = tag.getLong("lastUpdated");
    }
}
