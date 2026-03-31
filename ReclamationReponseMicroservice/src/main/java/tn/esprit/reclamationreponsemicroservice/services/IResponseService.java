package tn.esprit.reclamationreponsemicroservice.services;

import org.springframework.data.domain.Page;
import tn.esprit.reclamationreponsemicroservice.entities.Response;


public interface IResponseService {

    Response createResponse(Response response);

    Page<Response> getAllResponsesPaginated(int page, int size);

    Response getResponseById(Long id);

    Response updateResponse(Long id, Response response);

    void deleteResponse(Long id);
}
