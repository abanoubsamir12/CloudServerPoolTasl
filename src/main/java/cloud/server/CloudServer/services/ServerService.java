package cloud.server.CloudServer.services;

import cloud.server.CloudServer.DTOs.AllocationRequestDTO;
import cloud.server.CloudServer.DTOs.AllocationResultDTO;
import cloud.server.CloudServer.DTOs.ServerCreationDTO;
import cloud.server.CloudServer.DTOs.ServerDTO;

import java.util.List;

public interface ServerService {
    public AllocationResultDTO allocateOrCreateServer(AllocationRequestDTO allocationRequestDTO);
    public ServerDTO addServer(ServerCreationDTO serverDTO);
    public List<ServerDTO> getAllServers();

}
