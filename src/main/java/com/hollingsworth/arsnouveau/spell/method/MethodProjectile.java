package com.hollingsworth.arsnouveau.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.entity.EntityProjectileSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MethodProjectile extends AbstractCastMethod {

    public MethodProjectile() {
        super(ModConfig.MethodProjectileID, "Projectile");
    }



    @Override
    public int getManaCost() {
        return 15;
    }


    @Override
    public void onCast(ItemStack stack, PlayerEntity shooter, World world, ArrayList<AbstractAugment> augments) {
        System.out.println("On Cast");
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, shooter, this.resolver);
        projectileSpell.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 1.0f, 1.0F);
        world.addEntity(projectileSpell);
        resolver.expendMana(shooter);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {
        System.out.println("On cast on block");
        World world = context.getWorld();
        PlayerEntity shooter = context.getPlayer();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, shooter, this.resolver);
        projectileSpell.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 1.0f, 1.0F);
        world.addEntity(projectileSpell);
        resolver.expendMana(shooter);

    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {

    }
}
