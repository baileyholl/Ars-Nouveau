package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.FakePlayer;

public class BreezeEvent implements ITimedEvent {

    public Breeze breeze;
    int age;
    EntityOrbitProjectile orbitProjectile;
    Entity target;

    public BreezeEvent(Breeze breeze, EntityOrbitProjectile orbitProjectile, Entity target) {
        this.breeze = breeze;
        this.orbitProjectile = orbitProjectile;
        this.target = target;
    }


    @Override
    public void tick(boolean serverSide) {
        age++;
        if(age % 8 == 0) {
            this.orbitProjectile.setAccelerates(this.orbitProjectile.getAccelerates() + 1);
        }

        if(age == 45){
            this.orbitProjectile.remove(Entity.RemovalReason.DISCARDED);
            EntityProjectileSpell projectileSpell = new EntityProjectileSpell(breeze.level, new SpellResolver(SpellContext.fromEntity(orbitProjectile.resolver().spell, breeze, ItemStack.EMPTY)));
            // Aim the projectile at the player
            projectileSpell.shoot(target.getX() - breeze.getX(), target.getY() - breeze.getY(), target.getZ() - breeze.getZ(), 1.0F, 0.0F);
            breeze.level.addFreshEntity(projectileSpell);
        }
    }

    @Override
    public boolean isExpired() {
        return age > 45 || breeze.isRemoved() || breeze.isDeadOrDying() || this.orbitProjectile.isRemoved() || target.isRemoved();
    }

    public static void onSpellResolve(SpellResolveEvent.Pre pre){
        HitResult hitResult = pre.rayTraceResult;
        if(pre.context.getUnwrappedCaster() instanceof Breeze){
            return;
        }
        if(hitResult instanceof EntityHitResult entityHitResult
                && !(pre.shooter instanceof FakePlayer fakePlayer)
                && entityHitResult.getEntity() instanceof Breeze breeze
        && pre.resolver.spell.getCastMethod() instanceof MethodProjectile){
            pre.setCanceled(true);
            EntityOrbitProjectile orbitProjectile = new EntityOrbitProjectile(breeze.level, new SpellResolver(SpellContext.fromEntity(pre.spell, breeze, ItemStack.EMPTY)), entityHitResult.getEntity());
            breeze.level.addFreshEntity(orbitProjectile);
            EventQueue.getServerInstance().addEvent(new BreezeEvent(breeze, orbitProjectile, pre.shooter));
        }
    }
}
