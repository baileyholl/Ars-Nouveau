package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoBoss;
import org.jetbrains.annotations.Nullable;

public class ArcanoSetupPhase extends ArcanoState {
    public ArcanoSetupPhase(ArcanoBoss arcanoBoss) {
        super(arcanoBoss);
    }

    @Override
    public @Nullable ArcanoState tick() {
        if (!arcanoBoss.isSetupPhase) {
            System.out.println("ending setup");
            return new InitArcanoState(arcanoBoss);
        }
        return super.tick();
    }
}
