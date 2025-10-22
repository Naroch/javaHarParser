package org.example.integration.setup;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanup {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanDatabase() {
        disableForeignKeyChecks();

        // Order matters due to foreign keys
        entityManager.createNativeQuery("DELETE FROM products").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM reviews").executeUpdate();

        enableForeignKeyChecks();

        entityManager.flush();
        entityManager.clear();
    }

    private void disableForeignKeyChecks() {
        entityManager.createNativeQuery("SET session_replication_role = 'replica'").executeUpdate();
    }

    private void enableForeignKeyChecks() {
        entityManager.createNativeQuery("SET session_replication_role = 'origin'").executeUpdate();
    }

}
