package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRedstone;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.*;

import java.util.List;

public class TimerSpellTurretTile extends BasicSpellTurretTile implements IWandable {

    private int ticksPerSignal = 20;
    public boolean isLocked;
    public boolean isOff;
    public int ticksElapsed;

    public TimerSpellTurretTile(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    public TimerSpellTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.TIMER_SPELL_TURRET_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        ticksElapsed++;
        if (!level.isClientSide && ticksPerSignal > 0 && !isOff && ticksElapsed >= ticksPerSignal) {
            getBlockState().tick((ServerLevel) level, getBlockPos(), getLevel().random);
            ticksElapsed = 0;
        }
    }

    @Override
    public int getManaCost() {
        int cost = super.getManaCost();
        Spell spell = this.spellCaster.getSpell();
        cost -= spell.getInstanceCount(MethodTouch.INSTANCE) * MethodTouch.INSTANCE.getCastingCost();
        cost -= spell.getInstanceCount(EffectRedstone.INSTANCE) * EffectRedstone.INSTANCE.getCastingCost();
        cost -= spell.getInstanceCount(MethodProjectile.INSTANCE) * MethodProjectile.INSTANCE.getCastingCost();
        // TODO: Consider either limiting the discount to sensitive on form and redstone, or account for dynamic mana cost of augments
        cost -= spell.getInstanceCount(AugmentSensitive.INSTANCE) * AugmentSensitive.INSTANCE.getCastingCost();
        return Math.max(0, cost);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        super.registerControllers(data);
        data.add(new AnimationController<>(this, "spinController", 0, this::spinPredicate));
    }


    public PlayState spinPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("key_rotation"));
        return PlayState.CONTINUE;
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.isLocked = !isLocked;
        updateBlock();
    }

    public void addTime(int ticks) {
        ticksPerSignal += ticks;
        ticksPerSignal = Math.max(0, ticksPerSignal);
        ticksElapsed = 0;
        updateBlock();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        if (ticksPerSignal <= 0 && !isOff) {
            tooltip.add(Component.translatable("ars_nouveau.tooltip.turned_off"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.seconds", ticksPerSignal / 20));
        }
        if (isOff)
            tooltip.add(Component.translatable("ars_nouveau.tooltip.turned_off"));
        if (isLocked)
            tooltip.add(Component.translatable("ars_nouveau.locked"));
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        this.isLocked = tag.getBoolean("locked");
        this.ticksPerSignal = tag.getInt("time");
        this.isOff = tag.getBoolean("off");
        this.ticksElapsed = tag.getInt("ticksElapsed");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("locked", isLocked);
        tag.putInt("time", ticksPerSignal);
        tag.putBoolean("off", isOff);
        tag.putInt("ticksElapsed", ticksElapsed);
    }
}
