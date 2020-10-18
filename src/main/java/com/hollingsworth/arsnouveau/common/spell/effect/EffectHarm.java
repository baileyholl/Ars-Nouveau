package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.util.List;

public class EffectHarm extends AbstractEffect {

    public EffectHarm() {super(ModConfig.EffectHarmID, "Harm" ); }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            //Networking.sendToNearby(caster.world, caster.getPosition(), new PacketBeam(new BlockPos(MathUtil.getEntityLookHit(caster, 8f)), caster.getPosition().add(0, caster.getEyeHeight() -0.2f, 0), 0));

            float damage = 5.0f + 5.0f * getAmplificationBonus(augments);
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();


//            for(LivingEntity living : SpellUtil.calcAOEEntities(entity, getBuffCount(augments, AugmentAOE.class))){
//                if(living instanceof MobEntity){
////                   Networking.sendToNearby(shooter.world, shooter.getPosition(), new PacketBeam(entity.getPositionVector(), living.getPositionVector(), 80));
//                    EntityAOEProjectile aoeProjectile = new EntityAOEProjectile(world, entity.getPositionVec().add(0, 1,0), living.getPositionVec().add(0, 1,0));
//                    aoeProjectile.setPosition(entity.getPosX(), entity.getPosY() + 1, entity.getPosZ());
//                    world.addEntity(aoeProjectile);
//                    MobEntity mob = (MobEntity) living;
//                    if(mob.getHealth() <= damage && mob.getHealth() > 0 && hasBuff(augments, AugmentFortune.class)){
//                        int looting = getBuffCount(augments, AugmentFortune.class);
//                        LootContext.Builder lootContext = LootUtil.getLootingContext((ServerWorld)world,shooter, mob, looting, DamageSource.causePlayerDamage((PlayerEntity) shooter));
//                        ResourceLocation lootTable = mob.getLootTableResourceLocation();
//                        LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(lootTable);
//                        List<ItemStack> items = loottable.generate(lootContext.build(LootParameterSets.GENERIC));
//                        items.forEach(mob::entityDropItem);
//
//                    }
//                }
//                living.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) shooter), damage);
//            }
            if(entity instanceof MobEntity){
                MobEntity mob = (MobEntity) entity;
                if(mob.getHealth() <= damage && mob.getHealth() > 0 && hasBuff(augments, AugmentFortune.class)){
                    int looting = getBuffCount(augments, AugmentFortune.class);
                    LootContext.Builder lootContext = LootUtil.getLootingContext((ServerWorld)world,shooter, mob, looting, DamageSource.causePlayerDamage((PlayerEntity) shooter));
                    ResourceLocation lootTable = mob.getLootTableResourceLocation();
                    LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(lootTable);
                    List<ItemStack> items = loottable.generate(lootContext.build(LootParameterSets.GENERIC));
                    items.forEach(mob::entityDropItem);

                }
            }
            entity.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) shooter), damage);

        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_SWORD;
    }

    @Override
    protected String getBookDescription() {
        return "A spell you start with. Damages a target. May be increased by Amplify. Note, multiple Harms without a delay will not apply due to invincibility on hit.";
    }
}
