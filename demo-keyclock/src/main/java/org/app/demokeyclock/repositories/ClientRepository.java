package org.app.demokeyclock.repositories;

import org.app.demokeyclock.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    //FIND CLIENT BY CIN
    public Client findByCin(String cin);

    //CHECK: CIN EXISTS
    boolean existsById(String cin);
}
