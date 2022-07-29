package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class EnchantingApparatusTile extends AnimatedTile implements Container, ITickable, IAnimatable, IAnimationListener {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    private ItemStack catalystItem = ItemStack.EMPTY;
    public ItemEntity entity;

    public boolean isCrafting;

    public EnchantingApparatusTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ENCHANTING_APP_TILE, pos, state);
        counter = 1;
    }


    @Override
    public void tick() {
        int craftingLength = 210;
        if (level.isClientSide) {
            if (this.isCrafting) {
                Level world = getLevel();
                BlockPos pos = getBlockPos().offset(0, 0.5, 0);
                RandomSource rand = world.getRandom();

                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere());
                world.addParticle(ParticleLineData.createData(new ParticleColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

                for (BlockPos p : pedestalList()) {
                    if (level.getBlockEntity(p) instanceof ArcanePedestalTile pedestalTile && pedestalTile.getStack() != null && !pedestalTile.getStack().isEmpty())
                        getLevel().addParticle(
                                GlowParticleData.createData(new ParticleColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))),
                                p.getX() + 0.5 + ParticleUtil.inRange(-0.2, 0.2), p.getY() + 1.5 + ParticleUtil.inRange(-0.3, 0.3), p.getZ() + 0.5 + ParticleUtil.inRange(-0.2, 0.2),
                                0, 0, 0);
                }

            }
            return;
        }

        if (isCrafting) {
            if (this.getRecipe(catalystItem, null) == null) {
                this.isCrafting = false;
                setChanged();
            }
            counter += 1;
        }

        if (counter > craftingLength) {
            counter = 1;

            if (this.isCrafting) {
                IEnchantingRecipe recipe = this.getRecipe(catalystItem, null);
                List<ItemStack> pedestalItems = getPedestalItems();
                if (recipe != null) {
                    pedestalItems.forEach(i -> i = null);
                    this.catalystItem = recipe.getResult(pedestalItems, this.catalystItem, this);
                    clearItems();
                    setChanged();
                    ParticleUtil.spawnPoof((ServerLevel) level, worldPosition);
                    level.playSound(null, getBlockPos(), SoundRegistry.APPARATUS_FINISH, SoundSource.BLOCKS, 1, 1);
                }

                this.isCrafting = false;
                setChanged();
            }
            updateBlock();
        }
    }


    public void clearItems() {
        for (BlockPos blockPos : pedestalList()) {
            if (level.getBlockEntity(blockPos) instanceof ArcanePedestalTile tile && tile.getStack() != null) {
                tile.setStack(tile.getStack().getCraftingRemainingItem());
                BlockState state = level.getBlockState(blockPos);
                level.sendBlockUpdated(blockPos, state, state, 3);
                tile.setChanged();
            }
        }
    }

    // Used for rendering on the client
    public List<BlockPos> pedestalList() {
        int offset = 3;
        ArrayList<BlockPos> posList = new ArrayList<>();
        for (BlockPos b : BlockPos.betweenClosed(this.getBlockPos().offset(offset, -offset, offset), this.getBlockPos().offset(-offset, offset, -offset))) {
            if (level.getBlockEntity(b) instanceof ArcanePedestalTile tile) {
                posList.add(b.immutable());
            }
        }
        return posList;
    }

    public List<ItemStack> getPedestalItems() {
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        for (BlockPos blockPos : pedestalList()) {
            if (level.getBlockEntity(blockPos) instanceof ArcanePedestalTile tile && tile.getStack() != null && !tile.getStack().isEmpty()) {
                pedestalItems.add(tile.getStack());
            }
        }
        return pedestalItems;
    }

    public IEnchantingRecipe getRecipe(ItemStack stack, @Nullable Player playerEntity) {
        List<ItemStack> pedestalItems = getPedestalItems();
        return ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes(level).stream().filter(r -> r.isMatch(pedestalItems, stack, this, playerEntity)).findFirst().orElse(null);
    }

    public boolean attemptCraft(ItemStack catalyst, @Nullable Player playerEntity) {
        if (isCrafting)
            return false;
        if (!craftingPossible(catalyst, playerEntity)) {
            return false;
        }
        IEnchantingRecipe recipe = this.getRecipe(catalyst, playerEntity);
        if (recipe.consumesSource())
            SourceUtil.takeSourceNearbyWithParticles(worldPosition, level, 10, recipe.getSourceCost());
        this.isCrafting = true;
        updateBlock();
        Networking.sendToNearby(level, worldPosition, new PacketOneShotAnimation(worldPosition));
        level.playSound(null, getBlockPos(), SoundRegistry.APPARATUS_CHANNEL, SoundSource.BLOCKS, 1, 1);
        return true;
    }

    public boolean craftingPossible(ItemStack stack, Player playerEntity) {
        if (isCrafting || stack.isEmpty())
            return false;
        IEnchantingRecipe recipe = this.getRecipe(stack, playerEntity);
        return recipe != null && (!recipe.consumesSource() || (recipe.consumesSource() && SourceUtil.hasSourceNearby(worldPosition, level, 10, recipe.getSourceCost())));
    }

    public boolean updateBlock() {
        if (counter == 0)
            counter = 1;
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 2);
        setChanged();
        return true;
    }

    @Override
    public void load(CompoundTag compound) {
        catalystItem = ItemStack.of((CompoundTag) compound.get("itemStack"));
        isCrafting = compound.getBoolean("is_crafting");
        counter = compound.getInt("counter");
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (catalystItem != null) {
            CompoundTag reagentTag = new CompoundTag();
            catalystItem.save(reagentTag);
            tag.put("itemStack", reagentTag);
        }
        tag.putBoolean("is_crafting", isCrafting);
        tag.putInt("counter", counter);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("counter", this.counter);
        tag.putBoolean("is_crafting", this.isCrafting);
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return catalystItem.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return catalystItem;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (isCrafting || stack.isEmpty())
            return false;
        return catalystItem.isEmpty() && craftingPossible(stack, null);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (isCrafting)
            return ItemStack.EMPTY;
        ItemStack stack = catalystItem.copy().split(count);
        catalystItem.shrink(count);
        updateBlock();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return catalystItem;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (isCrafting)
            return;
        this.catalystItem = stack;
        updateBlock();
        attemptCraft(this.catalystItem, null);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        this.catalystItem = ItemStack.EMPTY;
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

    AnimationController craftController;
    AnimationController idleController;

    @Override
    public void registerControllers(AnimationData animationData) {
        idleController = new AnimationController(this, "controller", 0, this::idlePredicate);
        animationData.addAnimationController(idleController);
        craftController = new AnimationController(this, "craft_controller", 0, this::craftPredicate);
        animationData.addAnimationController(craftController);
        animationData.setResetSpeedInTicks(0.0);
    }

    AnimationFactory manager = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    private <E extends BlockEntity & IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("floating", true));
        return PlayState.CONTINUE;
    }

    private <E extends BlockEntity & IAnimatable> PlayState craftPredicate(AnimationEvent<E> event) {
        if (!this.isCrafting)
            return PlayState.STOP;
        return PlayState.CONTINUE;
    }

    @Override
    public void startAnimation(int arg) {
        try {
            if (craftController != null) {
                craftController.markNeedsReload();
                craftController.setAnimation(new AnimationBuilder().addAnimation("enchanting", false));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ItemStack getCatalystItem() {
        return catalystItem;
    }

    public void setCatalystItem(ItemStack catalystItem) {
        this.catalystItem = catalystItem;
        setChanged();
    }
}
