package de.jakob.lotm.abilities.black_emperor;

import de.jakob.lotm.util.BeyonderData;
import net.minecraft.world.entity.LivingEntity;

public final class BlackEmperorSanity {

    private BlackEmperorSanity() {
    }

    public static float applySanityLossResistance(LivingEntity entity, float sanityChange) {
        if (entity == null || sanityChange >= 0.0f) {
            return sanityChange; // only reduce LOSS, not gain
        }

        // ONLY Black Emperor
        if (!"black_emperor".equals(BeyonderData.getPathway(entity))) {
            return sanityChange;
        }

        int seq = BeyonderData.getSequence(entity);

        // only seq 8 → 0
        if (seq > 8 || seq < 0) {
            return sanityChange;
        }

        // seq 8 = 10% → seq 0 = 20%
        float resistance = 0.10f + ((8 - seq) * 0.0125f);

        return sanityChange * (1.0f - resistance);
    }
}
