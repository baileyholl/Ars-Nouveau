package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScribesTile extends ModdedTile implements IAnimatable, ITickable, Container, ITooltipProvider {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public ItemEntity entity; // For rendering
    public ItemStack stack = ItemStack.EMPTY;
    public int frames;
    boolean synced;
    public List<ItemStack> consumedStacks = new ArrayList<>();
    public GlyphRecipe recipe;
    ResourceLocation recipeID; // Cached for after load


    public ScribesTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCRIBES_TABLE_TILE, pos, state);
    }

    @Override
    public void tick() {
        if(getBlockState().getValue(ScribesBlock.PART) != BedPart.HEAD)
            return;
        if(!level.isClientSide && !synced){
            updateBlock();
            synced = true;
        }
        if(recipeID != null && (recipe == null || !recipe.id.equals(recipeID))){
            recipe = (GlyphRecipe) level.getRecipeManager().byKey(recipeID).orElse(null);
        }
        if(!level.isClientSide && level.getGameTime() % 20 == 0 && recipe != null){
            List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(2));
            for(ItemEntity e : nearbyItems){
                if(canConsumeItemstack(e.getItem())){
                    ItemStack copyStack = e.getItem().copy();
                    copyStack.setCount(1);
                    consumedStacks.add(copyStack);
                    e.getItem().shrink(1);
                    updateBlock();
                    break;
                }
            }
        }
    }

    public void refundConsumed(){
        for(ItemStack i : consumedStacks){
            ItemEntity entity = new ItemEntity(level, getX(), getY(), getZ(), i);
            level.addFreshEntity(entity);
            consumedStacks = new ArrayList<>();
        }
    }

    public void setRecipe(GlyphRecipe recipe){
        ScribesTile tile = this;
        refundConsumed();
        if(getBlockState().getValue(ScribesBlock.PART) != BedPart.HEAD) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(ScribesBlock.getConnectedDirection(getBlockState())));
            tile = tileEntity instanceof ScribesTile ? (ScribesTile) tileEntity : null;
            if(tile == null)
                return;
        }
        tile.recipe = recipe;
        tile.updateBlock();
    }

    public boolean canConsumeItemstack(ItemStack stack){
        if(recipe == null)
            return false;
        return getRemainingRequired().stream().anyMatch(i -> i.test(stack));
    }

    public List<Ingredient> getRemainingRequired(){
        if(consumedStacks.isEmpty())
            return recipe.inputs;
        List<Ingredient> unaccountedIngredients = new ArrayList<>();
        List<ItemStack> remainingItems = new ArrayList<>();
        for(ItemStack stack : consumedStacks){
            remainingItems.add(stack.copy());
        }
        for(Ingredient ingred : recipe.inputs){
            ItemStack matchingStack = null;

            for(ItemStack item : remainingItems){
                if(ingred.test(item)){
                    matchingStack = item;
                    break;
                }
            }
            if(matchingStack != null){
                remainingItems.remove(matchingStack);
            }else{
                unaccountedIngredients.add(ingred);
            }
        }
        return unaccountedIngredients;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        stack = ItemStack.of((CompoundTag)compound.get("itemStack"));
        if(compound.contains("recipe")){
            recipeID = new ResourceLocation(compound.getString("recipe"));
        }
        CompoundTag itemsTag = new CompoundTag();
        itemsTag.putInt("numStacks", consumedStacks.size());
        for(ItemStack i : consumedStacks){

        }
        this.consumedStacks = NBTUtil.readItems(compound, "consumed");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            compound.put("itemStack", reagentTag);
        }
        if(recipe !=  null){
            compound.putString("recipe", recipe.getId().toString());
        }
        NBTUtil.writeItems(compound, "consumed", consumedStacks);

    }

    @Override
    public void registerControllers(AnimationData data) {}

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(2);
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack == null || this.stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack copy = stack.copy();
        stack.shrink(1);
        updateBlock();
        return copy;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        updateBlock();
        return stack;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.stack = pStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
        updateBlock();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {

    }
}
