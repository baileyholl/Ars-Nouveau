package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.item.MirrorRenderer;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

public class EnchantersMirror extends ModItem implements ICasterTool, IAnimatable, ISpellModifierItem {

    public EnchantersMirror(Properties properties) {
        super(properties);
    }

    public EnchantersMirror() {
        super();
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = getSpellCaster(stack);
        caster.getSpell().addDiscount((int) (caster.getSpell().getDiscountedCost() * 0.25));
        return caster.castSpell(worldIn, playerIn, handIn, Component.translatable("ars_nouveau.mirror.invalid"));
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.mirror.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodSelf.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        getInformation(stack, worldIn, tooltip2, flagIn);
        new SpellStats.Builder().addDurationModifier(1.0).build().addTooltip(tooltip2);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDurationModifier(1.0D);
        return builder;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new MirrorRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}
