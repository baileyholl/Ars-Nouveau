package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRedstone;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.List;

public class TimerSpellTurretTile extends BasicSpellTurretTile implements TickableBlockEntity, IWandable {

    private int ticksPerSignal = 20;
    public boolean isLocked;
    public boolean isOff;
    public TimerSpellTurretTile(BlockEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    public TimerSpellTurretTile(){
        super(BlockRegistry.TIMER_SPELL_TURRET_TILE);
    }

    @Override
    public void tick() {
        if(!level.isClientSide && ticksPerSignal > 0 && !isOff & level.getGameTime() % ticksPerSignal == 0){
            getBlockState().tick((ServerLevel) level, getBlockPos(), getLevel().random);

        }
    }

    @Override
    public int getManaCost() {
        int cost = super.getManaCost();
        cost -= this.spell.getInstanceCount(MethodTouch.INSTANCE) * MethodTouch.INSTANCE.getConfigCost();
        cost -= this.spell.getInstanceCount(EffectRedstone.INSTANCE) * EffectRedstone.INSTANCE.getConfigCost();
        cost -= this.spell.getInstanceCount(MethodProjectile.INSTANCE) * MethodProjectile.INSTANCE.getConfigCost();
        return Math.max(0, cost);
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
        data.addAnimationController(new AnimationController<>(this, "spinController", 0, this::spinPredicate));
    }


    public PlayState spinPredicate(AnimationEvent event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("key_rotation", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.isLocked = !isLocked;
        update();
    }

    public void addTime(int ticks){
        ticksPerSignal += ticks;
        ticksPerSignal = Math.max(0, ticksPerSignal);
        update();
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = super.getTooltip();
        if(ticksPerSignal <= 0 && !isOff){
            tooltip.add(new TranslatableComponent("ars_nouveau.tooltip.turned_off").getString());
        }else{
            tooltip.add(new TranslatableComponent("ars_nouveau.seconds", ticksPerSignal/20).getString());
        }
        if(isOff)
            tooltip.add(new TranslatableComponent("ars_nouveau.tooltip.turned_off").getString());
        if(isLocked)
            tooltip.add(new TranslatableComponent("ars_nouveau.locked").getString());
        return tooltip;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.isLocked = tag.getBoolean("locked");
        this.ticksPerSignal = tag.getInt("time");
        this.isOff = tag.getBoolean("off");
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("locked", isLocked);
        tag.putInt("time", ticksPerSignal);
        tag.putBoolean("off", isOff);
        return super.save(tag);
    }
}
