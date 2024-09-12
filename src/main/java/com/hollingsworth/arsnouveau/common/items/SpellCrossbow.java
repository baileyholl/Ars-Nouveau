package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellCrossbowRenderer;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

public class SpellCrossbow extends CrossbowItem implements GeoItem, ICasterTool, IManaDiscountEquipment {
    private static final CrossbowItem.ChargingSounds DEFAULT_SOUNDS = new CrossbowItem.ChargingSounds(
            Optional.of(SoundEvents.CROSSBOW_LOADING_START), Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
    );
    public SpellCrossbow(Properties pProperties) {
        super(pProperties);
    }

    // Duplicate override except we use our own tryLoadProjectiles
    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving, int pTimeLeft) {
        int i = this.getUseDuration(pStack, pEntityLiving) - pTimeLeft;
        float f = getPowerForTime(i, pStack, pEntityLiving);
        if (f >= 1.0F && !isCharged(pStack) && tryLoadProjectiles(pEntityLiving, pStack)) {
            CrossbowItem.ChargingSounds crossbowitem$chargingsounds =  EnchantmentHelper.pickHighestLevel(pStack, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
            crossbowitem$chargingsounds.end()
                    .ifPresent(
                            p_352852_ -> pLevel.playSound(
                                    null,
                                    pEntityLiving.getX(),
                                    pEntityLiving.getY(),
                                    pEntityLiving.getZ(),
                                    p_352852_.value(),
                                    pEntityLiving.getSoundSource(),
                                    1.0F,
                                    1.0F / (pLevel.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
                            )
                    );
        }
    }

    private boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack pCrossbowStack) {
        int multishotLevel = EnchantmentHelper.getTagEnchantmentLevel(pShooter.level.holderOrThrow(Enchantments.MULTISHOT), pCrossbowStack);
        int numProjectiles = multishotLevel == 0 ? 1 : 3;
        boolean isCreative = pShooter instanceof Player && ((Player)pShooter).getAbilities().instabuild;
        ItemStack ammoStack = pShooter.getProjectile(pCrossbowStack);
        ItemStack ammoCopy = ammoStack.copy();

        AbstractCaster<?> caster = getSpellCaster(pCrossbowStack);
        SpellResolver resolver = new SpellResolver(new SpellContext(pShooter.level, caster.modifySpellBeforeCasting(pShooter.level, pShooter, InteractionHand.MAIN_HAND, caster.getSpell()), pShooter, LivingCaster.from(pShooter), pCrossbowStack));
        boolean consumedMana = false;

        if(!(pShooter instanceof Player) || resolver.withSilent(true).canCast(pShooter)){
            resolver.expendMana();
            consumedMana = true;
            numProjectiles += resolver.spell.getBuffsAtIndex(0, pShooter, AugmentSplit.INSTANCE);
        }
        if(ammoStack.getItem() instanceof FormSpellArrow formSpellArrow && formSpellArrow.part == AugmentSplit.INSTANCE){
            numProjectiles += formSpellArrow.numParts;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for(int k = 0; k < numProjectiles; ++k) {
            if (k > 0) {
                ammoStack = ammoCopy.copy();
            }

            if (ammoStack.isEmpty() && isCreative) {
                ammoStack = new ItemStack(Items.ARROW);
                ammoCopy = ammoStack.copy();
            }
            ItemStack checkedAmmo = useAmmo(pCrossbowStack, ammoStack, pShooter, k > 0);
            if(!checkedAmmo.isEmpty()) {
                stacks.add(checkedAmmo);
            }
        }

        if(!stacks.isEmpty()){
            pCrossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(stacks));
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("isSpell", true);
            pCrossbowStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        return true;
    }

    public void shootOne(Level worldIn, LivingEntity pShooter, InteractionHand pHand, ItemStack pCrossbowStack, ItemStack pAmmoStack, float pSoundPitch, boolean pIsCreativeMode, float pVelocity, float pInaccuracy, float pProjectileAngle, boolean isSpell) {
        if (!worldIn.isClientSide) {
            boolean flag = pAmmoStack.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (flag) {
                projectile = new FireworkRocketEntity(worldIn, pAmmoStack, pShooter, pShooter.getX(), pShooter.getEyeY() - (double)0.15F, pShooter.getZ(), true);
            } else {
                projectile = getArrow(worldIn, pShooter, pCrossbowStack, pAmmoStack);
                if (pIsCreativeMode || pProjectileAngle != 0.0F) {
                    ((AbstractArrow)projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
            LivingCaster livingCaster = pShooter instanceof Player ? new PlayerCaster((Player)pShooter) : new LivingCaster(pShooter);
            AbstractCaster<?> caster = getSpellCaster(pCrossbowStack);
            SpellResolver resolver = new SpellResolver(new SpellContext(worldIn, caster.modifySpellBeforeCasting(worldIn, pShooter, InteractionHand.MAIN_HAND, caster.getSpell()), pShooter, livingCaster, pCrossbowStack));
            if (isSpell) {
                projectile = buildSpellArrow(worldIn, pShooter, caster, pCrossbowStack, pAmmoStack);
                ((EntitySpellArrow) projectile).pierceLeft += EnchantmentHelper.getTagEnchantmentLevel(worldIn.holderOrThrow(Enchantments.PIERCING), pCrossbowStack);
            }else if(pAmmoStack.getItem() instanceof SpellArrow && projectile instanceof EntitySpellArrow spellArrow){
                spellArrow.pierceLeft += EnchantmentHelper.getTagEnchantmentLevel(worldIn.holderOrThrow(Enchantments.PIERCING), pCrossbowStack);
                spellArrow.setColors(resolver.spell.color());
            }

            Vec3 vec31 = pShooter.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(pProjectileAngle * ((float)Math.PI / 180F), vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = pShooter.getViewVector(1.0F);
            Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
            projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), pVelocity, pInaccuracy);


            worldIn.addFreshEntity(projectile);
            worldIn.playSound(null, pShooter.getX(), pShooter.getY(), pShooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, pSoundPitch);
        }
    }

    private AbstractArrow getArrow(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack) {
        ArrowItem arrowitem = (ArrowItem)(pAmmoStack.getItem() instanceof ArrowItem ? pAmmoStack.getItem() : Items.ARROW);
        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, pAmmoStack, pLivingEntity, pCrossbowStack);
        if (pLivingEntity instanceof Player) {
            abstractarrow.setCritArrow(true);
        }
        abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        return abstractarrow;
    }

    @Override
    public void performShooting(@NotNull Level pLevel, @NotNull LivingEntity pShooter, @NotNull InteractionHand pUsedHand, @NotNull ItemStack pCrossbowStack, float pVelocity, float pInaccuracy, @Nullable LivingEntity pTarget) {
        if (pShooter instanceof Player player && net.neoforged.neoforge.event.EventHooks.onArrowLoose(pCrossbowStack, pShooter.level, player, 1, true) < 0) return;
        ChargedProjectiles chargedprojectiles = pCrossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if(chargedprojectiles == null)
            return;
        var customData = pCrossbowStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData != null ? customData.getUnsafe() : new CompoundTag();
        boolean isSpell = tag.getBoolean("isSpell");
        for(int i = 0; i < chargedprojectiles.getItems().size(); ++i) {
            ItemStack itemstack = chargedprojectiles.getItems().get(i);
            boolean flag = pShooter instanceof Player && ((Player)pShooter).getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                float offset = 10.0f * (float)((i > 0 ? 1 + i : 0) / 2);
                boolean isOdd = i % 2 == 1;
                if (isOdd) {
                    offset *= -1;
                }
                shootOne(pLevel, pShooter, pUsedHand, pCrossbowStack, itemstack, i == 0 ? 1 : getRandomShotPitch(isOdd == pShooter.getRandom().nextBoolean(), pShooter.getRandom()), flag, pVelocity, pInaccuracy, offset, isSpell);
            }
        }

        if (pShooter instanceof ServerPlayer serverplayer) {
            if (!pLevel.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, pCrossbowStack);
            }

            serverplayer.awardStat(Stats.ITEM_USED.get(pCrossbowStack.getItem()));
        }
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return super.getAllSupportedProjectiles().or(i -> i.getItem() instanceof SpellArrow);
    }

    public EntitySpellArrow buildSpellArrow(Level worldIn, LivingEntity playerentity, AbstractCaster<?> caster, ItemStack bowStack, ItemStack arrowStack) {
        EntitySpellArrow spellArrow = new EntitySpellArrow(worldIn, playerentity, arrowStack, bowStack);
        spellArrow.spellResolver = new SpellResolver(new SpellContext(worldIn, caster.getSpell(), playerentity, LivingCaster.from(playerentity), playerentity.getMainHandItem())).withSilent(true);
        spellArrow.setColors(caster.getColor());
        return spellArrow;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        getInformation(stack, context, tooltip2, flagIn);
        super.appendHoverText(stack, context, tooltip2, flagIn);
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
    public int getManaDiscount(ItemStack i, Spell spell) {
        return MethodProjectile.INSTANCE.getCastingCost();
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new SpellCrossbowRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
