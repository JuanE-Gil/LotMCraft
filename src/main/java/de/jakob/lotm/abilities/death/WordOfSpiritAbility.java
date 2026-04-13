package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.helper.AbilityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class WordOfSpiritAbility extends Ability {

    private static final int DURATION_TICKS = 20 * 30; // 30 seconds

    public WordOfSpiritAbility(String id) {
        super(id, 20 * 45f); // 45-second cooldown
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 6));
    }

    @Override
    protected float getSpiritualityCost() {
        return 300;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 25, 1.5f);
        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.word_of_spirit.no_target").withColor(0xFF4444));
            return;
        }

        target.addEffect(new MobEffectInstance(ModEffects.SPIRIT_CALLED, DURATION_TICKS, 0, false, true, true));
        AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.word_of_spirit.applied").withColor(0x7ECFCF));
    }
}
