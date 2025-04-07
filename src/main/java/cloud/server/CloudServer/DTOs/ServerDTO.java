package cloud.server.CloudServer.DTOs;


import cloud.server.CloudServer.enums.ServerStatus;

public class ServerDTO {
    String id;
    int availableMemory;
    int allocatedMemory;
    ServerStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(int allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
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

    public void setState(ServerStatus status) {
        this.status = status;
    }

    public ServerDTO(String  id,int allocatedMemory, int availableMemory, ServerStatus status) {
        this.id=id;
        this.allocatedMemory=allocatedMemory;
        this.availableMemory = availableMemory;
        this.status = status;
    }

}
