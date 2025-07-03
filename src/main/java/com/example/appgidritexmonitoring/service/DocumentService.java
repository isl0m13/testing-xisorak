package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.*;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    ApiResult<DocumentDTO> add(UUID groupId, DocumentAddDTO documentAddDTO);

    ApiResult<DocumentDTO> edit(UUID documentId, DocumentEditDTO documentEditDTO);

    ApiResult<?> delete(UUID documentId);

    ApiResult<DocumentDTO> get(UUID documentId);

    ApiResult<List<DocumentDTO>> getAll(UUID groupId);


    ApiResult<List<GroupDTO>> getAllByGroupOfReservoir(UUID reservoirId);
}
