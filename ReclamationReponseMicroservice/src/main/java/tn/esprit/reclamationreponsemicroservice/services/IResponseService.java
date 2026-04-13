package tn.esprit.reclamationreponsemicroservice.services;

import java.util.List;
import tn.esprit.reclamationreponsemicroservice.entities.Response;


public interface IResponseService {

    Response createResponse(Response response);

    List<Response> getAllResponses();

    Response getResponseById(Long id);

    Response updateResponse(Long id, Response response);

    void deleteResponse(Long id);
}
