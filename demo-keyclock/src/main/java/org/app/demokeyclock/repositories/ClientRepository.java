package org.app.demokeyclock.repositories;

import org.app.demokeyclock.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    public Client findByCin(String cin);
    boolean existsById(String cin);

}
