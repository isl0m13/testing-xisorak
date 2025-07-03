package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DocumentControllerImpl implements DocumentController {

    private final DocumentService documentService;


    @Override
    public ApiResult<DocumentDTO> add(UUID groupId, DocumentAddDTO documentAddDTO) {
        return documentService.add(groupId, documentAddDTO);
    }

    @Override
    public ApiResult<DocumentDTO> edit(UUID documentId, DocumentEditDTO documentEditDTO) {
        return documentService.edit(documentId, documentEditDTO);
    }

    @Override
    public ApiResult<?> delete(UUID documentId) {
        return documentService.delete(documentId);
    }

    @Override
    public ApiResult<DocumentDTO> get(UUID documentId) {
        return documentService.get(documentId);
    }

    @Override
    public ApiResult<List<DocumentDTO>> getAll(UUID groupId) {
        return documentService.getAll(groupId);
    }

    @Override
    public ApiResult<List<GroupDTO>> getAllByGroupOfReservoir(UUID reservoirId) {
        return documentService.getAllByGroupOfReservoir(reservoirId);
    }


}
