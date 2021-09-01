package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class SpellTurretTile extends BasicSpellTurretTile  implements IPickupResponder, IPlaceBlockResponder, ITooltipProvider {

    public SpellTurretTile() {
        super(BlockRegistry.SPELL_TURRET_TYPE);
    }

    @Override
    public int getManaCost() {
        return spell.getCastingCost() / 2;
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
