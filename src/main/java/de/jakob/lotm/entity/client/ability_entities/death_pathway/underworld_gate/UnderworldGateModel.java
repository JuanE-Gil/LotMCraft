package de.jakob.lotm.entity.client.ability_entities.death_pathway.underworld_gate;// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.client.spirits.bizarro_bane.SpiritBizarroBaneAnimations;
import de.jakob.lotm.entity.custom.ability_entities.death_pathway.UnderworldGateEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class UnderworldGateModel<T extends UnderworldGateEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "underworld_gate"), "main");
	private final ModelPart root;
	private final ModelPart full_gate;
	private final ModelPart door;
	private final ModelPart door2;
	private final ModelPart arch3;
	private final ModelPart arch5;
	private final ModelPart arch13;
	private final ModelPart arch14;
	private final ModelPart arch8;
	private final ModelPart arch9;
	private final ModelPart arch6;
	private final ModelPart arch7;
	private final ModelPart arch10;
	private final ModelPart arch12;
	private final ModelPart door3;
	private final ModelPart door_right;
	private final ModelPart arch_right;
	private final ModelPart arch16;
	private final ModelPart arch_right_1;
	private final ModelPart arch22;
	private final ModelPart arch_left_2;
	private final ModelPart arch15;
	private final ModelPart arch_right_2;
	private final ModelPart arch20;
	private final ModelPart arch_left_3_2;
	private final ModelPart arch_left_3_1;
	private final ModelPart arch_right_3_2;
	private final ModelPart arch_right_3_1;
	private final ModelPart door5;
	private final ModelPart door4;
	private final ModelPart door_left;
	private final ModelPart start_of_left_door;
	private final ModelPart arch24;
	private final ModelPart bone2;
	private final ModelPart bone;
	private final ModelPart arch17;
	private final ModelPart arch18;
	private final ModelPart arch25;
	private final ModelPart arch26;
	private final ModelPart left;
	private final ModelPart arch19;
	private final ModelPart bb_main;

	public UnderworldGateModel(ModelPart root) {
		this.root = root;
		this.full_gate = root.getChild("full_gate");
		this.door = this.full_gate.getChild("door");
		this.door2 = this.door.getChild("door2");
		this.arch3 = this.door.getChild("arch3");
		this.arch5 = this.arch3.getChild("arch5");
		this.arch13 = this.door.getChild("arch13");
		this.arch14 = this.arch13.getChild("arch14");
		this.arch8 = this.door.getChild("arch8");
		this.arch9 = this.arch8.getChild("arch9");
		this.arch6 = this.door.getChild("arch6");
		this.arch7 = this.arch6.getChild("arch7");
		this.arch10 = this.door.getChild("arch10");
		this.arch12 = this.arch10.getChild("arch12");
		this.door3 = this.full_gate.getChild("door3");
		this.door_right = this.door3.getChild("door_right");
		this.arch_right = this.door_right.getChild("arch_right");
		this.arch16 = this.arch_right.getChild("arch16");
		this.arch_right_1 = this.door_right.getChild("arch_right_1");
		this.arch22 = this.arch_right_1.getChild("arch22");
		this.arch_left_2 = this.door_right.getChild("arch_left_2");
		this.arch15 = this.arch_left_2.getChild("arch15");
		this.arch_right_2 = this.door_right.getChild("arch_right_2");
		this.arch20 = this.arch_right_2.getChild("arch20");
		this.arch_left_3_2 = this.door_right.getChild("arch_left_3_2");
		this.arch_left_3_1 = this.arch_left_3_2.getChild("arch_left_3_1");
		this.arch_right_3_2 = this.door_right.getChild("arch_right_3_2");
		this.arch_right_3_1 = this.arch_right_3_2.getChild("arch_right_3_1");
		this.door5 = this.door_right.getChild("door5");
		this.door4 = this.door_right.getChild("door4");
		this.door_left = this.door3.getChild("door_left");
		this.start_of_left_door = this.door_left.getChild("start_of_left_door");
		this.arch24 = this.start_of_left_door.getChild("arch24");
		this.bone2 = this.door_left.getChild("bone2");
		this.bone = this.door_left.getChild("bone");
		this.arch17 = this.door_left.getChild("arch17");
		this.arch18 = this.arch17.getChild("arch18");
		this.arch25 = this.door_left.getChild("arch25");
		this.arch26 = this.arch25.getChild("arch26");
		this.left = this.door_left.getChild("left");
		this.arch19 = this.left.getChild("arch19");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition full_gate = partdefinition.addOrReplaceChild("full_gate", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition door = full_gate.addOrReplaceChild("door", CubeListBuilder.create().texOffs(0, 54).addBox(0.5F, 8.0F, -4.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(18, 27).addBox(0.5F, -9.0F, -5.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, -10).addBox(0.5F, -17.0F, -4.0F, 0.0F, 25.0F, 18.0F, new CubeDeformation(0.1F))
				.texOffs(0, 0).addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-1.0F, -28.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -5.0F));

		PartDefinition door2 = door.addOrReplaceChild("door2", CubeListBuilder.create().texOffs(22, 40).addBox(0.5F, -9.0F, 4.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 54).addBox(0.5F, 8.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 10.0F));

		PartDefinition cube_r1 = door2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -18.0F, -5.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -21.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r2 = door2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -21.0F, -5.0F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -20.0F, 9.4F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r3 = door2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -21.0F, -5.0F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r4 = door2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -22.0F, -5.0F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r5 = door2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(7, 7).addBox(-0.5F, -22.0F, -5.0F, 1.0F, 18.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -16.0F, 8.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r6 = door2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(7, 7).addBox(-0.5F, -22.0F, -5.0F, 1.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -16.0F, 7.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition arch3 = door.addOrReplaceChild("arch3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.2F, -16.1F, 13.1F, 3.0117F, -0.0173F, -3.1166F));

		PartDefinition cube_r23 = arch3.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r24 = arch3.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r25 = arch3.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r26 = arch3.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r27 = arch3.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch5 = arch3.addOrReplaceChild("arch5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r28 = arch5.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r29 = arch5.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r30 = arch5.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r31 = arch5.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r32 = arch5.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r33 = arch5.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r34 = arch5.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r35 = arch5.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r36 = arch5.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r37 = arch5.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r38 = arch5.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch13 = door.addOrReplaceChild("arch13", CubeListBuilder.create(), PartPose.offsetAndRotation(0.2F, -16.1F, 13.1F, 3.0117F, -0.0173F, -3.1166F));

		PartDefinition cube_r39 = arch13.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r40 = arch13.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r41 = arch13.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r42 = arch13.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r43 = arch13.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch14 = arch13.addOrReplaceChild("arch14", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r44 = arch14.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r45 = arch14.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r46 = arch14.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r47 = arch14.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r48 = arch14.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r49 = arch14.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r50 = arch14.addOrReplaceChild("cube_r50", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r51 = arch14.addOrReplaceChild("cube_r51", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r52 = arch14.addOrReplaceChild("cube_r52", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r53 = arch14.addOrReplaceChild("cube_r53", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r54 = arch14.addOrReplaceChild("cube_r54", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch8 = door.addOrReplaceChild("arch8", CubeListBuilder.create(), PartPose.offsetAndRotation(1.2F, -16.1F, 13.1F, 3.0117F, -0.0173F, -3.1166F));

		PartDefinition cube_r55 = arch8.addOrReplaceChild("cube_r55", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r56 = arch8.addOrReplaceChild("cube_r56", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r57 = arch8.addOrReplaceChild("cube_r57", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r58 = arch8.addOrReplaceChild("cube_r58", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r59 = arch8.addOrReplaceChild("cube_r59", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch9 = arch8.addOrReplaceChild("arch9", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r60 = arch9.addOrReplaceChild("cube_r60", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r61 = arch9.addOrReplaceChild("cube_r61", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r62 = arch9.addOrReplaceChild("cube_r62", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r63 = arch9.addOrReplaceChild("cube_r63", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r64 = arch9.addOrReplaceChild("cube_r64", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r65 = arch9.addOrReplaceChild("cube_r65", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r66 = arch9.addOrReplaceChild("cube_r66", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r67 = arch9.addOrReplaceChild("cube_r67", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r68 = arch9.addOrReplaceChild("cube_r68", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r69 = arch9.addOrReplaceChild("cube_r69", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r70 = arch9.addOrReplaceChild("cube_r70", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch6 = door.addOrReplaceChild("arch6", CubeListBuilder.create(), PartPose.offsetAndRotation(0.2F, -15.9F, -3.2F, -0.1053F, -0.0262F, 0.0399F));

		PartDefinition cube_r71 = arch6.addOrReplaceChild("cube_r71", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r72 = arch6.addOrReplaceChild("cube_r72", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r73 = arch6.addOrReplaceChild("cube_r73", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r74 = arch6.addOrReplaceChild("cube_r74", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r75 = arch6.addOrReplaceChild("cube_r75", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch7 = arch6.addOrReplaceChild("arch7", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.6371F, 0.014F, -0.0104F));

		PartDefinition cube_r76 = arch7.addOrReplaceChild("cube_r76", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r77 = arch7.addOrReplaceChild("cube_r77", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r78 = arch7.addOrReplaceChild("cube_r78", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r79 = arch7.addOrReplaceChild("cube_r79", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r80 = arch7.addOrReplaceChild("cube_r80", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r81 = arch7.addOrReplaceChild("cube_r81", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1612F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r82 = arch7.addOrReplaceChild("cube_r82", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r83 = arch7.addOrReplaceChild("cube_r83", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r84 = arch7.addOrReplaceChild("cube_r84", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r85 = arch7.addOrReplaceChild("cube_r85", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r86 = arch7.addOrReplaceChild("cube_r86", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch10 = door.addOrReplaceChild("arch10", CubeListBuilder.create(), PartPose.offsetAndRotation(1.2F, -15.9F, -3.2F, -0.1053F, -0.0262F, 0.0399F));

		PartDefinition cube_r87 = arch10.addOrReplaceChild("cube_r87", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r88 = arch10.addOrReplaceChild("cube_r88", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r89 = arch10.addOrReplaceChild("cube_r89", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r90 = arch10.addOrReplaceChild("cube_r90", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, 0.5F, -2.6F, 1.6754F, 0.004F, -0.0347F));

		PartDefinition cube_r91 = arch10.addOrReplaceChild("cube_r91", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r92 = arch10.addOrReplaceChild("cube_r92", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch12 = arch10.addOrReplaceChild("arch12", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.6371F, 0.014F, -0.0104F));

		PartDefinition cube_r93 = arch12.addOrReplaceChild("cube_r93", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r94 = arch12.addOrReplaceChild("cube_r94", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r95 = arch12.addOrReplaceChild("cube_r95", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r96 = arch12.addOrReplaceChild("cube_r96", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r97 = arch12.addOrReplaceChild("cube_r97", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4935F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r98 = arch12.addOrReplaceChild("cube_r98", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1612F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r99 = arch12.addOrReplaceChild("cube_r99", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r100 = arch12.addOrReplaceChild("cube_r100", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r101 = arch12.addOrReplaceChild("cube_r101", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r102 = arch12.addOrReplaceChild("cube_r102", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r103 = arch12.addOrReplaceChild("cube_r103", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition door3 = full_gate.addOrReplaceChild("door3", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, -5.0F));

		PartDefinition door_right = door3.addOrReplaceChild("door_right", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.2F, -6.1F, 14.35F, 0.0F, -2.3562F, 0.0F));

		PartDefinition arch_right = door_right.addOrReplaceChild("arch_right", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -10.0F, -2.0F, 3.0117F, 0.0173F, 3.1166F));

		PartDefinition cube_r104 = arch_right.addOrReplaceChild("cube_r104", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, -0.0158F, 0.0074F));

		PartDefinition cube_r105 = arch_right.addOrReplaceChild("cube_r105", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r106 = arch_right.addOrReplaceChild("cube_r106", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r107 = arch_right.addOrReplaceChild("cube_r107", CubeListBuilder.create().texOffs(6, 33).mirror().addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(7, 34).mirror().addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r108 = arch_right.addOrReplaceChild("cube_r108", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5988F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, -0.0183F, 0.0165F));

		PartDefinition arch16 = arch_right.addOrReplaceChild("arch16", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r109 = arch16.addOrReplaceChild("cube_r109", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r110 = arch16.addOrReplaceChild("cube_r110", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r111 = arch16.addOrReplaceChild("cube_r111", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r112 = arch16.addOrReplaceChild("cube_r112", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r113 = arch16.addOrReplaceChild("cube_r113", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r114 = arch16.addOrReplaceChild("cube_r114", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5026F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, 0.0098F, -0.0025F));

		PartDefinition cube_r115 = arch16.addOrReplaceChild("cube_r115", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r116 = arch16.addOrReplaceChild("cube_r116", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r117 = arch16.addOrReplaceChild("cube_r117", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r118 = arch16.addOrReplaceChild("cube_r118", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r119 = arch16.addOrReplaceChild("cube_r119", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch_right_1 = door_right.addOrReplaceChild("arch_right_1", CubeListBuilder.create(), PartPose.offsetAndRotation(2.4F, -10.0F, -2.0F, 3.0117F, -0.0173F, -3.1166F));

		PartDefinition cube_r120 = arch_right_1.addOrReplaceChild("cube_r120", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r121 = arch_right_1.addOrReplaceChild("cube_r121", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r122 = arch_right_1.addOrReplaceChild("cube_r122", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r123 = arch_right_1.addOrReplaceChild("cube_r123", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r124 = arch_right_1.addOrReplaceChild("cube_r124", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch22 = arch_right_1.addOrReplaceChild("arch22", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r125 = arch22.addOrReplaceChild("cube_r125", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r126 = arch22.addOrReplaceChild("cube_r126", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r127 = arch22.addOrReplaceChild("cube_r127", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r128 = arch22.addOrReplaceChild("cube_r128", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r129 = arch22.addOrReplaceChild("cube_r129", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r130 = arch22.addOrReplaceChild("cube_r130", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r131 = arch22.addOrReplaceChild("cube_r131", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r132 = arch22.addOrReplaceChild("cube_r132", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r133 = arch22.addOrReplaceChild("cube_r133", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r134 = arch22.addOrReplaceChild("cube_r134", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r135 = arch22.addOrReplaceChild("cube_r135", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch_left_2 = door_right.addOrReplaceChild("arch_left_2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -10.0F, -2.0F, 3.0117F, 0.0173F, 3.1166F));

		PartDefinition cube_r136 = arch_left_2.addOrReplaceChild("cube_r136", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, -0.0158F, 0.0074F));

		PartDefinition cube_r137 = arch_left_2.addOrReplaceChild("cube_r137", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r138 = arch_left_2.addOrReplaceChild("cube_r138", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r139 = arch_left_2.addOrReplaceChild("cube_r139", CubeListBuilder.create().texOffs(6, 33).mirror().addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(7, 34).mirror().addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r140 = arch_left_2.addOrReplaceChild("cube_r140", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5988F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, -0.0183F, 0.0165F));

		PartDefinition arch15 = arch_left_2.addOrReplaceChild("arch15", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r141 = arch15.addOrReplaceChild("cube_r141", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r142 = arch15.addOrReplaceChild("cube_r142", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r143 = arch15.addOrReplaceChild("cube_r143", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r144 = arch15.addOrReplaceChild("cube_r144", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r145 = arch15.addOrReplaceChild("cube_r145", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r146 = arch15.addOrReplaceChild("cube_r146", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5026F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, 0.0098F, -0.0025F));

		PartDefinition cube_r147 = arch15.addOrReplaceChild("cube_r147", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r148 = arch15.addOrReplaceChild("cube_r148", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r149 = arch15.addOrReplaceChild("cube_r149", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r150 = arch15.addOrReplaceChild("cube_r150", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r151 = arch15.addOrReplaceChild("cube_r151", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch_right_2 = door_right.addOrReplaceChild("arch_right_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.4F, -10.0F, -2.0F, 3.0117F, -0.0173F, -3.1166F));

		PartDefinition cube_r152 = arch_right_2.addOrReplaceChild("cube_r152", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r153 = arch_right_2.addOrReplaceChild("cube_r153", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r154 = arch_right_2.addOrReplaceChild("cube_r154", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r155 = arch_right_2.addOrReplaceChild("cube_r155", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r156 = arch_right_2.addOrReplaceChild("cube_r156", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch20 = arch_right_2.addOrReplaceChild("arch20", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r157 = arch20.addOrReplaceChild("cube_r157", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r158 = arch20.addOrReplaceChild("cube_r158", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r159 = arch20.addOrReplaceChild("cube_r159", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r160 = arch20.addOrReplaceChild("cube_r160", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r161 = arch20.addOrReplaceChild("cube_r161", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r162 = arch20.addOrReplaceChild("cube_r162", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r163 = arch20.addOrReplaceChild("cube_r163", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r164 = arch20.addOrReplaceChild("cube_r164", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r165 = arch20.addOrReplaceChild("cube_r165", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r166 = arch20.addOrReplaceChild("cube_r166", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r167 = arch20.addOrReplaceChild("cube_r167", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch_left_3_2 = door_right.addOrReplaceChild("arch_left_3_2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -10.0F, -2.0F, 3.0117F, 0.0173F, 3.1166F));

		PartDefinition cube_r168 = arch_left_3_2.addOrReplaceChild("cube_r168", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, -0.0158F, 0.0074F));

		PartDefinition cube_r169 = arch_left_3_2.addOrReplaceChild("cube_r169", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r170 = arch_left_3_2.addOrReplaceChild("cube_r170", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r171 = arch_left_3_2.addOrReplaceChild("cube_r171", CubeListBuilder.create().texOffs(6, 33).mirror().addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(7, 34).mirror().addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r172 = arch_left_3_2.addOrReplaceChild("cube_r172", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5988F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, -0.0183F, 0.0165F));

		PartDefinition arch_left_3_1 = arch_left_3_2.addOrReplaceChild("arch_left_3_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r173 = arch_left_3_1.addOrReplaceChild("cube_r173", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r174 = arch_left_3_1.addOrReplaceChild("cube_r174", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r175 = arch_left_3_1.addOrReplaceChild("cube_r175", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r176 = arch_left_3_1.addOrReplaceChild("cube_r176", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r177 = arch_left_3_1.addOrReplaceChild("cube_r177", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r178 = arch_left_3_1.addOrReplaceChild("cube_r178", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5026F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, 0.0098F, -0.0025F));

		PartDefinition cube_r179 = arch_left_3_1.addOrReplaceChild("cube_r179", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r180 = arch_left_3_1.addOrReplaceChild("cube_r180", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r181 = arch_left_3_1.addOrReplaceChild("cube_r181", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r182 = arch_left_3_1.addOrReplaceChild("cube_r182", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r183 = arch_left_3_1.addOrReplaceChild("cube_r183", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch_right_3_2 = door_right.addOrReplaceChild("arch_right_3_2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.4F, -10.0F, -2.0F, 3.0117F, -0.0173F, -3.1166F));

		PartDefinition cube_r184 = arch_right_3_2.addOrReplaceChild("cube_r184", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r185 = arch_right_3_2.addOrReplaceChild("cube_r185", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r186 = arch_right_3_2.addOrReplaceChild("cube_r186", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r187 = arch_right_3_2.addOrReplaceChild("cube_r187", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r188 = arch_right_3_2.addOrReplaceChild("cube_r188", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch_right_3_1 = arch_right_3_2.addOrReplaceChild("arch_right_3_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.637F, 0.0F, 0.0F));

		PartDefinition cube_r189 = arch_right_3_1.addOrReplaceChild("cube_r189", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r190 = arch_right_3_1.addOrReplaceChild("cube_r190", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r191 = arch_right_3_1.addOrReplaceChild("cube_r191", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r192 = arch_right_3_1.addOrReplaceChild("cube_r192", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r193 = arch_right_3_1.addOrReplaceChild("cube_r193", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r194 = arch_right_3_1.addOrReplaceChild("cube_r194", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1613F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r195 = arch_right_3_1.addOrReplaceChild("cube_r195", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r196 = arch_right_3_1.addOrReplaceChild("cube_r196", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r197 = arch_right_3_1.addOrReplaceChild("cube_r197", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r198 = arch_right_3_1.addOrReplaceChild("cube_r198", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r199 = arch_right_3_1.addOrReplaceChild("cube_r199", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition door5 = door_right.addOrReplaceChild("door5", CubeListBuilder.create().texOffs(22, 40).mirror().addBox(-1.5F, -9.0F, 4.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 54).mirror().addBox(-1.5F, 8.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 0).mirror().addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 0).mirror().addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.2F, 6.1F, -5.1F));

		PartDefinition cube_r200 = door5.addOrReplaceChild("cube_r200", CubeListBuilder.create().texOffs(9, 9).mirror().addBox(-0.5F, -13.7F, -5.0F, 1.0F, 8.7F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -19.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r201 = door5.addOrReplaceChild("cube_r201", CubeListBuilder.create().texOffs(9, 9).mirror().addBox(-0.5F, -13.7F, -5.0F, 1.0F, 4.7F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -21.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r202 = door5.addOrReplaceChild("cube_r202", CubeListBuilder.create().texOffs(9, 9).mirror().addBox(-0.5F, -14.399F, -5.0F, 1.0F, 7.399F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -20.0F, 9.4F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r203 = door5.addOrReplaceChild("cube_r203", CubeListBuilder.create().texOffs(7, 7).mirror().addBox(-0.5F, -13.499F, -5.0F, 1.0F, 9.499F, 3.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -16.0F, 8.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition door4 = door_right.addOrReplaceChild("door4", CubeListBuilder.create().texOffs(22, 40).addBox(0.5F, -9.0F, 4.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 54).addBox(0.5F, 8.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, -11.0F, -5.0F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.2F, 6.1F, -5.1F));

		PartDefinition cube_r204 = door4.addOrReplaceChild("cube_r204", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -13.7F, -5.0F, 1.0F, 8.7F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r205 = door4.addOrReplaceChild("cube_r205", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -13.7F, -5.0F, 1.0F, 4.7F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -21.0F, 8.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r206 = door4.addOrReplaceChild("cube_r206", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -14.399F, -5.0F, 1.0F, 7.399F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -20.0F, 9.4F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r207 = door4.addOrReplaceChild("cube_r207", CubeListBuilder.create().texOffs(7, 7).addBox(-0.5F, -13.499F, -5.0F, 1.0F, 9.499F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -16.0F, 8.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition door_left = door3.addOrReplaceChild("door_left", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.55F, -5.9F, -4.6F, 0.0F, 2.3562F, 0.0F));

		PartDefinition start_of_left_door = door_left.addOrReplaceChild("start_of_left_door", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -10.0F, 2.0F, -0.1053F, -0.0262F, 0.0399F));

		PartDefinition cube_r208 = start_of_left_door.addOrReplaceChild("cube_r208", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r209 = start_of_left_door.addOrReplaceChild("cube_r209", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r210 = start_of_left_door.addOrReplaceChild("cube_r210", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r211 = start_of_left_door.addOrReplaceChild("cube_r211", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r212 = start_of_left_door.addOrReplaceChild("cube_r212", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch24 = start_of_left_door.addOrReplaceChild("arch24", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.6371F, 0.014F, -0.0104F));

		PartDefinition cube_r213 = arch24.addOrReplaceChild("cube_r213", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r214 = arch24.addOrReplaceChild("cube_r214", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r215 = arch24.addOrReplaceChild("cube_r215", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r216 = arch24.addOrReplaceChild("cube_r216", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r217 = arch24.addOrReplaceChild("cube_r217", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r218 = arch24.addOrReplaceChild("cube_r218", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1612F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r219 = arch24.addOrReplaceChild("cube_r219", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r220 = arch24.addOrReplaceChild("cube_r220", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r221 = arch24.addOrReplaceChild("cube_r221", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r222 = arch24.addOrReplaceChild("cube_r222", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r223 = arch24.addOrReplaceChild("cube_r223", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition bone2 = door_left.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 54).mirror().addBox(-1.5F, 28.0F, -23.4F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(18, 27).mirror().addBox(-1.5F, 11.0F, -24.4F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 0).mirror().addBox(-0.5F, 9.0F, -24.4F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.8F, -14.1F, 24.6F));

		PartDefinition cube_r224 = bone2.addOrReplaceChild("cube_r224", CubeListBuilder.create().texOffs(7, 7).mirror().addBox(-0.5F, -22.501F, -5.0F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 4.0F, -0.9F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r225 = bone2.addOrReplaceChild("cube_r225", CubeListBuilder.create().texOffs(9, 9).mirror().addBox(-0.5F, -18.0F, -5.0F, 1.0F, 4.3F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, -0.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r226 = bone2.addOrReplaceChild("cube_r226", CubeListBuilder.create().texOffs(9, 9).mirror().addBox(-0.5F, -22.0F, -5.0F, 1.0F, 8.3F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, -0.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r227 = bone2.addOrReplaceChild("cube_r227", CubeListBuilder.create().texOffs(9, 9).mirror().addBox(-0.5F, -21.401F, -5.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition bone = door_left.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 54).addBox(0.5F, 28.0F, -23.4F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(18, 27).addBox(0.5F, 11.0F, -24.4F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, 9.0F, -24.4F, 1.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.8F, -14.1F, 24.6F));

		PartDefinition cube_r228 = bone.addOrReplaceChild("cube_r228", CubeListBuilder.create().texOffs(7, 7).addBox(-0.5F, -22.501F, -5.0F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 4.0F, -0.9F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r229 = bone.addOrReplaceChild("cube_r229", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -18.0F, -5.0F, 1.0F, 4.3F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -0.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r230 = bone.addOrReplaceChild("cube_r230", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -22.0F, -5.0F, 1.0F, 8.3F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -0.7F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r231 = bone.addOrReplaceChild("cube_r231", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -21.401F, -5.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition arch17 = door_left.addOrReplaceChild("arch17", CubeListBuilder.create(), PartPose.offsetAndRotation(0.6F, -10.0F, 2.0F, -0.1053F, 0.0262F, -0.0399F));

		PartDefinition cube_r232 = arch17.addOrReplaceChild("cube_r232", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, -0.0158F, 0.0074F));

		PartDefinition cube_r233 = arch17.addOrReplaceChild("cube_r233", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r234 = arch17.addOrReplaceChild("cube_r234", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r235 = arch17.addOrReplaceChild("cube_r235", CubeListBuilder.create().texOffs(6, 33).mirror().addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.2F, 0.5F, -2.6F, 1.6754F, -0.004F, 0.0347F));

		PartDefinition cube_r236 = arch17.addOrReplaceChild("cube_r236", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r237 = arch17.addOrReplaceChild("cube_r237", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5988F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, -0.0183F, 0.0165F));

		PartDefinition arch18 = arch17.addOrReplaceChild("arch18", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.6371F, -0.014F, 0.0104F));

		PartDefinition cube_r238 = arch18.addOrReplaceChild("cube_r238", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r239 = arch18.addOrReplaceChild("cube_r239", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r240 = arch18.addOrReplaceChild("cube_r240", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r241 = arch18.addOrReplaceChild("cube_r241", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r242 = arch18.addOrReplaceChild("cube_r242", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5065F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r243 = arch18.addOrReplaceChild("cube_r243", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5026F, -0.411F, -2.1612F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, 0.0098F, -0.0025F));

		PartDefinition cube_r244 = arch18.addOrReplaceChild("cube_r244", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r245 = arch18.addOrReplaceChild("cube_r245", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r246 = arch18.addOrReplaceChild("cube_r246", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r247 = arch18.addOrReplaceChild("cube_r247", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r248 = arch18.addOrReplaceChild("cube_r248", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition arch25 = door_left.addOrReplaceChild("arch25", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -10.0F, 2.0F, -0.1053F, -0.0262F, 0.0399F));

		PartDefinition cube_r249 = arch25.addOrReplaceChild("cube_r249", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, 0.0158F, -0.0074F));

		PartDefinition cube_r250 = arch25.addOrReplaceChild("cube_r250", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r251 = arch25.addOrReplaceChild("cube_r251", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r252 = arch25.addOrReplaceChild("cube_r252", CubeListBuilder.create().texOffs(6, 33).addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, 0.5F, -2.6F, 1.6754F, 0.004F, -0.0347F));

		PartDefinition cube_r253 = arch25.addOrReplaceChild("cube_r253", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r254 = arch25.addOrReplaceChild("cube_r254", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4012F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, 0.0183F, -0.0165F));

		PartDefinition arch26 = arch25.addOrReplaceChild("arch26", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.6371F, 0.014F, -0.0104F));

		PartDefinition cube_r255 = arch26.addOrReplaceChild("cube_r255", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r256 = arch26.addOrReplaceChild("cube_r256", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, -0.0098F, 0.0025F));

		PartDefinition cube_r257 = arch26.addOrReplaceChild("cube_r257", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r258 = arch26.addOrReplaceChild("cube_r258", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r259 = arch26.addOrReplaceChild("cube_r259", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4936F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, -0.0098F, 0.0025F));

		PartDefinition cube_r260 = arch26.addOrReplaceChild("cube_r260", CubeListBuilder.create().texOffs(7, 34).addBox(-0.4974F, -0.411F, -2.1612F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, -0.0098F, 0.0025F));

		PartDefinition cube_r261 = arch26.addOrReplaceChild("cube_r261", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r262 = arch26.addOrReplaceChild("cube_r262", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r263 = arch26.addOrReplaceChild("cube_r263", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r264 = arch26.addOrReplaceChild("cube_r264", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r265 = arch26.addOrReplaceChild("cube_r265", CubeListBuilder.create().texOffs(7, 34).addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition left = door_left.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offsetAndRotation(1.6F, -10.0F, 2.0F, -0.1053F, 0.0262F, -0.0399F));

		PartDefinition cube_r266 = left.addOrReplaceChild("cube_r266", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.7237F, -4.0611F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1344F, -0.0158F, 0.0074F));

		PartDefinition cube_r267 = left.addOrReplaceChild("cube_r267", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.9076F, -3.9617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r268 = left.addOrReplaceChild("cube_r268", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0225F, -3.9139F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4661F, 0.0F, 0.0F));

		PartDefinition cube_r269 = left.addOrReplaceChild("cube_r269", CubeListBuilder.create().texOffs(6, 33).mirror().addBox(-0.5F, 0.828F, -6.9523F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(7, 34).mirror().addBox(-0.5F, 0.828F, -4.9523F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -2.7F, 1.7017F, 0.0F, 0.0F));

		PartDefinition cube_r270 = left.addOrReplaceChild("cube_r270", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5988F, -8.8316F, -3.5734F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, 7.3F, 1.6229F, -0.0183F, 0.0165F));

		PartDefinition arch19 = left.addOrReplaceChild("arch19", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.1F, 3.2F, -0.6371F, -0.014F, 0.0104F));

		PartDefinition cube_r271 = arch19.addOrReplaceChild("cube_r271", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.8F, 3.6F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r272 = arch19.addOrReplaceChild("cube_r272", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.2F, 3.0F, 0.8112F, 0.0098F, -0.0025F));

		PartDefinition cube_r273 = arch19.addOrReplaceChild("cube_r273", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2616F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.5F, 2.3F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r274 = arch19.addOrReplaceChild("cube_r274", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.8F, 1.7F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r275 = arch19.addOrReplaceChild("cube_r275", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5064F, -0.3884F, -1.2617F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8461F, 0.0098F, -0.0025F));

		PartDefinition cube_r276 = arch19.addOrReplaceChild("cube_r276", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5026F, -0.411F, -2.1612F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 0.8984F, 0.0098F, -0.0025F));

		PartDefinition cube_r277 = arch19.addOrReplaceChild("cube_r277", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -0.6146F, -3.0625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.0123F, 0.0F, 0.0F));

		PartDefinition cube_r278 = arch19.addOrReplaceChild("cube_r278", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.084F, -3.9454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.2F, 1.2F, 1.1781F, 0.0F, 0.0F));

		PartDefinition cube_r279 = arch19.addOrReplaceChild("cube_r279", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0814F, -3.9419F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r280 = arch19.addOrReplaceChild("cube_r280", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition cube_r281 = arch19.addOrReplaceChild("cube_r281", CubeListBuilder.create().texOffs(7, 34).mirror().addBox(-0.5F, 0.0707F, -4.0707F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.5F, -1.7F, 1.5533F, 0.0F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -12.0F, 23.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(UnderworldGateEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.openAnimationState, UnderworldGateAnimations.open, ageInTicks, 1.0F);
	}

	@Override
	public @NotNull ModelPart root() {
		return root;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		full_gate.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}