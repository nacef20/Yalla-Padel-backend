package tn.esprit.reclamationreponsemicroservice.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
	List<Complaint> findByStatus(ComplaintStatus status);

	long countByStatus(ComplaintStatus status);

	long count();

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT FUNCTION('DATE', c.createdAt), COUNT(c) "
			+ "FROM Complaint c "
			+ "GROUP BY FUNCTION('DATE', c.createdAt) "
			+ "ORDER BY FUNCTION('DATE', c.createdAt)")
	List<Object[]> countComplaintsGroupedByDay();

	@Query("SELECT FUNCTION('YEAR', c.createdAt), FUNCTION('MONTH', c.createdAt), COUNT(c) "
			+ "FROM Complaint c "
			+ "GROUP BY FUNCTION('YEAR', c.createdAt), FUNCTION('MONTH', c.createdAt) "
			+ "ORDER BY FUNCTION('YEAR', c.createdAt), FUNCTION('MONTH', c.createdAt)")
	List<Object[]> countComplaintsGroupedByMonth();

	@Query("SELECT FUNCTION('YEAR', c.createdAt), COUNT(c) "
			+ "FROM Complaint c "
			+ "GROUP BY FUNCTION('YEAR', c.createdAt) "
			+ "ORDER BY FUNCTION('YEAR', c.createdAt)")
	List<Object[]> countComplaintsGroupedByYear();

	@Query("SELECT c FROM Complaint c JOIN FETCH c.response WHERE c.status <> :pendingStatus")
	List<Complaint> findProcessedComplaintsWithResponse(@Param("pendingStatus") ComplaintStatus pendingStatus);

	@Query("SELECT c FROM Complaint c WHERE "
			+ "(:status IS NULL OR c.status = :status) "
			+ "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
			+ "AND (:startDate IS NULL OR c.createdAt >= :startDate) "
			+ "AND (:endDate IS NULL OR c.createdAt <= :endDate)")
	List<Complaint> searchComplaints(
			@Param("status") ComplaintStatus status,
			@Param("keyword") String keyword,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);
}
