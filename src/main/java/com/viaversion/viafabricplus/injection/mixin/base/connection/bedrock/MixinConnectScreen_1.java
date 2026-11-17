/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IEventLoopGroupHolder;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.io.IOException;
import net.minecraft.network.Connection;
import net.raphimc.minecraftauth.bedrock.BedrockAuthManager;
import net.raphimc.minecraftauth.bedrock.model.MinecraftMultiplayerToken;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.storage.AuthData;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Exception;getMessage()Ljava/lang/String;", remap = false))
    private String handleNullExceptionMessage(Exception instance, Operation<String> original) {
        // Vanilla doesn't have these cases, but we do because of RakNet and other modifications to the Netty pipeline
        return instance.getMessage() == null ? "" : original.call(instance);
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/EventLoopGroupHolder;remote(Z)Lnet/minecraft/server/network/EventLoopGroupHolder;"))
    private EventLoopGroupHolder markAsConnecting(boolean bl) {
        final EventLoopGroupHolder holder = EventLoopGroupHolder.remote(bl);
        ((IEventLoopGroupHolder) holder).viaFabricPlus$setConnecting(true);
        return holder;
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;syncUninterruptibly()Lio/netty/channel/ChannelFuture;", remap = false, shift = At.Shift.AFTER))
    private void setupBedrockAccount(final CallbackInfo ci, @Local final Connection clientConnection) throws IOException {
        if (!BedrockProtocolVersion.BEDROCK_LATEST.equals(((IConnection) clientConnection).viaFabricPlus$getTargetVersion())) {
            return;
        }

        final UserConnection user = ((IConnection) clientConnection).viaFabricPlus$getUserConnection();
        final BedrockAuthManager account = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount();
        if (account == null) {
            return; // Offline Bedrock servers do not require an Xbox account.
        }

        final MinecraftMultiplayerToken multiplayerToken = account.getMinecraftMultiplayerToken().refresh();
        user.put(new AuthData(multiplayerToken.getToken(), account.getSessionKeyPair(), account.getDeviceId()));
    }

}
