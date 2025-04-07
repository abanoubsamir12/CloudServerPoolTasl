package cloud.server.CloudServer.models;
import cloud.server.CloudServer.DTOs.ServerDTO;
import cloud.server.CloudServer.enums.ServerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.UUID;

import static cloud.server.CloudServer.services.impl.ServerServiceImpl.SERVER_CAPACITY;


@Data
@Document
@AllArgsConstructor
public class Server {

    @Id
    private String id;
    int allocatedMemory;
    int availableMemory;
    ServerStatus status;
    public Server() {
        this.id = UUID.randomUUID().toString();
        this.allocatedMemory = 0;
        this.availableMemory = SERVER_CAPACITY;
        this.status = ServerStatus.CREATED;
    }
    public String getId() {
        return id;
    }

    public int getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(int allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }

    public int getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(int availableMemory) {
        this.availableMemory = availableMemory;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    public Server(int allocatedMemory, int availableMemory, ServerStatus state) {
        this.id = UUID.randomUUID().toString();
        this.allocatedMemory=allocatedMemory;
        this.availableMemory=availableMemory;
        this.status=state;
    }

    public boolean validateState()
    {
        return this.status== ServerStatus.ACTIVE;

    }
    public ServerDTO convertToDTO()
    {
        return new ServerDTO(this.id,this.allocatedMemory,this.availableMemory,this.status);
    }
}
