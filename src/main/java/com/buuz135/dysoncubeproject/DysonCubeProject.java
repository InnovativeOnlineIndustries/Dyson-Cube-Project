package com.buuz135.dysoncubeproject;

import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.block.tile.RayReceiverBlockEntity;
import com.buuz135.dysoncubeproject.client.ClientSetup;
import com.buuz135.dysoncubeproject.datagen.*;
import com.buuz135.dysoncubeproject.network.ClientSubscribeSphereMessage;
import com.buuz135.dysoncubeproject.network.DysonSphereSyncMessage;
import com.buuz135.dysoncubeproject.world.DysonSphereConfiguration;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


@Mod(DysonCubeProject.MODID)
public class DysonCubeProject extends ModuleController {

    public static final String MODID = "dysoncubeproject";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static NetworkHandler NETWORK = new NetworkHandler(MODID);

    public DysonCubeProject(Dist dist, IEventBus modEventBus, ModContainer modContainer) {
        super(modContainer);
        NETWORK.registerMessage("dyson_sphere_sync", DysonSphereSyncMessage.class);
        NETWORK.registerMessage("client_subscribe_sphere", ClientSubscribeSphereMessage.class);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if (dist == Dist.CLIENT) ClientSetup.init();

        EventManager.forge(LevelTickEvent.Pre.class).process(post -> {
            if (post.getLevel() instanceof ServerLevel serverLevel && serverLevel.dimensionTypeRegistration().getRegisteredName().equals(BuiltinDimensionTypes.OVERWORLD.location().toString())) {
                var data = DysonSphereProgressSavedData.get(serverLevel);
                if (post.getLevel().getGameTime() % 4 == 0) {
                    var packet = new DysonSphereSyncMessage(data.save(new CompoundTag(), serverLevel.getServer().registryAccess()));
                    for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                        NETWORK.sendTo(packet, player);
                    }
                }
                data.getSpheres().values().forEach(DysonSphereConfiguration::generatePower);
                data.setDirty();
            }
        }).subscribe();
        EventManager.mod(RegisterCapabilitiesEvent.class).process(event -> {
            event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, blockPos, blockState, blockEntity, direction) -> {
                if (level instanceof ServerLevel serverLevel && blockEntity instanceof EMRailEjectorBlockEntity emRailEjectorBlockEntity && direction == Direction.DOWN) {
                    return emRailEjectorBlockEntity.getInput();
                }
                return null;
            }, DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.getBlock());
            event.registerBlock(Capabilities.EnergyStorage.BLOCK, (level, blockPos, blockState, blockEntity, direction) -> {
                if (level instanceof ServerLevel serverLevel && blockEntity instanceof RayReceiverBlockEntity rayReceiverBlockEntity && direction == Direction.DOWN) {
                    return rayReceiverBlockEntity.getEnergyStorageComponent();
                }
                return null;
            }, DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.getBlock());
        }).subscribe();
        EventManager.forge(RegisterCommandsEvent.class).process(event -> {
            var dispatcher = event.getDispatcher();
            SuggestionProvider<CommandSourceStack> sphereIdSuggestions = (ctx, builder) -> {
                var level = ctx.getSource().getLevel();
                var data = DysonSphereProgressSavedData.get(level);
                if (data != null) {
                    return SharedSuggestionProvider.suggest(data.getSpheres().keySet(), builder);
                }
                return SharedSuggestionProvider.suggest(java.util.List.of(), builder);
            };
            dispatcher.register(Commands.literal("dysoncubeproject")
                    .requires(source -> source.hasPermission(4))
                    .then(Commands.literal("set")
                            .then(Commands.literal("beams")
                                    .then(Commands.argument("sphereId", StringArgumentType.string()).suggests(sphereIdSuggestions)
                                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                    .executes(ctx -> {
                                                        var source = ctx.getSource();
                                                        var level = source.getLevel();
                                                        var sphereId = StringArgumentType.getString(ctx, "sphereId");
                                                        final int input = IntegerArgumentType.getInteger(ctx, "value");
                                                        var data = DysonSphereProgressSavedData.get(level);
                                                        if (data == null) return 0;
                                                        var config = data.getSpheres().computeIfAbsent(sphereId, s -> new DysonSphereConfiguration());
                                                        final int clamped = Math.max(0, Math.min(input, config.getMaxBeams()));
                                                        config.setBeams(clamped);
                                                        data.setDirty();
                                                        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Set beams for sphere '" + sphereId + "' to " + clamped), true);
                                                        return 1;
                                                    })))))
                    .then(Commands.literal("set")
                            .then(Commands.literal("panels")
                                    .then(Commands.argument("sphereId", StringArgumentType.string()).suggests(sphereIdSuggestions)
                                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                    .executes(ctx -> {
                                                        var source = ctx.getSource();
                                                        var level = source.getLevel();
                                                        var sphereId = StringArgumentType.getString(ctx, "sphereId");
                                                        final int input = IntegerArgumentType.getInteger(ctx, "value");
                                                        var data = DysonSphereProgressSavedData.get(level);
                                                        if (data == null) return 0;
                                                        var config = data.getSpheres().computeIfAbsent(sphereId, s -> new DysonSphereConfiguration());
                                                        final int clamped = Math.max(0, Math.min(input, config.getMaxSolarPanels()));
                                                        config.setSolarPanels(clamped);
                                                        data.setDirty();
                                                        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Set solar panels for sphere '" + sphereId + "' to " + clamped), true);
                                                        return 1;
                                                    })))))
                    .then(Commands.literal("add")
                            .then(Commands.literal("beams")
                                    .then(Commands.argument("sphereId", StringArgumentType.string()).suggests(sphereIdSuggestions)
                                            .then(Commands.argument("delta", IntegerArgumentType.integer())
                                                    .executes(ctx -> {
                                                        var source = ctx.getSource();
                                                        var level = source.getLevel();
                                                        var sphereId = StringArgumentType.getString(ctx, "sphereId");
                                                        int delta = IntegerArgumentType.getInteger(ctx, "delta");
                                                        var data = DysonSphereProgressSavedData.get(level);
                                                        if (data == null) return 0;
                                                        var config = data.getSpheres().computeIfAbsent(sphereId, s -> new DysonSphereConfiguration());
                                                        int newVal = Math.max(0, Math.min(config.getBeams() + delta, config.getMaxBeams()));
                                                        config.setBeams(newVal);
                                                        data.setDirty();
                                                        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Beams for sphere '" + sphereId + "' is now " + newVal), true);
                                                        return 1;
                                                    })))))
                    .then(Commands.literal("add")
                            .then(Commands.literal("panels")
                                    .then(Commands.argument("sphereId", StringArgumentType.string()).suggests(sphereIdSuggestions)
                                            .then(Commands.argument("delta", IntegerArgumentType.integer())
                                                    .executes(ctx -> {
                                                        var source = ctx.getSource();
                                                        var level = source.getLevel();
                                                        var sphereId = StringArgumentType.getString(ctx, "sphereId");
                                                        int delta = IntegerArgumentType.getInteger(ctx, "delta");
                                                        var data = DysonSphereProgressSavedData.get(level);
                                                        if (data == null) return 0;
                                                        var config = data.getSpheres().computeIfAbsent(sphereId, s -> new DysonSphereConfiguration());
                                                        int newVal = Math.max(0, Math.min(config.getSolarPanels() + delta, config.getMaxSolarPanels()));
                                                        config.setSolarPanels(newVal);
                                                        data.setDirty();
                                                        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Solar panels for sphere '" + sphereId + "' is now " + newVal), true);
                                                        return 1;
                                                    })))))
            );
        }).subscribe();
        DCPAttachments.DR.register(modEventBus);
    }


    @Override
    protected void initModules() {
        addCreativeTab("main", () -> new ItemStack(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER), "dyson_cube_project", DCPContent.TAB);
        DCPContent.Blocks.init();
        DCPContent.Items.init();
        DCPContent.Sounds.init();
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.addProvider(new DCPBlockstateProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
        event.addProvider(new DCPLangItemProvider(event.getGenerator(), MODID, "en_us"));
        event.addProvider(new DCPRecipesProvider(event.getGenerator(), () -> new ArrayList<>(), event.getLookupProvider()));
        event.addProvider(new DCPLootTableDataProvider(event.getGenerator(), () -> List.of(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.getBlock(),
                DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.getBlock()), event.getLookupProvider()));
        event.addProvider(new DCPBlockTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), MODID, event.getExistingFileHelper()));
    }
}
