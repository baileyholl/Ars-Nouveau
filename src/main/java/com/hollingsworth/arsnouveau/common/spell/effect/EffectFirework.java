package com.hollingsworth.arsnouveau.common.spell.effect;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class EffectFirework extends AbstractEffect {
    public EffectFirework() {
        super(GlyphLib.EffectFireworkID, "Firework");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext);
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(world, getFirework((int)spellStats.getDurationMultiplier(),(int) spellStats.getAmpMultiplier()), (LivingEntity) rayTraceResult.getEntity());
            fireworkrocketentity.setOwner(shooter);
            world.addFreshEntity(fireworkrocketentity);
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext);
        for(int i = 0; i < spellStats.getBuffCount(AugmentSplit.INSTANCE) + 1; i++) {
            Vector3d pos = rayTraceResult.getLocation();
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(world, shooter,
                    pos.x + i * ParticleUtil.inRange(-0.3, 0.3), pos.y, pos.z + i *ParticleUtil.inRange(-0.3, 0.3),
                    getFirework((int) spellStats.getDurationMultiplier(), (int) spellStats.getAmpMultiplier()));
            world.addFreshEntity(fireworkrocketentity);
        }
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentExtendTime.INSTANCE, AugmentAmplify.INSTANCE, AugmentSplit.INSTANCE);
    }


    public static FireworkRocketItem.Shape[] shapes = FireworkRocketItem.Shape.values();
    private static List<DyeColor> dyes;

    public static List<DyeColor> getColorfulDyes(){
        if(dyes == null){
            dyes = Arrays.stream(DyeColor.values())
                    .filter(d -> d != DyeColor.BLACK && d != DyeColor.GRAY && d != DyeColor.LIGHT_GRAY && d != DyeColor.BROWN).collect(Collectors.toList());
        }
        return dyes;
    }

    public static ItemStack getFirework(int numGunpowder, int numStars){
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundNBT rocketTag = stack.getOrCreateTagElement("Fireworks");
        rocketTag.putByte("Flight", (byte)numGunpowder);
        ListNBT listnbt = new ListNBT();
        for(int i = 0; i < numStars; i++){
            listnbt.add(getRandomStar().getTagElement("Explosion"));
        }
        if (!listnbt.isEmpty()) {
            rocketTag.put("Explosions", listnbt);
        }
        return stack;
    }

    public static ItemStack getRandomStar(){
        ItemStack star = new ItemStack(Items.FIREWORK_STAR);
        CompoundNBT starTag = star.getOrCreateTagElement("Explosion");
        Random random = new Random();
        FireworkRocketItem.Shape fireworkrocketitem$shape = shapes[random.nextInt(shapes.length)];
        List<Integer> list = Lists.newArrayList();
        for(int i = 0; i < random.nextInt(8); i++){
            list.add(getColorfulDyes().get(random.nextInt(getColorfulDyes().size())).getFireworkColor());
        }
        starTag.putBoolean("Flicker", random.nextBoolean());
        starTag.putBoolean("Trail", random.nextBoolean());
        starTag.putIntArray("Colors", list);
        starTag.putByte("Type", (byte)fireworkrocketitem$shape.getId());
        return star;
    }


    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.FIREWORK_ROCKET;
    }
}
