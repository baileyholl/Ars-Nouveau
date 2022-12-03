package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDamageEffect {

    default boolean canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Entity entity) {
        return !(entity instanceof LivingEntity living && living.getHealth() <= 0);
    }


    /**
     * @param world        World
     * @param shooter      caster
     * @param stats        SpellStats
     * @param spellContext SpellContext
     * @param resolver     SpellResolver
     * @param entity       Target
     * @param source       DamageType
     * @param baseDamage   Starting damage
     * //TODO @return true if Damage is dealt, false if damage was canceled or reduced to 0
     */
    default void attemptDamage(Level world,@NotNull LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Entity entity, DamageSource source, float baseDamage) {
        if (!canDamage(shooter, stats, spellContext, resolver, entity))
            return; //false;
        ServerLevel server = (ServerLevel) world;
        float totalDamage = (float) (baseDamage + stats.getDamageModifier());

        SpellDamageEvent.Pre preDamage = new SpellDamageEvent.Pre(source, shooter, entity, totalDamage, spellContext);
        MinecraftForge.EVENT_BUS.post(preDamage);

        source = preDamage.damageSource;
        totalDamage = preDamage.damage;
        if (totalDamage <= 0 || preDamage.isCanceled())
            return; // false;

        if (!entity.hurt(source, totalDamage)) {
            return; //false;
        }

        SpellDamageEvent.Post postDamage = new SpellDamageEvent.Post(source, shooter, entity, totalDamage, spellContext);
        MinecraftForge.EVENT_BUS.post(postDamage);

        if (entity instanceof LivingEntity mob && mob.getHealth() <= 0 && !mob.isRemoved() && stats.hasBuff(AugmentFortune.INSTANCE)) {
            Player playerContext = shooter instanceof Player player ? player : ANFakePlayer.getPlayer(server);
            int looting = stats.getBuffCount(AugmentFortune.INSTANCE);
            LootContext.Builder lootContext = LootUtil.getLootingContext(server, shooter, mob, looting, DamageSource.playerAttack(playerContext));
            ResourceLocation lootTable = mob.getLootTable();
            LootTable loottable = server.getServer().getLootTables().get(lootTable);
            List<ItemStack> items = loottable.getRandomItems(lootContext.create(LootContextParamSets.ENTITY));
            items.forEach(mob::spawnAtLocation);
        }

        //return true;
    }

    /**
     * @param world   world
     * @param shooter source
     * @return Player-Based Damage Source, will use Ars FakePlayer if the source is not a Player
     */
    default DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        shooter = !(shooter instanceof Player) ? ANFakePlayer.getPlayer((ServerLevel) world) : shooter;
        return DamageSource.playerAttack((Player) shooter);
    }

}
