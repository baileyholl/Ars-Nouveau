package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class RelayTile extends AbstractSourceMachine implements ITooltipProvider, IWandable, GeoBlockEntity, ITickable {

    public RelayTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_RELAY_TILE.get(), pos, state);
    }

    public RelayTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockPos getToPos() {
        return toPos;
    }

    public void setToPos(BlockPos toPos) {
        this.toPos = toPos;
    }

    public BlockPos getFromPos() {
        return fromPos;
    }

    public void setFromPos(BlockPos fromPos) {
        this.fromPos = fromPos;
    }

    private BlockPos toPos;
    private BlockPos fromPos;
    public boolean disabled;

    public boolean setTakeFrom(BlockPos pos) {
        if (BlockUtil.distanceFrom(pos, this.worldPosition) > getMaxDistance() || pos.equals(getBlockPos())) {
            return false;
        }
        this.fromPos = pos;
        updateBlock();
        return true;
    }

    public boolean setSendTo(BlockPos pos) {
        if (BlockUtil.distanceFrom(pos, this.worldPosition) > getMaxDistance() || pos.equals(getBlockPos())) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof AbstractSourceMachine) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, pos, null) == null) {
            return false;
        }
        this.toPos = pos;
        updateBlock();
        return true;
    }

    public int getMaxDistance() {
        return 30;
    }

    public void clearPos() {
        this.toPos = null;
        this.fromPos = null;
        updateBlock();
    }

    @Override
    protected @NotNull SourceStorage createDefaultStorage() {
        return new SourceStorage(1000, 1000);
    }

    public boolean closeEnough(BlockPos pos) {
        return BlockUtil.distanceFrom(pos, this.worldPosition) <= getMaxDistance() && !pos.equals(getBlockPos());
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level.isClientSide || storedPos.equals(getBlockPos())) {
            return;
        }
        if (!(level.getBlockEntity(storedPos) instanceof AbstractSourceMachine) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, storedPos, null) == null) {
            return;
        }
        // Let relays take from us, no action needed.
        if (this.setSendTo(storedPos.immutable())) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", DominionWand.getPosString(storedPos)));
            ParticleUtil.beam(storedPos, worldPosition, level);
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || storedPos.equals(getBlockPos()) || level.getBlockEntity(storedPos) instanceof RelayTile)
            return;
        if (!(level.getBlockEntity(storedPos) instanceof AbstractSourceMachine) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, storedPos, null) == null) {
            return;
        }
        if (this.setTakeFrom(storedPos.immutable())) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.take", DominionWand.getPosString(storedPos)));
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.clearPos();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if (toPos != null) {
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        }
        if (fromPos != null) {
            list.add(ColorPos.centered(fromPos, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }


    @Override
    public void tick() {
        if (level.isClientSide || disabled) {
            return;
        }
        if (level.getGameTime() % 20 != 0)
            return;

        if (fromPos != null && level.isLoaded(fromPos)) {
            // Block has been removed
            if (!(level.getBlockEntity(fromPos) instanceof AbstractSourceMachine) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, fromPos, null) == null) {
                fromPos = null;
                updateBlock();
                return;
            } else if (level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, fromPos, null) instanceof ISourceCap sourceHandler) {
                // Transfer mana fromPos to this
                if (transferSource(sourceHandler, getSourceStorage()) > 0) {
                    ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition, this.getColor());
                }
            } else if (level.getBlockEntity(fromPos) instanceof AbstractSourceMachine fromTile) {
                // Transfer mana fromPos to this
                if (transferSource(fromTile, this) > 0) {
                    updateBlock();
                    ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition, this.getColor());
                }
            }
        }

        if (toPos != null && level.isLoaded(toPos)) {
            if (!(level.getBlockEntity(toPos) instanceof AbstractSourceMachine) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, toPos, null) == null) {
                toPos = null;
                updateBlock();
                return;
            }
            if (level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, toPos, null) instanceof ISourceCap sourceHandler) {
                // Transfer mana from this to toPos
                if (transferSource(this.getSourceStorage(), sourceHandler) > 0) {
                    ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos, this.getColor());
                }
            } else if (level.getBlockEntity(toPos) instanceof AbstractSourceMachine toTile) {
                // Transfer mana from this to toPos
                if (transferSource(this, toTile) > 0) {
                    ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos, this.getColor());
                }
            }
        }
    }

    String TO = "to_";
    String FROM = "from";

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        this.toPos = null;
        this.fromPos = null;

        if (NBTUtil.hasBlockPos(tag, TO)) {
            this.toPos = NBTUtil.getBlockPos(tag, TO);
        }
        if (NBTUtil.hasBlockPos(tag, FROM)) {
            this.fromPos = NBTUtil.getBlockPos(tag, FROM);
        }
        this.disabled = tag.getBoolean("disabled");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (toPos != null) {
            NBTUtil.storeBlockPos(tag, TO, toPos.immutable());
        } else {
            NBTUtil.removeBlockPos(tag, TO);
        }

        if (fromPos != null) {
            NBTUtil.storeBlockPos(tag, FROM, fromPos.immutable());
        } else {
            NBTUtil.removeBlockPos(tag, FROM);
        }
        tag.putBoolean("disabled", disabled);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (toPos == null) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_to"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_to", 1));
        }
        if (fromPos == null) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_from"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_from", 1));
        }

        if (disabled) {
            tooltip.add(Component.translatable("ars_nouveau.tooltip.turned_off"));
        }
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "rotate_controller", 0, this::idlePredicate));
        data.add(new AnimationController<>(this, "float_controller", 0, this::floatPredicate));
    }

    private <P extends GeoAnimatable> PlayState idlePredicate(AnimationState<P> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("floating"));
        return PlayState.CONTINUE;
    }

    private <P extends GeoAnimatable> PlayState floatPredicate(AnimationState<P> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("rotation"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
