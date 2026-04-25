package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.abilities.core.ToggleAbility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class MindWorldAuthorityEnvisioning extends ToggleAbility {
    protected MindWorldAuthorityEnvisioning(String id) {
        super(id);

        canBeCopied = false;
        canBeReplicated = false;
        cannotBeStolen = true;
        canBeUsedByNPC = false;
        canBeUsedInArtifact = false;
        doesNotIncreaseDigestion = true;

        this.shouldBeHidden = true;
    }

    @Override
    public void tick(Level level, LivingEntity entity) {

    }

    @Override
    public void start(Level level, LivingEntity entity) {

    }

    @Override
    public void stop(Level level, LivingEntity entity) {

    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 0));
    }

    @Override
    protected float getSpiritualityCost() {
        return 10;
    }
}
