package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.recipe.RecipeWrapper;
import com.hollingsworth.arsnouveau.api.recipe.ShapedHelper;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class WixieCauldronTile extends TileEntity implements ITickableTileEntity, ITooltipProvider {

    public List<BlockPos> inventories;
    public Item craftingItem;

    int tickCounter;
    boolean converted;
    public int entityID;

    public boolean hasMana;
    public boolean isOff;

    RecipeWrapper recipeWrapper;
    public CraftingProgress craftManager = new CraftingProgress();
    public WixieCauldronTile() {
        super(BlockRegistry.WIXIE_CAULDRON_TYPE);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;

        if (!converted) {
            convertedEffect();
            return;
        }

        if(!hasMana && world.getGameTime() % 5 == 0){
            if(ManaUtil.takeManaNearbyWithParticles(pos, world, 6, 50) != null) {
                this.hasMana = true;
                world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, true));
            }
        }

        if(!hasMana)
            return;

        if(this.recipeWrapper == null && craftingItem != null)
            setRecipes(null, new ItemStack(craftingItem));

        if (world.getGameTime() % 100 == 0) {
            updateInventories(); // Update the inventories available to use

           // attemptFinish();
        }

    }

    public boolean hasWixie(){
        return !this.converted || world.getEntityByID(entityID) != null;
    }

    public boolean isCraftingDone(){
        return craftManager.isDone();
    }

    public boolean giveItem(ItemStack stack) {
        return craftManager.giveItem(stack.getItem());
    }

    public void attemptFinish(){
        if(craftManager.isDone()){
            if(!craftManager.outputStack.isEmpty()) {
                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), craftManager.outputStack.copy()));
                this.hasMana = false;
                world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, false));
            }
            for(ItemStack i : craftManager.remainingItems){
                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(),  i.copy()));

            }

            craftManager = new CraftingProgress();
            setNewCraft();

        }

    }

    public void setNewCraft(){
        if(recipeWrapper == null)
            return;
        Map<Item, Integer> count = getInventoryCount();
        RecipeWrapper.SingleRecipe recipe = recipeWrapper.canCraftFromInventory(count);
        if(recipe != null) {
            craftManager = new CraftingProgress(recipe.outputStack.copy(), recipe.canCraftFromInventory(count), recipe.iRecipe);
        }
    }

    public void setRecipes(PlayerEntity playerEntity, ItemStack stack){
        this.craftingItem = stack.getItem();
        RecipeWrapper recipes = new RecipeWrapper();
        for(IRecipe r : world.getServer().getRecipeManager().getRecipes()){
            if(r.getRecipeOutput().getItem() != craftingItem)
                continue;

            if(r instanceof ShapedRecipe){
                ShapedHelper helper = new ShapedHelper((ShapedRecipe) r);
                for (List<Ingredient> iList : helper.getPossibleRecipes()) {
                    recipes.addRecipe(iList, r.getRecipeOutput(), r);
                }
            }

            if(r instanceof ShapelessRecipe)
                recipes.addRecipe(r.getIngredients(), r.getRecipeOutput(), r);

        }
        this.recipeWrapper = recipes;
        if(recipeWrapper.recipes.isEmpty() && playerEntity != null){
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.wixie.no_recipe"));
            this.craftingItem = null;
        }else if(playerEntity != null){
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.wixie.recipe_set"));
        }
    }

    public void updateInventories() {
        inventories = new ArrayList<>();
        for (BlockPos bPos : BlockPos.getAllInBoxMutable(pos.north(6).east(6).down(2), pos.south(6).west(6).up(2))) {
            if (world.getTileEntity(bPos) instanceof IInventory)
                inventories.add(bPos.toImmutable());
        }
    }

    public void spawnFlyingItem(BlockPos from, ItemStack stack) {
        EntityFlyingItem flyingItem = new EntityFlyingItem(world, from.up(), pos);
        flyingItem.getDataManager().set(EntityFlyingItem.HELD_ITEM, stack.copy());
        world.addEntity(flyingItem);
    }


    public void convertedEffect() {
        tickCounter++;
        if (tickCounter >= 120 && !world.isRemote) {
            converted = true;
            world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, false).with(WixieCauldron.CONVERTED, true));
            EntityWixie wixie = new EntityWixie(world, true, pos);
            wixie.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            world.addEntity(wixie);
            ParticleUtil.spawnPoof((ServerWorld) world, pos.up());
            entityID = wixie.getEntityId();
            tickCounter = 0;
            return;
        }
        if (tickCounter % 10 == 0 && !world.isRemote) {
            Random r = world.rand;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(world, pos.add(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), pos, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            world.addEntity(proj1);
        }
    }

    private Map<Item, Integer> getInventoryCount(){
        List<BlockPos> stale = new ArrayList<>();
        Map<Item, Integer> itemsAvailable = new HashMap<>();
        if(inventories == null)
            return itemsAvailable;
        for(BlockPos p : inventories){
            if(world.getTileEntity(p) instanceof IInventory){
                IInventory inventory = (IInventory) world.getTileEntity(p);
                for(int i = 0; i < inventory.getSizeInventory(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(!itemsAvailable.containsKey(stack.getItem())) {
                        itemsAvailable.put(stack.getItem(), stack.getCount());
                        continue;
                    }
                    itemsAvailable.put(stack.getItem(), itemsAvailable.get(stack.getItem()) + stack.getCount());
                }
            }else {
                stale.add(p);
            }
        }

        for(BlockPos p : stale){
            inventories.remove(p);
        }
        return itemsAvailable;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        if(compound.contains("crafting")) {
            this.craftingItem = ItemStack.read(compound.getCompound("crafting")).getItem();
        }
        this.converted = compound.getBoolean("converted");

        craftManager = CraftingProgress.read(compound);
        this.entityID = compound.getInt("entityid");
        this.hasMana = compound.getBoolean("hasmana");
        this.isOff = compound.getBoolean("off");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("converted", converted);

        if(craftingItem != null){
            CompoundNBT itemTag = new CompoundNBT();
            new ItemStack(craftingItem).write(itemTag);
            compound.put("crafting", itemTag);
        }
        if(craftManager != null)
            craftManager.write(compound);

        compound.putInt("entityid", entityID);
        compound.putBoolean("hasmana",hasMana);
        compound.putBoolean("off", isOff);
        return super.write(compound);
    }

    @Override
    public List<String> getTooltip() {
        if(craftingItem == null)
            return new ArrayList<>();
        List<String> strings = new ArrayList<>();
        strings.add("Crafting: " +new TranslationTextComponent(craftingItem.getTranslationKey()).getString());
        if(!hasMana){
            strings.add("Mana needed.");
        }
        if(this.craftManager != null && !this.craftManager.neededItems.isEmpty())
            strings.add("Needs: " + new TranslationTextComponent(this.craftManager.neededItems.get(0).getTranslationKey()).getString());
        return strings;
    }

    public static class CraftingProgress{
        public ItemStack outputStack;
        public List<ItemStack> neededItems;
        public List<ItemStack> remainingItems;
        public CraftingProgress(){
            outputStack = ItemStack.EMPTY;
            neededItems = new ArrayList<>();
            remainingItems = new ArrayList<>();
        }

        public CraftingProgress(ItemStack outputStack, List<ItemStack> neededItems, IRecipe recipe){
            CraftingInventory inventory = new CraftingInventory(new Container((ContainerType)null, -1) {
                public boolean canInteractWith(PlayerEntity playerIn) {
                    return false;
                }
            }, 3, 3);
            for(int i = 0; i < neededItems.size(); i++) {
                inventory.setInventorySlotContents(i, neededItems.get(i).copy());
            }
            this.remainingItems = recipe.getRemainingItems(inventory);

            this.outputStack = outputStack;
            this.neededItems = neededItems;
        }

        public ItemStack getNextItem(){
            return !neededItems.isEmpty() ? neededItems.get(0) : ItemStack.EMPTY;
        }

        public boolean giveItem(Item i){
            if(isDone())
                return false;

            ItemStack stackToRemove = ItemStack.EMPTY;
            for(ItemStack stack : neededItems){
                if(stack.getItem() == i){
                    stackToRemove = stack;
                    break;
                }
            }
            return neededItems.remove(stackToRemove);
        }

        public boolean isDone(){
            return neededItems.isEmpty();
        }

        public void write(CompoundNBT tag){
            CompoundNBT stack = new CompoundNBT();
            outputStack.write(stack);
            tag.put("output_stack", stack);
            NBTUtil.writeItems(tag, "progress", neededItems);
            NBTUtil.writeItems(tag, "refund", remainingItems);
        }

        public static CraftingProgress read(CompoundNBT tag){
            CraftingProgress progress = new CraftingProgress();
            progress.outputStack = ItemStack.read(tag.getCompound("output_stack"));
            progress.neededItems = NBTUtil.readItems(tag,"progress");
            progress.remainingItems = NBTUtil.readItems(tag, "refund");
            return progress;
        }

    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(world.getBlockState(pos),pkt.getNbtCompound());
    }
}
