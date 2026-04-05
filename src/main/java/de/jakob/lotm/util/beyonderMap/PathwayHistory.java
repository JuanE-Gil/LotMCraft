package de.jakob.lotm.util.beyonderMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;

/**
 * Stores which pathway was active at each sequence (index 0-9).
 * A null/empty entry means no switch occurred at that sequence — the player used their original pathway.
 */
public record PathwayHistory(String[] pathways) {

    public static final String NBT_PATHWAY_HISTORY = "pathway_history";

    public PathwayHistory() {
        this(new String[10]);
    }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        boolean any = false;
        for (int i = 9; i >= 0; i--) {
            String p = get(i);
            if (p != null) {
                sb.append("\n   Seq ").append(i).append(": ").append(p);
                any = true;
            }
        }
        return any ? sb.toString() : " None";
    }

    /** Returns the pathway at the given sequence, or null if none was recorded. */
    public String get(int sequence) {
        if (sequence < 0 || sequence >= pathways.length) return null;
        String val = pathways[sequence];
        return (val == null || val.isEmpty()) ? null : val;
    }

    /** Returns a new PathwayHistory with the given sequence set to the given pathway. */
    public PathwayHistory set(int sequence, String pathway) {
        String[] copy = Arrays.copyOf(pathways, pathways.length);
        copy[sequence] = pathway;
        return new PathwayHistory(copy);
    }

    /** Returns a new PathwayHistory with the entry at the given sequence cleared. */
    public PathwayHistory clear(int sequence) {
        String[] copy = Arrays.copyOf(pathways, pathways.length);
        copy[sequence] = null;
        return new PathwayHistory(copy);
    }

    /** Returns a new PathwayHistory with all entries cleared. */
    public PathwayHistory clear() {
        return new PathwayHistory();
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < pathways.length; i++) {
            list.add(StringTag.valueOf(pathways[i] == null ? "" : pathways[i]));
        }
        tag.put(NBT_PATHWAY_HISTORY, list);
        return tag;
    }

    public static PathwayHistory fromNBT(CompoundTag tag) {
        String[] pathways = new String[10];
        if (tag.contains(NBT_PATHWAY_HISTORY, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_PATHWAY_HISTORY, Tag.TAG_STRING);
            for (int i = 0; i < Math.min(list.size(), 10); i++) {
                String val = list.getString(i);
                pathways[i] = val.isEmpty() ? null : val;
            }
        }
        return new PathwayHistory(pathways);
    }
}
