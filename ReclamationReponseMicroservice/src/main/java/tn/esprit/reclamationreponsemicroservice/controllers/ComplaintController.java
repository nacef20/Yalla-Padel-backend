package tn.esprit.reclamationreponsemicroservice.controllers;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.ComplaintStatus;
import tn.esprit.reclamationreponsemicroservice.services.IComplaintService;


@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    private final IComplaintService complaintService;

    public ComplaintController(IComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody Complaint complaint) {
        Complaint createdComplaint = complaintService.createComplaint(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComplaint);
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Complaint>> getAllComplaintsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(complaintService.getAllComplaintsPaginated(page, size));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getByStatus(@PathVariable ComplaintStatus status) {
        return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Complaint>> search(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(complaintService.searchComplaints(status, keyword, startDate, endDate));
    }

    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable ComplaintStatus status) {
        return ResponseEntity.ok(complaintService.countByStatus(status));
    }

    @GetMapping("/stats/total")
    public ResponseEntity<Long> countTotalComplaints() {
        return ResponseEntity.ok(complaintService.countTotalComplaints());
    }

    @GetMapping("/stats/period")
    public ResponseEntity<Long> countByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(complaintService.countComplaintsBetweenDates(start, end));
    }

    @GetMapping("/stats/by-day")
    public ResponseEntity<Map<String, Long>> countByDay() {
        return ResponseEntity.ok(complaintService.countComplaintsByDay());
    }

    @GetMapping("/stats/by-month")
    public ResponseEntity<Map<String, Long>> countByMonth() {
        return ResponseEntity.ok(complaintService.countComplaintsByMonth());
    }

    @GetMapping("/stats/by-year")
    public ResponseEntity<Map<String, Long>> countByYear() {
        return ResponseEntity.ok(complaintService.countComplaintsByYear());
    }

    @GetMapping("/stats/average-processing-time")
    public ResponseEntity<Double> getAverageProcessingTime() {
        return ResponseEntity.ok(complaintService.getAverageProcessingTimeHours());
    }

    @GetMapping("/stats/unprocessed-rate")
    public ResponseEntity<Double> getUnprocessedRate() {
        return ResponseEntity.ok(complaintService.getUnprocessedRate());
    }

    @GetMapping("/stats/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("total", complaintService.countTotalComplaints());
        dashboard.put("en_attente", complaintService.countByStatus(ComplaintStatus.EN_ATTENTE));
        dashboard.put("traitee", complaintService.countByStatus(ComplaintStatus.TRAITEE));
        dashboard.put("rejetee", complaintService.countByStatus(ComplaintStatus.REJETEE));
        dashboard.put("averageProcessingTimeHours", complaintService.getAverageProcessingTimeHours());
        dashboard.put("unprocessedRate", complaintService.getUnprocessedRate());
        dashboard.put("byDay", complaintService.countComplaintsByDay());
        dashboard.put("byMonth", complaintService.countComplaintsByMonth());
        dashboard.put("byYear", complaintService.countComplaintsByYear());
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @RequestBody Complaint complaint) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, complaint));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
