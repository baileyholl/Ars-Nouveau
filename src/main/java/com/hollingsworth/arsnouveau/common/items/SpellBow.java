package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBowRenderer;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpellBow extends BowItem implements IAnimatable, ICasterTool {
    public SpellBow() {
        super(ItemsRegistry.defaultItemProperties().maxStackSize(1).setISTER(() -> SpellBowRenderer::new));
    }

    public boolean canPlayerCastSpell(ItemStack bow, PlayerEntity playerentity){
        ISpellCaster caster = getSpellCaster(bow);
        return new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).withSilent(true).canCast(playerentity);
    }

    public ItemStack findAmmo(PlayerEntity playerEntity, ItemStack shootable) {
        if (!(shootable.getItem() instanceof ShootableItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((ShootableItem)shootable.getItem()).getAmmoPredicate()
                    .and(i -> !(i.getItem() instanceof SpellArrow) || (i.getItem() instanceof SpellArrow && canPlayerCastSpell(shootable, playerEntity)));
            ItemStack itemstack = ShootableItem.getHeldAmmo(playerEntity, predicate);
            if (!itemstack.isEmpty()) {
                return itemstack;
            } else {
                predicate = ((ShootableItem)shootable.getItem()).getInventoryAmmoPredicate().and(i -> !(i.getItem() instanceof SpellArrow) || (i.getItem() instanceof SpellArrow && canPlayerCastSpell(shootable, playerEntity)));

                for(int i = 0; i < playerEntity.inventory.getSizeInventory(); ++i) {
                    ItemStack itemstack1 = playerEntity.inventory.getStackInSlot(i);
                    if (predicate.test(itemstack1)) {
                        return itemstack1;
                    }
                }

                return playerEntity.abilities.isCreativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
            }
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ISpellCaster caster = getSpellCaster(playerIn.getHeldItem(handIn));
        boolean hasAmmo = !findAmmo(playerIn, itemstack).isEmpty();

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, hasAmmo);
        if (ret != null) return ret;

        if(hasAmmo || (caster.getSpell() != null && new SpellResolver(new SpellContext(caster.getSpell(), playerIn)).withSilent(true).canCast(playerIn))){
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }

        if (!playerIn.abilities.isCreativeMode && !hasAmmo) {
            return ActionResult.resultFail(itemstack);
        } else {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }
    }

    public EntitySpellArrow buildSpellArrow(World worldIn, PlayerEntity playerentity, ISpellCaster caster, boolean isSpellArrow){
        EntitySpellArrow spellArrow = new EntitySpellArrow(worldIn, playerentity);
        spellArrow.spellResolver = new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).withSilent(true);
        if(isSpellArrow)
            spellArrow.setDamage(0);
        return spellArrow;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack bowStack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        //Copied from BowItem so we can spawn arrows in case there are no items.
        if (!(entityLiving instanceof PlayerEntity))
            return;
        PlayerEntity playerentity = (PlayerEntity)entityLiving;
        boolean isInfinity = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bowStack) > 0;
        ItemStack arrowStack = findAmmo(playerentity, bowStack);

        int useTime = this.getUseDuration(bowStack) - timeLeft;
        useTime = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(bowStack, worldIn, playerentity, useTime, !arrowStack.isEmpty() || isInfinity);
        if (useTime < 0) return;
        boolean canFire = false;
        if (!arrowStack.isEmpty() || isInfinity) {
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(Items.ARROW);
            }
            canFire = true;
        }
        ISpellCaster caster = getSpellCaster(bowStack);
        boolean isSpellArrow = false;
        if(arrowStack.isEmpty() && caster.getSpell() != null && new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).canCast(playerentity)){
            canFire = true;
            isSpellArrow = true;
        }

        if(!canFire)
            return;

        float f = getArrowVelocity(useTime);
        if (((double)f >= 0.1D) && canFire) {
            boolean isArrowInfinite = playerentity.abilities.isCreativeMode || (arrowStack.getItem() instanceof ArrowItem && ((ArrowItem)arrowStack.getItem()).isInfinite(arrowStack, bowStack, playerentity));
            if (!worldIn.isRemote) {
                ArrowItem arrowitem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, arrowStack, playerentity);
                abstractarrowentity = customArrow(abstractarrowentity);

                List<AbstractArrowEntity> arrows = new ArrayList<>();
                boolean didCastSpell = false;
                if(arrowitem == Items.ARROW && caster.getSpell() != null && new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).withSilent(true).canCast(playerentity)){
                    abstractarrowentity = buildSpellArrow(worldIn, playerentity, caster, isSpellArrow);
                    new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).expendMana(playerentity);
                    didCastSpell = true;
                }else if(arrowitem instanceof SpellArrow){
                    if(caster.getSpell() == null || !(new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).canCast(playerentity))){
                        return;
                    }else if(new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).canCast(playerentity)){
                        new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).expendMana(playerentity);
                        didCastSpell = true;
                    }
                }
                arrows.add(abstractarrowentity);
                if(caster.getSpell() != null && caster.getSpell().isValid() && didCastSpell){
                    int numSplits = caster.getSpell().getBuffsAtIndex(0, playerentity, AugmentSplit.class);
                    if(abstractarrowentity instanceof EntitySpellArrow){
                        numSplits = ((EntitySpellArrow) abstractarrowentity).spellResolver.spell.getBuffsAtIndex(0, playerentity, AugmentSplit.class);
                    }

                           // (abstractarrowentity instanceof EntitySpellArrow ? ((EntitySpellArrow) abstractarrowentity).spellResolver.spell.getBuffsAtIndex(0, AugmentSplit));

                    for(int i =1; i < numSplits + 1; i++){
                        Direction offset = playerentity.getHorizontalFacing().rotateY();
                        if(i%2==0) offset = offset.getOpposite();
                        // Alternate sides
                        BlockPos projPos = playerentity.getPosition().offset(offset, i);
                        projPos = projPos.add(0, 1.5, 0);
                        EntitySpellArrow spellArrow = buildSpellArrow(worldIn, playerentity, caster, isSpellArrow);
                        spellArrow.setPosition(projPos.getX(), spellArrow.getPosition().getY(), projPos.getZ());
                        arrows.add(spellArrow);
                    }
                }
                for(AbstractArrowEntity arr : arrows){
                    arr.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                    if (f >= 1.0F) {
                        arr.setIsCritical(true);
                    }
                    addArrow(arr, bowStack, arrowStack, isArrowInfinite, playerentity);
                }
            }

            worldIn.playSound(null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (!isArrowInfinite && !playerentity.abilities.isCreativeMode) {
                arrowStack.shrink(1);
            }
        }
    }

    public void addArrow(AbstractArrowEntity abstractarrowentity, ItemStack bowStack,ItemStack arrowStack, boolean isArrowInfinite, PlayerEntity playerentity){
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
        if (power > 0) {
            abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double)power * 0.5D + 0.5D);
        }

        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
        if (punch > 0) {
            abstractarrowentity.setKnockbackStrength(punch);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack) > 0) {
            abstractarrowentity.setFire(100);
        }

        if (isArrowInfinite || playerentity.abilities.isCreativeMode && (arrowStack.getItem() == Items.SPECTRAL_ARROW || arrowStack.getItem() == Items.TIPPED_ARROW)) {
            abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }
        playerentity.world.addEntity(abstractarrowentity);
    }

    /**
     * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
     */
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return ARROWS.or(i -> i.getItem() instanceof SpellArrow);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
        return super.customArrow(arrow);
    }

    public AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        getInformation(stack, worldIn, tooltip2, flagIn);
        super.addInformation(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(PlayerEntity player) {
        PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.bow.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(new MethodProjectile());
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability();
    }


    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }
}
