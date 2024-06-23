package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Arrays;

public class FamiliarBookwyrm extends FlyingFamiliarEntity implements ISpellCastListener {

    public FamiliarBookwyrm(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(EntityBookwyrm.COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide || level.getGameTime() % 20 != 0)
            return;
        LivingEntity owner = getOwner();
        if(!(owner instanceof Player player))
            return;
        FilterableItemHandler filterableItemHandler = new FilterableItemHandler(new PlayerMainInvWrapper(player.inventory), new ArrayList<>());
        InventoryManager manager = new InventoryManager(new ArrayList<>(){{
            add(filterableItemHandler);
        }});
        for(Entity entity : level.getEntities(owner, new AABB(owner.getOnPos()).inflate(5.0))){
            if(entity instanceof ItemEntity i){
                ItemStack stack = i.getItem();
                if (stack.isEmpty()
                        || NeoForge.EVENT_BUS.post(new EntityItemPickupEvent(player, i))
                        || getOwnerID().equals(i.getOwner())
                        || i.hasPickUpDelay()
                        || i.getPersistentData().getBoolean("PreventRemoteMovement")
                        || !i.isAlive())
                    continue;
                stack = manager.insertStack(stack);
                i.setItem(stack);
            }
            if(entity instanceof ExperienceOrb orb){
                if (orb.isRemoved() || NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp(player, orb)))
                    continue;
                player.giveExperiencePoints(orb.value);
                orb.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public PlayState walkPredicate(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("fly"));
        return PlayState.CONTINUE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get();
    }

    @Override
    public ResourceLocation getTexture(FamiliarEntity entity) {
        String color = getColor().toLowerCase();
        if (color.isEmpty())
            color = "blue";
        return ArsNouveau.prefix( "textures/entity/book_wyrm_" + color + ".png");
    }
}
