package com.hollingsworth.arsnouveau.common.entity.statemachine.memory;

import com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss.ArcanoState;

public class MemoryTypes {

    public static MemoryType<ArcanoState> LAST_STATE = new MemoryType<>("last_state");
}
