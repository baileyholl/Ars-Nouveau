package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.util.LootUtil;
import com.hollingsworth.craftedmagic.spell.augment.AugmentFortune;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

public class EffectDamage extends AbstractEffect {

    public EffectDamage() {super(ModConfig.EffectDamageID, "Damage" ); }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            float damage = 5.0f + 5.0f * getAmplificationBonus(augments);
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(entity instanceof MobEntity){
                MobEntity mob = (MobEntity) entity;
                if(mob.getHealth() <= damage && mob.getHealth() > 0 && hasBuff(augments, AugmentFortune.class)){
                    int looting = getBuffCount(augments, AugmentFortune.class);
                    LootContext.Builder lootContext = LootUtil.getLootingContext((ServerWorld)world, (PlayerEntity)shooter, mob, looting, DamageSource.causePlayerDamage((PlayerEntity) shooter));
                    ResourceLocation lootTable = mob.func_213346_cF();
                    LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(lootTable);
                    List<ItemStack> items = loottable.generate(lootContext.build(LootParameterSets.GENERIC));
                    items.forEach(mob::entityDropItem);

                }
            }
            entity.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) shooter), damage);

        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 20;
    }

}
