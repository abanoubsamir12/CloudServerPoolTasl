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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ServerServiceImpl implements ServerService {
    public static final int  DELAY_TIME =20;
    public static final int SERVER_CAPACITY = 100;
    private final Map<String, Object> serverLocks = new ConcurrentHashMap<>();
    @Autowired
    ServerRepository serverRepository;
    private final ExecutorService executorService= Executors.newCachedThreadPool();
    @Override
    public  AllocationResultDTO allocateOrCreateServer(AllocationRequestDTO allocationRequestDTO)throws InvalidMemoryRequestException {
        validateRequest(allocationRequestDTO.getMemoryGb());
        AllocationResultDTO resultDTO =  findOrCreateBestFitServer(allocationRequestDTO.getMemoryGb());
        Server bestFit = allocateToServerWithLock(resultDTO.getServer().getId(), allocationRequestDTO.getMemoryGb());

        return new AllocationResultDTO(bestFit.convertToDTO(), resultDTO.isNewServerCreated());
    }

    private void validateRequest(int memoryGB)throws InvalidMemoryRequestException {
        if(memoryGB<=0 ||memoryGB>100)
            throw new InvalidMemoryRequestException("Invalid Memory request, must be between 1-100");
    }

    private AllocationResultDTO findOrCreateBestFitServer(int memoryRequired) {
        List<Server> activeServers = serverRepository.findAllByStatus(ServerStatus.ACTIVE);
        Server bestFit = chooseBestFit(activeServers, memoryRequired);
        if (bestFit != null) {
            return new AllocationResultDTO(bestFit.convertToDTO(),false);
        }
        synchronized (this) {
            activeServers = serverRepository.findAllByStatus(ServerStatus.ACTIVE);
            bestFit = chooseBestFit(activeServers, memoryRequired);
            if (bestFit != null) {
                return new AllocationResultDTO(bestFit.convertToDTO(),false);
            }
            List<Server> createdServers = serverRepository.findAllByStatus(ServerStatus.CREATING);
            bestFit = chooseBestFit(createdServers,memoryRequired);
            if (bestFit != null) {
                return new AllocationResultDTO(bestFit.convertToDTO(),false);
            }

            Server newServer = new Server();
            serverRepository.save(newServer);
            activateServerAfterDelay(newServer.getId());
            return new AllocationResultDTO(newServer.convertToDTO(), true);
        }
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
    private void activateServerAfterDelay(String serverId) {
        executorService.submit(() -> {
            try {
                Thread.sleep(20_000);
                Optional<Server> optional = serverRepository.findById(serverId);
                if (optional.isPresent()) {
                    Server server = optional.get();
                    server.setStatus(ServerStatus.ACTIVE);
                    serverRepository.save(server);
                }
                else
                    throw new NullPointerException();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }



    @Override
    public ServerDTO addServer(ServerCreationDTO serverDTO) throws InvalidMemoryRequestException {
        validateRequest(serverDTO.getAvailableMemory());
        Server server = new Server(0,serverDTO.getAvailableMemory(),ServerStatus.CREATING);
        serverRepository.save(server);
        activateServerAfterDelay(server.getId());
        return server.convertToDTO();
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

    @Override
    public List<ServerDTO> getAllCreatedServers() {
        List<Server> list = serverRepository.findAllByStatus(ServerStatus.CREATING);
        List<ServerDTO> DTOs = new ArrayList<>();
        for(Server s: list)
            DTOs.add(s.convertToDTO());
        return DTOs;
    }

}
