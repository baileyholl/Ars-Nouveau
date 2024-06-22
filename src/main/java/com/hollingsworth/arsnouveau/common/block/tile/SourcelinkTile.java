package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.registry.RegistryWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SourcelinkTile extends AbstractSourceMachine implements GeoBlockEntity, ITickable {

    int progress;
    public boolean isDisabled = false;
    public boolean registered = false;

    public SourcelinkTile(BlockEntityType<?> sourceLinkTile, BlockPos pos, BlockState state) {
        super(sourceLinkTile, pos, state);
    }

    public SourcelinkTile(RegistryWrapper<? extends BlockEntityType<?>> sourceLinkTile, BlockPos pos, BlockState state) {
        super(sourceLinkTile.get(), pos, state);
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
    public void tick() {
        if (level.isClientSide)
            return;
        if (level.getGameTime() % 120 == 0 && usesEventQueue()) {
            SourcelinkEventQueue.addPosition(level, this.worldPosition);
            registered = true;
        }

        if (level.getGameTime() % 100 == 0 && getSource() > 0) {
            List<ISpecialSourceProvider> providers = SourceUtil.canGiveSource(worldPosition, level, 5);
            if(!providers.isEmpty()){
                transferSource(this, providers.get(0).getSource());
                ParticleUtil.spawnFollowProjectile(level, this.worldPosition, providers.get(0).getCurrentPos(), this.getColor());
            }
        }
    }

    public List<ArcanePedestalTile> getSurroundingPedestals() {
        List<ArcanePedestalTile> inventories = new ArrayList<>();
        for (BlockPos p : BlockPos.betweenClosed(getBlockPos().below().east().north(), getBlockPos().above().west().south())) {
            if (level.getBlockEntity(p) instanceof ArcanePedestalTile pedestal) {
                inventories.add(pedestal);
            }
        }
        return inventories;
    }

    public void getManaEvent(BlockPos sourcePos, int total) {
        this.addSource(total);
        ParticleUtil.spawnFollowProjectile(level, sourcePos, this.worldPosition, this.getColor());
    }

    public boolean eventInRange(BlockPos sourcePos, @Nullable Event event) {
        return BlockUtil.distanceFrom(this.worldPosition, sourcePos) <= 15;
    }

    public boolean usesEventQueue() {
        return false;
    }

    public void doRandomAction() {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "rotate_controller", 0, this::idlePredicate));
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        isDisabled = tag.getBoolean("disabled");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
        tag.putBoolean("disabled", isDisabled);
    }

    private <E extends BlockEntity & GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        if (this.isDisabled)
            return PlayState.STOP;
        event.getController().setAnimation(RawAnimation.begin().thenPlay("rotation"));
        return PlayState.CONTINUE;
    }
}
