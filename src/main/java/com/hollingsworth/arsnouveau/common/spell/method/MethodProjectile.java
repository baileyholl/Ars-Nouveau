package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MethodProjectile extends AbstractCastMethod {

    public MethodProjectile() {
        super(ModConfig.MethodProjectileID, "Projectile");
    }



    @Override
    public int getManaCost() {
        return 10;
    }


    public void summonProjectiles(World world, PlayerEntity shooter, ArrayList<AbstractAugment> augments){
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, shooter, this.resolver);
        projectiles.add(projectileSpell);
        int numSplits = getBuffCount(augments, AugmentSplit.class);
        for(int i =1; i < numSplits + 1; i++){
            Direction offset =shooter.getHorizontalFacing().rotateY();
            if(i%2==0) offset = offset.getOpposite();
             // Alternate sides
            BlockPos projPos = shooter.getPosition().offset(offset, i);
            projPos = projPos.add(0, 1.5, 0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, shooter, this.resolver);
            spell.setPosition(projPos.getX(), projPos.getY(), projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = 1.0f + getBuffCount(augments, AugmentAccelerate.class);

        for(EntityProjectileSpell proj : projectiles) {
            proj.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, velocity, .80F);
            world.addEntity(proj);
        }
    }

    @Override
    public void onCast(ItemStack stack, PlayerEntity shooter, World world, ArrayList<AbstractAugment> augments) {
        summonProjectiles(world, shooter, augments);
        resolver.expendMana(shooter);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {
        World world = context.getWorld();
        PlayerEntity shooter = context.getPlayer();
        summonProjectiles(world, shooter, augments);
        resolver.expendMana(shooter);

    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {

    }

    @Override
    protected String getBookDescription() {
        return "Summons a projectile that applies spell effects when this projectile hits a target or block.";
    }
}
