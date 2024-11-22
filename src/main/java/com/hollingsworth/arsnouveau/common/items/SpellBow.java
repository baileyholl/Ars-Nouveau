package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBowRenderer;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SpellBow extends BowItem implements GeoItem, ICasterTool, IManaDiscountEquipment {

    public SpellBow(Properties properties) {
        super(properties);
    }

    public SpellBow() {
        this(ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.SPELL_CASTER, new SpellCaster()));
    }

    public boolean canPlayerCastSpell(ItemStack bow, Player playerentity) {
        AbstractCaster<?> caster = getSpellCaster(bow);
        return new SpellResolver(new SpellContext(playerentity.level, caster.getSpell(), playerentity, new PlayerCaster(playerentity), bow)).withSilent(true).canCast(playerentity);
    }

    public ItemStack findAmmo(Player playerEntity, ItemStack shootable) {
        if (!(shootable.getItem() instanceof ProjectileWeaponItem projectileWeaponItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = projectileWeaponItem.getSupportedHeldProjectiles()
                    .and(i -> !(i.getItem() instanceof SpellArrow) || (i.getItem() instanceof SpellArrow && canPlayerCastSpell(shootable, playerEntity)));
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(playerEntity, predicate);
            if (!itemstack.isEmpty()) {
                return CommonHooks.getProjectile(playerEntity, shootable, itemstack);
            } else {
                predicate = projectileWeaponItem.getAllSupportedProjectiles().and(i ->
                        !(i.getItem() instanceof SpellArrow) ||
                                (i.getItem() instanceof SpellArrow &&
                                        canPlayerCastSpell(shootable, playerEntity)));

                for (int i = 0; i < playerEntity.getInventory().getContainerSize(); ++i) {
                    ItemStack itemstack1 = playerEntity.inventory.getItem(i);
                    if (predicate.test(itemstack1)) {
                        return CommonHooks.getProjectile(playerEntity, shootable, itemstack1);
                    }
                }

                return CommonHooks.getProjectile(playerEntity, shootable, playerEntity.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        AbstractCaster<?> caster = getSpellCaster(playerIn.getItemInHand(handIn));
        boolean hasAmmo = !findAmmo(playerIn, itemstack).isEmpty();

        InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, worldIn, playerIn, handIn, hasAmmo);
        if (ret != null) return ret;

        if (hasAmmo || (caster.getSpell().isValid() && new SpellResolver(new SpellContext(worldIn, caster.getSpell(), playerIn, new PlayerCaster(playerIn), itemstack)).withSilent(true).canCast(playerIn))) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }

        if (!playerIn.abilities.instabuild && !hasAmmo) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public EntitySpellArrow buildSpellArrow(Level worldIn, Player playerentity, AbstractCaster<?> caster, boolean isSpellArrow, ItemStack bowStack, ItemStack arrowStack) {
        EntitySpellArrow spellArrow = new EntitySpellArrow(worldIn, playerentity, arrowStack, bowStack);
        spellArrow.spellResolver = new SpellResolver(new SpellContext(worldIn, caster.getSpell(), playerentity, new PlayerCaster(playerentity), bowStack)).withSilent(true);
        spellArrow.setColors(caster.getColor());
        if (isSpellArrow)
            spellArrow.setBaseDamage(0);
        return spellArrow;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack bowStack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving, int timeLeft) {
        //Copied from BowItem, so we can spawn arrows in case there are no items.
        if (!(entityLiving instanceof Player playerentity))
            return;
        boolean isInfinity = playerentity.abilities.instabuild || bowStack.getEnchantmentLevel(HolderHelper.unwrap(worldIn, Enchantments.INFINITY)) > 0;
        ItemStack arrowStack = findAmmo(playerentity, bowStack);

        int useTime = this.getUseDuration(bowStack, entityLiving) - timeLeft;
        useTime = net.neoforged.neoforge.event.EventHooks.onArrowLoose(bowStack, worldIn, playerentity, useTime, !arrowStack.isEmpty() || isInfinity);
        if (useTime < 0) return;
        boolean canFire = false;
        if (!arrowStack.isEmpty() || isInfinity) {
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(Items.ARROW);
            }
            canFire = true;
        }
        AbstractCaster<?> caster = getSpellCaster(bowStack);
        boolean isSpellArrow = false;
        if (arrowStack.isEmpty() && caster.getSpell().isValid() && new SpellResolver(new SpellContext(worldIn, caster.getSpell(), playerentity, new PlayerCaster(playerentity), bowStack)).canCast(playerentity)) {
            canFire = true;
            isSpellArrow = true;
        }

        if (!canFire)
            return;

        float f = getPowerForTime(useTime);
        boolean didCastSpell = false;
        if (f >= 0.1D) {

            boolean isArrowInfinite = playerentity.abilities.instabuild || (arrowStack.getItem() instanceof ArrowItem arrowItem && arrowItem.isInfinite(arrowStack, bowStack, playerentity));
            if (!worldIn.isClientSide) {
                int use = EnchantmentHelper.processAmmoUse((ServerLevel) worldIn, bowStack, arrowStack, 1);
                ArrowItem arrowitem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                AbstractArrow abstractarrowentity = arrowitem.createArrow(worldIn, arrowStack, playerentity, bowStack);
                abstractarrowentity = customArrow(abstractarrowentity, arrowStack, bowStack);

                List<AbstractArrow> arrows = new ArrayList<>();
                SpellResolver resolver = new SpellResolver(new SpellContext(worldIn, caster.modifySpellBeforeCasting((ServerLevel) worldIn, entityLiving, InteractionHand.MAIN_HAND, caster.getSpell()), playerentity, new PlayerCaster(playerentity), bowStack));
                if (arrowitem instanceof SpellArrow) {
                    if (!(resolver.canCast(playerentity))) {
                        return;
                    } else if (resolver.canCast(playerentity)) {
                        resolver.expendMana();
                        didCastSpell = true;
                    }
                }else if (resolver.withSilent(true).canCast(playerentity)) {
                    abstractarrowentity = buildSpellArrow(worldIn, playerentity, caster, isSpellArrow, bowStack, arrowStack);
                    resolver.expendMana();
                    didCastSpell = true;
                }
                arrows.add(abstractarrowentity);
                if (caster.getSpell().isValid() && didCastSpell) {
                    int numSplits = caster.getSpell().getBuffsAtIndex(0, playerentity, AugmentSplit.INSTANCE);
                    if (abstractarrowentity instanceof EntitySpellArrow arrow) {
                        numSplits = arrow.spellResolver.spell.getBuffsAtIndex(0, playerentity, AugmentSplit.INSTANCE);
                    }

                    for (int i = 0; i < numSplits; i++) {
                        EntitySpellArrow spellArrow = buildSpellArrow(worldIn, playerentity, caster, isSpellArrow, bowStack, arrowStack);
                        arrows.add(spellArrow);
                    }
                }
                int opposite = -1;
                int counter = 0;
                for (AbstractArrow arr : arrows) {
                    arr.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot() + Math.round(counter / 2.0) * 10 * opposite, 0.0F, f * 3.0F, 1.0F);
                    opposite = opposite * -1;
                    counter++;
                    if (f >= 1.0F) {
                        arr.setCritArrow(true);
                    }
                    if(isArrowInfinite && use == 0){
                        arrowStack = arrowStack.copyWithCount(1);
                        arrowStack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
                    }
                    addArrow(arr, bowStack, arrowStack, isArrowInfinite, playerentity);
                }
                if (!isArrowInfinite && !playerentity.abilities.instabuild) {
                    arrowStack.shrink(use);
                }
            }

            worldIn.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if (didCastSpell)
                caster.playSound(playerentity.getOnPos(), playerentity.level, playerentity, caster.getCurrentSound(), SoundSource.PLAYERS);
        }
    }

    public void addArrow(AbstractArrow abstractarrowentity, ItemStack bowStack, ItemStack arrowStack, boolean isArrowInfinite, Player playerentity) {
        // TODO: verify if enchant checking is needed here
        int power = bowStack.getEnchantmentLevel(HolderHelper.unwrap(playerentity.level, Enchantments.POWER));
        if (power > 0) {
            abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + power * 0.5D + 0.5D);
        }

        if (bowStack.getEnchantmentLevel(HolderHelper.unwrap(playerentity.level, Enchantments.FLAME)) > 0) {
            abstractarrowentity.setRemainingFireTicks(100);
        }

        if (isArrowInfinite || playerentity.abilities.instabuild && (arrowStack.getItem() == Items.SPECTRAL_ARROW || arrowStack.getItem() == Items.TIPPED_ARROW)) {
            abstractarrowentity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        playerentity.level.addFreshEntity(abstractarrowentity);
    }

    /**
     * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
     */
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return super.getAllSupportedProjectiles().or(i -> i.getItem() instanceof SpellArrow);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (Screen.hasShiftDown() || !Config.GLYPH_TOOLTIPS.get())
            getInformation(stack, context, tooltip2, flagIn);
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }

    @Override
    public boolean isScribedSpellValid(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.unsafeList().stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.bow.invalid"));
    }

    @Override
    public void scribeModifiedSpell(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell.Mutable spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodProjectile.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        return true;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new SpellBowRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return MethodProjectile.INSTANCE.getCastingCost();
    }
}
