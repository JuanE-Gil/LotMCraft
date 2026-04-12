package de.jakob.lotm.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.UUID;

public class ControllingDataComponent {
    private boolean isControlling = false;
    private UUID ownerUUID = null;
    private String ownerName = null;
    private UUID bodyUUID = null;
    private UUID targetUUID = null;
    private CompoundTag targetEntity = null;
    private CompoundTag bodyEntity = null;

    public ControllingDataComponent() {}

    public boolean isControlling() {
        return isControlling;
    }

    public void setControlling(boolean isControlling) {
        this.isControlling = isControlling;
    }


    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID controllerUUID) {
        this.ownerUUID = controllerUUID;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String controllerUUID) {
        this.ownerName = controllerUUID;
    }


    public UUID getBodyUUID() {
        return bodyUUID;
    }

    public void setBodyUUID(UUID controllerUUID) {
        this.bodyUUID = controllerUUID;
    }


    public UUID getTargetUUID() {
        return targetUUID;
    }

    public void setTargetUUID(UUID controllerUUID) {
        this.targetUUID = controllerUUID;
    }


    public CompoundTag getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(CompoundTag controllerUUID) {
        this.targetEntity = controllerUUID;
    }


    public CompoundTag getBodyEntity() {
        return bodyEntity;
    }

    public void setBodyEntity(CompoundTag controllerUUID) {
        this.bodyEntity = controllerUUID;
    }


    public static final IAttachmentSerializer<CompoundTag, ControllingDataComponent> SERIALIZER =
            new IAttachmentSerializer<>() {
                @Override
                public ControllingDataComponent read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider lookup) {
                    ControllingDataComponent component = new ControllingDataComponent();
                    if (tag.hasUUID("ownerUUID")) component.ownerUUID = tag.getUUID("ownerUUID");
                    if (tag.contains("ownerName")) component.ownerName = tag.getString("ownerName");
                    if (tag.hasUUID("bodyUUID")) component.bodyUUID = tag.getUUID("bodyUUID");
                    if (tag.hasUUID("targetUUID")) component.targetUUID = tag.getUUID("targetUUID");
                    if (tag.contains("targetEntity")) component.targetEntity = tag.getCompound("targetEntity");
                    if (tag.contains("bodyEntity")) component.bodyEntity = tag.getCompound("bodyEntity");
                    return component;
                }

                @Override
                public CompoundTag write(ControllingDataComponent component, HolderLookup.Provider lookup) {
                    CompoundTag tag = new CompoundTag();
                    if (component.ownerUUID != null) tag.putUUID("ownerUUID", component.ownerUUID);
                    if (component.ownerName != null) tag.putString("ownerName", component.ownerName);
                    if (component.bodyUUID != null) tag.putUUID("bodyUUID", component.bodyUUID);
                    if (component.targetUUID != null) tag.putUUID("targetUUID", component.targetUUID);
                    if (component.targetEntity != null) tag.put("targetEntity", component.targetEntity);
                    if (component.bodyEntity != null) tag.put("bodyEntity", component.bodyEntity);
                    return tag;
                }
            };
}
