package de.jakob.lotm.abilities.visionary.prophecy.triggers;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.visionary.prophecy.TokenStream;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsHelper;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class TriggerHelper {
    private static @Nullable TriggerEnum getType(String str){
        return switch (str){
            case "on" -> TriggerEnum.POSITION;
            default -> null;
        };
    }

    private static TriggerContextEnum getContextType(TriggerEnum value){
        return switch (value){
            case TriggerEnum.POSITION -> TriggerContextEnum.POSITION;
        };
    }

    public static @Nullable TriggerBase deduceWithContext(String str, int casterSeq){
        TokenStream stream = new TokenStream(str);

        stream.next();

        LOTMCraft.LOGGER.info("INSIDE OF TR: nick: {}", stream.peek());

        String nick = stream.peek();
        UUID id = BeyonderData.playerMap.getKeyByName(nick);

        if(id == null) return null;

        var data = BeyonderData.playerMap.get(id).get();
        if(casterSeq > data.sequence() && data.pathway().equals("visionary"))
            return null;

        stream.next();
        var type = getType(Objects.requireNonNull(stream.peek()));

        LOTMCraft.LOGGER.info("INSIDE OF TR: type str: {}, type: {}", stream.peek(), type);
        if(type == null) return null;

        var contextType = getContextType(type);
        if(contextType == null) return null;

        LOTMCraft.LOGGER.info("INSIDE OF TR: stream: {}", stream.toString());

        var context = TriggerContextBase.create(contextType, id);
        context.fillFromStream(stream);


        LOTMCraft.LOGGER.info("INSIDE OF TR: deduce action");
        var action = ActionsHelper.deduceActionWithContext(str);
        if(action == null) return null;

        return TriggerBase.create(type, action, context);
    }

    public static Item getItemFromString(String input) {
        return ActionsHelper.getItemFromString(input);
    }
}
