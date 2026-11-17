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

import com.viaversion.viafabricplus.base.bedrock.NetherNetInetSocketAddress;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IServerAddress;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddressResolver;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.cloudburstmc.netty.channel.nethernet.config.NetherNetAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerNameResolver.class)
public abstract class MixinServerNameResolver {

    @Shadow
    @Final
    private ServerAddressResolver resolver;

    @Inject(method = "resolveAddress", at = @At("HEAD"), cancellable = true)
    private void returnNetherNetAddressEarly(final ServerAddress serverAddress, final CallbackInfoReturnable<Optional<ResolvedServerAddress>> cir) {
        final SocketAddress netherNetAddress = ((IServerAddress) (Object) serverAddress).viaFabricPlus$getNetherNetAddress();
        if (netherNetAddress != null) {
            cir.setReturnValue(Optional.of(new ResolvedServerAddress() {
                @Override
                public String getHostName() {
                    return netherNetAddress instanceof NetherNetAddress xboxAddress ? xboxAddress.getNetworkId() : netherNetAddress.toString();
                }

                @Override
                public String getHostIp() {
                    return netherNetAddress.toString();
                }

                @Override
                public int getPort() {
                    return netherNetAddress instanceof com.viaversion.viafabricplus.base.bedrock.NetherNetLanAddress lanAddress
                        ? lanAddress.port()
                        : 0;
                }

                @Override
                public InetSocketAddress asInetSocketAddress() {
                    return new NetherNetInetSocketAddress(netherNetAddress);
                }
            }));
        } else if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.BEDROCK_LATEST)) {
            // NetherNet addresses do not use Java SRV lookups.
            cir.setReturnValue(this.resolver.resolve(serverAddress));
        }
    }

}
