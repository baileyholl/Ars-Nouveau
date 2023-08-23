package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class RelayTile extends AbstractSourceMachine implements ITooltipProvider, IWandable, GeoBlockEntity, ITickable {

    public RelayTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_RELAY_TILE, pos, state);
    }

    public RelayTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RelayTile(RegistryWrapper<? extends BlockEntityType<?>> type, BlockPos pos, BlockState state) {
        super(type.get(), pos, state);
    }

    public Pair<BlockPos, Direction> getToPos() {
        return toPos;
    }

    public void setToPos(Pair<BlockPos, Direction> toPos) {
        this.toPos = toPos;
    }

    public Pair<BlockPos, Direction> getFromPos() {
        return fromPos;
    }

    public void setFromPos(Pair<BlockPos, Direction> fromPos) {
        this.fromPos = fromPos;
    }

    private Pair<BlockPos, Direction> toPos;
    private Pair<BlockPos, Direction> fromPos;
    public boolean disabled;

    public boolean setTakeFrom(Pair<BlockPos, Direction> pos) {
        if (BlockUtil.distanceFrom(pos.getFirst(), this.worldPosition) > getMaxDistance() || pos.getFirst().equals(getBlockPos())) {
            return false;
        }
        this.fromPos = pos;
        updateBlock();
        return true;
    }

    public boolean setSendTo(Pair<BlockPos, Direction> pos) {
        if (BlockUtil.distanceFrom(pos.getFirst(), this.worldPosition) > getMaxDistance() || pos.getFirst().equals(getBlockPos()) || !(level.getBlockEntity(pos.getFirst()) instanceof AbstractSourceMachine)) {
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
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public int getMaxSource() {
        return 1000;
    }

    @Override
    public boolean canProvideSource() {
        return true;
    }

    public boolean closeEnough(BlockPos pos) {
        return BlockUtil.distanceFrom(pos, this.worldPosition) <= getMaxDistance() && !pos.equals(getBlockPos());
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction direction, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level.isClientSide || storedPos.equals(getBlockPos()) || !(level.getBlockEntity(storedPos) instanceof AbstractSourceMachine))
            return;
        direction = direction == null ? Direction.UP : direction;
        // Let relays take from us, no action needed.
        if (this.setSendTo(Pair.of(storedPos.immutable(),direction))) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", DominionWand.getPosString(storedPos)));
            ParticleUtil.beam(storedPos, worldPosition, level);
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos,  @Nullable Direction direction, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || storedPos.equals(getBlockPos()) || level.getBlockEntity(storedPos) instanceof RelayTile || !(level.getBlockEntity(storedPos) instanceof AbstractSourceMachine))
            return;
        direction = direction == null ? Direction.UP : direction;
        if (this.setTakeFrom(Pair.of(storedPos.immutable(),direction))) {
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
            list.add(ColorPos.centered(toPos.getFirst(), ParticleColor.TO_HIGHLIGHT));
        }
        if(fromPos != null){
            list.add(ColorPos.centered(fromPos.getFirst(), ParticleColor.FROM_HIGHLIGHT));
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

        if (fromPos != null && level.isLoaded(fromPos.getFirst())) {
            BlockEntity fromBE = level.getBlockEntity(fromPos.getFirst());
            if (fromBE == null) {
                //block has been removed
                fromPos = null;
                updateBlock();
            } else {
                LazyOptional<ISourceTile> cap = fromBE.getCapability(CapabilityRegistry.SOURCE_TILE);

                cap.resolve().ifPresentOrElse(
                        (otherTile) -> {
                            if (transferSource(otherTile, this) > 0) {
                                updateBlock();
                                ParticleUtil.spawnFollowProjectile(level, fromPos.getFirst(), worldPosition);
                            }
                        },
                        () -> {
                            //capability is no longer present
                            fromPos = null;
                            updateBlock();
                        }
                );
            }
        }

        if (toPos != null && level.isLoaded(toPos.getFirst())) {
            BlockEntity toBE = level.getBlockEntity(toPos.getFirst());
            if (toBE == null) {
                //block has been removed
                toPos = null;
                updateBlock();
            } else {
                LazyOptional<ISourceTile> cap = toBE.getCapability(CapabilityRegistry.SOURCE_TILE);

                cap.resolve().ifPresentOrElse(
                        (otherTile) -> {
                            if (transferSource(this, otherTile) > 0) {
                                updateBlock();
                                ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos.getFirst());
                            }
                        },
                        () -> {
                            //capability is no longer present
                            toPos = null;
                            updateBlock();
                        }
                );
            }
        }
    }

    String TO = "to_";
    String FROM = "from";
    String DIR_SUFFIX = "dir_";

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.toPos = null;
        this.fromPos = null;

        BlockPos toBlockPos = null;
        Direction toDir = null;
        BlockPos fromBlockPos = null;
        Direction fromDir = null;
        
        if (NBTUtil.hasBlockPos(tag, TO)) {
            toBlockPos = NBTUtil.getBlockPos(tag, TO);
        }
        if (NBTUtil.hasBlockPos(tag, FROM)) {
            fromBlockPos = NBTUtil.getBlockPos(tag, FROM);
        }
        if(tag.contains(TO + DIR_SUFFIX)){
            toDir = Direction.byName(tag.getString(TO + DIR_SUFFIX));
        }
        if(tag.contains(FROM + DIR_SUFFIX)){
            fromDir = Direction.byName(tag.getString(FROM + DIR_SUFFIX));
        }

        //default directions to not break existing worlds
        toDir = toDir == null ? Direction.UP : toDir;
        fromDir = fromDir == null ? Direction.UP : fromDir;

        //assemble pairs
        if(toBlockPos != null){
            toPos = Pair.of(toBlockPos,toDir);
        }
        if(fromBlockPos != null){
            fromPos = Pair.of(fromBlockPos,fromDir);
        }

        this.disabled = tag.getBoolean("disabled");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (toPos != null) {
            NBTUtil.storeBlockPos(tag, TO, toPos.getFirst().immutable());
            tag.putString(TO + DIR_SUFFIX, toPos.getSecond().getName());
        } else {
            NBTUtil.removeBlockPos(tag, TO);
        }

        if (fromPos != null) {
            NBTUtil.storeBlockPos(tag, FROM, fromPos.getFirst().immutable());
            tag.putString(FROM + DIR_SUFFIX, fromPos.getSecond().getName());
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
