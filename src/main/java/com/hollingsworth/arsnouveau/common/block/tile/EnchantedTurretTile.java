package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;


public class EnchantedTurretTile extends BasicSpellTurretTile {

    public EnchantedTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE, pos, state);
    }

    @Override
    public int getManaCost() {
        return getSpellCaster().getSpell().getCost() / 2;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        super.registerControllers(data);
        data.add(new AnimationController<>(this, "spinController", 0, this::spinPredicate));
    }

    public PlayState spinPredicate(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("gem_rotation"));
        return PlayState.CONTINUE;
    }
}
