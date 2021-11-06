package com.twof.automata;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EntityTestingClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_AUTOMATA_LAYER = new EntityModelLayer(new Identifier("entitytesting", "automata"), "main");
    @Override
    public void onInitializeClient() {
        // In 1.17, use EntityRendererRegistry.register (seen below) instead of EntityRendererRegistry.INSTANCE.register (seen above)
        EntityRendererRegistry.register(TutorialMod.AUTOMATA, (context) -> {
            return new AutomataEntityRenderer(context, false);
        });

        EntityModelLayerRegistry.registerModelLayer(MODEL_AUTOMATA_LAYER, AutomataEntityModel::getTexturedModelData);
    }
}
