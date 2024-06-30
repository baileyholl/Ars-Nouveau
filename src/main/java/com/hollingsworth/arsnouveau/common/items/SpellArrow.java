package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellArrow extends ArrowItem {

    public AbstractSpellPart part;
    public int numParts;

    public SpellArrow(AbstractAugment augment, int numParts) {
        super(ItemsRegistry.defaultItemProperties());
        this.part = augment;
        this.numParts = numParts;
    }

    public void modifySpell(Spell spell) {
        for (int i = 0; i < numParts; i++) {
            spell.recipe.add(part);
        }
    }

    @Override
    public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack bowStack) {
        IManaCap mana = CapabilityRegistry.getMana(shooter).orElse(null);
        if (mana == null)
            return new Arrow(world, shooter, new ItemStack(Items.ARROW), bowStack);
        EntitySpellArrow spellArrow = new EntitySpellArrow(world, shooter, ItemStack.EMPTY, bowStack);
        if (!(shooter instanceof Player entity) || !((shooter).getMainHandItem().getItem() instanceof ICasterTool caster))
            return super.createArrow(world, stack, shooter, bowStack);
        ISpellCaster spellCaster = caster.getSpellCaster(entity.getMainHandItem());
        Spell spell = spellCaster.getSpell();
        modifySpell(spell);
        spellArrow.spellResolver = new SpellResolver(new SpellContext(world, spell, entity, new PlayerCaster(entity), shooter.getMainHandItem())).withSilent(true);
        spellArrow.pierceLeft = spell.getBuffsAtIndex(0, shooter, AugmentPierce.INSTANCE);
        return spellArrow;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("ars_nouveau.spell_arrow.desc"));
        Spell spell = new Spell();
        for (int i = 0; i < numParts; i++) {
            spell.recipe.add(part);
        }
        pTooltipComponents.add(Component.literal(spell.getDisplayString()));
    }
}
