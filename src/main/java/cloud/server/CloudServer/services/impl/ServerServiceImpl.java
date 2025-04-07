package cloud.server.CloudServer.services.impl;

import cloud.server.CloudServer.DTOs.AllocationRequestDTO;
import cloud.server.CloudServer.DTOs.AllocationResultDTO;
import cloud.server.CloudServer.DTOs.ServerCreationDTO;

import cloud.server.CloudServer.DTOs.ServerDTO;
import cloud.server.CloudServer.enums.ServerStatus;
import cloud.server.CloudServer.exception.InvalidMemoryRequestException;
import cloud.server.CloudServer.models.Server;
import cloud.server.CloudServer.repositories.ServerRepository;
import cloud.server.CloudServer.services.ServerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class ServerServiceImpl implements ServerService {
    public static final int  DELAY_TIME =20;
    public static final int SERVER_CAPACITY = 100;
    private final Map<String, Object> serverLocks = new ConcurrentHashMap<>();
    @Autowired
    ServerRepository serverRepository;
    @Override
    public  AllocationResultDTO allocateOrCreateServer(AllocationRequestDTO allocationRequestDTO)throws InvalidMemoryRequestException {
        validateRequest(allocationRequestDTO.getMemoryGb());
        AllocationResultDTO resultDTO = findOrCreateBestFitServer(allocationRequestDTO.getMemoryGb());
        Server bestFit = allocateToServerWithLock(resultDTO.getServer().getId(), allocationRequestDTO.getMemoryGb());

        return new AllocationResultDTO(bestFit.convertToDTO(), resultDTO.isNewServerCreated());
    }

    private void validateRequest(int memoryGB)throws InvalidMemoryRequestException {
        if(memoryGB<=0 ||memoryGB>100)
            throw new InvalidMemoryRequestException("Invalid Memory request, must be between 1-100");
    }

    private synchronized AllocationResultDTO findOrCreateBestFitServer(int memoryRequired) {
        List<Server> activeServers = serverRepository.findAllByStatus(ServerStatus.ACTIVE);
        Server bestFit = chooseBestFit(activeServers, memoryRequired);
        boolean newServerChecker =false;

        if (bestFit == null) {
            newServerChecker=true;
            bestFit = new Server();
            serverRepository.save(bestFit);
            activateServerAfterDelay(bestFit);
        }
        return new AllocationResultDTO(bestFit.convertToDTO(),newServerChecker);
    }

    public Server chooseBestFit(List<Server> activeServers,int requestedGB)
    {
        Server bestFit = null;
        for(Server server: activeServers)
        {
            if(server.getAvailableMemory() >= requestedGB)
            {
                if(bestFit==null || bestFit.getAvailableMemory()>server.getAvailableMemory())
                    bestFit=server;
            }
        }
        return bestFit;
    }

    private Server allocateToServerWithLock(String serverId, int memoryGb) {
        Object lock = serverLocks.computeIfAbsent(serverId, id -> new Object());

        synchronized (lock) {
            Server server = serverRepository.findById(serverId).orElseThrow(() -> new RuntimeException("Server not found"));

            if (server.getAvailableMemory() < memoryGb) {
                throw new IllegalStateException("Not enough memory available");
            }

            server.setAllocatedMemory(server.getAllocatedMemory() + memoryGb);
            server.setAvailableMemory(server.getAvailableMemory() - memoryGb);
            if(server.getAvailableMemory()==0)
                server.setStatus(ServerStatus.FULL);
            serverRepository.save(server);
            return server;
        }
    }


    @Override
    public ServerDTO addServer(ServerCreationDTO serverDTO) throws InvalidMemoryRequestException {
        validateRequest(serverDTO.getAvailableMemory());
        Server server = new Server(0,serverDTO.getAvailableMemory(),ServerStatus.CREATED);
        serverRepository.save(server);
        activateServerAfterDelay(server);
        return server.convertToDTO();
    }

    @Async
    public void activateServerAfterDelay(Server server) {
        try {
            // this is based on the documentation
            TimeUnit.SECONDS.sleep(DELAY_TIME);
            if (server != null) {
                server.setStatus(ServerStatus.ACTIVE);
                serverRepository.save(server);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public List<ServerDTO> getAllServers()
    {
        List<Server> list = serverRepository.findAllByStatus(ServerStatus.ACTIVE);
        List<ServerDTO> DTOs = new ArrayList<>();
        for(Server s: list)
            DTOs.add(s.convertToDTO());
        return DTOs;
    }

}
