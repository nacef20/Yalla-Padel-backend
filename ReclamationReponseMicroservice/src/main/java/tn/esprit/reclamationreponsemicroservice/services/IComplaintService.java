package tn.esprit.reclamationreponsemicroservice.services;

import java.util.List;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;


public interface IComplaintService {

    Complaint createComplaint(Complaint complaint);

    List<Complaint> getAllComplaints();

    List<Complaint> getComplaintsByStatus(ComplaintStatus status);

    Complaint getComplaintById(Long id);

    Complaint updateComplaint(Long id, Complaint complaint);

    void deleteComplaint(Long id);
}
