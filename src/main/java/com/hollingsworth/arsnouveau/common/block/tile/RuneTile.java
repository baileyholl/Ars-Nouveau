package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.List;

public class RuneTile extends AnimatedTile {
    public List<AbstractSpellPart> recipe;
    public boolean isTemporary;
    public boolean isCharged;
    public int ticksUntilCharge;
    public RuneTile() {
        super(BlockRegistry.RUNE_TILE);
        isCharged = true;
        isTemporary = false;
        ticksUntilCharge = 0;
    }

    public void setRecipe(ArrayList<AbstractSpellPart> recipe) {
        this.recipe = recipe;
    }

    public void castSpell(Entity entity){

        if(!this.isCharged || recipe == null || recipe.size() == 0 || !(entity instanceof LivingEntity) || !(world instanceof ServerWorld))
            return;

        EntitySpellResolver resolver = new EntitySpellResolver(recipe);
        resolver.onCastOnEntity(ItemStack.EMPTY, FakePlayerFactory.getMinecraft((ServerWorld) world),(LivingEntity)entity, Hand.MAIN_HAND);
        if(this.isTemporary){
            world.destroyBlock(pos, false);
            return;
        }
        this.isCharged = false;
        // cycle
        world.setBlockState(pos, world.getBlockState(pos).func_235896_a_(RuneBlock.POWERED));
        ticksUntilCharge = 20 * 2;
    }

    public void setParsedSpell(List<AbstractSpellPart> spell){
        if(spell.size() <= 1){
            this.recipe = null;
            return;
        }
        spell.set(0, new MethodTouch());
        this.recipe = spell;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(recipe != null)
            tag.putString("spell", SpellRecipeUtil.serializeForNBT(recipe));
        tag.putBoolean("charged", isCharged);
        tag.putBoolean("temp", isTemporary);
        tag.putInt("cooldown", ticksUntilCharge);
        return super.write(tag);
    }

    @Override
    public void read( BlockState state, CompoundNBT tag) {
        this.recipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        this.isCharged = tag.getBoolean("charged");
        this.isTemporary = tag.getBoolean("temp");
        this.ticksUntilCharge = tag.getInt("cooldown");
        super.read(state, tag);
    }

    @Override
    public void tick() {
        if(world == null)
            return;
        if(!world.isRemote) {
            if (ticksUntilCharge > 0) {
                ticksUntilCharge -= 1;
                return;
            }
        }
        if(this.isCharged)
            return;
        if(!world.isRemote && this.isTemporary){
            world.destroyBlock(this.pos, false);
        }
        if(!world.isRemote && !this.isCharged){
            BlockPos fromPos = ManaUtil.takeManaNearby(pos, world, 10, 100);
            if(fromPos != null) {
                this.isCharged = true;
                Networking.sendToNearby(world, this.pos, new PacketANEffect(PacketANEffect.EffectType.TIMED_GLOW,
                        pos.getX(), pos.getY(), pos.getZ(),fromPos.getX(), fromPos.getY(), fromPos.getZ(), 5));
                world.setBlockState(pos, world.getBlockState(pos).func_235896_a_(RuneBlock.POWERED));
            }else
                ticksUntilCharge = 20 * 3;
        }
    }
}