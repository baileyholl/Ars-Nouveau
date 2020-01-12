package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.ModEntities;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class ModifierProjectile extends CastMethod {

    public ModifierProjectile() {
        super(ModConfig.ModifierProjectileID, "Projectile");

    }



    @Override
    public int getManaCost() {
        return 0;
    }




    @Override
    public void onCast(Position position, World world, LivingEntity shooter) {
        System.out.println("Summoning projectile");
        if(!world.isRemote){

            EntityProjectileSpell projectileSpell = new EntityProjectileSpell(EntityType.SNOWBALL, world, shooter, this.resolver);
            projectileSpell.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 1.0f, 1.0F);
            world.addEntity(projectileSpell);

        }
    }
}
