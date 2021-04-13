package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
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
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
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
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WixieCauldronTile extends TileEntity implements ITickableTileEntity, ITooltipProvider {

    public List<BlockPos> inventories;
    public ItemStack craftingItem;

    int tickCounter;
    boolean converted;
    public int entityID;

    public boolean hasMana;
    public boolean isOff;
    public boolean isCraftingPotion;
    public boolean needsPotionStorage;
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
            setRecipes(null, craftingItem);

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

    public boolean needsPotion(){return craftManager.isPotionCrafting && !craftManager.hasObtainedPotion();}

    public Potion getNeededPotion(){
        return craftManager.getPotionNeeded();
    }

    public void givePotion(){
        craftManager.setHasObtainedPotion(true);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public boolean giveItem(ItemStack stack) {
        boolean res = craftManager.giveItem(stack.getItem());
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        return res;
    }

    public void attemptFinish(){

        if(craftManager.isDone()){
            if(!isCraftingPotion) {

                if (!craftManager.outputStack.isEmpty()) {
                    world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), craftManager.outputStack.copy()));
                    this.hasMana = false;
                    world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, false));
                }
                for (ItemStack i : craftManager.remainingItems) {
                    world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), i.copy()));

                }

                craftManager = new CraftingProgress();
                setNewCraft();
            }else{

                if(craftManager.potionOut == null){
                    setNewCraft();
                    return;
                }


                BlockPos jarPos = findPotionStorage(craftManager.potionOut);
                if(jarPos == null){
                    if(!needsPotionStorage) {
                        needsPotionStorage = true;
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    }
                    return;
                }

                if(world.getTileEntity(jarPos) instanceof PotionJarTile){
                    needsPotionStorage = false;
                    ((PotionJarTile) world.getTileEntity(jarPos)).addAmount(craftManager.potionOut, 300);
                    int color = ((PotionJarTile) world.getTileEntity(jarPos)).getColor();
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = (color >> 0) & 0xFF;
                    int a = (color >> 24) & 0xFF;
                    EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, pos, jarPos, r,g,b);
                    world.addEntity(aoeProjectile);
                    this.hasMana = false;
                    world.setBlockState(pos, world.getBlockState(pos).with(WixieCauldron.FILLED, false));
                    craftManager = new CraftingProgress();
                    setNewCraft();
                }
            }
        }

    }

    public void setNewCraft(){
        if(recipeWrapper == null)
            return;
        Map<Item, Integer> count = getInventoryCount();

        if(isCraftingPotion && recipeWrapper.recipes.size() > 0){

            RecipeWrapper.SingleRecipe recipe = (RecipeWrapper.SingleRecipe) recipeWrapper.recipes.toArray()[0];
            PotionIngredient potionIngred = (PotionIngredient) recipe.recipe.get(0);
            Ingredient itemIngred = recipe.recipe.get(1);
            List<ItemStack> needed = new ArrayList<ItemStack>(Arrays.asList(itemIngred.getMatchingStacks()));
            craftManager = new CraftingProgress(PotionUtils.getPotionFromItem(potionIngred.getStack()),needed, PotionUtils.getPotionFromItem(recipe.outputStack));
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            //BrewingRecipe
        }else {

            RecipeWrapper.SingleRecipe recipe = recipeWrapper.canCraftFromInventory(count);
            if (recipe != null) {
                craftManager = new CraftingProgress(recipe.outputStack.copy(), recipe.canCraftFromInventory(count), recipe.iRecipe);
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }

    }

    public void setRecipes(PlayerEntity playerEntity, ItemStack stack){
        this.craftingItem = stack;
        RecipeWrapper recipes = new RecipeWrapper();
        if(craftingItem.getItem() == Items.POTION){
            for(BrewingRecipe r : ArsNouveauAPI.getInstance().getAllPotionRecipes()){
                if(ItemStack.areItemStacksEqual(stack, r.getOutput())) {
                    isCraftingPotion = true;
                    List<Ingredient> list = new ArrayList<>();
                    list.add(new PotionIngredient(r.getInput().getMatchingStacks()[0]));
                    list.add(r.getIngredient());
                    recipes.addRecipe(list, r.getOutput(), null);
                    break;
                }
            }
        }else {
            for (IRecipe r : world.getServer().getRecipeManager().getRecipes()) {
                if (r.getRecipeOutput().getItem() != craftingItem.getItem())
                    continue;

                if (r instanceof ShapedRecipe) {
                    ShapedHelper helper = new ShapedHelper((ShapedRecipe) r);
                    for (List<Ingredient> iList : helper.getPossibleRecipes()) {
                        recipes.addRecipe(iList, r.getRecipeOutput(), r);
                    }
                }

                if (r instanceof ShapelessRecipe)
                    recipes.addRecipe(r.getIngredients(), r.getRecipeOutput(), r);

            }
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

    public @Nullable BlockPos findPotionStorage(Potion passedPot){
        AtomicReference<BlockPos> foundPod = new AtomicReference<>();
        AtomicBoolean foundOptimal = new AtomicBoolean(false);
        BlockPos.getProximitySortedBoxPositions(pos.down(2), 4, 3,4).forEach(bPos ->{
            if (!foundOptimal.get() && world.getTileEntity(bPos) instanceof PotionJarTile) {
                PotionJarTile tile = (PotionJarTile) world.getTileEntity(bPos);
                if(tile.canAcceptNewPotion() || tile.isMixEqual(passedPot)){
                    if(tile.getMaxFill() - tile.getCurrentFill() >= 300) {
                        if(tile.isMixEqual(passedPot) && tile.getAmount() >= 0) {
                            foundOptimal.set(true);
                            foundPod.set(bPos.toImmutable());
                        }
                        if(foundPod.get() == null)
                            foundPod.set(bPos.toImmutable());
                    }
                }
            }
        });

        return foundPod.get();
    }

    public @Nullable BlockPos findNeededPotion(Potion passedPot, int amount){
        AtomicReference<BlockPos> foundPod = new AtomicReference<>();
        BlockPos.getProximitySortedBoxPositions(pos.down(2), 4, 3,4).forEach(bPos ->{
            if (foundPod.get() == null && world.getTileEntity(bPos) instanceof PotionJarTile) {
                PotionJarTile tile = (PotionJarTile) world.getTileEntity(bPos);
                if(tile.getCurrentFill() >= amount && tile.isMixEqual(passedPot)){
                    foundPod.set(bPos.toImmutable());
                }
            }
        });
        return foundPod.get();
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
            this.craftingItem = ItemStack.read(compound.getCompound("crafting"));
        }
        this.converted = compound.getBoolean("converted");

        craftManager = CraftingProgress.read(compound);
        this.entityID = compound.getInt("entityid");
        this.hasMana = compound.getBoolean("hasmana");
        this.isOff = compound.getBoolean("off");
        this.isCraftingPotion = compound.getBoolean("isPotion");
        needsPotionStorage = compound.getBoolean("storage");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("converted", converted);

        if(craftingItem != null){
            CompoundNBT itemTag = new CompoundNBT();
            craftingItem.write(itemTag);
            compound.put("crafting", itemTag);
        }
        if(craftManager != null)
            craftManager.write(compound);

        compound.putInt("entityid", entityID);
        compound.putBoolean("hasmana",hasMana);
        compound.putBoolean("off", isOff);
        compound.putBoolean("isPotion", isCraftingPotion);
        compound.putBoolean("storage", needsPotionStorage);
        return super.write(compound);
    }

    @Override
    public List<String> getTooltip() {

        if(craftingItem == null)
            return new ArrayList<>();
        List<String> strings = new ArrayList<>();



        if(!isCraftingPotion){
            strings.add(new TranslationTextComponent("ars_nouveau.wixie.crafting").getString() +new TranslationTextComponent(craftingItem.getTranslationKey()).getString());
        }else if(this.craftManager != null && this.craftManager.isPotionCrafting()){
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.addPotionToItemStack(potionStack, this.craftManager.potionOut);
            strings.add(new TranslationTextComponent("ars_nouveau.wixie.crafting").getString() + potionStack.getDisplayName().getString());
//            strings.add(potionStack.getDisplayName().getString());
            List<ITextComponent> tooltip = new ArrayList<>();
            PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
            for(ITextComponent i : tooltip){
                strings.add(i.getString());
            }
        }

        if(!hasMana){
            strings.add(new TranslationTextComponent("ars_nouveau.wixie.need_mana").getString());
        }
        if(this.craftManager != null && !this.craftManager.neededItems.isEmpty())
            strings.add(new TranslationTextComponent("ars_nouveau.wixie.needs").getString() + new TranslationTextComponent(this.craftManager.neededItems.get(0).getTranslationKey()).getString());

        if(this.craftManager != null && this.craftManager.isPotionCrafting() && !this.craftManager.hasObtainedPotion()){
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.addPotionToItemStack(potionStack, this.craftManager.getPotionNeeded());
            strings.add(new TranslationTextComponent("ars_nouveau.wixie.needs").getString() + potionStack.getDisplayName().getString());
        }
        if(this.needsPotionStorage)
            strings.add(new TranslationTextComponent("ars_nouveau.wixie.needs_storage").getString());

        return strings;
    }

    public static class CraftingProgress{
        public ItemStack outputStack;
        public List<ItemStack> neededItems;
        public List<ItemStack> remainingItems;
        private Potion potionNeeded;
        public Potion potionOut;
        public boolean isPotionCrafting;
        private boolean hasObtainedPotion;

        public CraftingProgress(){
            outputStack = ItemStack.EMPTY;
            neededItems = new ArrayList<>();
            remainingItems = new ArrayList<>();
        }

        public CraftingProgress(Potion potionNeeded, List<ItemStack> itemsNeeded, Potion potionOut){
            this.setPotionNeeded(potionNeeded);
            this.potionOut = potionOut;
            neededItems = itemsNeeded;
            remainingItems = itemsNeeded;
            isPotionCrafting = true;
            setHasObtainedPotion(false);
            outputStack = ItemStack.EMPTY;
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
            return !isPotionCrafting ? neededItems.isEmpty() : hasObtainedPotion() && neededItems.isEmpty();
        }

        public boolean isPotionCrafting(){
            return isPotionCrafting || (potionOut != Potions.EMPTY && potionOut != null);
        }


        public void write(CompoundNBT tag){
            CompoundNBT stack = new CompoundNBT();
            outputStack.write(stack);
            tag.put("output_stack", stack);
            NBTUtil.writeItems(tag, "progress", neededItems);
            NBTUtil.writeItems(tag, "refund", remainingItems);
            CompoundNBT outputTag = new CompoundNBT();
            PotionUtil.addPotionToTag(potionOut, outputTag);
            tag.put("potionout", outputTag);

            CompoundNBT neededTag = new CompoundNBT();
            PotionUtil.addPotionToTag(getPotionNeeded(), neededTag);
            tag.put("potionNeeded", neededTag);
            tag.putBoolean("gotPotion", hasObtainedPotion());
            tag.putBoolean("isPotionCraft", isPotionCrafting);
        }

        public static CraftingProgress read(CompoundNBT tag){
            CraftingProgress progress = new CraftingProgress();
            progress.outputStack = ItemStack.read(tag.getCompound("output_stack"));
            progress.neededItems = NBTUtil.readItems(tag,"progress");
            progress.remainingItems = NBTUtil.readItems(tag, "refund");
            progress.potionOut = PotionUtils.getPotionTypeFromNBT(tag.getCompound("potionout"));
            progress.setPotionNeeded(PotionUtils.getPotionTypeFromNBT(tag.getCompound("potionNeeded")));
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
