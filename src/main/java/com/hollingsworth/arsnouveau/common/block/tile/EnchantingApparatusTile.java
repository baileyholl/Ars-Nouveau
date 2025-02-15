package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.block.IPedestalMachine;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.*;
import com.hollingsworth.arsnouveau.common.block.ArcanePlatform;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantingApparatusTile extends SingleItemTile implements Container, IPedestalMachine, ITickable, GeoBlockEntity {
    private int counter;
    public boolean isCrafting;
    public static final int craftingLength = 210;

    public EnchantingApparatusTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ENCHANTING_APP_TILE.get(), pos, state);
    }

    @Override
    public void lightPedestal(Level level) {
        if (level != null) {
            for (BlockPos pos : pedestalList()) {
                ParticleUtil.spawnOrb(level, ParticleColor.makeRandomColor(255, 255, 255, level.random), pos.above(), 300);
            }
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            if (this.isCrafting) {
                Level world = getLevel();
                BlockPos pos = getBlockPos().offset(0, 0, 0);
                RandomSource rand = world.getRandom();
                BlockState state = this.getBlockState();
                Direction facing = state.getValue(BlockStateProperties.FACING);
                Vector3f step = facing.step();

                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5 + step.x * 0.5, 0.5 + step.y * 0.5, 0.5 + step.z * 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere());
                world.addParticle(ParticleLineData.createData(new ParticleColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                for (BlockPos p : pedestalList()) {
                    if (level.getBlockEntity(p) instanceof ArcanePedestalTile pedestalTile && pedestalTile.getStack() != null && !pedestalTile.getStack().isEmpty()) {
                        var yOffset = level.getBlockState(p).getBlock() instanceof ArcanePlatform ? 0.6 : 1.3;
                        getLevel().addParticle(
                                GlowParticleData.createData(new ParticleColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))),
                                p.getX() + 0.5 + ParticleUtil.inRange(-0.2, 0.2), p.getY() + yOffset + ParticleUtil.inRange(-0.3, 0.3), p.getZ() + 0.5 + ParticleUtil.inRange(-0.2, 0.2),
                                0, 0, 0);
                    }
                }

            }
            return;
        }

        if (isCrafting) {
            if (this.getRecipe(stack, null) == null) {
                this.isCrafting = false;
                setChanged();
            }
            counter += 1;
        }

        if (counter >= craftingLength) {
            counter = 0;

            if (this.isCrafting) {
                IEnchantingRecipe recipe = this.getRecipe(stack, null);
                if (recipe != null) {
                    this.stack = recipe.assemble(new ApparatusRecipeInput(this), level.registryAccess());
                    clearItems();
                    setChanged();
                    ParticleUtil.spawnPoof((ServerLevel) level, worldPosition);
                    level.playSound(null, getBlockPos(), SoundRegistry.APPARATUS_FINISH.get(), SoundSource.BLOCKS, 1, 1);
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

    public List<BlockPos> pedestalList() {
        return pedestalList(getBlockPos(), 3, getLevel());
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
        var recipe = IEnchantingRecipe.getRecipe(level, new ApparatusRecipeInput(stack, getPedestalItems(), playerEntity));
        return recipe != null ? recipe.value() : null;
    }

    public boolean attemptCraft(ItemStack catalyst, @Nullable Player playerEntity) {
        if (isCrafting)
            return false;
        if (!craftingPossible(catalyst, playerEntity)) {
            return false;
        }
        IEnchantingRecipe recipe = this.getRecipe(catalyst, playerEntity);
        if (recipe.consumesSource())
            SourceUtil.takeSourceMultipleWithParticles(worldPosition, level, 10, recipe.sourceCost());
        this.isCrafting = true;
        updateBlock();
        Networking.sendToNearbyClient(level, worldPosition, new PacketOneShotAnimation(worldPosition));
        level.playSound(null, getBlockPos(), SoundRegistry.APPARATUS_CHANNEL.get(), SoundSource.BLOCKS, 1, 1);
        return true;
    }

    public boolean craftingPossible(ItemStack stack, Player playerEntity) {
        if (isCrafting || stack.isEmpty())
            return false;
        IEnchantingRecipe recipe = this.getRecipe(stack, playerEntity);
        if(recipe == null && playerEntity != null){
            List<ColorPos> colorPos = new ArrayList<>();
            for(BlockPos pos : pedestalList()){
                if(level.getBlockEntity(pos) instanceof ArcanePedestalTile tile){
                    colorPos.add(ColorPos.centeredAbove(tile.getBlockPos()));
                }
            }
            Networking.sendToNearbyClient(level, worldPosition, new HighlightAreaPacket(colorPos, 60));
        }

        return recipe != null && (!recipe.consumesSource() || (recipe.consumesSource() && SourceUtil.hasSourceNearby(worldPosition, level, 10, recipe.sourceCost())));
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        isCrafting = compound.getBoolean("is_crafting");
        counter = compound.getInt("counter");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("is_crafting", isCrafting);
        tag.putInt("counter", counter);
    }

    @Override
    public ItemStack getItem(int index) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return super.getItem(index);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack newStack) {
        if (isCrafting || newStack.isEmpty())
            return false;
        return this.stack.isEmpty() && craftingPossible(newStack, null);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return super.removeItem(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return super.removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (isCrafting)
            return;
        super.setItem(index, stack);
        attemptCraft(stack, null);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "controller", 0, event ->{
            event.getController().setAnimation(RawAnimation.begin().thenPlay("floating"));
            return PlayState.CONTINUE;
        }));
        animatableManager.add(new AnimationController<>(this, "craft_controller", 0, event ->{
            if (!this.isCrafting) {
                event.getController().forceAnimationReset();
                return PlayState.STOP;
            }else{
                event.getController().setAnimation(RawAnimation.begin().thenPlay("enchanting"));
            }
            return PlayState.CONTINUE;
        }));
    }

    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    @Override
    public double getBoneResetTime() {
        return 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }
}
