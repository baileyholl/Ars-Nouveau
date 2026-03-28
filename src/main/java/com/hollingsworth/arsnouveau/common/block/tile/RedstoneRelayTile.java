package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RedstoneRelay;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class RedstoneRelayTile extends ModdedTile implements IWandable, ITooltipProvider, ITickable, GeoBlockEntity {
    public List<BlockPos> poweredFrom = new ArrayList<>();
    public List<BlockPos> powering = new ArrayList<>();

    private int localPower;
    private int powerFromParentRelays;
    private int currentPower;

    private @Nullable BlockPos currentParent;
    boolean updateListeners;

    public RedstoneRelayTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.REDSTONE_RELAY_TILE.get(), pos, state);
    }

    public int getOutputPower() {
        return currentPower;
    }

    @Override
    public void tick() {

        if (!level.isClientSide() && updateListeners) {
            // force update a tick later to account for connection checks
            calculateNewPower();
            updateListeners = false;
        }
    }

    public void onParentPowerChange(BlockPos pos, int newParentPower) {
        if (!this.poweredFrom.contains(pos)) {
            level.updateNeighborsAt(worldPosition, BlockRegistry.REDSTONE_RELAY.get());
            return;
        }
        if (pos.equals(currentParent)) {
            powerFromParentRelays = newParentPower;
            calculateNewPower();
        } else if (newParentPower > powerFromParentRelays) {
            currentParent = pos.immutable();
            powerFromParentRelays = newParentPower;
            calculateNewPower();
        }

        level.updateNeighborsAt(worldPosition, BlockRegistry.REDSTONE_RELAY.get());
    }

    public void calculateNewPower() {
        if (level.isClientSide()) {
            return;
        }
        int oldPower = currentPower;
        int newPower = localPower;

        for (BlockPos pos : poweredFrom) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof RedstoneRelayTile redstoneRelayTile) {
                if (redstoneRelayTile.getOutputPower() > localPower) {
                    newPower = redstoneRelayTile.getOutputPower();
                    currentParent = pos.immutable();
                    powerFromParentRelays = redstoneRelayTile.getOutputPower();
                }
            }
        }

        if (newPower != oldPower) {
            setNewPower(newPower);
        }
    }

    protected void setNewPower(int power) {
        this.currentPower = power;
        BlockState state = level.getBlockState(worldPosition);
        if (!state.hasProperty(RedstoneRelay.POWER)) {
            return;
        }
        this.level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(RedstoneRelay.POWER, power), 3);
        updateBlock();
        level.updateNeighborsAt(worldPosition, state.getBlock());
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(worldPosition.relative(direction), state.getBlock());
        }
        updateListeners();

    }

    @Override
    public boolean updateBlock() {
        return super.updateBlock();
    }

    public void updateListeners() {
        for (BlockPos pos : powering) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof RedstoneRelayTile redstoneRelayTile) {
                redstoneRelayTile.onParentPowerChange(worldPosition, currentPower);
            }
        }
    }

    public void onParentRemoved(BlockPos pos) {
        poweredFrom.remove(pos);
        updateBlock();
        if (currentParent != null && currentParent.equals(pos)) {
            calculateNewPower();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level.isClientSide() || storedPos.equals(getBlockPos()) || !(level.getBlockEntity(storedPos) instanceof RedstoneRelayTile))
            return;

        if (BlockUtil.distanceFrom(storedPos, this.worldPosition) <= getMaxDistance()) {
            storedPos = storedPos.immutable();
            if (this.poweredFrom.contains(storedPos)) {
                this.poweredFrom.remove(storedPos);
            } else {
                this.poweredFrom.add(storedPos);
            }
            updateListeners = true;
            updateBlock();
        }
    }

    public int getMaxDistance() {
        return 30;
    }

    @Override
    public void onFinishedConnectionFirst(@javax.annotation.Nullable BlockPos storedPos, @javax.annotation.Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level.isClientSide() || storedPos.equals(getBlockPos()) || !(level.getBlockEntity(storedPos) instanceof RedstoneRelayTile))
            return;

        if (BlockUtil.distanceFrom(storedPos, this.worldPosition) <= getMaxDistance()) {
            storedPos = storedPos.immutable();
            if (this.powering.contains(storedPos)) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.remove", DominionWand.getPosString(storedPos)));
                this.powering.remove(storedPos);
            } else {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", DominionWand.getPosString(storedPos)));
                this.powering.add(storedPos);
                ParticleUtil.beam(storedPos, worldPosition, level);
            }
            updateListeners = true;
            updateBlock();
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        for (BlockPos pos : poweredFrom) {
            list.add(ColorPos.centered(pos, ParticleColor.FROM_HIGHLIGHT));
        }
        for (BlockPos pos : powering) {
            list.add(ColorPos.centered(pos, ParticleColor.TO_HIGHLIGHT));
        }
        return list;
    }

    @Override
    public void loadAdditional(ValueInput pTag) {
        super.loadAdditional(pTag);
        poweredFrom = new ArrayList<>();
        powering = new ArrayList<>();
        currentParent = null;
        pTag.childrenListOrEmpty("poweredFrom").forEach(entry ->
            poweredFrom.add(BlockPos.of(entry.getLongOr("pos", 0L)))
        );
        pTag.childrenListOrEmpty("powering").forEach(entry ->
            powering.add(BlockPos.of(entry.getLongOr("pos", 0L)))
        );
        localPower = pTag.getIntOr("localPower", 0);
        currentPower = pTag.getIntOr("currentPower", 0);
        powerFromParentRelays = pTag.getIntOr("powerFromParentRelays", 0);
        pTag.getLong("currentParent").ifPresent(l -> currentParent = BlockPos.of(l));
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        var listTag = tag.childrenList("poweredFrom");
        for (BlockPos pos : poweredFrom) {
            listTag.addChild().putLong("pos", pos.asLong());
        }
        var poweringTag = tag.childrenList("powering");
        for (BlockPos pos : powering) {
            poweringTag.addChild().putLong("pos", pos.asLong());
        }
        tag.putInt("localPower", localPower);
        tag.putInt("currentPower", currentPower);
        tag.putInt("powerFromParentRelays", powerFromParentRelays);
        if (currentParent != null) {
            tag.putLong("currentParent", currentParent.asLong());
        }
    }

    public int getLocalPower() {
        return localPower;
    }

    public void setLocalPower(int newLocalPower) {
        if (level.isClientSide()) {
            return;
        }
        if (newLocalPower != localPower) {
            this.localPower = newLocalPower;
            calculateNewPower();
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.relay.current_power", currentPower));
        if (powering.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_to"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_to", powering.size()));
        }
        if (poweredFrom.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_from"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.powered_from", poweredFrom.size()));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<RedstoneRelayTile>("rotate_controller", 0, this::idlePredicate));
        data.add(new AnimationController<RedstoneRelayTile>("float_controller", 0, this::floatPredicate));

    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private PlayState idlePredicate(AnimationTest<RedstoneRelayTile> event) {
        event.controller().setAnimation(RawAnimation.begin().thenPlay("floating"));
        return PlayState.CONTINUE;
    }

    private PlayState floatPredicate(AnimationTest<RedstoneRelayTile> event) {
        event.controller().setAnimation(RawAnimation.begin().thenPlay("rotating"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
