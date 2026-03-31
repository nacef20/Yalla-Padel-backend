package tn.esprit.reclamationreponsemicroservice.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
	List<Complaint> findByStatus(ComplaintStatus status);

	long countByStatus(ComplaintStatus status);

	long count();

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
