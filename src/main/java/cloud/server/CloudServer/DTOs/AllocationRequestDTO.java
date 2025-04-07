package cloud.server.CloudServer.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class AllocationRequestDTO {

    @Min(value = 1, message = "Available memory must be greater than 0 GB")
    @Max(value = 100, message = "Available memory cannot exceed 100 GB")
    private int memoryGb;

    public AllocationRequestDTO() {
    }

    public AllocationRequestDTO(int memoryGb) {
        this.memoryGb = memoryGb;
    }

    public int getMemoryGb() {
        return memoryGb;
    }

    public void setMemoryGb(int memoryGb) {
        this.memoryGb = memoryGb;
    }
}
