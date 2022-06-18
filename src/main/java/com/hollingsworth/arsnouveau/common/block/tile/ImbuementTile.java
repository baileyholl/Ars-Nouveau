package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ImbuementTile extends AbstractSourceMachine implements Container, ITickable, IAnimatable {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public ItemStack stack = ItemStack.EMPTY;
    public ItemEntity entity;
    public boolean draining;
    ImbuementRecipe recipe;
    int backoff;
    public float frames;
    boolean hasRecipe;
    int craftTicks;

    public ImbuementTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.IMBUEMENT_TILE, pos, state);
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    @Override
    public void tick() {
        if(level.isClientSide) {

            int baseAge = draining ? 20 : 40;
            int randBound = draining ? 3 : 6;
            int numParticles = draining ? 2 : 1;
            float scaleAge = draining ?(float) ParticleUtil.inRange(0.1, 0.2) : (float) ParticleUtil.inRange(0.05, 0.15);
            if(level.random.nextInt( randBound)  == 0 && !Minecraft.getInstance().isPaused()){
                for(int i =0; i< numParticles; i++){
                    Vec3 particlePos = new Vec3(getX(), getY(), getZ()).add(0.5, 0.5, 0.5);
                    particlePos = particlePos.add(ParticleUtil.pointInSphere());
                    level.addParticle(ParticleLineData.createData(new ParticleColor(255,25,180) ,scaleAge, baseAge + level.random.nextInt(20)) ,
                            particlePos.x(), particlePos.y(), particlePos.z(),
                            getX() + 0.5  , getY() +0.5 , getZ()+ 0.5);
                }
            }
            return;
        }
        this.hasRecipe = recipe != null;
        if(backoff > 0) {
            backoff--;
            return;
        }
        if(stack.isEmpty()) {
            return;
        }
        if(craftTicks > 0)
            craftTicks--;

        // Restore the recipe on world restart
        if(recipe == null){
            for (ImbuementRecipe recipe : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get())) {
                if (recipe.matches(this, level)) {
                    this.recipe = recipe;
                    this.craftTicks = 100;
                    break;
                }
            }
        }

        if(recipe == null || !recipe.matches(this, level)) {
            backoff = 20;
            recipe = null;
            if(this.draining) {
                this.draining = false;
                update();
            }
            return;
        }

        int transferRate = 200;


        if(this.level.getGameTime() % 20 == 0 && this.getSource() < recipe.source){
            if(!canAcceptSource(Math.min(200, recipe.source)))
                return;

            BlockPos takePos = SourceUtil.takeSourceNearby(worldPosition, level, 2, Math.min(200, recipe.source));
            if(takePos != null){
                this.addSource(transferRate);
                EntityFlyingItem item = new EntityFlyingItem(level,takePos.above(), worldPosition, 255, 50, 80)
                        .withNoTouch();
                item.setDistanceAdjust(2f);
                level.addFreshEntity(item);
                if(!draining) {
                    draining = true;
                    update();
                }
            }else{
                this.addSource(10);
                if(draining){
                    draining = false;
                    update();
                }
            }

        }

        if(this.getSource() >= recipe.source && craftTicks <= 0){
            this.setItem(0, recipe.output.copy());
            this.addSource(-recipe.source);
            ParticleUtil.spawnTouchPacket(level, worldPosition, ParticleUtil.defaultParticleColorWrapper());
            update();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        stack = ItemStack.of((CompoundTag)tag.get("itemStack"));
        draining = tag.getBoolean("draining");
        this.hasRecipe = tag.getBoolean("hasRecipe");
        this.craftTicks = tag.getInt("craftTicks");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            tag.put("itemStack", reagentTag);
        }
        tag.putBoolean("draining", draining);
        tag.putBoolean("hasRecipe", hasRecipe);
        tag.putInt("craftTicks", craftTicks);
    }

    @Override
    public int getMaxSource() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if(stack.isEmpty() || !this.stack.isEmpty())
            return false;
        this.stack = stack.copy();
        ImbuementRecipe recipe = level.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get()).stream()
                .filter(f -> f.matches(this, level)).findFirst().orElse(null);
        this.stack = ItemStack.EMPTY;
        return recipe != null;
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack copy = stack.copy().split(count);
        stack.shrink(count);
        updateBlock();
        return copy;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.stack = stack;
        this.craftTicks = 100;
        updateBlock();
    }

    @Override
    public boolean stillValid(Player player) {
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
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 1,  this::idlePredicate));
        data.addAnimationController(new AnimationController(this, "slowcraft_controller", 1,  this::slowCraftPredicate));
    }

    private PlayState slowCraftPredicate(AnimationEvent animationEvent) {
        animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("imbue_slow", true));
        return PlayState.CONTINUE;
    }

    private PlayState idlePredicate(AnimationEvent animationEvent) {
        animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("float", true));
        return PlayState.CONTINUE;
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public List<ItemStack> getPedestalItems(){
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        for(BlockPos p : BlockPos.betweenClosed(this.getBlockPos().offset(1, -1, 1), this.getBlockPos().offset(-1, 1, -1))){
            if(level.getBlockEntity(p) instanceof ArcanePedestalTile pedestalTile  && !pedestalTile.stack.isEmpty()) {
                pedestalItems.add(pedestalTile.stack);
            }
        }
        return pedestalItems;
    }
}
