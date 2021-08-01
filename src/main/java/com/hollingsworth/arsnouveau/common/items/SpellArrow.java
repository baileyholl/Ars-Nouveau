package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class SpellArrow extends ArrowItem {

    public AbstractSpellPart part;
    public int numParts;

    public SpellArrow(String registryName, AbstractAugment augment, int numParts) {
        super(ItemsRegistry.defaultItemProperties());
        setRegistryName(ArsNouveau.MODID, registryName);
        this.part = augment;
        this.numParts = numParts;
    }

    public void modifySpell(Spell spell){
        for(int i = 0; i < numParts; i++){
            spell.recipe.add(part);
        }
    }

    @Override
    public AbstractArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        IMana mana = ManaCapability.getMana(shooter).orElse(null);
        if(mana == null)
            return new ArrowEntity(world, shooter);
        EntitySpellArrow spellArrow = new EntitySpellArrow(world, shooter);
        if(!(shooter instanceof PlayerEntity) || !(( shooter).getMainHandItem().getItem() instanceof ICasterTool))
            return super.createArrow(world, stack, shooter);
        PlayerEntity entity = (PlayerEntity)shooter;
        ICasterTool caster = (ICasterTool) entity.getMainHandItem().getItem();
        ISpellCaster spellCaster = caster.getSpellCaster(entity.getMainHandItem());
        Spell spell = spellCaster.getSpell();
        modifySpell(spell);
        spell.setCost(spell.getCastingCost() - part.getManaCost() * numParts);
        spellArrow.spellResolver = new SpellResolver(new SpellContext(spell, entity)).withSilent(true);
        spellArrow.pierceLeft = spell.getBuffsAtIndex(0, shooter, AugmentPierce.class);
        return spellArrow;
    }



    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("ars_nouveau.spell_arrow.desc"));
        Spell spell = new Spell();
        for(int i = 0; i < numParts; i++){
            spell.recipe.add(part);
        }
        tooltip.add(new StringTextComponent(spell.getDisplayString()));
    }
}
