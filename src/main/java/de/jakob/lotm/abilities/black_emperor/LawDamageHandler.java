package de.jakob.lotm.abilities.black_emperor;

import de.jakob.lotm.LOTMCraft;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public final class LawDamageHandler {

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        System.out.println("LAW EVENT FIRED");

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        LivingEntity target = event.getEntity();

        if (!attacker.getPersistentData().getBoolean("lotm_law_active")) return;
        if (!target.getPersistentData().getBoolean("lotm_law_violated")) return;
        if (attacker.distanceTo(target) > 6.0f) return;

        event.setNewDamage(event.getNewDamage() * 1.5f);
    }
}
