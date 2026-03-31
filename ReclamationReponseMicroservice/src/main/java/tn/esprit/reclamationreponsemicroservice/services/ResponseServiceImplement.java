package tn.esprit.reclamationreponsemicroservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit.reclamationreponsemicroservice.entities.Complaint;
import tn.esprit.reclamationreponsemicroservice.entities.Response;
import tn.esprit.reclamationreponsemicroservice.repositories.ComplaintRepository;
import tn.esprit.reclamationreponsemicroservice.repositories.ResponseRepository;


@Service
public class ResponseServiceImplement implements IResponseService {

    private final ResponseRepository responseRepository;
    private final ComplaintRepository complaintRepository;

    public ResponseServiceImplement(ResponseRepository responseRepository, ComplaintRepository complaintRepository) {
        this.responseRepository = responseRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public Response createResponse(Response response) {
        if (response.getComplaint() == null || response.getComplaint().getId() == null) {
            throw new RuntimeException("Complaint id is required");
        }

        Long complaintId = response.getComplaint().getId();
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));

        response.setId(null);
        response.setComplaint(complaint);
        return responseRepository.save(response);
    }

    @Override
    public Page<Response> getAllResponsesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return responseRepository.findAll(pageable);
    }

    @Override
    public Response getResponseById(Long id) {
        return responseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Response not found with id: " + id));
    }

    @Override
    public Response updateResponse(Long id, Response response) {
        Response existingResponse = getResponseById(id);
        existingResponse.setMessage(response.getMessage());
        return responseRepository.save(existingResponse);
    }

    @Override
    public void deleteResponse(Long id) {
        Response response = getResponseById(id);
        responseRepository.delete(response);
    }
}