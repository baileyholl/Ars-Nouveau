package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.entity.statemachine.memory.MemoryTypes;
import com.hollingsworth.nuggets.common.state_machine.IStateEvent;
import com.hollingsworth.nuggets.common.state_machine.SimpleStateMachine;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ArcanoStateMachine extends SimpleStateMachine<ArcanoState, IStateEvent> {
    public ArcanoBoss arcanoBoss;

    public ArcanoStateMachine(ArcanoBoss arcanoBoss, @Nonnull ArcanoState initialState) {
        super(initialState);
        this.arcanoBoss = arcanoBoss;
    }

    @Override
    protected void changeState(@NotNull ArcanoState nextState) {
        arcanoBoss.memoryMap.put(MemoryTypes.LAST_STATE, this.currentState);
        super.changeState(nextState);
    }
}
