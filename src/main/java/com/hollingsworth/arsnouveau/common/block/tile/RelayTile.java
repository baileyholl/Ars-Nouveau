package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;

public class RelayTile extends AbstractSourceMachine implements ITooltipProvider, IWandable, IAnimatable, ITickable {

    public RelayTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_RELAY_TILE, pos, state);
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
        update();
        return true;
    }

    public boolean setSendTo(BlockPos pos) {
        if (BlockUtil.distanceFrom(pos, this.worldPosition) > getMaxDistance() || pos.equals(getBlockPos())) {
            return false;
        }
        this.toPos = pos;
        update();
        return true;
    }

    public int getMaxDistance() {
        return 30;
    }

    public void clearPos() {
        this.toPos = null;
        this.fromPos = null;
        update();
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public int getMaxSource() {
        return 1000;
    }

    public boolean closeEnough(BlockPos pos) {
        return BlockUtil.distanceFrom(pos, this.worldPosition) <= getMaxDistance() && !pos.equals(getBlockPos());
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level.isClientSide || storedPos.equals(getBlockPos()))
            return;
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
        if (storedPos == null || storedPos.equals(getBlockPos()))
            return;
        if (level.getBlockEntity(storedPos) instanceof RelayTile)
            return;
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
    public void tick() {
        if (level.isClientSide || disabled) {
            return;
        }
        if (level.getGameTime() % 20 != 0)
            return;

        if (fromPos != null && level.isLoaded(fromPos)) {
            // Block has been removed
            if (!(level.getBlockEntity(fromPos) instanceof AbstractSourceMachine)) {
                fromPos = null;
                update();
                return;
            } else if (level.getBlockEntity(fromPos) instanceof AbstractSourceMachine fromTile) {
                // Transfer mana fromPos to this
                if (transferSource(fromTile, this) > 0) {
                    update();
                    ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition);
                }
            }
        }

        if (toPos != null && level.isLoaded(toPos)) {
            if (!(level.getBlockEntity(toPos) instanceof AbstractSourceMachine)) {
                toPos = null;
                update();
                return;
            }
            AbstractSourceMachine toTile = (AbstractSourceMachine) this.level.getBlockEntity(toPos);
            if (transferSource(this, toTile) > 0) {
                ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos);
            }
        }
    }

    String TO = "to_";
    String FROM = "from";

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (NBTUtil.hasBlockPos(tag, TO)) {
            this.toPos = NBTUtil.getBlockPos(tag, TO);
        }
        if (NBTUtil.hasBlockPos(tag, FROM)) {
            this.fromPos = NBTUtil.getBlockPos(tag, FROM);
        }
        this.disabled = tag.getBoolean("disabled");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
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

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
        data.addAnimationController(new AnimationController(this, "float_controller", 0, this::floatPredicate));
    }

    private <P extends IAnimatable> PlayState idlePredicate(AnimationEvent<P> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("floating", true));
        return PlayState.CONTINUE;
    }

    private <P extends IAnimatable> PlayState floatPredicate(AnimationEvent<P> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("rotation", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
