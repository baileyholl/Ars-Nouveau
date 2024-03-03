package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.mixin.MobAccessor;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EffectWololo extends AbstractEffect {
    public static EffectWololo INSTANCE = new EffectWololo();

    public EffectWololo() {
        super("wololo", "Wololo");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (!(rayTraceResult.getEntity() instanceof Mob living)) return;
        DyeItem dye = spellStats.isRandomized() ? getRandomDye(shooter.getRandom()) : getDyeItemFromSpell(spellContext);

        Player player = ANFakePlayer.getPlayer((ServerLevel) world);
        ItemStack stack = dye.getDefaultInstance();
        if (living instanceof Sheep sheep)
            sheep.setColor(dye.getDyeColor());
        else {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            ((MobAccessor) living).callMobInteract(player, InteractionHand.MAIN_HAND);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }

    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof IWololoable sheep) {
            ParticleColor color = spellStats.isRandomized() ? ParticleColor.makeRandomColor(255, 255, 255, shooter.getRandom()) : spellContext.getSpell().color;
            sheep.setColor(color);
        } else if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof SignBlockEntity sign) {
            DyeItem dye = spellStats.isRandomized() ? getRandomDye(shooter.getRandom()) : getDyeItemFromSpell(spellContext);
            dye.tryApplyToSign(world, sign, true, ANFakePlayer.getPlayer((ServerLevel) world));
        }
    }

    private DyeItem getRandomDye(RandomSource random) {
        return (DyeItem) vanillaColors.values().toArray()[random.nextInt(vanillaColors.size())];
    }

    private static DyeItem getDyeItemFromSpell(SpellContext spellContext) {
        ParticleColor spellColor = spellContext.getSpell().color;

        ParticleColor targetColor = vanillaColors.keySet().stream().min(Comparator.comparingDouble(d -> d.euclideanDistance(spellColor))).orElse(ParticleColor.WHITE);
        return (DyeItem) vanillaColors.get(targetColor);
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    /**
     * Returns the set of augments that this spell part can be enhanced by.
     * Mods should use {@link AbstractSpellPart#compatibleAugments} for addon-supported augments.
     *
     * @see AbstractSpellPart#augmentSetOf(AbstractAugment...) for easy syntax to make the Set.
     * This should not be accessed directly, but can be overridden.
     */
    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentRandomize.INSTANCE);
    }

    public static Map<ParticleColor, Item> vanillaColors = new HashMap<>();

    static {
        vanillaColors.put(ParticleColor.WHITE, Items.WHITE_DYE);
        vanillaColors.put(ParticleColor.RED, Items.RED_DYE);
        vanillaColors.put(ParticleColor.GREEN, Items.GREEN_DYE);
        vanillaColors.put(ParticleColor.BLUE, Items.BLUE_DYE);
        vanillaColors.put(ParticleColor.YELLOW, Items.YELLOW_DYE);
        vanillaColors.put(ParticleColor.PURPLE, Items.PURPLE_DYE);
        vanillaColors.put(ParticleColor.CYAN, Items.CYAN_DYE);
        vanillaColors.put(ParticleColor.ORANGE, Items.ORANGE_DYE);
        vanillaColors.put(ParticleColor.MAGENTA, Items.MAGENTA_DYE);
        vanillaColors.put(ParticleColor.LIGHT_BLUE, Items.LIGHT_BLUE_DYE);
        vanillaColors.put(ParticleColor.LIME, Items.LIME_DYE);
        vanillaColors.put(ParticleColor.PINK, Items.PINK_DYE);
        vanillaColors.put(ParticleColor.GRAY, Items.GRAY_DYE);
        vanillaColors.put(ParticleColor.LIGHT_GRAY, Items.LIGHT_GRAY_DYE);
        vanillaColors.put(ParticleColor.BROWN, Items.BROWN_DYE);
        vanillaColors.put(ParticleColor.BLACK, Items.BLACK_DYE);
    }


}
