package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.block.IPedestalMachine;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.registry.ImbuementRecipeRegistry;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class ImbuementTile extends AbstractSourceMachine implements Container, ITickable, GeoBlockEntity, ITooltipProvider, IPedestalMachine, RecipeInput {
    public ItemStack stack = ItemStack.EMPTY;
    public ItemEntity entity;
    public boolean draining;
    IImbuementRecipe recipe;
    int backoff;
    public float frames;
    boolean hasRecipe;
    int craftTicks;

    public ImbuementTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.IMBUEMENT_TILE.get(), pos, state);
    }

    @Override
    protected @NotNull SourceStorage createDefaultStorage() {
        return new SourceStorage(10000000, 10000000, 0, 0){
            @Override
            public boolean canProvideSource(int source) {
                return false;
            }

            public void onContentsChanged() {
                ImbuementTile.this.updateBlock();
            }
        };
    }

    @Override
    public void lightPedestal(Level level) {
        if (level != null) {
            for (BlockPos pos : pedestalList(getBlockPos(), 1, level)) {
                ParticleUtil.spawnOrb(level, ParticleColor.makeRandomColor(255, 255, 255, level.random), pos.above(), 300);
            }
        }
    }

    @Override
    public void tick() {
        if (level == null) return;
        if (level.isClientSide) {

            int baseAge = draining ? 20 : 40;
            int randBound = draining ? 3 : 6;
            int numParticles = draining ? 2 : 1;
            float scaleAge = draining ? (float) ParticleUtil.inRange(0.1, 0.2) : (float) ParticleUtil.inRange(0.05, 0.15);
            if (level.random.nextInt(randBound) == 0 && !Minecraft.getInstance().isPaused()) {
                for (int i = 0; i < numParticles; i++) {
                    Vec3 particlePos = new Vec3(getX(), getY(), getZ()).add(0.5, 0.5, 0.5);
                    particlePos = particlePos.add(ParticleUtil.pointInSphere());
                    level.addParticle(ParticleLineData.createData(new ParticleColor(255, 25, 180), scaleAge, baseAge + level.random.nextInt(20)),
                            particlePos.x(), particlePos.y(), particlePos.z(),
                            getX() + 0.5, getY() + 0.5, getZ() + 0.5);
                }
            }
            if (!stack.isEmpty() && recipe == null) {
                var holder = getRecipeNow();
                this.recipe = holder != null ? holder.value() : null;
            }
            return;
        }
        this.hasRecipe = recipe != null;
        if (backoff > 0) {
            backoff--;
            return;
        }
        if (stack.isEmpty()) {
            return;
        }
        if (craftTicks > 0)
            craftTicks--;

        // Restore the recipe on world restart
        if (recipe == null) {
            var foundRecipe = getRecipeNow();
            if (foundRecipe != null) {
                this.recipe = foundRecipe.value();
                this.craftTicks = 100;
            }
        }

        if (recipe == null || !recipe.matches(this, level)) {
            backoff = 20;
            recipe = null;
            if (this.draining) {
                this.draining = false;
                updateBlock();
            }
            return;
        }

        int transferRate = 200;

        int cost = recipe.getSourceCost(this);

        if (level instanceof ServerLevel serverLevel && this.level.getGameTime() % 20 == 0 && this.getSource() < cost) {
            if (!canAcceptSource(Math.min(200, cost)))
                return;

            ISpecialSourceProvider takePos = SourceUtil.takeSource(worldPosition, level, 2, Math.min(200, cost));
            if (takePos != null) {
                this.addSource(transferRate);
                EntityFlyingItem.spawn(worldPosition, serverLevel, takePos.getCurrentPos().above(), worldPosition)
                        .withNoTouch().setDistanceAdjust(2f);

                if (!draining) {
                    draining = true;
                    updateBlock();
                }
            } else {
                this.addSource(10);
                if (draining) {
                    draining = false;
                    updateBlock();
                }
            }

        }

        if (this.getSource() >= cost && craftTicks <= 0) {
            this.setItem(0, recipe.assemble(this, this.level.registryAccess()).copy());
            this.addSource(-cost);
            ParticleUtil.spawnTouchPacket(level, worldPosition, ParticleColor.defaultParticleColor());
            updateBlock();
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        stack = ItemStack.parseOptional(pRegistries, tag.getCompound("itemStack"));

        draining = tag.getBoolean("draining");
        this.hasRecipe = tag.getBoolean("hasRecipe");
        this.craftTicks = tag.getInt("craftTicks");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (!stack.isEmpty()) {
            Tag reagentTag = stack.save(pRegistries);
            tag.put("itemStack", reagentTag);
        }
        tag.putBoolean("draining", draining);
        tag.putBoolean("hasRecipe", hasRecipe);
        tag.putInt("craftTicks", craftTicks);
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
        if (stack.isEmpty() || !this.stack.isEmpty())
            return false;
        this.stack = stack.copy();
        var recipe = getRecipeNow();
        this.stack = ItemStack.EMPTY;
        return recipe != null;
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack split = stack.split(count);
        updateBlock();
        return split;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        setChanged();
        return stack;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        this.stack = stack;
        this.craftTicks = 100;
        updateBlock();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
        updateBlock();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 1, this::idlePredicate));
        data.add(new AnimationController<>(this, "slowcraft_controller", 1, this::slowCraftPredicate));
    }

    private PlayState slowCraftPredicate(AnimationState<?> AnimationState) {
        AnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("imbue_slow"));
        return PlayState.CONTINUE;
    }

    private PlayState idlePredicate(AnimationState<?> AnimationState) {
        AnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("float"));
        return PlayState.CONTINUE;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public List<ItemStack> getPedestalItems() {
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        for (BlockPos p : getNearbyPedestals()) {
            if (level.getBlockEntity(p) instanceof ArcanePedestalTile pedestalTile && !pedestalTile.getStack().isEmpty()) {
                pedestalItems.add(pedestalTile.getStack());
            }
        }
        return pedestalItems;
    }

    public List<BlockPos> getNearbyPedestals() {
        return pedestalList(getBlockPos(), 1, getLevel());
    }

    public @Nullable RecipeHolder<? extends IImbuementRecipe> getRecipeNow() {
        return ImbuementRecipeRegistry.INSTANCE.getRecipes().stream().filter(r -> r.value().matches(this, level)).findFirst().orElse(null);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        var holder = getRecipeNow();
        var recipe = holder == null ? null : holder.value();
        if (recipe != null && !recipe.getResultItem(this.level.registryAccess()).isEmpty() && stack != null && !stack.isEmpty()) {
            int cost = recipe.getSourceCost(this);
            tooltip.add(recipe.getCraftingText(this));
            if (cost > 0) {
                int progress = Math.min(100, (getSource() * 100 / cost));
                tooltip.add(recipe.getCraftingProgressText(this, progress));
            }
        }
    }

    public ItemStack getStack() {
        return stack;
    }
}
