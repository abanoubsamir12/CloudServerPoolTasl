package cloud.server.CloudServer.repositories;


import cloud.server.CloudServer.enums.ServerStatus;
import cloud.server.CloudServer.models.Server;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ServerRepository extends AerospikeRepository<Server, String> {
    public List<Server> findAllByStatus(ServerStatus status);
}