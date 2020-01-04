package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import com.hollingsworth.craftedmagic.spell.ISpellCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.world.World;

public class ModifierProjectile extends CastMethod {

    public ModifierProjectile() {
        super(ModConfig.ModifierProjectileID);
    }



    @Override
    public int getManaCost() {
        return 0;
    }


    @Override
    public void onCast(Position position, World world, EntityLivingBase shooter) {
        System.out.println("Summoning projectile");
        if(!world.isRemote){

            EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, shooter, this.resolver);
            projectileSpell.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 1.0f, 1.0F);
            world.spawnEntity(projectileSpell);
        }

    }



}
