package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.ISpellCasterProvider;
import com.hollingsworth.arsnouveau.api.spell.TurretSpellCaster;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.registry.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BasicSpellTurretTile extends ModdedTile implements ITooltipProvider, GeoBlockEntity, IAnimationListener, ITickable, ISpellCasterProvider {

    boolean playRecoil;
    public TurretSpellCaster spellCaster = new TurretSpellCaster(new CompoundTag());

    public BasicSpellTurretTile(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }


    public BasicSpellTurretTile(RegistryWrapper<? extends BlockEntityType<?>> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_.get(), pos, state);
    }

    public BasicSpellTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BASIC_SPELL_TURRET_TILE, pos, state);
    }

    public int getManaCost() {
        return this.spellCaster.getSpell().getCost();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        spellCaster.serializeOnTag(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.spellCaster = new TurretSpellCaster(tag);
        super.load(tag);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.spell_turret.casting"));
        if (!spellCaster.getSpellName().isEmpty()) tooltip.add(Component.literal(spellCaster.getSpellName()));
        tooltip.add(Component.literal(spellCaster.getSpell().getDisplayString()));
    }

    public PlayState walkPredicate(AnimationState<?> event) {
        if (playRecoil) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay("recoil"));
            playRecoil = false;
        }
        return PlayState.CONTINUE;
    }

    AnimationController castController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        castController = new AnimationController<>(this, "castController", 0, this::walkPredicate);
        data.add(castController);
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        this.playRecoil = true;
    }

    @Override
    public ISpellCaster getSpellCaster() {
        return spellCaster;
    }

    @Override
    public ISpellCaster getSpellCaster(CompoundTag tag) {
        return spellCaster;
    }
}
