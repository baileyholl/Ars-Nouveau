package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.recipe.ShapedHelper;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class WixieCauldronTile extends TileEntity implements ITickableTileEntity {
    List<ItemStack> itemsToCraft; // List of items to craft
    List<ItemStack> itemsRequired; // Items still required for crafting
    HashMap<ItemStack,List<List<Ingredient>>> validRecipes;
    ItemStack craftingItem;
    public List<BlockPos> inventories;
    int itemCounter;
    int recipeItemCounter;
    boolean converted;

    public WixieCauldronTile() {
        super(BlockRegistry.WIXIE_CAULDRON_TYPE);
    }

    @Override
    public void tick() {
        if (!converted) {
            convertedEffect();
            return;
        }
        // Pick our next recipe to make and update nearby inventories for wixies to access
        if (world.getGameTime() % 100 == 0 && !world.isRemote) {
            updateCrafting();
            updateInventories();
        }

        if (itemsRequired == null)
            return;

        if (itemsRequired.isEmpty() && craftingItem != null && !craftingItem.isEmpty()) {
            ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            entity.setItem(craftingItem);
            craftingItem = ItemStack.EMPTY;
        }

    }

    public ItemStack getNextRequiredItem() {
        if (itemsRequired == null || itemsRequired.isEmpty())
            return ItemStack.EMPTY;
        if (recipeItemCounter > itemsRequired.size())
            recipeItemCounter = 0;
        ItemStack stack = itemsRequired.get(recipeItemCounter);
        recipeItemCounter++;
        return stack;
    }

    public boolean giveItem(ItemStack stack) {
        return itemsToCraft.remove(stack);
    }

    public void updateInventories() {
        if (inventories == null)
            inventories = new ArrayList<>();

        for (BlockPos bPos : BlockPos.getAllInBoxMutable(pos.north(6).down(2), pos.south(6).up(2))) {
            if (world.getTileEntity(bPos) instanceof IInventory)
                inventories.add(bPos.toImmutable());
        }
    }

    public void updateCrafting() {
        if (itemsToCraft == null || itemsToCraft.isEmpty() || craftingItem != null)
            return;
        itemCounter++;
        if (itemCounter > itemsToCraft.size())
            itemCounter = 0;

        craftingItem = itemsToCraft.get(itemCounter).copy();

    }


    public void spawnFlyingItem(BlockPos from) {
        EntityFlyingItem flyingItem = new EntityFlyingItem(world, from, pos.down());
        world.addEntity(flyingItem);
    }


    public void convertedEffect() {

        itemCounter++;
        if (itemCounter >= 120 && !world.isRemote) {
            converted = true;
            world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, false).with(WixieCauldron.CONVERTED, true));
            EntityWixie wixie = new EntityWixie(world, true, pos);
            wixie.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            world.addEntity(wixie);
            ParticleUtil.spawnPoof((ServerWorld) world, pos.up());
            itemCounter = 0;
            return;
        }
        if (itemCounter % 10 == 0 && !world.isRemote) {
            Random r = world.rand;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(world, pos.add(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), pos, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            world.addEntity(proj1);
        }
    }

    public void setRecipes(ItemStack stack){
        validRecipes = new HashMap<>();

        List<List<Ingredient>> recipes;
        if(stack.getItem() == ItemsRegistry.ALLOW_ITEM_SCROLL){
            ItemScroll itemScroll = (ItemScroll)stack.getItem();
            List<ItemStack> items = itemScroll.getItems(stack);
            for(ItemStack item : items){
                recipes = getRecipes(item);
                if(!recipes.isEmpty())
                    validRecipes.put(item, recipes);
            }
        }
        System.out.println(validRecipes.toString());
        for(ItemStack stack1 : validRecipes.keySet()){
            for(List<Ingredient> ingreds : validRecipes.get(stack1)){
                for(Ingredient i : ingreds){
                    System.out.println(Arrays.toString(i.getMatchingStacks()));
                }
            }
        }
        System.out.println(validRecipes.toString());
    }

    private  List<List<Ingredient>> getRecipes(ItemStack stack) {
        List<List<Ingredient>> ingredientList = new ArrayList<>();

        for (IRecipe r : world.getServer().getRecipeManager().getRecipes()) {
            if (!r.getRecipeOutput().isItemEqual(stack))
                continue;
            if (r instanceof ShapedRecipe && r.getRecipeOutput().isItemEqual(stack)) {
                ShapedHelper helper = new ShapedHelper((ShapedRecipe) r);

                for (List<Ingredient> iList : helper.matches()) {
                    if (!ingredientList.contains(iList))
                        ingredientList.add(iList);
                }
            }
            if (r instanceof ShapelessRecipe && !ingredientList.contains(r.getIngredients())) {
                ingredientList.add(r.getIngredients());
            }
        }
        return ingredientList;
    }


    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        itemsToCraft = NBTUtil.readItems(compound, "to_craft_");
        itemsRequired = NBTUtil.readItems(compound, "required_");
        this.converted = compound.getBoolean("converted");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {

        if (itemsToCraft != null)
            NBTUtil.writeItems(compound, "to_craft_", itemsToCraft);
        if (itemsRequired != null)
            NBTUtil.writeItems(compound, "required_", itemsRequired);
        compound.putBoolean("converted", converted);
        return super.write(compound);
    }
}
