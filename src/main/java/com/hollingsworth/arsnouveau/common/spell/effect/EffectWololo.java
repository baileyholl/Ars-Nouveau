package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.mixin.MobAccessor;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
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
        world.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.EVOKER_PREPARE_WOLOLO, SoundSource.PLAYERS, spellContext.getSpell().sound.volume, spellContext.getSpell().sound.pitch);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof IWololoable sheep) {
            ParticleColor color = spellStats.isRandomized() ? ParticleColor.makeRandomColor(255, 255, 255, shooter.getRandom()) : spellContext.getSpell().color;
            sheep.setColor(color);
        } else if (blockEntity instanceof SignBlockEntity sign) {
            DyeItem dye = spellStats.isRandomized() ? getRandomDye(shooter.getRandom()) : getDyeItemFromSpell(spellContext);
            dye.tryApplyToSign(world, sign, true, ANFakePlayer.getPlayer((ServerLevel) world));
        } else {
            DyeItem dye = spellStats.isRandomized() ? getRandomDye(shooter.getRandom()) : getDyeItemFromSpell(spellContext);
            // Try block + dye
            Block hitBlock = world.getBlockState(blockPos).getBlock();
            if (hitBlock == Blocks.AIR) return;
            ItemStack result = getResultingBlock(dye, hitBlock, (ServerLevel) world);
            BlockItem blockItem;
            if (result.isEmpty() || !(result.getItem() instanceof BlockItem)) {
                // Try blocks surrounding the dye
                result = getResultingBlock8(dye, hitBlock, (ServerLevel) world);
                if (result.isEmpty() || !(result.getItem() instanceof BlockItem)) return;
            }
            blockItem = (BlockItem) result.getItem();
            world.setBlockAndUpdate(blockPos, blockItem.getBlock().defaultBlockState());
        }

        world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.EVOKER_PREPARE_WOLOLO, SoundSource.PLAYERS, .5F, 1.0F);
    }

    private ItemStack getResultingBlock(DyeItem dye, Block block, ServerLevel world) {
        CraftingContainer craftingcontainer = makeContainer(dye, block);
        return world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, world).map((craftingRecipe) -> craftingRecipe.assemble(craftingcontainer, world.registryAccess())).orElse(ItemStack.EMPTY);
    }

    private ItemStack getResultingBlock8(DyeItem dye, Block block, ServerLevel world) {
        CraftingContainer craftingcontainer = makeContainer8(dye, block);
        return world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, world).map((craftingRecipe) -> craftingRecipe.assemble(craftingcontainer, world.registryAccess())).orElse(ItemStack.EMPTY);
    }

    private static CraftingContainer makeContainer(DyeItem targetColor, Block blockToDye) {
        CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            /**
             * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the
             * player inventory and the other inventory(s).
             */
            public ItemStack quickMoveStack(Player p_218264_, int p_218265_) {
                return ItemStack.EMPTY;
            }

            /**
             * Determines whether supplied player can use this container
             */
            public boolean stillValid(Player p_29888_) {
                return false;
            }
        }, 2, 1);
        craftingcontainer.setItem(0, new ItemStack(targetColor));
        craftingcontainer.setItem(1, new ItemStack(blockToDye));
        return craftingcontainer;
    }

    private static CraftingContainer makeContainer8(DyeItem targetColor, Block blockToDye) {
        CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            /**
             * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the
             * player inventory and the other inventory(s).
             */
            public ItemStack quickMoveStack(Player p_218264_, int p_218265_) {
                return ItemStack.EMPTY;
            }

            /**
             * Determines whether supplied player can use this container
             */
            public boolean stillValid(Player p_29888_) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < 9; i++) {
            craftingcontainer.setItem(i, i == 4 ? new ItemStack(targetColor) : new ItemStack(blockToDye));
        }
        return craftingcontainer;
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

    @Override
    public String getBookDescription() {
        return "Changes the color of an entity or compatible block to the color of the spell.";
    }
}
