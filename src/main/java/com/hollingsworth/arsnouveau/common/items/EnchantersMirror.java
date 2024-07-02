package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnchantersMirror extends ModItem implements ICasterTool, GeoItem, ISpellModifierItem, IManaDiscountEquipment {

    public EnchantersMirror(Properties properties) {
        super(properties);
    }

    public EnchantersMirror() {
        super();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = getSpellCaster(stack);
        return caster.castSpell(worldIn, playerIn, handIn, Component.translatable("ars_nouveau.mirror.invalid"), caster.getSpell());
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return (int) (spell.getCost() * .25);
    }

    @Override
    public boolean isScribedSpellValid(SpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.unsafeList().stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.mirror.invalid"));
    }

    @Override
    public void scribeModifiedSpell(SpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell.Mutable spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodSelf.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        getInformation(stack, context, tooltip2, flagIn);
        new SpellStats.Builder().addDurationModifier(1.0).build().addTooltip(tooltip2);
        super.appendHoverText(stack, context, tooltip2, flagIn);
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
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
