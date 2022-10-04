package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class EnchantedTurretTile extends BasicSpellTurretTile {

    public EnchantedTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ENCHANTED_SPELL_TURRET_TYPE, pos, state);
    }

    @Override
    public int getManaCost() {
        return getSpellCaster().getSpell().getDiscountedCost() / 2;
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
        data.addAnimationController(new AnimationController<>(this, "spinController", 0, this::spinPredicate));
    }

    public PlayState spinPredicate(AnimationEvent event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("gem_rotation", true));
        return PlayState.CONTINUE;
    }
}
