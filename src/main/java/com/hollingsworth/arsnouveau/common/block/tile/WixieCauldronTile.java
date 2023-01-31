package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.api.recipe.*;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nullable;
import java.util.*;

public class WixieCauldronTile extends SummoningTile implements ITooltipProvider {

    public List<BlockPos> inventories;
    public ItemStack craftingItem;
    public int entityID;
    public boolean hasSource;
    public boolean isCraftingPotion;
    public boolean needsPotionStorage;
    MultiRecipeWrapper recipeWrapper;
    public CraftingProgress craftManager = new CraftingProgress();

    public WixieCauldronTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.WIXIE_CAULDRON_TYPE, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide())
            return;

        if (!hasSource && level.getGameTime() % 5 == 0 && SourceUtil.takeSourceWithParticles(worldPosition, level, 6, 50) != null) {
            this.hasSource = true;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, true));
            setChanged();
        }

        if (!hasSource) return;

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
        if(!craftManager.isDone())
            return;

        if (!craftManager.isPotionCrafting()) {
            if(craftManager.outputStack.isEmpty()){
                setNewCraft();
                return;
            }
            craftManager.dropCompletedItems(this);
            this.hasSource = false;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, false));
            level.playSound(null, getBlockPos(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.BLOCKS, 0.15f, 0.6f);
            craftManager = new CraftingProgress();
            setNewCraft();
            return;
        }

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

        if (level.getBlockEntity(jarPos) instanceof PotionJarTile jar) {
            needsPotionStorage = false;
            jar.add(new PotionData(craftManager.potionOut),300);
            ParticleColor color2 = ParticleColor.fromInt(jar.getColor());
            EntityFlyingItem flying = new EntityFlyingItem(level, new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ()+ 0.5),
                    new Vec3(jarPos.getX() + 0.5, jarPos.getY(), jarPos.getZ() + 0.5),
                    Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                    .withNoTouch();
            level.addFreshEntity(flying);
            this.hasSource = false;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(WixieCauldron.FILLED, false));
            craftManager = new CraftingProgress();
            setNewCraft();
        }

    }

    public void setNewCraft() {
        if (recipeWrapper == null)
            return;
        Map<Item, Integer> count = getInventoryCount();

        IRecipeWrapper.InstructionsForRecipe instructions = recipeWrapper.canCraft(count, level, worldPosition);
        if (instructions != null && !recipeWrapper.isEmpty() && (instructions.recipe().recipeIngredients.get(0) instanceof PotionIngredient potionIngred)) {

            Ingredient itemIngred = instructions.recipe().recipeIngredients.get(1);
            List<ItemStack> needed = new ArrayList<>(Arrays.asList(itemIngred.getItems()));
            craftManager = new CraftingProgress(PotionUtils.getPotion(potionIngred.getStack()), needed, PotionUtils.getPotion(instructions.recipe().outputStack));
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            //BrewingRecipe
        } else if (instructions != null) {
            craftManager = new CraftingProgress(instructions.recipe().outputStack.copy(), instructions.itemsNeeded(), instructions.recipe().iRecipe);
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        }
    }

    public void setRecipes(Player playerEntity, ItemStack stack) {
        MultiRecipeWrapper recipes;
        if (stack.getItem() == Items.POTION) {
            recipes = new PotionRecipeWrapper();
            for (BrewingRecipe r : ArsNouveauAPI.getInstance().getAllPotionRecipes()) {
                if (ItemStack.matches(stack, r.getOutput())) {
                    List<Ingredient> list = new ArrayList<>();
                    list.add(new PotionIngredient(r.getInput().getItems()[0]));
                    list.add(r.getIngredient());
                    recipes.addRecipe(list, r.getOutput(), null);
                }
            }
        } else {
            recipes = new MultiRecipeWrapper();
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
        }
        if (!recipes.isEmpty()) {
            this.recipeWrapper = recipes;
            this.craftingItem = stack.copy();
        }

        if ((recipes.isEmpty() || recipeWrapper == null || recipeWrapper.recipes.isEmpty()) && playerEntity != null) {
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
        for(BlockPos bPos : BlockPos.withinManhattan(worldPosition.below(2), 4, 3, 4)){
            if (level.getBlockEntity(bPos) instanceof PotionJarTile tile && tile.canAccept(new PotionData(passedPot), 300)) {
                return bPos.immutable();
            }
        }
        return null;
    }

    public static @Nullable BlockPos findNeededPotion(Potion passedPot, int amount, Level level, BlockPos worldPosition) {
        for(BlockPos bPos : BlockPos.withinManhattan(worldPosition.below(2), 4, 3, 4)){
            if (level.getBlockEntity(bPos) instanceof PotionJarTile tile &&
                    tile.getAmount() >= amount &&
                    tile.getData().areSameEffects(new PotionData(passedPot))) {
                return bPos.immutable();
            }
        }
        return null;
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

        if (this.craftManager != null && !this.craftManager.isPotionCrafting()) {
            tooltip.add(Component.literal(
                    Component.translatable("ars_nouveau.wixie.crafting").getString() +
                            Component.translatable(craftingItem.getDescriptionId()).getString())
            );
        } else if (this.craftManager != null && this.craftManager.isPotionCrafting()) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, this.craftManager.potionOut);
            tooltip.add(Component.literal(Component.translatable("ars_nouveau.wixie.crafting").getString() + potionStack.getHoverName().getString()));
            PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
        }

        if (!hasSource) {
            tooltip.add(Component.translatable("ars_nouveau.wixie.need_mana").withStyle(ChatFormatting.GOLD));
        }
        if (this.craftManager != null && !this.craftManager.neededItems.isEmpty())
            tooltip.add(Component.literal(
                    Component.translatable("ars_nouveau.wixie.needs").getString() +
                            Component.translatable(this.craftManager.neededItems.get(0).getDescriptionId()).getString()).withStyle(ChatFormatting.GOLD));

        if (this.craftManager != null && this.craftManager.isPotionCrafting() && !this.craftManager.hasObtainedPotion()) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, this.craftManager.getPotionNeeded());
            tooltip.add(Component.literal(Component.translatable("ars_nouveau.wixie.needs").getString() + potionStack.getHoverName().getString()).withStyle(ChatFormatting.GOLD));
        }
        if (this.needsPotionStorage)
            tooltip.add(Component.translatable("ars_nouveau.wixie.needs_storage").withStyle(ChatFormatting.GOLD));

    }

}
