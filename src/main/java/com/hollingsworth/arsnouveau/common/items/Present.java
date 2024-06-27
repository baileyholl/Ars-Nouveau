package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootEnhancerModifier;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.UUID;

public class Present extends ModItem{

    public Present(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pLevel.isClientSide)
            return;
        PresentData data = new PresentData(pStack);
        if(data.uuid == null && pEntity instanceof Player player){
            data.setName(player.getName().getString());
            data.setUUID(player.getUUID());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide)
            return super.use(pLevel, pPlayer, pUsedHand);
        PresentData presentData = new PresentData(pPlayer.getItemInHand(pUsedHand));
        int bonusRolls = presentData.uuid != null && !presentData.uuid.equals(pPlayer.getUUID()) ? 2 : 0;
        DungeonLootEnhancerModifier modifier = new DungeonLootEnhancerModifier(new LootItemCondition[]{},
                0.5, 0.2, 0.1,3 + bonusRolls, 1 + bonusRolls, 1 + bonusRolls);
        List<ItemStack> stacks = DungeonLootTables.getRandomRoll(modifier);
        if(stacks.isEmpty()){
            Starbuncle giftStarby = new Starbuncle(pLevel, true);
            giftStarby.setPos(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            pLevel.addFreshEntity(giftStarby);
        }
        for(ItemStack stack : stacks){
            ItemEntity entity = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), stack);
            pLevel.addFreshEntity(entity);
        }
        pPlayer.getItemInHand(pUsedHand).shrink(1);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        PresentData data = new PresentData(stack);
        if(data.uuid != null){
            if(data.uuid.equals(ArsNouveau.proxy.getPlayer().getUUID())){
                tooltip2.add(Component.translatable("ars_nouveau.present.give"));
            }else {
                tooltip2.add(Component.translatable("ars_nouveau.present.from", data.name).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    public static class PresentData extends ItemstackData{
        String name;
        UUID uuid;

        public PresentData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if(tag == null)
                return;
            this.name = tag.getString("name");
            if(tag.contains("uuid"))
                this.uuid = tag.getUUID("uuid");
        }

        public void setName(String name){
            this.name = name;
            writeItem();
        }

        public void setUUID(UUID uuid){
            this.uuid = uuid;
            writeItem();
        }


        @Override
        public void writeToNBT(CompoundTag tag) {
            if(name != null)
                tag.putString("name", name);
            if(uuid != null)
                tag.putUUID("uuid", uuid);
        }

        @Override
        public String getTagString() {
            return "an_present_data";
        }
    }
}
