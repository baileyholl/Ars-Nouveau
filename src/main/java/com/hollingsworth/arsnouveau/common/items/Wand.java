package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.renderer.item.WandRenderer;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Wand extends ModItem implements GeoItem, ICasterTool {
    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public Wand(Properties properties) {
        super(properties);
    }

    public Wand() {
        super(new Item.Properties().stacksTo(1)
                .component(DataComponents.BASE_COLOR, DyeColor.PURPLE)
                .component(DataComponentRegistry.SPELL_CASTER, new SpellCaster()));
    }

    private <P extends Item & GeoAnimatable> PlayState predicate(AnimationState<P> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("wand_gem_spin"));
        return PlayState.CONTINUE;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        var caster = this.getSpellCaster(stack);
        if (caster == null) {
            return InteractionResultHolder.pass(stack);
        }
        caster.castOnServer(handIn, Component.translatable("ars_nouveau.wand.invalid"));

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public boolean isScribedSpellValid(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.mutable().recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.wand.invalid"));
    }

    @Override
    public void scribeModifiedSpell(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell.Mutable spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodProjectile.INSTANCE);
        recipe.add(AugmentAccelerate.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (Screen.hasShiftDown() || !Config.GLYPH_TOOLTIPS.get())
            getInformation(stack, context, tooltip2, flagIn);
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            final WandRenderer renderer = new WandRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }


    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }
}
