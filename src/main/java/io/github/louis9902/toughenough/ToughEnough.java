package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;


public class ToughEnough implements ModInitializer {

    public static final String ID = "tough_enough";

    public static Identifier identifier(String name) {
        return new Identifier(ID, name);
    }

    @Override
    public void onInitialize() {
        ToughEnoughItems.register();
        ToughEnoughRegistries.register();
        ToughEnoughStatusEffects.register();
        ToughEnoughTags.register();
        ToughEnoughFluids.register();
        ToughEnoughBlockEntities.register();
        ToughEnoughScreenHandlers.register();
        ToughEnoughBlocks.register();
    }
}
