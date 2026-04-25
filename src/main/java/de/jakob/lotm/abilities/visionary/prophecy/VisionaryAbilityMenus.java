package de.jakob.lotm.abilities.visionary.prophecy;

import de.jakob.lotm.gui.custom.TextDisplay.AbilityMenuBuilder;
import de.jakob.lotm.gui.custom.TextDisplay.ColoredTextDisplayScreen;
import de.jakob.lotm.gui.custom.TextDisplay.TextColorHelper;
import net.minecraft.client.gui.screens.Screen;

public class VisionaryAbilityMenus {

    public static ColoredTextDisplayScreen createStoryWritingAbilityMenu(Screen previousScreen) {
        return new AbilityMenuBuilder("Story Writing", previousScreen)
                .header("Overview", TextColorHelper.GOLD)
                .line("Write prophecies into existence. Type natural language commands", TextColorHelper.WHITE)
                .line("to create permanent story-based effects on targets.", TextColorHelper.WHITE)

                .spacing()
                .header("Stats", TextColorHelper.GOLD)
                .bullet("Spirituality Cost: 200 per use", TextColorHelper.YELLOW)
                .bullet("Requirement: Visionary Sequence 1 or lower", TextColorHelper.YELLOW)
                .bullet("Prophecy Limit: 5-80 per sequence level", TextColorHelper.GOLD)
                .bullet("Range: Unlimited", TextColorHelper.GOLD)

                .spacing()
                .header("How to Use", TextColorHelper.GOLD)
                .line("1. Activate the ability", TextColorHelper.WHITE)
                .line("2. Type in chat: <player_name> <trigger> then <action>", TextColorHelper.WHITE)
                .line("3. The prophecy is recorded and stored on the target", TextColorHelper.WHITE)

                .spacing()
                .header("Available Triggers", TextColorHelper.GOLD)
                .ability("instant", "Activates immediately when created", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("on <x y z>", "Triggers when player reaches coordinates", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("has <item>", "Triggers when player picks up item", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("health <amount>", "Triggers when player health drops below amount", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("sanity <amount>", "Triggers when sanity drops below amount", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("player <name>", "Triggers when another player is nearby", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("sealed", "Triggers when sealed artifact is opened", TextColorHelper.GOLD, TextColorHelper.WHITE)

                .spacing()
                .header("Available Actions", TextColorHelper.GOLD)
                .ability("health <number>", "Damage (negative) or heal (positive)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("sanity <number>", "Drain or restore sanity", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("teleport <x y z>", "Teleport target to coordinates", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("drop <item>", "Force target to drop item", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("digest <number>", "Modify digestion (Seq 4+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("health <number>", "Apply or remove health (Seq 3+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("sanity <number>", "Drain or restore sanity (Seq 3+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("stun", "Stun target (Seq 3+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("skill <name>", "Trigger skill effect (Seq 4+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("confusion", "Confuse target (Seq 5+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("seal", "Seal target (Seq 6+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("unseal", "Unseal target (Seq 6+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("calamity <type>", "Trigger calamity (Seq 2+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("spawn <entity>", "Spawn entity at target (Seq 1+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("say <message>", "Target says message in chat (Seq 1+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("weather <type>", "Change weather (rain/thunder/clear) (Seq 1+)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("time <value>", "Change world time (Seq 1+)", TextColorHelper.GOLD, TextColorHelper.WHITE)

                .spacing()
                .header("Examples", TextColorHelper.GOLD)
                .line("@Steve instant then health -15", TextColorHelper.CYAN)
                .line("@Alex on 100 64 200 then teleport 0 100 0", TextColorHelper.CYAN)
                .line("@Diana health 3 then sanity -5", TextColorHelper.CYAN)
                .line("@Bob has minecraft:golden_apple then say You picked up forbidden fruit", TextColorHelper.CYAN)
                .line("@Eve player @Frank then confusion", TextColorHelper.CYAN)
                .line("@Charlie sealed then spawn zombie", TextColorHelper.CYAN)

                .spacing()
                .separator()
                .spacing()
                .line("Press ESC to close this menu", TextColorHelper.DARK_GRAY)

                .build();
    }

    public static ColoredTextDisplayScreen createPsychologicalCueAbilityMenu(Screen previousScreen) {
        return new AbilityMenuBuilder("Psychological Cue", previousScreen)
                .header("Overview", TextColorHelper.GOLD)
                .line("Plant psychological cues in nearby players' minds.", TextColorHelper.WHITE)
                .line("Create immediate mental effects on targets within your range.", TextColorHelper.WHITE)

                .spacing()
                .header("Stats", TextColorHelper.GOLD)
                .bullet("Spirituality Cost: 5 per tick while active", TextColorHelper.YELLOW)
                .bullet("Requirement: Visionary Sequence 7 or lower", TextColorHelper.YELLOW)
                .bullet("Range by Sequence:", TextColorHelper.GOLD)
                .line("  Seq 7: 10 blocks | Seq 6: 20 | Seq 5: 30", TextColorHelper.YELLOW)
                .line("  Seq 4: 50 | Seq 3: 75 | Seq 2: 125 | Seq 1: 200 | Seq 0: 300", TextColorHelper.YELLOW)

                .spacing()
                .header("How to Use", TextColorHelper.GOLD)
                .line("1. Activate the ability", TextColorHelper.WHITE)
                .line("2. Type in chat: <player_name> <trigger> then <action>", TextColorHelper.WHITE)
                .line("3. Target must be within range or cue fails", TextColorHelper.WHITE)
                .line("4. Cannot cue significantly stronger targets", TextColorHelper.WHITE)

                .spacing()
                .header("Available Triggers", TextColorHelper.GOLD)
                .ability("instant", "Activates immediately when created", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("on <x y z>", "Triggers when player reaches coordinates", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("has <item>", "Triggers when player picks up item", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("health <amount>", "Triggers when player health drops below amount", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("sanity <amount>", "Triggers when sanity drops below amount", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("player <name>", "Triggers when another player is nearby", TextColorHelper.GOLD, TextColorHelper.WHITE)

                .spacing()
                .header("Available Actions", TextColorHelper.GOLD)
                .ability("health <number>", "Damage (negative) or heal (positive)", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("sanity <number>", "Drain or restore sanity", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("teleport <x y z>", "Teleport target to coordinates", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("stun", "Stun target", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("confusion", "Confuse target", TextColorHelper.GOLD, TextColorHelper.WHITE)
                .ability("say <message>", "Target says message in chat", TextColorHelper.GOLD, TextColorHelper.WHITE)

                .spacing()
                .header("Examples", TextColorHelper.GOLD)
                .line("@Steve instant then health -10", TextColorHelper.CYAN)
                .line("@Alex on 50 64 100 then sanity -3", TextColorHelper.CYAN)
                .line("@Diana health 2 then stun", TextColorHelper.CYAN)
                .line("@Bob has minecraft:apple then confusion", TextColorHelper.CYAN)

                .spacing()
                .warning("Target must be within distance limit or cue fails!")
                .warning("Attempting to cue stronger targets triggers 'Losing Control' debuff (25 seconds)")

                .spacing()
                .separator()
                .spacing()
                .line("Press ESC to close this menu", TextColorHelper.DARK_GRAY)

                .build();
    }
}