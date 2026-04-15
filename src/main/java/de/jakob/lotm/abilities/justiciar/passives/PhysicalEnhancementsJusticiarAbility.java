package de.jakob.lotm.abilities.justiciar.passives;

import de.jakob.lotm.abilities.PhysicalEnhancementsAbility;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicalEnhancementsJusticiarAbility extends PhysicalEnhancementsAbility {

    public PhysicalEnhancementsJusticiarAbility(Properties properties) {
        super(properties);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 9));
    }

    @Override
    public List<PhysicalEnhancement> getEnhancements() {
        return List.of();
    }

    @Override
    protected List<PhysicalEnhancement> getEnhancementsForSequence(int sequenceLevel) {
        return switch (sequenceLevel) {
            case 9 -> List.of(
                    new PhysicalEnhancement(EnhancementType.STRENGTH, 2),
                    new PhysicalEnhancement(EnhancementType.SPEED, 2),
                    new PhysicalEnhancement(EnhancementType.REGENERATION, 1)
            );
            default -> List.of();
        };
    }

    @Override
    protected int getCurrentSequenceLevel(LivingEntity entity) {
        return BeyonderData.getSequence(entity);
    }
}