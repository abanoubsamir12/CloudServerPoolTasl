package cloud.server.CloudServer.DTOs;

import cloud.server.CloudServer.models.Server;

public class AllocationResultDTO {
    private final ServerDTO server;
    private final boolean newServerCreated;

    public AllocationResultDTO(ServerDTO server, boolean newServerCreated) {
        this.server = server;
        this.newServerCreated = newServerCreated;
    }

    public ServerDTO getServer() {
        return server;
    }

    public boolean isNewServerCreated() {
        return newServerCreated;
    }
}
