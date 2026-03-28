package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.renderer.item.FishingRodRenderer;
import com.hollingsworth.arsnouveau.common.entity.EnchantedHook;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.gui.screens.Screen;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.Minecraft;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.server.level.ServerLevel;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.sounds.SoundEvents;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.sounds.SoundSource;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionHand;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionResult;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.Entity;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.LivingEntity;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.player.Player;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.Item;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.ItemStack;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.TooltipFlag;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.component.TooltipDisplay;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.level.Level;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EnchantersFishingRod extends ModItem implements ICasterTool, GeoItem {

    public EnchantersFishingRod() {
        super(ItemsRegistry.newItemProperties().stacksTo(1).component(DataComponentRegistry.SPELL_CASTER, new SpellCaster()));
    }

    // 1.21.11: isEnchantable removed from Item; handled via IItemExtension in NeoForge
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.fishing != null) {
            if (!level.isClientSide()) {
                Entity hookedIn = player.fishing.getHookedIn();
                if (hookedIn != null && player.fishing instanceof EnchantedHook enchantedHook) {
                    enchantedHook.castSpell();
                } else {
                    int i = player.fishing.retrieve(itemstack);
                    ItemStack original = itemstack.copy();
                    // 1.21.11: LivingEntity.getSlotForHand removed; use inline ternary
                    itemstack.hurtAndBreak(i, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    if (itemstack.isEmpty()) {
                        net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(player, original, hand);
                    }

                    level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.FISHING_BOBBER_RETRIEVE,
                            SoundSource.NEUTRAL,
                            1.0F,
                            0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
                    );
                    player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }
            }
        } else {
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (level instanceof ServerLevel serverlevel) {
                int j = (int) (EnchantmentHelper.getFishingTimeReduction(serverlevel, itemstack, player) * 20.0F);
                int k = EnchantmentHelper.getFishingLuckBonus(serverlevel, itemstack, player);
                ItemStack stack = player.getItemInHand(hand);
                AbstractCaster<?> caster = getSpellCaster(stack);
                Spell spell = caster.modifySpellBeforeCasting(serverlevel, player, hand, caster.getSpell());
                if (!spell.isValid()) {
                    PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.fishing_rod.invalid"));
                    return InteractionResult.SUCCESS;
                }
                IWrappedCaster wrappedCaster = new PlayerCaster(player);
                level.addFreshEntity(new EnchantedHook(player, level, k, j, new SpellContext(level, spell, player, wrappedCaster)));
            }
        }

        return InteractionResult.SUCCESS;
    }

    // 1.21.11: getEnchantmentValue removed from Item; not needed
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean isScribedSpellValid(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.mutable().recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.fishing_rod.invalid"));
    }

    @Override
    public void scribeModifiedSpell(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell.Mutable spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodTouch.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
    }

    // 1.21.11: appendHoverText changed to (ItemStack, TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (Minecraft.getInstance().hasShiftDown() || !Config.GLYPH_TOOLTIPS.get())
            getInformation(stack, context, tooltip2, flagIn);
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Minecraft.getInstance().hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            final FishingRodRenderer renderer = new FishingRodRenderer();

            @Override
            public software.bernie.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                return renderer;
            }
        });
    }
}
