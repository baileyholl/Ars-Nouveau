package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.client.renderer.item.ShieldRenderer;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.defaultItemProperties;

public class EnchantersShield extends ShieldItem implements IAnimatable {

    public EnchantersShield() {
        super(defaultItemProperties().durability(500).setISTER(() -> ShieldRenderer::new));
        setRegistryName(LibItemNames.ENCHANTERS_SHIELD);
    }

    public EnchantersShield(Properties p_i48470_1_) {
        super(p_i48470_1_);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
        if(world.isClientSide() || world.getGameTime() % 200 !=  0 || stack.getDamageValue() == 0 || !(entity instanceof PlayerEntity))
            return;

        ManaCapability.getMana((LivingEntity) entity).ifPresent(mana -> {
            if(mana.getCurrentMana() > 20){
                mana.removeMana(20);
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        });
    }

    @Override
    public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        return super.use(p_77659_1_, p_77659_2_, p_77659_3_);
    }

    @Override
    public void registerControllers(AnimationData animationData) { }

    public AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
