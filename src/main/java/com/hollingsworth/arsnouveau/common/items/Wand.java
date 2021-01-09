package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.renderer.item.WandRenderer;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;

public class Wand extends ModItem  implements IAnimatable, ICaster {
    public AnimationFactory factory = new AnimationFactory(this);
    public Wand() {
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup).setISTER(() -> WandRenderer::new), LibItemNames.WAND);
    }


    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("wand_gem_spin", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data)
    {
       data.addAnimationController(new AnimationController(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(PlayerEntity player) {
        PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.wand.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(new MethodProjectile());
        recipe.add(new AugmentAccelerate());
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICaster.super.setSpell(caster, player, hand, stack, spell);
    }
}
