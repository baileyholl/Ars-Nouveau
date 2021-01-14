package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBowRenderer;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
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

    @Override
    public void onPlayerStoppedUsing(ItemStack bowStack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        //Copied from BowItem so we can spawn arrows in case there are no items.
        if (!(entityLiving instanceof PlayerEntity))
            return;
        PlayerEntity playerentity = (PlayerEntity)entityLiving;
        boolean isInfinity = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bowStack) > 0;
        ItemStack arrowStack = findAmmo(playerentity, bowStack);

        int i = this.getUseDuration(bowStack) - timeLeft;
        i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(bowStack, worldIn, playerentity, i, !arrowStack.isEmpty() || isInfinity);
        if (i < 0) return;
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

        float f = getArrowVelocity(i);
        if (((double)f >= 0.1D) && canFire) {
            boolean isArrowInfinite = playerentity.abilities.isCreativeMode || (arrowStack.getItem() instanceof ArrowItem && ((ArrowItem)arrowStack.getItem()).isInfinite(arrowStack, bowStack, playerentity));
            if (!worldIn.isRemote) {
                ArrowItem arrowitem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, arrowStack, playerentity);
                abstractarrowentity = customArrow(abstractarrowentity);
                if(arrowitem == Items.ARROW && caster.getSpell() != null && new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).withSilent(true).canCast(playerentity)){
                    EntitySpellArrow spellArrow = new EntitySpellArrow(worldIn, playerentity);
                    spellArrow.spellResolver = new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).withSilent(true);
                    abstractarrowentity = spellArrow;
                    spellArrow.spellResolver.expendMana(playerentity);
                    if(isSpellArrow)
                        spellArrow.setDamage(0);
                }else if(arrowitem instanceof SpellArrow){
                    if(caster.getSpell() == null || !(new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).canCast(playerentity))){
                        return;
                    }else if(new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).canCast(playerentity)){
                        new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).expendMana(playerentity);
                    }

                }

                abstractarrowentity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                if (f == 1.0F) {
                    abstractarrowentity.setIsCritical(true);
                }
                addArrow(abstractarrowentity, bowStack, arrowStack, isArrowInfinite, playerentity);
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
}
