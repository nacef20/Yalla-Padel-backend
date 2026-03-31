package tn.esprit.reclamationreponsemicroservice.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;


public interface IComplaintService {

    Complaint createComplaint(Complaint complaint);

    List<Complaint> getAllComplaints();

    Page<Complaint> getAllComplaintsPaginated(int page, int size);

    List<Complaint> getComplaintsByStatus(ComplaintStatus status);

    List<Complaint> searchComplaints(ComplaintStatus status, String keyword, LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(ComplaintStatus status);

    long countTotalComplaints();

    long countComplaintsBetweenDates(LocalDateTime start, LocalDateTime end);

    double getUnprocessedRate();

    Complaint getComplaintById(Long id);

    Complaint updateComplaint(Long id, Complaint complaint);

    void deleteComplaint(Long id);
}
