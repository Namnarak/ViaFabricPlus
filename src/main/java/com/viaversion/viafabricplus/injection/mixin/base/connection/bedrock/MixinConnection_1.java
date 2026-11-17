/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors and ViaVersion contributors
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

import com.viaversion.viafabricplus.injection.access.base.IConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.BatchLengthCodec;
import net.raphimc.viabedrock.netty.DisconnectHandler;
import net.raphimc.viabedrock.netty.PacketCodec;
import org.cloudburstmc.netty.channel.nethernet.NetherNetClientChannel;
import org.cloudburstmc.netty.channel.nethernet.config.NetherChannelOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.Connection$1")
public abstract class MixinConnection_1 {

    @Final
    @Shadow
    private Connection val$connection;

    @Inject(method = "initChannel", at = @At("RETURN"))
    private void injectBedrockIntoPipeline(final Channel channel, final CallbackInfo ci) {
        final IConnection connection = (IConnection) this.val$connection;
        if (!BedrockProtocolVersion.BEDROCK_LATEST.equals(connection.viaFabricPlus$getTargetVersion())) {
            return;
        }

        final ChannelConfig config = channel.config();
        config.setOption(NetherChannelOption.NETHER_CLIENT_HANDSHAKE_TIMEOUT_MS, config.getOption(ChannelOption.CONNECT_TIMEOUT_MILLIS));
        config.setOption(NetherChannelOption.NETHER_CLIENT_MAX_HANDSHAKE_ATTEMPTS, 1);

        final ChannelPipeline pipeline = channel.pipeline();
        if (channel instanceof NetherNetClientChannel && pipeline.get(HandlerNames.TIMEOUT) instanceof ReadTimeoutHandler) {
            pipeline.remove(HandlerNames.TIMEOUT);
        }
        pipeline.addBefore(HandlerNames.SPLITTER, DisconnectHandler.NAME, new DisconnectHandler());
        pipeline.replace(HandlerNames.SPLITTER, HandlerNames.SPLITTER, new BatchLengthCodec());
        pipeline.remove(HandlerNames.PREPENDER);
        pipeline.addBefore(com.viaversion.viaversion.platform.ViaDecodeHandler.NAME, PacketCodec.NAME, new PacketCodec());
    }

}
