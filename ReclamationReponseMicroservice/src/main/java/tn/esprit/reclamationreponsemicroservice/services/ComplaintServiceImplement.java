package tn.esprit.reclamationreponsemicroservice.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<Complaint> getAllComplaintsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findAll(pageable);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    @Override
    public List<Complaint> searchComplaints(ComplaintStatus status, String keyword, LocalDateTime startDate,
            LocalDateTime endDate) {
        return complaintRepository.searchComplaints(status, keyword, startDate, endDate);
    }

    @Override
    public long countByStatus(ComplaintStatus status) {
        return complaintRepository.countByStatus(status);
    }

    @Override
    public long countTotalComplaints() {
        return complaintRepository.count();
    }

    @Override
    public long countComplaintsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return complaintRepository.countByCreatedAtBetween(start, end);
    }

    @Override
    public double getUnprocessedRate() {
        long total = complaintRepository.count();
        if (total == 0) {
            return 0.0;
        }
        long enAttente = complaintRepository.countByStatus(ComplaintStatus.EN_ATTENTE);
        return (enAttente * 100.0) / total;
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