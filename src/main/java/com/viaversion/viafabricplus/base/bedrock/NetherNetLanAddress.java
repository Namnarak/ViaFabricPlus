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

package com.viaversion.viafabricplus.base.bedrock;

import java.net.SocketAddress;

public final class NetherNetLanAddress extends SocketAddress {

    private final String host;
    private final int port;

    public NetherNetLanAddress(final String host, final int port) {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("NetherNet LAN host must not be blank");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("NetherNet LAN port must be between 1 and 65535");
        }
        this.host = host;
        this.port = port;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }

}
