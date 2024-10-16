package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.entity.debug.FixedStack;
import com.hollingsworth.arsnouveau.common.mixin.MobAccessor;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EffectWololo extends AbstractEffect {
    public static EffectWololo INSTANCE = new EffectWololo();

    public EffectWololo() {
        super("wololo", "Wololo");
    }

    public static int MAX_RECIPE_CACHE = 16;
    public static FixedStack<CraftingRecipe> recipeCache = new FixedStack<>(MAX_RECIPE_CACHE);

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        Player player = ANFakePlayer.getPlayer((ServerLevel) world);

        ItemStack dyeStack = getDye(shooter, spellStats, spellContext, player);
        if (dyeStack.isEmpty()) return;
        DyeItem dye = (DyeItem) dyeStack.getItem();

        if (rayTraceResult.getEntity() instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().getItem() instanceof IDyeable iDyeable)
                iDyeable.onDye(itemEntity.getItem(), dye.getDyeColor());
            else if (itemEntity.getItem().getItem() instanceof BlockItem blockItem) {
                ItemStack result = getDyedResult((ServerLevel) world, makeContainer(dye, blockItem));
                result.setCount(itemEntity.getItem().getCount());
                if (!result.isEmpty() && result.getItem() instanceof BlockItem) {
                    itemEntity.setItem(result);
                }
            }
        } else if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            if (living instanceof Sheep sheep)
                sheep.setColor(dye.getDyeColor());
            else if (spellStats.isSensitive() || living instanceof ArmorStand) {
                for (ItemStack armorStack : living.getArmorSlots()) {
                    if (!armorStack.isEmpty()) {
                        var dyeComponent = armorStack.get(DataComponents.DYED_COLOR);
                        if (dyeComponent != null) {
                            armorStack.set(DataComponents.DYED_COLOR, new DyedItemColor(dye.getDyeColor().getTextureDiffuseColor(), false));
                        } else if (armorStack.getItem() instanceof IDyeable iDyeable) {
                            iDyeable.onDye(armorStack, dye.getDyeColor());
                        }
                    }
                }
            } else if (living instanceof Mob mob) {
                player.setItemInHand(InteractionHand.MAIN_HAND, dyeStack);
                ((MobAccessor) mob).callMobInteract(player, InteractionHand.MAIN_HAND);
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        world.playSound(null, rayTraceResult.getEntity().getX(), rayTraceResult.getEntity().getY(), rayTraceResult.getEntity().getZ(), SoundEvents.EVOKER_PREPARE_WOLOLO, SoundSource.PLAYERS, spellContext.getSpell().sound().getVolume(), spellContext.getSpell().sound().getPitch());
    }

    @NotNull
    private ItemStack getDye(@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, Player player) {

        if (spellContext.getCaster() instanceof TileCaster) {
            InventoryManager manager = spellContext.getCaster().getInvManager();
            SlotReference reference = manager.findItem(i -> i.getItem() instanceof DyeItem, InteractType.EXTRACT);
            if (!reference.isEmpty()) {
                return reference.getHandler().getStackInSlot(reference.getSlot());
            }
        } else if (isRealPlayer(shooter)) {
            ItemStack stack = player.getOffhandItem();
            if (stack.getItem() instanceof DyeItem) {
                return stack;
            }
        }

        DyeItem dye = spellStats.isRandomized() ? getRandomDye(shooter.getRandom()) : getDyeItemFromSpell(spellContext);

        return dye.getDefaultInstance();
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof IWololoable tileToDye) {
            ParticleColor color = spellStats.isRandomized() ? ParticleColor.makeRandomColor(255, 255, 255, shooter.getRandom()) : spellContext.getSpell().color();
            tileToDye.setColor(color);
        } else {
            ItemStack dyeStack = getDye(shooter, spellStats, spellContext, ANFakePlayer.getPlayer((ServerLevel) world));
            if (dyeStack.isEmpty()) return;
            DyeItem dye = (DyeItem) dyeStack.getItem();

            if (blockEntity instanceof SignBlockEntity sign) {
                var player = getPlayer(shooter, (ServerLevel) world);
                dye.tryApplyToSign(world, sign, sign.isFacingFrontText(player), player);
            } else {
                // Try block + dye
                BlockState hitBlock = world.getBlockState(blockPos);
                if (hitBlock.isAir()) return;
                ItemStack result = getDyedResult((ServerLevel) world, makeContainer(dye, hitBlock.getBlock()));
                BlockItem blockItem;
                if (result.isEmpty() || !(result.getItem() instanceof BlockItem)) {
                    // Try blocks surrounding the dye
                    result = getDyedResult((ServerLevel) world, makeContainer8(dye, hitBlock.getBlock()));
                    if (result.isEmpty() || !(result.getItem() instanceof BlockItem)) return;
                }
                blockItem = (BlockItem) result.getItem();
                BlockState newState = blockItem.getBlock().withPropertiesOf(hitBlock);
                world.setBlockAndUpdate(blockPos, newState);
            }
        }
    }

    @NotNull
    private ItemStack getDyedResult(ServerLevel world, CraftingContainer craftingcontainer) {
        Optional<CraftingRecipe> recipe = recipeCache.stream().filter(craftingRecipe -> craftingRecipe.matches(craftingcontainer.asCraftInput(), world)).findFirst();
        if (recipe.isPresent()) {
            recipeCache.add(recipe.get());
            return recipe.get().assemble(craftingcontainer.asCraftInput(), world.registryAccess());
        }
        return world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer.asCraftInput(), world).map(craftingRecipe -> craftingRecipe.value().assemble(craftingcontainer.asCraftInput(), world.registryAccess())).orElse(ItemStack.EMPTY);
    }

    private static CraftingContainer makeContainer(DyeItem targetColor, ItemLike blockToDye) {
        CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            /**
             * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the
             * player inventory and the other inventory(s).
             */
            public @NotNull ItemStack quickMoveStack(@NotNull Player p_218264_, int p_218265_) {
                return ItemStack.EMPTY;
            }

            /**
             * Determines whether supplied player can use this container
             */
            public boolean stillValid(@NotNull Player p_29888_) {
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
            public @NotNull ItemStack quickMoveStack(@NotNull Player p_218264_, int p_218265_) {
                return ItemStack.EMPTY;
            }

            /**
             * Determines whether supplied player can use this container
             */
            public boolean stillValid(@NotNull Player p_29888_) {
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
        ParticleColor spellColor = spellContext.getSpell().color();

        ParticleColor targetColor = vanillaColors.keySet().stream().min(Comparator.comparingDouble(d -> d.euclideanDistance(spellColor))).orElse(ParticleColor.WHITE);
        return (DyeItem) vanillaColors.get(targetColor);
    }

    @Override
    protected int getDefaultManaCost() {
        return 30;
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
        return augmentSetOf(AugmentRandomize.INSTANCE, AugmentSensitive.INSTANCE);
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
