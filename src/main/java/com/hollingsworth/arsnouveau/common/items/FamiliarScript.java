package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FamiliarScript extends ModItem {
    public AbstractFamiliarHolder familiar;

    public FamiliarScript(AbstractFamiliarHolder familiar) {
        super();
        this.familiar = familiar;
    }

    public FamiliarScript(Properties properties) {
        super(properties);
    }

    public FamiliarScript() {
        super();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND)
            return super.use(worldIn, playerIn, handIn);

        IPlayerCap familiarCap = CapabilityRegistry.getPlayerDataCap(playerIn);
        if (familiarCap != null) {
            if (familiarCap.ownsFamiliar(familiar)) {
                playerIn.sendSystemMessage(Component.translatable("ars_nouveau.familiar.owned"));
                return super.use(worldIn, playerIn, handIn);
            }
            familiarCap.unlockFamiliar(familiar);
            CapabilityRegistry.EventHandler.syncPlayerCap(playerIn);
            playerIn.sendSystemMessage(Component.translatable("ars_nouveau.familiar.unlocked"));
            if (!playerIn.hasInfiniteMaterials()) {
                playerIn.getItemInHand(handIn).shrink(1);
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.translatable("ars_nouveau.bound_script", familiar.getLangName().getString());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        tooltip2.add(Component.translatable("ars_nouveau.familiar.script"));
    }
}
