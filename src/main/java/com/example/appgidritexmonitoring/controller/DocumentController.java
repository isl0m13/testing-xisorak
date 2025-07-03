package com.example.appgidritexmonitoring.controller;


import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(DocumentController.DOCUMENT_CONTROLLER_BASE_PATH)
public interface DocumentController {
    String DOCUMENT_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/document";
    String ADD = "/group/{groupId}";
    String EDIT = "/{documentId}";
    String DELETE = "/{documentId}";
    String GET = "/{documentId}";
    String GET_ALL = "/group/{groupId}";
    String GET_ALL_BY_GROUP_OF_RESERVOIR = "/reservoir/{reservoirId}/get-all/by-group";

    @PostMapping(path = ADD)
    ApiResult<DocumentDTO> add(@PathVariable("groupId") UUID groupId,
                               @RequestBody DocumentAddDTO documentAddDTO);

    @PutMapping(path = EDIT)
    ApiResult<DocumentDTO> edit(@PathVariable UUID documentId,
                                @RequestBody DocumentEditDTO documentEditDTO);

    @DeleteMapping(path = DELETE)
    ApiResult<?> delete(@PathVariable UUID documentId);

    @GetMapping(path = GET)
    ApiResult<DocumentDTO> get(@PathVariable UUID documentId);

    @GetMapping(path = GET_ALL)
    ApiResult<List<DocumentDTO>> getAll(@PathVariable UUID groupId);

    @GetMapping(path = GET_ALL_BY_GROUP_OF_RESERVOIR)
    ApiResult<List<GroupDTO>> getAllByGroupOfReservoir(@PathVariable UUID reservoirId);


}
