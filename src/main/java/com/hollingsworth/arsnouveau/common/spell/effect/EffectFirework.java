package com.hollingsworth.arsnouveau.common.spell.effect;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class EffectFirework extends AbstractEffect implements IDamageEffect {

    public static EffectFirework INSTANCE = new EffectFirework();

    public EffectFirework() {
        super(GlyphLib.EffectFireworkID, "Firework");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) {
            return;
        }

        ItemStack firework = fireworkFromInv(spellContext, spellStats, shooter);
        for (int i = 0; i < spellStats.getBuffCount(AugmentSplit.INSTANCE) + 1; i++) {
            spawnFireworkOnEntity(rayTraceResult, world, shooter, firework);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        ItemStack firework = fireworkFromInv(spellContext, spellStats, shooter);
        for (int i = 0; i < spellStats.getBuffCount(AugmentSplit.INSTANCE) + 1; i++) {
            spawnFireworkOnBlock(rayTraceResult, world, shooter, i, firework, spellContext);
        }
    }

    public ItemStack fireworkFromInv(SpellContext spellContext, SpellStats spellStats, LivingEntity shooter) {
        InventoryManager manager = spellContext.getCaster().getInvManager();
        SlotReference slotReference = manager.findItem(i -> i.getItem() == Items.FIREWORK_ROCKET, InteractType.EXTRACT);
        if(slotReference.getHandler() != null){
            ItemStack firework = slotReference.getHandler().getStackInSlot(slotReference.getSlot());
            if(!firework.isEmpty()){
                return firework;
            }
        }
        return getFirework((int) spellStats.getDurationMultiplier(), (int) spellStats.getAmpMultiplier());
    }

    public void spawnFireworkOnBlock(BlockHitResult rayTraceResult, Level world, LivingEntity shooter, int i, ItemStack fireworkStack, SpellContext context) {
        FireworkRocketEntity fireworkrocketentity;
        if (context.getType() == SpellContext.CasterType.TURRET) {
            BlockPos pos = rayTraceResult.getBlockPos();
            Direction direction = rayTraceResult.getDirection().getOpposite();
            fireworkrocketentity = new FireworkRocketEntity(world, fireworkStack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, true);
            fireworkrocketentity.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), 0.5F, 1.0F);
        } else {
            BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
            fireworkrocketentity = new FireworkRocketEntity(world, shooter,
                    pos.getX() + 0.5 + i * ParticleUtil.inRange(-0.3, 0.3), pos.getY() + 0.5, pos.getZ() + 0.5 + i * ParticleUtil.inRange(-0.3, 0.3),
                    fireworkStack);
        }
        world.addFreshEntity(fireworkrocketentity);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    public void spawnFireworkOnEntity(EntityHitResult rayTraceResult, Level world, LivingEntity shooter, ItemStack firework) {
        FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(world, firework, (LivingEntity) rayTraceResult.getEntity());
        fireworkrocketentity.setOwner(shooter);
        world.addFreshEntity(fireworkrocketentity);
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Override
    public String getBookDescription() {
        return "Creates a firework at the location or entity. Amplify will add Firework Stars, while Extend Time will add additional flight time. If a firework exists in the casters inventory, the created firework will mimic the held one. Spell Turrets with Touch will create fireworks as if they were dispensed.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentAmplify.INSTANCE, AugmentSplit.INSTANCE);
    }


    public static FireworkRocketItem.Shape[] shapes = FireworkRocketItem.Shape.values();
    private static List<DyeColor> dyes;

    public static List<DyeColor> getColorfulDyes() {
        if (dyes == null) {
            dyes = Arrays.stream(DyeColor.values())
                    .filter(d -> d != DyeColor.BLACK && d != DyeColor.GRAY && d != DyeColor.LIGHT_GRAY && d != DyeColor.BROWN).collect(Collectors.toList());
        }
        return dyes;
    }

    public static ItemStack getFirework(int numGunpowder, int numStars) {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag rocketTag = stack.getOrCreateTagElement("Fireworks");
        rocketTag.putByte("Flight", (byte) numGunpowder);
        ListTag listnbt = new ListTag();
        for (int i = 0; i < numStars; i++) {
            listnbt.add(getRandomStar().getTagElement("Explosion"));
        }
        if (!listnbt.isEmpty()) {
            rocketTag.put("Explosions", listnbt);
        }
        return stack;
    }

    public static ItemStack getRandomStar() {
        ItemStack star = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag starTag = star.getOrCreateTagElement("Explosion");
        Random random = new Random();
        FireworkRocketItem.Shape fireworkrocketitem$shape = shapes[random.nextInt(shapes.length)];
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < random.nextInt(8); i++) {
            list.add(getColorfulDyes().get(random.nextInt(getColorfulDyes().size())).getFireworkColor());
        }
        starTag.putBoolean("Flicker", random.nextBoolean());
        starTag.putBoolean("Trail", random.nextBoolean());
        starTag.putIntArray("Colors", list);
        starTag.putByte("Type", (byte) fireworkrocketitem$shape.getId());
        return star;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

}
