package cloud.server.CloudServer.DTOs;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ServerCreationDTO {


    @Min(value = 1, message = "Available memory must be greater than 0 GB")
    @Max(value = 100, message = "Available memory cannot exceed 100 GB")
    int availableMemory;


    public int getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(int availableMemory) {
        this.availableMemory = availableMemory;
    }

    public ServerCreationDTO( int availableMemory) {
        this.availableMemory = availableMemory;
    }

}
