package tn.esprit.reclamationreponsemicroservice.services;

import tn.esprit.reclamationreponsemicroservice.entities.Response;


public interface IResponseService {

    Response createResponse(Response response);

    Response getResponseById(Long id);

    void deleteResponse(Long id);
}
