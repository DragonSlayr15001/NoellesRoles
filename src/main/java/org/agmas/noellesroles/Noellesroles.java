package org.agmas.noellesroles;

import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.commands.ForceRoleCommand;
import org.agmas.noellesroles.commands.ListRolesCommand;
import org.agmas.noellesroles.commands.SetEnabledRoleCommand;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.agmas.noellesroles.packet.MorphC2SPacket;
import org.agmas.noellesroles.packet.SwapperC2SPacket;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class Noellesroles implements ModInitializer {

    public static String MOD_ID = "noellesroles";

    public static HashMap<Identifier, Integer> rolePlayerCaps = new HashMap<>();
    public static HashMap<PlayerEntity, TMMRoles.Role> forceRoles = new HashMap<>();
    public static HashMap<RoleAnnouncementTexts.RoleAnnouncementText, Boolean> roleAnnouncementIsEvil = new HashMap<>();

    public static Identifier JESTER_ID = Identifier.of(MOD_ID, "jester");
    public static Identifier MORPHLING_ID = Identifier.of(MOD_ID, "morphling");
    public static Identifier HOST_ID = Identifier.of(MOD_ID, "host");
    public static Identifier BARTENDER_ID = Identifier.of(MOD_ID, "bartender");
    public static Identifier NOISEMAKER_ID = Identifier.of(MOD_ID, "noisemaker");
    public static Identifier PHANTOM_ID = Identifier.of(MOD_ID, "phantom");
    public static Identifier AWESOME_BINGLUS_ID = Identifier.of(MOD_ID, "awesome_binglus");
    public static Identifier SWAPPER_ID = Identifier.of(MOD_ID, "swapper");

    public static HashMap<TMMRoles.Role, RoleAnnouncementTexts.RoleAnnouncementText> roleRoleAnnouncementTextHashMap = new HashMap<>();
    public static TMMRoles.Role JESTER = trueRegisterRole(new TMMRoles.Role(JESTER_ID,new Color(255,86,243).getRGB() ,false,false));
    public static TMMRoles.Role MORPHLING =trueRegisterRole(new TMMRoles.Role(MORPHLING_ID, new Color(170, 2, 61).getRGB(),false,true));
    public static TMMRoles.Role HOST =trueRegisterRole(new TMMRoles.Role(HOST_ID, new Color(255, 205, 84).getRGB(),true,false));
    public static TMMRoles.Role AWESOME_BINGLUS = trueRegisterRole(new TMMRoles.Role(AWESOME_BINGLUS_ID, new Color(155, 255, 168).getRGB(),true,false));

    public static TMMRoles.Role BARTENDER =trueRegisterRole(new TMMRoles.Role(BARTENDER_ID, new Color(217,241,240).getRGB(),true,false));
    public static TMMRoles.Role NOISEMAKER =trueRegisterRole(new TMMRoles.Role(NOISEMAKER_ID, new Color(200, 255, 0).getRGB(),true,false));
    public static TMMRoles.Role SWAPPER = trueRegisterRole(new TMMRoles.Role(SWAPPER_ID, new Color(63, 0, 255).getRGB(),false,true));
    public static TMMRoles.Role PHANTOM =trueRegisterRole(new TMMRoles.Role(PHANTOM_ID, new Color(80, 5, 5, 192).getRGB(),false,true));


    public static final CustomPayload.Id<MorphC2SPacket> MORPH_PACKET = MorphC2SPacket.ID;
    public static final CustomPayload.Id<SwapperC2SPacket> SWAP_PACKET = SwapperC2SPacket.ID;
    public static final CustomPayload.Id<AbilityC2SPacket> ABILITY_PACKET = AbilityC2SPacket.ID;
    public static final ArrayList<TMMRoles.Role> VANNILA_ROLES = new ArrayList<>();
    public static final ArrayList<Identifier> VANNILA_ROLE_IDS = new ArrayList<>();
    @Override
    public void onInitialize() {
        VANNILA_ROLES.add(TMMRoles.KILLER);
        VANNILA_ROLES.add(TMMRoles.VIGILANTE);
        VANNILA_ROLES.add(TMMRoles.CIVILIAN);
        VANNILA_ROLES.add(TMMRoles.LOOSE_END);
        VANNILA_ROLE_IDS.add(TMMRoles.LOOSE_END.identifier());
        VANNILA_ROLE_IDS.add(TMMRoles.VIGILANTE.identifier());
        VANNILA_ROLE_IDS.add(TMMRoles.CIVILIAN.identifier());
        VANNILA_ROLE_IDS.add(TMMRoles.KILLER.identifier());
        NoellesRolesConfig.HANDLER.save();
        NoellesRolesConfig.HANDLER.load();
        ModItems.init();

        rolePlayerCaps.put(JESTER_ID, 1);
        rolePlayerCaps.put(HOST_ID, 1);

        PayloadTypeRegistry.playC2S().register(MorphC2SPacket.ID, MorphC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SwapperC2SPacket.ID, SwapperC2SPacket.CODEC);

        registerPackets();
        registerCommands();
    }


    public static TMMRoles.Role trueRegisterRole(TMMRoles.Role role) {
        TMMRoles.registerRole(role);
        try {
            Constructor<RoleAnnouncementTexts.RoleAnnouncementText> constructor = RoleAnnouncementTexts.RoleAnnouncementText.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            RoleAnnouncementTexts.RoleAnnouncementText announcementText = constructor.newInstance(role.identifier().getPath(), role.color());
            RoleAnnouncementTexts.registerRoleAnnouncementText(announcementText);
            roleRoleAnnouncementTextHashMap.put(role,announcementText);
            roleAnnouncementIsEvil.put(announcementText, !role.canUseKiller());;
            return role;
        } catch (Exception e) {
            Log.info(LogCategory.GENERAL, e.getMessage());
        }
        return null;
    }


    public static boolean isAnyModdedRole(PlayerEntity player) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        return gameWorldComponent.getRole(player) != null && (gameWorldComponent.getRole(player) != TMMRoles.CIVILIAN && gameWorldComponent.getRole(player) != TMMRoles.VIGILANTE && gameWorldComponent.getRole(player) != TMMRoles.KILLER);
    }

    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.MORPH_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());

            if (gameWorldComponent.isRole(context.player(), MORPHLING)) {
                MorphlingPlayerComponent morphlingPlayerComponent = (MorphlingPlayerComponent) MorphlingPlayerComponent.KEY.get(context.player());
                morphlingPlayerComponent.startMorph(payload.player());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.SWAP_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), SWAPPER)) {
                if (payload.player() != null) {
                    if (context.player().getWorld().getPlayerByUuid(payload.player()) != null) {
                        if (payload.player2() != null) {
                            if (context.player().getWorld().getPlayerByUuid(payload.player2()) != null) {
                                Vec3d swapperPos = context.player().getWorld().getPlayerByUuid(payload.player2()).getPos();
                                Vec3d swappedPos = context.player().getWorld().getPlayerByUuid(payload.player()).getPos();
                                context.player().getWorld().getPlayerByUuid(payload.player2()).refreshPositionAfterTeleport(swappedPos.x, swappedPos.y, swappedPos.z);
                                context.player().getWorld().getPlayerByUuid(payload.player()).refreshPositionAfterTeleport(swapperPos.x, swapperPos.y, swapperPos.z);
                            }
                        }
                    }
                }
                AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(1, 0);
                abilityPlayerComponent.sync();
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.ABILITY_PACKET, (payload, context) -> {
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), PHANTOM) && abilityPlayerComponent.cooldown <= 0) {
                context.player().addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 30 * 20,0,true,false,true));
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(1, 30);
            }
        });
    }

    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ForceRoleCommand.register(dispatcher);
            SetEnabledRoleCommand.register(dispatcher);
            ListRolesCommand.register(dispatcher);
        });
    }



}
