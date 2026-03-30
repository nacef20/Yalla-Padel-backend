package tn.esprit.reclamationreponsemicroservice.services;

import java.util.List;
import org.springframework.stereotype.Service;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;
import tn.esprit.reclamationreponsemicroservice.repositories.ComplaintRepository;


@Service
public class ComplaintServiceImplement implements IComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImplement(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public Complaint createComplaint(Complaint complaint) {
        complaint.setId(null);
        if (complaint.getStatus() == null) {
            complaint.setStatus(ComplaintStatus.EN_ATTENTE);
        }
        return complaintRepository.save(complaint);
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    @Override
    public Complaint updateComplaint(Long id, Complaint complaint) {
        Complaint existingComplaint = getComplaintById(id);
        existingComplaint.setTitle(complaint.getTitle());
        existingComplaint.setDescription(complaint.getDescription());
        existingComplaint.setStatus(complaint.getStatus());
        return complaintRepository.save(existingComplaint);
    }

    @Override
    public void deleteComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        complaintRepository.delete(complaint);
    }
}