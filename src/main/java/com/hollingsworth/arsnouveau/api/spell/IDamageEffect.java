package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDamageEffect {

    default boolean canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, @NotNull Entity entity) {
        return !(entity instanceof LivingEntity living && living.getHealth() <= 0 || entity.isAlliedTo(shooter));
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
     */
    default boolean attemptDamage(Level world, @NotNull LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Entity entity, DamageSource source, float baseDamage) {
        if (!canDamage(shooter, stats, spellContext, resolver, entity))
            return false;
        ServerLevel server = (ServerLevel) world;
        float totalDamage = (float) (baseDamage + stats.getDamageModifier() + (shooter.getAttributes().hasAttribute(PerkAttributes.SPELL_DAMAGE_BONUS) ?
                shooter.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS) : 0));

        //randomize damage buff or debuff
        if (stats.isRandomized())
            totalDamage += randomRolls(stats, server);

        SpellDamageEvent.Pre preDamage = new SpellDamageEvent.Pre(source, shooter, entity, totalDamage, spellContext);
        NeoForge.EVENT_BUS.post(preDamage);

        source = preDamage.damageSource;
        totalDamage = preDamage.damage;
        if (totalDamage <= 0 || preDamage.isCanceled())
            return false;

        if (!entity.hurt(source, totalDamage)) {
            return false;
        }

        shooter.setLastHurtMob(entity);

        SpellDamageEvent.Post postDamage = new SpellDamageEvent.Post(source, shooter, entity, totalDamage, spellContext);
        NeoForge.EVENT_BUS.post(postDamage);

        if (entity instanceof
                    LivingEntity mob && mob.getHealth() <= 0 && !mob.isRemoved() && stats.hasBuff(AugmentFortune.INSTANCE)) {
            Player playerContext = shooter instanceof Player player ? player : ANFakePlayer.getPlayer(server);
            int looting = stats.getBuffCount(AugmentFortune.INSTANCE);
            LootParams lootContext = LootUtil.getLootingContext(server, shooter, mob, looting, world.damageSources().playerAttack(playerContext)).create(LootContextParamSets.ENTITY);
            LootTable loottable = server.getServer().reloadableRegistries().getLootTable( mob.getLootTable());
            List<ItemStack> items = loottable.getRandomItems(lootContext);
            items.forEach(mob::spawnAtLocation);
        }

        return true;
    }

    default int randomRolls(SpellStats stats, ServerLevel server) {
        return stats.getBuffCount(AugmentRandomize.INSTANCE) * server.random.nextIntBetweenInclusive(-1, 1);
    }

    /**
     * @param world   world
     * @param shooter source
     * @return Player-Based Damage Source, will use Ars FakePlayer if the source is not a Player
     */
    default DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypesRegistry.GENERIC_SPELL_DAMAGE, !(shooter instanceof Player player) ? ANFakePlayer.getPlayer((ServerLevel) world) : player);
    }

}
