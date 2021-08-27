package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.item.MirrorRenderer;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantersMirror extends ModItem implements ICasterTool, IAnimatable, ISpellModifierItem {
    public EnchantersMirror(Properties properties) {
        super(properties);
    }

    public EnchantersMirror(Properties properties, String registryName) {
        super(properties.setISTER(() -> MirrorRenderer::new), registryName);
    }

    public EnchantersMirror(String registryName) {
        super(registryName);
    }


    @Override
    public void registerControllers(AnimationData data) {

    }
    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = getSpellCaster(stack);
        caster.getSpell().setCost((int) (caster.getSpell().getCastingCost() - caster.getSpell().getCastingCost() * 0.25));
        return caster.castSpell(worldIn, playerIn, handIn, new TranslationTextComponent("ars_nouveau.mirror.invalid"));
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(PlayerEntity player) {
        PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_nouveau.mirror.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodSelf.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        getInformation(stack, worldIn, tooltip2, flagIn);
        new SpellStats.Builder().addDurationModifier(1.0).build().addTooltip(tooltip2);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        return builder.addDurationModifier(1.0D);
    }
}
