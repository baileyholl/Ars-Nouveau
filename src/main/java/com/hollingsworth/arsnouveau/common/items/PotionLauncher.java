package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetLauncher;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class PotionLauncher extends ModItem implements IRadialProvider {
    public PotionLauncher(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        PotionLauncherData potionLauncherData = new PotionLauncherData(itemstack);
        if(pLevel.isClientSide)
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        PotionData potionData = potionLauncherData.getPotionDataFromSlot(pPlayer);
        if(potionData.isEmpty()){
            PortUtil.sendMessage(pPlayer, Component.translatable("arsnouveau.potionlauncher.no_potion"));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        }

        ThrownPotion thrownpotion = new ThrownPotion(pLevel, pPlayer);
        thrownpotion.setItem(getThrownStack(pLevel, pPlayer,  pHand, itemstack));
        thrownpotion.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.5F, 1.0F);
        pLevel.addFreshEntity(thrownpotion);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    public abstract ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack);

    @OnlyIn(Dist.CLIENT)
    @Override
    public int forKey() {
        return ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        List<RadialMenuSlot<AlchemistsCrown.SlotData>> slots = new ArrayList<>();
        for(int i = 0; i < player.inventory.getContainerSize(); i++) {
            if(slots.size() >= 9)
                break;
            ItemStack item = player.inventory.getItem(i);
            PotionData potionData = new PotionData(item);
            if(potionData.isEmpty())
                continue;
            slots.add(new RadialMenuSlot<>(item.getHoverName().getString(), new AlchemistsCrown.SlotData(i, item)));
        }
        if(slots.isEmpty()) {
            PortUtil.sendMessage(Minecraft.getInstance().player, Component.translatable("ars_nouveau.alchemists_crown.no_flasks"));
            return;
        }
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(new RadialMenu<>((int index) -> {
            Networking.INSTANCE.sendToServer(new PacketSetLauncher(slots.get(index).primarySlotIcon().getSlot()));
        }, slots, (slotData, posestack, positionx, posy, size, transparent) -> RenderUtils.drawItemAsIcon(slotData.getStack(), posestack, positionx, posy, size, transparent), 3)));
    }

    public static class SplashLauncher extends PotionLauncher{

        public SplashLauncher(Properties properties) {
            super(properties);
        }

        @Override
        public ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack) {
            PotionLauncherData data = new PotionLauncherData(launcherStack);
            ItemStack splashStack = new ItemStack(Items.SPLASH_POTION);
            PotionData potionData = data.expendPotion(pPlayer);
            PotionUtils.setPotion(splashStack, potionData.getPotion());
            PotionUtils.setCustomEffects(splashStack, potionData.getCustomEffects());
            return splashStack;
        }
    }

    public static class LingeringLauncher extends PotionLauncher{

        public LingeringLauncher(Properties properties) {
            super(properties);
        }

        @Override
        public ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack) {
            PotionLauncherData data = new PotionLauncherData(launcherStack);
            ItemStack splashStack = new ItemStack(Items.LINGERING_POTION);
            PotionData potionData = data.expendPotion(pPlayer);
            PotionUtils.setPotion(splashStack, potionData.getPotion());
            PotionUtils.setCustomEffects(splashStack, potionData.getCustomEffects());
            return splashStack;
        }
    }

    public static class PotionLauncherData extends ItemstackData {
        private PotionData lastDataForRender;
        private int lastSlot;

        public PotionLauncherData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if(tag == null)
                return;
            lastDataForRender = PotionData.fromTag(tag.getCompound("lastDataForRender"));
            lastSlot = tag.getInt("lastSlot");
        }

        public PotionData getPotionDataFromSlot(Player player){
            if(lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
                return new PotionData();
            ItemStack stack = player.inventory.getItem(lastSlot);
            return new PotionData(stack);
        }

        public PotionData expendPotion(Player player){
            if(lastSlot >= player.inventory.getContainerSize())
                return new PotionData();
            ItemStack item = player.inventory.getItem(lastSlot);
            if(item.getItem() instanceof PotionFlask){
                PotionFlask.FlaskData flaskData = new PotionFlask.FlaskData(item);
                if(flaskData.getCount() <= 0 || flaskData.getPotion().isEmpty())
                    return new PotionData();
                PotionData data = flaskData.getPotion().clone();
                flaskData.setCount(flaskData.getCount() - 1);
                return data;
            }else if(item.getItem() instanceof PotionItem){
                PotionData data = new PotionData(item).clone();
                if(data.isEmpty())
                    return new PotionData();
                item.shrink(1);
                player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
                return data;
            }
            return new PotionData();
        }

        public void setLastSlot(int lastSlot) {
            this.lastSlot = lastSlot;
            writeItem();
        }

        public void setLastDataForRender(PotionData lastDataForRender) {
            this.lastDataForRender = lastDataForRender;
            writeItem();
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            tag.putInt("lastSlot", lastSlot);
            tag.put("lastDataForRender", lastDataForRender.toTag());
        }

        @Override
        public String getTagString() {
            return "potion_launcher";
        }
    }
}
