package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.item.SwordRenderer;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.defaultItemProperties;

public class EnchantersSword extends SwordItem implements ICasterTool, IAnimatable {

    public EnchantersSword(Tier iItemTier, int baseDamage, float baseAttackSpeed) {
        super(iItemTier, baseDamage, baseAttackSpeed, defaultItemProperties().stacksTo(1));
    }

    public EnchantersSword(Tier iItemTier, int baseDamage, float baseAttackSpeed, Properties properties) {
        super(iItemTier, baseDamage, baseAttackSpeed, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
        if (world.isClientSide() || world.getGameTime() % 200 != 0 || stack.getDamageValue() == 0 || !(entity instanceof Player))
            return;

        CapabilityRegistry.getMana((LivingEntity) entity).ifPresent(mana -> {
            if (mana.getCurrentMana() > 20) {
                mana.removeMana(20);
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        });
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.sword.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodTouch.INSTANCE);
        recipe.addAll(spell.recipe);
        recipe.add(AugmentAmplify.INSTANCE);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity player) {
        ISpellCaster caster = getSpellCaster(stack);
        SpellResolver resolver = new SpellResolver(new SpellContext(player.level, caster.modifySpellBeforeCasting(target.level, player, InteractionHand.MAIN_HAND, caster.getSpell()), player));
        EntityHitResult entityRes = new EntityHitResult(target);
        resolver.onCastOnEntity(stack, entityRes.getEntity(), InteractionHand.MAIN_HAND);
        return super.hurtEnemy(stack, target, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new SwordRenderer();

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @NotNull
    @Override
    public ISpellCaster getSpellCaster(ItemStack stack) {
        return new BasicReductionCaster(stack, (spell -> {
            spell.addDiscount(AugmentAmplify.INSTANCE.getCastingCost());
            return spell;
        }));
    }
}
