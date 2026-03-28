package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animatable.manager.AnimatableManager;


public class EnchantedTurretTile extends BasicSpellTurretTile {

    public EnchantedTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE.get(), pos, state);
    }

    @Override
    public int getManaCost() {
        return this.spellCaster.getSpell().getCost() / 2;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        super.registerControllers(data);
        data.add(new AnimationController<EnchantedTurretTile>("spinController", 0, this::spinPredicate));
    }

    public PlayState spinPredicate(AnimationTest<EnchantedTurretTile> event) {
        event.controller().setAnimation(RawAnimation.begin().thenPlay("gem_rotation"));
        return PlayState.CONTINUE;
    }
}
