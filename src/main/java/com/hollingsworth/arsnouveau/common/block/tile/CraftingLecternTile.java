package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.SetTerminalSettingsPacket;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Function;

public class CraftingLecternTile extends StorageLecternTile implements GeoBlockEntity {
    private Function<UUID, AbstractContainerMenu> craftingContainer = (uuid) -> new AbstractContainerMenu(MenuType.CRAFTING, 0) {
        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void slotsChanged(Container inventory) {
            if (level != null && !level.isClientSide) {
                onCraftingMatrixChanged(uuid);
            }
        }

        @Override
        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            return ItemStack.EMPTY;
        }
    };
    public final Map<UUID, TransientCustomContainer> craftingMatrices = new HashMap<>();
    private final Map<UUID, ResultContainer> craftingResults = new HashMap<>();
    private final Map<UUID, CraftingRecipe> currentRecipes = new HashMap<>();


    public CraftingLecternTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CRAFTING_LECTERN_TILE.get(), pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory plInv, Player arg2) {
        return new CraftingTerminalMenu(id, plInv, this);
    }

    private ListTag getCraftMatrixTag(TransientCustomContainer craftingMatrix, HolderLookup.Provider pRegistries) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < craftingMatrix.getContainerSize(); ++i) {
            ItemStack itemstack = craftingMatrix.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("Slot", i);
                compoundTag.put("item", itemstack.save(pRegistries));
                listTag.add(compoundTag);
            }
        }
        return listTag;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);

        ListTag matrices = new ListTag();
        for (Map.Entry<UUID, TransientCustomContainer> entry : craftingMatrices.entrySet()) {
            UUID uuid = entry.getKey();
            CompoundTag matrix = new CompoundTag();

            matrix.putUUID("UUID", uuid);
            matrix.put("CraftingTable", getCraftMatrixTag(entry.getValue(), pRegistries));
            matrices.add(matrix);
        }

        tag.put("CraftingMatrices", matrices);
    }

    private boolean reading;

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        reading = true;

        ListTag matrices = compound.getList("CraftingMatrices", Tag.TAG_COMPOUND);

        for (int i = 0; i < matrices.size(); i++) {
            CompoundTag entry = matrices.getCompound(i);
            UUID uuid = entry.getUUID("UUID");
            TransientCustomContainer container = getCraftingInv(uuid);

            ListTag table = entry.getList("CraftingTable", Tag.TAG_COMPOUND);
            for (int j = 0; j < table.size(); j++) {
                CompoundTag itemSlot = table.getCompound(j);
                int slot = itemSlot.getInt("Slot");
                if (slot >= 0 && slot < container.getContainerSize()) {
                    container.setItem(slot, ItemStack.parseOptional(pRegistries, itemSlot.getCompound("item")));
                }
            }
        }

        reading = false;
    }

    public TransientCustomContainer getCraftingInv(Player player) {
        return getCraftingInv(player.getUUID());
    }

    public TransientCustomContainer getCraftingInv(UUID uuid) {
        return craftingMatrices.computeIfAbsent(uuid, (key) -> new TransientCustomContainer(craftingContainer.apply(uuid), 3, 3));
    }

    public ResultContainer getCraftResult(Player player) {
        return getCraftResult(player.getUUID());
    }

    public ResultContainer getCraftResult(UUID uuid) {
        return craftingResults.computeIfAbsent(uuid, (key) -> new ResultContainer());
    }

    public void craftShift(ServerPlayer player, @Nullable String tab) {
        ResultContainer craftResult = getCraftResult(player);
        TransientCustomContainer craftMatrix = getCraftingInv(player);

        List<ItemStack> craftedItemsList = new ArrayList<>();
        int amountCrafted = 0;
        ItemStack crafted = craftResult.getItem(0);
        do {
            craft(player, tab);
            craftedItemsList.add(crafted.copy());
            amountCrafted += crafted.getCount();
        } while (ItemStack.isSameItem(crafted, craftResult.getItem(0)) && (amountCrafted + crafted.getCount()) <= crafted.getMaxStackSize());

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.getInventory().add(craftedItem.copy())) {
                ItemStack is = pushStack(craftedItem, tab);
                if (!is.isEmpty()) {
                    Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), is);
                }
            }
        }

        crafted.onCraftedBy(player.level, player, amountCrafted);
        var copyStack = crafted.copy();
        copyStack.setCount(amountCrafted);
        EventHooks.firePlayerCraftingEvent(player, copyStack, craftMatrix);
    }

    public void craft(ServerPlayer thePlayer, @Nullable String tab) {
        TransientCustomContainer craftMatrix = getCraftingInv(thePlayer);
        CraftingRecipe recipe = currentRecipes.get(thePlayer.getUUID());

        if (recipe == null) {
            return;
        }
        boolean playerInvUpdate = false;
        List<ItemStack> remainingItems = recipe.getRemainingItems(craftMatrix.asCraftInput());
        for (int i = 0; i < 9; ++i) {
            ItemStack currentStack = craftMatrix.getItem(i);
            ItemStack oldItem = currentStack.copy();
            ItemStack rem = remainingItems.size() > i ? remainingItems.get(i) : ItemStack.EMPTY;
            if (!currentStack.isEmpty()) {
                craftMatrix.removeItemNoUpdate(i, 1);
                currentStack = craftMatrix.getItem(i);
            }
            if (currentStack.isEmpty() && !oldItem.isEmpty()) {
                StoredItemStack is = null;
                for (int j = 0; j < thePlayer.getInventory().items.size(); j++) {
                    ItemStack st = thePlayer.getInventory().getItem(j);
                    if (ItemStack.isSameItemSameComponents(oldItem, st)) {
                        st = thePlayer.getInventory().removeItem(j, 1);
                        if (!st.isEmpty()) {
                            is = new StoredItemStack(st, 1);
                            playerInvUpdate = true;
                            break;
                        }
                    }
                }

                if (is == null) {
                    is = pullStack(new StoredItemStack(oldItem), 1, tab);
                }

                if (is != null) {
                    craftMatrix.setItemNoUpdate(i, is.getActualStack());
                    currentStack = craftMatrix.getItem(i);
                }
            }
            if (rem.isEmpty()) {
                continue;
            }
            if (currentStack.isEmpty()) {
                craftMatrix.setItemNoUpdate(i, rem);
                continue;
            }
            if (ItemStack.isSameItem(currentStack, rem) && ItemStack.matches(currentStack, rem)) {
                rem.grow(currentStack.getCount());
                craftMatrix.setItemNoUpdate(i, rem);
                continue;
            }
            rem = pushStack(rem, tab);
            if (rem.isEmpty() || thePlayer.getInventory().add(rem)) {
                continue;
            }
            thePlayer.drop(rem, false);
        }
        if (playerInvUpdate) {
            thePlayer.containerMenu.broadcastChanges();
        }
        onCraftingMatrixChanged(thePlayer.getUUID());
    }

    protected void onCraftingMatrixChanged(UUID uuid) {
        if (sortSettings.expanded()) {
            setSorting(sortSettings.setExpanded(false));
            Player player = level.getPlayerByUUID(uuid);
            if (player instanceof ServerPlayer serverPlayer) {
                Networking.sendToPlayerClient(new SetTerminalSettingsPacket(sortSettings, searches.get(uuid)), serverPlayer);
            }
        }
        ResultContainer craftResult = getCraftResult(uuid);
        TransientCustomContainer craftMatrix = getCraftingInv(uuid);

        CraftingRecipe recipe = currentRecipes.get(uuid);
        if (recipe == null || !recipe.matches(craftMatrix.asCraftInput(), level)) {
            var holder = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftMatrix.asCraftInput(), level).orElse(null);
            recipe = holder == null ? null : holder.value();
            currentRecipes.put(uuid, recipe);
        }

        if (recipe == null) {
            craftResult.setItem(0, ItemStack.EMPTY);
        } else {
            craftResult.setItem(0, recipe.assemble(craftMatrix.asCraftInput(), level.registryAccess()));
        }

        if (!reading) {
            setChanged();
        }
    }

    public void clear(Player player, @Nullable String tab) {
        TransientCustomContainer craftMatrix = getCraftingInv(player);

        for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
            ItemStack st = craftMatrix.removeItemNoUpdate(i);
            if (!st.isEmpty()) {
                pushOrDrop(st, tab);
            }
        }
        onCraftingMatrixChanged(player.getUUID());
    }

    public void transferToGrid(Player player, ItemStack[][] ingredients, @Nullable String tab) {
        clear(player, tab);
        TransientCustomContainer craftMatrix = getCraftingInv(player);
        for (int i = 0; i < 9; i++) {
            ItemStack[] ingredient = ingredients[i];
            if (ingredient == null) {
                continue;
            }

            ItemStack stack = ItemStack.EMPTY;
            for (ItemStack itemStack : ingredient) {
                boolean br = false;
                Inventory playerInv = player.getInventory();
                for (int k = 0; k < playerInv.getContainerSize(); k++) {
                    if (ItemStack.isSameItem(playerInv.getItem(k), itemStack)) {
                        stack = playerInv.removeItem(k, 1);
                        br = true;
                        break;
                    }
                }
                if (br) {
                    break;
                }
            }

            if (stack.isEmpty()) {
                Map<Item, Long> inv = itemCounts;
                // sort ingredient by the amount of items in the inv map
                ingredient = Arrays.stream(ingredient).filter(Objects::nonNull).sorted(Comparator.comparingLong(a -> inv.getOrDefault(((ItemStack) a).getItem(), 0L)).reversed()).toArray(ItemStack[]::new);

                // Sort ingredient by the amount of items in this inventory
                for (ItemStack itemStack : ingredient) {
                    ItemStack pulled = pullStack(itemStack, tab);
                    if (!pulled.isEmpty()) {
                        stack = pulled;
                        break;
                    }
                }
            }

            if (!stack.isEmpty()) {
                craftMatrix.setItem(i, stack);
            }
        }
        onCraftingMatrixChanged(player.getUUID());
    }

    private ItemStack pullStack(ItemStack itemStack, @Nullable String tab) {
        StoredItemStack is = pullStack(new StoredItemStack(itemStack), 1, tab);
        if (is == null) return ItemStack.EMPTY;
        else return is.getActualStack();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 1, (event -> {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("ledger_float"));
            return PlayState.CONTINUE;
        })));
    }

    AnimatableInstanceCache AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return AnimatableInstanceCache;
    }
}
