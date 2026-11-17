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

import com.viaversion.viafabricplus.base.bedrock.NetherNetAddressParser;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IServerAddress;
import java.net.SocketAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import java.net.SocketAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerAddress.class)
public abstract class MixinServerAddress implements IServerAddress {

    @Inject(method = "parseString", at = @At("HEAD"), cancellable = true)
    private static void parseNetherNetAddress(final String input, final CallbackInfoReturnable<ServerAddress> cir) {
        if (input == null) {
            return;
        }
        final SocketAddress netherNetAddress = NetherNetAddressParser.parse(input);
        if (netherNetAddress != null) {
            final ServerAddress address = new ServerAddress("nethernet.viafabricplus.localhost", 25565);
            ((IServerAddress) (Object) address).viaFabricPlus$setNetherNetAddress(netherNetAddress);
            cir.setReturnValue(address);
        }
    }

    @Inject(method = "isValidAddress", at = @At("HEAD"), cancellable = true)
    private static void allowNetherNetAddress(final String input, final CallbackInfoReturnable<Boolean> cir) {
        if (NetherNetAddressParser.parse(input) != null) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private SocketAddress viaFabricPlus$netherNetAddress;

    @Override
    public SocketAddress viaFabricPlus$getNetherNetAddress() {
        return this.viaFabricPlus$netherNetAddress;
    }

    @Override
    public void viaFabricPlus$setNetherNetAddress(final SocketAddress address) {
        this.viaFabricPlus$netherNetAddress = address;
    }

}
