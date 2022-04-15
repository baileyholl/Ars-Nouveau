package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
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

public class ScribesTile extends ModdedTile implements IAnimatable, ITickable, Container, ITooltipProvider,IAnimationListener {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public ItemStack stack = ItemStack.EMPTY;
    boolean synced;
    public List<ItemStack> consumedStacks = new ArrayList<>();
    public GlyphRecipe recipe;
    ResourceLocation recipeID; // Cached for after load
    public boolean crafting;
    public int craftingTicks;


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
        if(craftingTicks > 0)
            craftingTicks--;

        if(recipeID != null && recipeID.equals(new ResourceLocation(""))){
            recipe = null; // Used on client to remove recipe since for some forsaken reason world is missing during load.
        }

        if(recipeID != null && !recipeID.toString().isEmpty() &&  (recipe == null || !recipe.id.equals(recipeID))){
            recipe = (GlyphRecipe) level.getRecipeManager().byKey(recipeID).orElse(null);
        }
        if(!level.isClientSide && level.getGameTime() % 5 == 0 && recipe != null){
            boolean foundStack = false;
            List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(2));
            for(ItemEntity e : nearbyItems){
                if(canConsumeItemstack(e.getItem())){
                    ItemStack copyStack = e.getItem().copy();
                    copyStack.setCount(1);
                    consumedStacks.add(copyStack);
                    e.getItem().shrink(1);
                    ParticleUtil.spawnTouchPacket(level, e.getOnPos(), ParticleUtil.defaultParticleColorWrapper());
                    updateBlock();
                    foundStack = true;
                    break;
                }
            }
            if(!foundStack && level.getGameTime() % 20 == 0)
                checkInventories();

            if(getRemainingRequired().isEmpty() && !crafting){
                crafting = true;
                craftingTicks = 120;
                Networking.sendToNearby(level, getBlockPos(), new PacketOneShotAnimation(getBlockPos(), 0));
                updateBlock();
            }
        }
        if(level.isClientSide && craftingTicks == 0 && crafting){
            crafting = false;

        }
        if(!level.isClientSide && crafting && craftingTicks == 0 && recipe != null){
            level.addFreshEntity(new ItemEntity(level, getX(), getY() + 1, getZ(), recipe.output.copy()));
            recipe = null;
            recipeID = new ResourceLocation("");
            crafting = false;
            consumedStacks = new ArrayList<>();
            updateBlock();
        }
    }

    public void checkInventories(){
        for (BlockPos bPos : BlockPos.betweenClosed(worldPosition.north(6).east(6).below(2), worldPosition.south(6).west(6).above(2))) {
            if (level.getBlockEntity(bPos) != null && level.getBlockEntity(bPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent()) {
                IItemHandler handler = level.getBlockEntity(bPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (canConsumeItemstack(stack)) {
                            ItemStack stack1 = handler.extractItem(i, 1, false);
                            stack1.copy().setCount(1);
                            consumedStacks.add(stack1);
                            EntityFlyingItem flyingItem = new EntityFlyingItem(level, bPos, getBlockPos());
                            flyingItem.setStack(stack1);
                            level.addFreshEntity(flyingItem);
                            updateBlock();
                            return;
                        }
                    }
                }
            }
        }

    }

    public boolean consumeStack(ItemStack stack){
        if(!canConsumeItemstack(stack))
            return false;
        ItemStack copyStack = stack.split(1);
        consumedStacks.add(copyStack);
        ParticleUtil.spawnTouchPacket(level, getBlockPos().above(), ParticleUtil.defaultParticleColorWrapper());
        updateBlock();
        return true;
    }

    public void refundConsumed(){
        for(ItemStack i : consumedStacks){
            ItemEntity entity = new ItemEntity(level, getX(), getY(), getZ(), i);
            level.addFreshEntity(entity);
            consumedStacks = new ArrayList<>();
        }
        if(recipe != null) {
            int exp = recipe.exp;
            if (level instanceof ServerLevel serverLevel)
                ExperienceOrb.award(serverLevel, new Vec3(getX(), getY(), getZ()), exp);
        }
        recipe = null;
        recipeID = null;
        craftingTicks = 0;
        crafting = false;
        updateBlock();
    }

    public void setRecipe(GlyphRecipe recipe, Player player){
        if(ScribesTile.getTotalPlayerExperience(player) < recipe.exp && !player.isCreative()){
            PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.not_enough_exp"));
            return;
        }else if(!player.isCreative()){
            player.giveExperiencePoints(-recipe.exp);
        }
        ScribesTile tile = getLogicTile();
        if(tile == null)
            return;
        tile.refundConsumed();
        tile.recipe = recipe;
        tile.recipeID = recipe.getId();
        tile.updateBlock();
    }

    public static int getTotalPlayerExperience(Player player){
        return (int) (getExperienceForLevel(player.experienceLevel) + player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static int getLevelsFromExp(int exp){
        if(exp <= 352){
            return (int) (Math.sqrt(exp + 9) - 3);
        }else if(exp <= 1507){
            return (int) (8.1 + Math.sqrt(0.4 * (exp - 195.975)));
        }
        return (int) (18.056 + Math.sqrt(0.222 * (exp - 752.986)));
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0)
            return 0;
        if (level > 0 && level < 17)
            return (int) (Math.pow(level, 2) + 6 * level);
        else if (level > 16 && level < 32)
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        else
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
    }

    public @Nullable ScribesTile getLogicTile(){
        ScribesTile tile = this;
        if(!isMasterTile()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(ScribesBlock.getConnectedDirection(getBlockState())));
            tile = tileEntity instanceof ScribesTile ? (ScribesTile) tileEntity : null;
            if(tile == null)
                return null;
        }
        return tile;
    }

    public boolean isMasterTile(){
        return getBlockState().getValue(ScribesBlock.PART) == BedPart.HEAD;
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
        this.consumedStacks = NBTUtil.readItems(compound, "consumed");
        this.craftingTicks = compound.getInt("craftingTicks");
        this.crafting = compound.getBoolean("crafting");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            compound.put("itemStack", reagentTag);
        }
        if(recipe != null){
            compound.putString("recipe", recipe.getId().toString());
        }else{
            compound.putString("recipe", "");
        }
        NBTUtil.writeItems(compound, "consumed", consumedStacks);
        compound.putInt("craftingTicks", craftingTicks);
        compound.putBoolean("crafting", crafting);
    }

    private <E extends BlockEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void startAnimation(int arg) {
        if(controller == null){
            System.out.println("NULL CONTROLLER");
            return;
        }
        controller.markNeedsReload();
        controller.setAnimation(new AnimationBuilder().addAnimation("create_glyph", false));
    }

    @Override
    public void registerControllers(AnimationData data) {
        this.controller = new AnimationController(this, "controller", 1, this::idlePredicate);
        data.addAnimationController(controller);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(2);
    }

    AnimationFactory factory = new AnimationFactory(this);
    AnimationController controller;
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
        if(!isMasterTile()) {
            ScribesTile tile = getLogicTile();
            if(tile == null)
                return;
            tile.getTooltip(tooltip);
            return;
        }
        if(recipe != null){
            tooltip.add(new TranslatableComponent("ars_nouveau.crafting", recipe.output.getHoverName()));
        }
    }
}
