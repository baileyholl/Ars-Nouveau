package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.IAutoFillTerminal;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.network.ClientToServerStoragePacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CraftingTerminalTransferHandler<C extends AbstractContainerMenu & IAutoFillTerminal> implements IRecipeTransferHandler<C, RecipeHolder<CraftingRecipe>> {
	private final Class<C> containerClass;
	private final IRecipeTransferHandlerHelper helper;
	private static final List<Class<? extends AbstractContainerMenu>> containerClasses = new ArrayList<>();
	private static final IRecipeTransferError ERROR_INSTANCE = new IRecipeTransferError() {
		@Override public Type getType() { return Type.INTERNAL; }
	};
	static {
		containerClasses.add(CraftingTerminalMenu.class);
	}

	public CraftingTerminalTransferHandler(Class<C> containerClass, IRecipeTransferHandlerHelper helper) {
		this.containerClass = containerClass;
		this.helper = helper;
	}

	@Override
	public Class<C> getContainerClass() {
		return containerClass;
	}

	@Override
	public @Nullable IRecipeTransferError transferRecipe(C container, RecipeHolder<CraftingRecipe> recipe,
			IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
		if (container instanceof IAutoFillTerminal term) {
			List<IRecipeSlotView> missing = new ArrayList<>();
			List<IRecipeSlotView> views = recipeSlots.getSlotViews();
			List<ItemStack[]> inputs = new ArrayList<>();
			Set<StoredItemStack> stored = new HashSet<>(term.getStoredItems());

			for (IRecipeSlotView view : views) {
				if(view.getRole() == RecipeIngredientRole.INPUT || view.getRole() == RecipeIngredientRole.CATALYST) {
					ItemStack[] possibleStacks = view.getIngredients(VanillaTypes.ITEM_STACK).toArray(ItemStack[]::new);
					if(possibleStacks.length == 0){
						inputs.add(null);
						continue;
					}

					inputs.add(possibleStacks);

					boolean found = false;
					for (ItemStack stack : possibleStacks) {
						if (stack != null && player.getInventory().findSlotMatchingItem(stack) != -1) {
							found = true;
							break;
						}
					}

					if (!found) {
						for (ItemStack stack : possibleStacks) {
							StoredItemStack s = new StoredItemStack(stack);
							if(stored.contains(s)) {
								found = true;
								break;
							}
						}
					}

					if (!found) {
						missing.add(view);
					}

				}
			}
			if (doTransfer) {
				ItemStack[][] stacks = inputs.toArray(new ItemStack[][]{});
				CompoundTag compound = new CompoundTag();
				ListTag list = new ListTag();
				for (int i = 0;i < stacks.length;++i) {
					if (stacks[i] != null) {
						CompoundTag CompoundNBT = new CompoundTag();
						CompoundNBT.putByte("s", (byte) i);
						int k = 0;
						for (int j = 0;j < stacks[i].length && k < 9;j++) {
							if (stacks[i][j] != null && !stacks[i][j].isEmpty()) {
								StoredItemStack s = new StoredItemStack(stacks[i][j]);
								if(stored.contains(s) || player.getInventory().findSlotMatchingItem(stacks[i][j]) != -1) {
									Tag tag = stacks[i][j].save(player.level.registryAccess());
									CompoundNBT.put("i" + (k++), tag);
								}
							}
						}
						CompoundNBT.putByte("l", (byte) Math.min(9, k));
						list.add(CompoundNBT);
					}
				}
				compound.put("i", list);
				Networking.sendToServer(new ClientToServerStoragePacket(compound));
			}

			if(!missing.isEmpty()) {
				return new TransferWarning(helper.createUserErrorForMissingSlots(Component.translatable("tooltip.ars_nouveau.items_missing"), missing));
			}
		} else {
			return ERROR_INSTANCE;
		}
		return null;
	}

	public static void registerTransferHandlers(IRecipeTransferRegistration recipeTransferRegistry) {
		for (Class<? extends AbstractContainerMenu> aClass : containerClasses)
			recipeTransferRegistry.addRecipeTransferHandler(new CraftingTerminalTransferHandler(aClass, recipeTransferRegistry.getTransferHelper()), RecipeTypes.CRAFTING);
	}

	private static class TransferWarning implements IRecipeTransferError {
		private final IRecipeTransferError parent;

		public TransferWarning(IRecipeTransferError parent) {
			this.parent = parent;
		}

		@Override
		public Type getType() {
			return Type.COSMETIC;
		}

		@Override
		public void showError(GuiGraphics matrixStack, int mouseX, int mouseY, IRecipeSlotsView recipeLayout, int recipeX,
							  int recipeY) {
			this.parent.showError(matrixStack, mouseX, mouseY, recipeLayout, recipeX, recipeY);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<MenuType<C>> getMenuType() {
		return Optional.of((MenuType<C>) MenuRegistry.STORAGE.get());
	}

	@Override
	public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
		return RecipeTypes.CRAFTING;
	}

}
