package com.twof.automata;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TutorialMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("automata");
	public static final EntityType<AutomataEntity> AUTOMATA = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("automata", "automata"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AutomataEntity::new).dimensions(EntityDimensions.fixed(1f, 2f)).build()
	);
	public static final Item AUTOMATA_SPAWN_EGG = new SpawnEggItem(AUTOMATA, 12895428, 11382189, new Item.Settings().group(ItemGroup.MISC));



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		FabricDefaultAttributeRegistry.register(AUTOMATA, AutomataEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("automata", "automata_spawn_egg"), AUTOMATA_SPAWN_EGG);
	}
}
