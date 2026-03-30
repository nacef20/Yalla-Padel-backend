package tn.esprit.reclamationreponsemicroservice.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reclamationreponsemicroservice.entities.Response;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    Optional<Response> findByComplaintId(Long complaintId);

    boolean existsByComplaintId(Long complaintId);
}
