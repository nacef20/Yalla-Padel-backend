package tn.esprit.reclamationreponsemicroservice.services;

import java.time.LocalDateTime;
import java.util.List;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;


public interface IComplaintService {

    Complaint createComplaint(Complaint complaint);

    List<Complaint> getAllComplaints();

    List<Complaint> getComplaintsByStatus(ComplaintStatus status);

    long countByStatus(ComplaintStatus status);

    long countTotalComplaints();

    long countComplaintsBetweenDates(LocalDateTime start, LocalDateTime end);

    double getUnprocessedRate();

    Complaint getComplaintById(Long id);

    Complaint updateComplaint(Long id, Complaint complaint);

    void deleteComplaint(Long id);
}
