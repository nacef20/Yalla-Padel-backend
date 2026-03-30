package tn.esprit.reclamationreponsemicroservice.services;

import java.util.List;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;


public interface IComplaintService {

    Complaint createComplaint(Complaint complaint);

    List<Complaint> getAllComplaints();

    Complaint getComplaintById(Long id);

    Complaint updateComplaint(Long id, Complaint complaint);

    void deleteComplaint(Long id);
}
