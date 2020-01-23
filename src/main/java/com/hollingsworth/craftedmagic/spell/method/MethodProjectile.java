package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;

public class MethodProjectile extends CastMethod {

    public MethodProjectile() {
        super(ModConfig.MethodProjectileID, "Projectile");
    }



    @Override
    public int getManaCost() {
        return 0;
    }


    @Override
    public void onCast(ItemStack stack, PlayerEntity shooter, World world) {
        System.out.println("Summoning projectile");
        if(!world.getWorld().isRemote){
            EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, shooter, this.resolver);
            projectileSpell.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 1.0f, 1.0F);
            world.addEntity(projectileSpell);

        }
    }

    @Override
    public void onCastOnBlock(ItemUseContext context) {

    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {

    }
}
