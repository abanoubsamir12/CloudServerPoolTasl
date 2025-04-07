package cloud.server.CloudServer.controllers;


import cloud.server.CloudServer.DTOs.AllocationRequestDTO;
import cloud.server.CloudServer.DTOs.AllocationResultDTO;
import cloud.server.CloudServer.DTOs.ServerCreationDTO;
import cloud.server.CloudServer.DTOs.ServerDTO;
import cloud.server.CloudServer.services.ServerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/server")
public class ServerController {
    @Autowired
    ServerService serverService;


    @PostMapping(value = "/allocate")
    public ResponseEntity<ServerDTO> allocateOrCreateServer(@RequestBody @Valid AllocationRequestDTO allocationRequestDTO)
    {
        AllocationResultDTO result = serverService.allocateOrCreateServer(allocationRequestDTO);
        ServerDTO dto = result.getServer();
        return ResponseEntity
                .status(result.isNewServerCreated() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(dto);
    }
    @PostMapping(value = "/add")
    public ResponseEntity<ServerDTO> addServer(@RequestBody @Valid ServerCreationDTO serverDTO)
    {
        ServerDTO server = serverService.addServer(serverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(server);
    }

    @GetMapping(value =  "/all")
    public ResponseEntity<List<ServerDTO>> getAllServers()
    {
        List<ServerDTO> result = serverService.getAllServers();
         return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
