package de.jakob.lotm.abilities.visionary.prophecy.triggers;

import de.jakob.lotm.abilities.visionary.prophecy.TokenStream;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsHelper;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    public static @Nullable TriggerBase deduceWithContext(String str){
        TokenStream stream = new TokenStream(str);

        if(stream.match("if"))
            stream.next();

        String nick = stream.peek();
        UUID id = BeyonderData.beyonderMap.getKeyByName(nick);

        var type = getType(Objects.requireNonNull(stream.next()));
        if(type == null) return null;

        var contextType = getContextType(type);
        if(contextType == null) return null;

        var context = TriggerContextBase.create(contextType, id);
        context.fillFromStream(stream);

        var action = ActionsHelper.deduceActionWithContext(str);
        if(action == null) return null;

        return TriggerBase.create(type, action, context);
    }

    public static Item getItemFromString(String input) {
        return ActionsHelper.getItemFromString(input);
    }
}
