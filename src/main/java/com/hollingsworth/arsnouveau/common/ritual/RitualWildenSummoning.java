package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static com.hollingsworth.arsnouveau.common.lib.RitualLib.WILDEN_SUMMON;

public class RitualWildenSummoning extends AbstractRitual {

    @Override
    protected void tick() {
        EntityChimera.spawnPhaseParticles(getPos().above(), getWorld(), 1);
        if(getWorld().getGameTime() % 20 == 0)
            incrementProgress();
        if (getWorld().getGameTime() % 60 == 0 && !getWorld().isClientSide) {
            if(!isBossSpawn()){
                int wild = rand.nextInt(3);
                BlockPos summonPos = getPos().above().east(rand.nextInt(3) - rand.nextInt(6)).north(rand.nextInt(3) - rand.nextInt(6));
                MobEntity mobEntity;
                switch (wild){
                    case 0:
                        mobEntity = new WildenStalker(getWorld());
                        break;
                    case 1:
                        mobEntity = new WildenGuardian(getWorld());
                        break;
                    default:
                        mobEntity = new WildenHunter(getWorld());
                }
                summon(mobEntity, summonPos);
                if (getProgress() >= 15) {
                    setFinished();
                }
            }else{
                if(getProgress() >= 8){
                    BlockPos.betweenClosedStream(getPos().east(5).north(5).above(), getPos().west(5).south(5).above(5)).forEach(p ->{
                        BlockUtil.destroyBlockSafelyWithoutSound(getWorld(), p, true);
                    });
                    EntityChimera chimera = new EntityChimera(getWorld());
                    summon(chimera, getPos().above());
                    setFinished();
                }
            }
        }
    }

    public boolean isBossSpawn(){
        return didConsumeItem(ItemsRegistry.WILDEN_HORN) && didConsumeItem(ItemsRegistry.WILDEN_WING) && didConsumeItem(ItemsRegistry.WILDEN_SPIKE);
    }

    public void summon(MobEntity mob, BlockPos pos){
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.level.addFreshEntity(mob);
    }

    @Override
    public String getLangName() {
        return "Summon Wilden";
    }

    @Override
    public String getLangDescription() {
        return "Without augments, this ritual will summon a random variety of Wilden monsters for a short duration. When augmented with a Wilden Spike, Wilden Horn, and a Wilden Wing, this ritual will summon the Wilden Chimera, a challenging and destructive monster. Note: If summoning the chimera, this ritual will destroy blocks around the brazier.";
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        Item item = stack.getItem();
        return item == ItemsRegistry.WILDEN_SPIKE || item == ItemsRegistry.WILDEN_WING || item == ItemsRegistry.WILDEN_HORN;
    }

    @Override
    public String getID() {
        return WILDEN_SUMMON;
    }
}
