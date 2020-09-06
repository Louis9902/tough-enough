package io.github.louis9902.toughenough.client.fluid;

import io.github.louis9902.toughenough.init.ToughEnoughFluids;
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
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;

public class WaterTypeRenderer implements SimpleSynchronousResourceReloadListener, FluidRenderHandler {

    private final Identifier identifier;

    private final Identifier spriteStill;
    private final Identifier spriteFlow;
    private final int color;

    private final Sprite[] sprites = {null, null};

    private WaterTypeRenderer(Identifier id, int c) {
        identifier = new Identifier(id.getNamespace(), id.getPath() + "_reload_listener");
        spriteStill = new Identifier(id.getNamespace(), "block/" + id.getPath() + "_still");
        spriteFlow = new Identifier(id.getNamespace(), "block/" + id.getPath() + "_flow");
        color = c;
    }

    public static void register(Fluid still, Fluid flow, Identifier parent, int color) {
        WaterTypeRenderer renderer = new WaterTypeRenderer(parent, color);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(renderer);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(renderer::register);
        FluidRenderHandlerRegistry.INSTANCE.register(still, renderer);
        FluidRenderHandlerRegistry.INSTANCE.register(flow, renderer);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), still, flow);
    }

    private void register(SpriteAtlasTexture atlas, ClientSpriteRegistryCallback.Registry registry) {
        registry.register(spriteStill);
        registry.register(spriteFlow);
    }

    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    @Override
    public void apply(ResourceManager manager) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        sprites[0] = atlas.apply(spriteStill);
        sprites[1] = atlas.apply(spriteFlow);
    }

    @Override
    public Sprite[] getFluidSprites(BlockRenderView blockRenderView, BlockPos blockPos, FluidState fluidState) {
        return sprites;
    }

    @Override
    public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
        return color;
    }
}
