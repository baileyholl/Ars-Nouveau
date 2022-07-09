package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRedstone;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.List;

public class TimerSpellTurretTile extends BasicSpellTurretTile implements IWandable {

    private int ticksPerSignal = 20;
    public boolean isLocked;
    public boolean isOff;
    public int ticksElapsed;

    public TimerSpellTurretTile(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    public TimerSpellTurretTile(BlockPos pos, BlockState state){
        super(BlockRegistry.TIMER_SPELL_TURRET_TILE, pos, state);
    }

    @Override
    public void tick() {
        ticksElapsed++;
        if(!level.isClientSide && ticksPerSignal > 0 && !isOff && ticksElapsed >= ticksPerSignal){
            getBlockState().tick((ServerLevel) level, getBlockPos(), getLevel().random);
            ticksElapsed = 0;
        }
    }

    @Override
    public int getManaCost() {
        int cost = super.getManaCost();
        Spell spell = this.getSpellCaster().getSpell();
        cost -= spell.getInstanceCount(MethodTouch.INSTANCE) * MethodTouch.INSTANCE.getConfigCost();
        cost -= spell.getInstanceCount(EffectRedstone.INSTANCE) * EffectRedstone.INSTANCE.getConfigCost();
        cost -= spell.getInstanceCount(MethodProjectile.INSTANCE) * MethodProjectile.INSTANCE.getConfigCost();
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
        ticksElapsed = 0;
        update();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        if(ticksPerSignal <= 0 && !isOff){
            tooltip.add(new TranslatableComponent("ars_nouveau.tooltip.turned_off"));
        }else{
            tooltip.add(new TranslatableComponent("ars_nouveau.seconds", ticksPerSignal/20));
        }
        if(isOff)
            tooltip.add(new TranslatableComponent("ars_nouveau.tooltip.turned_off"));
        if(isLocked)
            tooltip.add(new TranslatableComponent("ars_nouveau.locked"));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isLocked = tag.getBoolean("locked");
        this.ticksPerSignal = tag.getInt("time");
        this.isOff = tag.getBoolean("off");
        this.ticksElapsed = tag.getInt("ticksElapsed");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("locked", isLocked);
        tag.putInt("time", ticksPerSignal);
        tag.putBoolean("off", isOff);
        tag.putInt("ticksElapsed", ticksElapsed);
    }
}
