package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.api.recipe.RecipeWrapper;
import com.hollingsworth.arsnouveau.api.recipe.ShapedHelper;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WixieCauldronTile extends SummoningTile implements ITooltipProvider {

    public List<BlockPos> inventories;
    public ItemStack craftingItem;
    public int entityID;
    public boolean hasSource;
    public boolean isOff;
    public boolean isCraftingPotion;
    public boolean needsPotionStorage;
    RecipeWrapper recipeWrapper;
    public CraftingProgress craftManager = new CraftingProgress();

    public WixieCauldronTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.WIXIE_CAULDRON_TYPE, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
            return;

        if (!hasSource && level.getGameTime() % 5 == 0) {
            if (SourceUtil.takeSourceNearbyWithParticles(worldPosition, level, 6, 50) != null) {
                this.hasSource = true;
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, true));
                setChanged();
            }
        }

        if (!hasSource)
            return;

        if (this.recipeWrapper == null && craftingItem != null)
            setRecipes(null, craftingItem);

        if (level.getGameTime() % 100 == 0) {
            updateInventories(); // Update the inventories available to use
        }

    }

    public boolean hasWixie() {
        return !this.converted || level.getEntity(entityID) != null;
    }

    public boolean isCraftingDone() {
        return craftManager.isDone();
    }

    public boolean needsPotion() {
        return craftManager.isPotionCrafting && !craftManager.hasObtainedPotion();
    }

    public Potion getNeededPotion() {
        return craftManager.getPotionNeeded();
    }

    public void givePotion() {
        craftManager.setHasObtainedPotion(true);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    public boolean giveItem(ItemStack stack) {
        boolean res = craftManager.giveItem(stack.getItem());
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        return res;
    }

    public void attemptFinish() {

        if (craftManager.isDone()) {
            if (!isCraftingPotion) {

                if (!craftManager.outputStack.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), craftManager.outputStack.copy()));
                    this.hasSource = false;
                    level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, false));
                    level.playSound(null, getBlockPos(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.BLOCKS, 0.15f, 0.6f);
                }
                for (ItemStack i : craftManager.remainingItems) {
                    level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), i.copy()));

                }

                craftManager = new CraftingProgress();
                setNewCraft();
            } else {

                if (craftManager.potionOut == null) {
                    setNewCraft();
                    return;
                }


                BlockPos jarPos = findPotionStorage(craftManager.potionOut);
                if (jarPos == null) {
                    if (!needsPotionStorage) {
                        needsPotionStorage = true;
                        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
                    }
                    return;
                }

                if (level.getBlockEntity(jarPos) instanceof PotionJarTile) {
                    needsPotionStorage = false;
                    ((PotionJarTile) level.getBlockEntity(jarPos)).addAmount(craftManager.potionOut, 300);
                    int color = ((PotionJarTile) level.getBlockEntity(jarPos)).getColor();
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = (color) & 0xFF;
                    int a = (color >> 24) & 0xFF;
                    EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(level, worldPosition, jarPos, r, g, b);
                    level.addFreshEntity(aoeProjectile);
                    this.hasSource = false;
                    level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, false));
                    craftManager = new CraftingProgress();
                    setNewCraft();
                }
            }
        }

    }

    public void setNewCraft() {
        if (recipeWrapper == null)
            return;
        Map<Item, Integer> count = getInventoryCount();

        if (isCraftingPotion && recipeWrapper.recipes.size() > 0) {

            RecipeWrapper.SingleRecipe recipe = recipeWrapper.canCraftPotionFromInventory(count, level, worldPosition);
            if (recipe == null)
                return;
            if (!(recipe.recipe.get(0) instanceof PotionIngredient)) {
                isCraftingPotion = false;
                return;
            }

            PotionIngredient potionIngred = (PotionIngredient) recipe.recipe.get(0);
            Ingredient itemIngred = recipe.recipe.get(1);
            List<ItemStack> needed = new ArrayList<>(Arrays.asList(itemIngred.getItems()));
            craftManager = new CraftingProgress(PotionUtils.getPotion(potionIngred.getStack()), needed, PotionUtils.getPotion(recipe.outputStack));
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            //BrewingRecipe
        } else {

            RecipeWrapper.SingleRecipe recipe = recipeWrapper.canCraftFromInventory(count);
            if (recipe != null) {
                craftManager = new CraftingProgress(recipe.outputStack.copy(), recipe.canCraftFromInventory(count), recipe.iRecipe);
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            }
        }

    }

    public void setRecipes(Player playerEntity, ItemStack stack) {
        RecipeWrapper recipes = new RecipeWrapper();
        if (stack.getItem() == Items.POTION) {
            for (BrewingRecipe r : ArsNouveauAPI.getInstance().getAllPotionRecipes()) {
                if (ItemStack.matches(stack, r.getOutput())) {
                    isCraftingPotion = true;
                    List<Ingredient> list = new ArrayList<>();
                    list.add(new PotionIngredient(r.getInput().getItems()[0]));
                    list.add(r.getIngredient());
                    recipes.addRecipe(list, r.getOutput(), null);
                }
            }
        } else {
            for (Recipe r : level.getServer().getRecipeManager().getRecipes()) {
                if (r.getResultItem() == null || r.getResultItem().getItem() != stack.getItem())
                    continue;

                if (r instanceof ShapedRecipe) {
                    ShapedHelper helper = new ShapedHelper((ShapedRecipe) r);
                    for (List<Ingredient> iList : helper.getPossibleRecipes()) {
                        recipes.addRecipe(iList, r.getResultItem(), r);
                    }
                }

                if (r instanceof ShapelessRecipe)
                    recipes.addRecipe(r.getIngredients(), r.getResultItem(), r);

            }
            if (!recipes.recipes.isEmpty())
                isCraftingPotion = false;
        }
        if (!recipes.recipes.isEmpty()) {
            this.recipeWrapper = recipes;
            this.craftingItem = stack.copy();
        }

        if ((recipes.recipes.isEmpty() || recipeWrapper == null || recipeWrapper.recipes.isEmpty()) && playerEntity != null) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.wixie.no_recipe"));
        } else if (playerEntity != null) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.wixie.recipe_set"));
        }
        setChanged();
    }


    public void updateInventories() {
        inventories = new ArrayList<>();
        for (BlockPos bPos : BlockPos.betweenClosed(worldPosition.north(6).east(6).below(2), worldPosition.south(6).west(6).above(2))) {
            if (level.getBlockEntity(bPos) instanceof Container)
                inventories.add(bPos.immutable());
        }
        setChanged();
    }

    public @Nullable BlockPos findPotionStorage(Potion passedPot) {
        AtomicReference<BlockPos> foundPod = new AtomicReference<>();
        AtomicBoolean foundOptimal = new AtomicBoolean(false);
        BlockPos.withinManhattanStream(worldPosition.below(2), 4, 3, 4).forEach(bPos -> {
            if (!foundOptimal.get() && level.getBlockEntity(bPos) instanceof PotionJarTile tile) {
                if (tile.canAcceptNewPotion() || tile.isMixEqual(passedPot)) {
                    if (tile.getMaxFill() - tile.getAmount() >= 300) {
                        if (tile.isMixEqual(passedPot) && tile.getAmount() >= 0) {
                            foundOptimal.set(true);
                            foundPod.set(bPos.immutable());
                        }
                        if (foundPod.get() == null)
                            foundPod.set(bPos.immutable());
                    }
                }
            }
        });

        return foundPod.get();
    }

    public static @Nullable BlockPos findNeededPotion(Potion passedPot, int amount, Level level, BlockPos worldPosition) {
        AtomicReference<BlockPos> foundPod = new AtomicReference<>();
        BlockPos.withinManhattanStream(worldPosition.below(2), 4, 3, 4).forEach(bPos -> {
            if (foundPod.get() == null && level.getBlockEntity(bPos) instanceof PotionJarTile tile) {
                if (tile.getAmount() >= amount && tile.isMixEqual(passedPot)) {
                    foundPod.set(bPos.immutable());
                }
            }
        });
        return foundPod.get();
    }

    public @Nullable BlockPos findNeededPotion(Potion passedPot, int amount) {
        return findNeededPotion(passedPot, amount, level, worldPosition);
    }

    public void spawnFlyingItem(BlockPos from, ItemStack stack) {
        EntityFlyingItem flyingItem = new EntityFlyingItem(level, from.above(), worldPosition);
        flyingItem.getEntityData().set(EntityFlyingItem.HELD_ITEM, stack.copy());
        level.addFreshEntity(flyingItem);
    }


    public void convertedEffect() {
        super.convertedEffect();
        if (tickCounter >= 120 && !level.isClientSide) {
            converted = true;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, false).setValue(SummoningTile.CONVERTED, true));
            EntityWixie wixie = new EntityWixie(level, true, worldPosition);
            wixie.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5);
            level.addFreshEntity(wixie);
            ParticleUtil.spawnPoof((ServerLevel) level, worldPosition.above());
            entityID = wixie.getId();
            tickCounter = 0;
            setChanged();
            return;
        }
        if (tickCounter % 10 == 0 && !level.isClientSide) {
            RandomSource r = level.random;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(level, worldPosition.offset(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), worldPosition, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            level.addFreshEntity(proj1);
        }
    }

    private Map<Item, Integer> getInventoryCount() {
        List<BlockPos> stale = new ArrayList<>();
        Map<Item, Integer> itemsAvailable = new HashMap<>();
        if (inventories == null)
            return itemsAvailable;
        for (BlockPos p : inventories) {
            if (level.getBlockEntity(p) instanceof Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack stack = container.getItem(i);
                    if (stack == null) {
                        System.out.println("======");
                        System.out.println("A MOD IS RETURNING A NULL STACK. THIS IS NOT ALLOWED YOU NERD. TELL THIS MOD AUTHOR TO FIX IT");
                        System.out.println(container.toString());
                        System.out.println("AT POS " + p.toString());
                        continue;
                    }
                    if (!itemsAvailable.containsKey(stack.getItem())) {
                        itemsAvailable.put(stack.getItem(), stack.getCount());
                        continue;
                    }
                    itemsAvailable.put(stack.getItem(), itemsAvailable.get(stack.getItem()) + stack.getCount());
                }
            } else {
                stale.add(p);
            }
        }

        for (BlockPos p : stale) {
            inventories.remove(p);
        }
        return itemsAvailable;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("crafting")) {
            this.craftingItem = ItemStack.of(compound.getCompound("crafting"));
        }

        craftManager = CraftingProgress.read(compound);
        this.entityID = compound.getInt("entityid");
        this.hasSource = compound.getBoolean("hasmana");
        this.isOff = compound.getBoolean("off");
        this.isCraftingPotion = compound.getBoolean("isPotion");
        needsPotionStorage = compound.getBoolean("storage");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        if (craftingItem != null) {
            CompoundTag itemTag = new CompoundTag();
            craftingItem.save(itemTag);
            compound.put("crafting", itemTag);
        }
        if (craftManager != null)
            craftManager.write(compound);

        compound.putInt("entityid", entityID);
        compound.putBoolean("hasmana", hasSource);
        compound.putBoolean("off", isOff);
        compound.putBoolean("isPotion", isCraftingPotion);
        compound.putBoolean("storage", needsPotionStorage);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {

        if (craftingItem == null)
            return;

        if (isOff) {
            tooltip.add(Component.translatable("ars_nouveau.tooltip.turned_off"));
        }

        if (!isCraftingPotion) {
            tooltip.add(Component.literal(
                    Component.translatable("ars_nouveau.wixie.crafting").getString() +
                            Component.translatable(craftingItem.getDescriptionId()).getString())
            );
        } else if (this.craftManager != null && this.craftManager.isPotionCrafting()) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, this.craftManager.potionOut);
            tooltip.add(Component.literal(Component.translatable("ars_nouveau.wixie.crafting").getString() + potionStack.getHoverName()));
            PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
        }

        if (!hasSource) {
            tooltip.add(Component.translatable("ars_nouveau.wixie.need_mana"));
        }
        if (this.craftManager != null && !this.craftManager.neededItems.isEmpty())
            tooltip.add(Component.literal(
                    Component.translatable("ars_nouveau.wixie.needs").getString() +
                            Component.translatable(this.craftManager.neededItems.get(0).getDescriptionId()).getString()));

        if (this.craftManager != null && this.craftManager.isPotionCrafting() && !this.craftManager.hasObtainedPotion()) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, this.craftManager.getPotionNeeded());
            tooltip.add(Component.literal(Component.translatable("ars_nouveau.wixie.needs").getString() + potionStack.getHoverName().getString()));
        }
        if (this.needsPotionStorage)
            tooltip.add(Component.translatable("ars_nouveau.wixie.needs_storage"));

    }

    public static class CraftingProgress {
        public ItemStack outputStack;
        public List<ItemStack> neededItems;
        public List<ItemStack> remainingItems;
        private Potion potionNeeded;
        public Potion potionOut;
        public boolean isPotionCrafting;
        private boolean hasObtainedPotion;

        public CraftingProgress() {
            outputStack = ItemStack.EMPTY;
            neededItems = new ArrayList<>();
            remainingItems = new ArrayList<>();
        }

        public CraftingProgress(Potion potionNeeded, List<ItemStack> itemsNeeded, Potion potionOut) {
            this.setPotionNeeded(potionNeeded);
            this.potionOut = potionOut;
            neededItems = itemsNeeded;
            remainingItems = itemsNeeded;
            isPotionCrafting = true;
            setHasObtainedPotion(false);
            outputStack = ItemStack.EMPTY;
        }

        public CraftingProgress(ItemStack outputStack, List<ItemStack> neededItems, Recipe recipe) {
            CraftingContainer inventory = new CraftingContainer(new AbstractContainerMenu(null, -1) {
                @Override
                public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
                    return ItemStack.EMPTY;
                }

                public boolean stillValid(Player playerIn) {
                    return false;
                }
            }, 3, 3);
            for (int i = 0; i < neededItems.size(); i++) {
                inventory.setItem(i, neededItems.get(i).copy());
            }
            this.remainingItems = recipe.getRemainingItems(inventory);
            this.outputStack = outputStack;
            this.neededItems = neededItems;
        }

        public ItemStack getNextItem() {
            return !neededItems.isEmpty() ? neededItems.get(0) : ItemStack.EMPTY;
        }

        public boolean giveItem(Item i) {
            if (isDone())
                return false;

            ItemStack stackToRemove = ItemStack.EMPTY;
            for (ItemStack stack : neededItems) {
                if (stack.getItem() == i) {
                    stackToRemove = stack;
                    break;
                }
            }
            return neededItems.remove(stackToRemove);
        }

        public boolean isDone() {
            return !isPotionCrafting ? neededItems.isEmpty() : hasObtainedPotion() && neededItems.isEmpty();
        }

        public boolean isPotionCrafting() {
            return isPotionCrafting || (potionOut != Potions.EMPTY && potionOut != null);
        }


        public void write(CompoundTag tag) {
            CompoundTag stack = new CompoundTag();
            outputStack.save(stack);
            tag.put("output_stack", stack);
            NBTUtil.writeItems(tag, "progress", neededItems);
            NBTUtil.writeItems(tag, "refund", remainingItems);
            CompoundTag outputTag = new CompoundTag();
            PotionUtil.addPotionToTag(potionOut, outputTag);
            tag.put("potionout", outputTag);

            CompoundTag neededTag = new CompoundTag();
            PotionUtil.addPotionToTag(getPotionNeeded(), neededTag);
            tag.put("potionNeeded", neededTag);
            tag.putBoolean("gotPotion", hasObtainedPotion());
            tag.putBoolean("isPotionCraft", isPotionCrafting);
        }

        public static CraftingProgress read(CompoundTag tag) {
            CraftingProgress progress = new CraftingProgress();
            progress.outputStack = ItemStack.of(tag.getCompound("output_stack"));
            progress.neededItems = NBTUtil.readItems(tag, "progress");
            progress.remainingItems = NBTUtil.readItems(tag, "refund");
            progress.potionOut = PotionUtils.getPotion(tag.getCompound("potionout"));
            progress.setPotionNeeded(PotionUtils.getPotion(tag.getCompound("potionNeeded")));
            progress.setHasObtainedPotion(tag.getBoolean("gotPotion"));
            progress.isPotionCrafting = tag.getBoolean("isPotionCraft");
            return progress;
        }

        public Potion getPotionNeeded() {
            return potionNeeded;
        }

        public void setPotionNeeded(Potion potionNeeded) {
            this.potionNeeded = potionNeeded;
        }

        public boolean hasObtainedPotion() {
            return hasObtainedPotion || potionNeeded == Potions.WATER;
        }

        public void setHasObtainedPotion(boolean hasObtainedPotion) {
            this.hasObtainedPotion = hasObtainedPotion;
        }
    }
}
