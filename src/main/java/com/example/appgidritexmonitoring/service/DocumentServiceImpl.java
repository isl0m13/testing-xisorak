package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Attachment;
import com.example.appgidritexmonitoring.entity.Document;
import com.example.appgidritexmonitoring.entity.Group;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.repository.AttachmentRepository;
import com.example.appgidritexmonitoring.repository.DocumentRepository;
import com.example.appgidritexmonitoring.repository.GroupRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final AttachmentRepository attachmentRepository;
    private final GroupRepository groupRepository;
    private final ReservoirRepository reservoirRepository;



    @Override
    public ApiResult<DocumentDTO> add(UUID groupId, DocumentAddDTO documentAddDTO) {
        Group group = getGroupById(groupId);
        Attachment attachment = getAttachmentById(documentAddDTO.getAttachmentId());

        Integer countByGroup = documentRepository.countByGroupId(groupId);
        Document document = Document.builder()
                .name("Item " + (countByGroup + 1))
                .description("Description..........")
                .group(group)
                .attachment(attachment)
                .build();
        Document savedDocument = documentRepository.save(document);
        DocumentDTO result = mapDocumentDTOFromDocument(document);
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<DocumentDTO> edit(UUID documentId, DocumentEditDTO documentEditDTO) {
        Document document = getDocumentById(documentId);
        document.setName(documentEditDTO.getName());
        document.setDescription(documentEditDTO.getDescription());
        documentRepository.save(document);
        DocumentDTO result = mapDocumentDTOFromDocument(document);
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<?> delete(UUID documentId) {
        getDocumentById(documentId);
        documentRepository.deleteById(documentId);
        return ApiResult.successResponse(MessageConstants.SUCCESSFULLY_DELETED);
    }

    @Override
    public ApiResult<DocumentDTO> get(UUID documentId) {
        Document document = getDocumentById(documentId);
        DocumentDTO result = mapDocumentDTOFromDocument(document);
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<DocumentDTO>> getAll(UUID groupId) {
        Group group = getGroupById(groupId);
        List<Document> documentList = documentRepository.getAllByGroupIdOrderByCreatedAtDesc(groupId);
        List<DocumentDTO> documentDTOS = documentList.stream().map(this::mapDocumentDTOFromDocument).toList();
        return ApiResult.successResponse(documentDTOS);
    }

    @Override
    public ApiResult<List<GroupDTO>> getAllByGroupOfReservoir(UUID reservoirId) {
        getReservoirById(reservoirId);
        List<Group> groups = groupRepository.getAllByReservoirIdOrderByCreatedAtDesc(reservoirId);
        List<GroupDTO> result = new ArrayList<>();
        List<Document> documentList = documentRepository.getAllByReservoirId(reservoirId);

        for (Group group : groups) {
            List<DocumentDTO> documentDTOS = documentList
                    .stream()
                    .filter(document -> Objects.equals(document.getGroup(), group))
                    .map(this::mapDocumentDTOFromDocument)
                    .toList();

            GroupDTO groupDTO = GroupDTO.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .documents(documentDTOS)
                    .build();
            result.add(groupDTO);
        }

        return ApiResult.successResponse(result);
    }

    private DocumentDTO mapDocumentDTOFromDocument(Document document) {
        Attachment attachment = getAttachmentById(document.getAttachment().getId());
        return DocumentDTO.builder()
                .documentId(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .attachment(mapFileResponseFromAttachment(attachment))
                .build();
    }

    private Document getDocumentById(UUID documentId) {
        return documentRepository
                .findById(documentId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT));
    }

    private Group getGroupById(UUID groupId) {
        return groupRepository
                .findById(groupId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT));
    }

    private Attachment getAttachmentById(UUID attachmentId) {
        return attachmentRepository
                .findById(attachmentId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT));
    }

    private Reservoir getReservoirById(UUID reservoirId) {
        return reservoirRepository
                .findById(reservoirId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT));
    }

    private FileResponse mapFileResponseFromAttachment(Attachment attachment) {
        return new FileResponse(
                attachment.getId(),
                attachment.getName(),
                attachment.getContentType(),
                attachment.getSize(),
                attachment.getDownloadUri()
        );
    }

}
