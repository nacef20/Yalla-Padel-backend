package tn.esprit.reclamationreponsemicroservice.services;

import tn.esprit.reclamationreponsemicroservice.entities.Response;


public interface IResponseService {

    Response createResponse(Response response);

    Response getResponseById(Long id);

    Response updateResponse(Long id, Response response);

    void deleteResponse(Long id);
}
