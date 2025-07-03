package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Group;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GroupAddDTO;
import com.example.appgidritexmonitoring.payload.GroupDTO;
import com.example.appgidritexmonitoring.repository.DocumentRepository;
import com.example.appgidritexmonitoring.repository.GroupRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final DocumentRepository documentRepository;
    private final ReservoirRepository reservoirRepository;

    @Override
    public ApiResult<GroupDTO> add(UUID reservoirId) {
        Integer countByReservoir = groupRepository.getCountByReservoirId(reservoirId);
        Group group = Group.builder()
                .name("Guruh nomi " + (countByReservoir + 1))
                .reservoir(getReservoirById(reservoirId))
                .build();
        Group savedGroup = groupRepository.save(group);
        return ApiResult.successResponse(mapGroupToDTO(savedGroup));
    }

    @Override
    public ApiResult<GroupDTO> edit(UUID groupId, GroupAddDTO groupAddDTO) {
        Group group = getGroupById(groupId);
        group.setName(groupAddDTO.getName());
        groupRepository.save(group);
        return ApiResult.successResponse(mapGroupToDTO(group));
    }

    @Override
    public ApiResult<?> delete(UUID groupId) {
        Group group = getGroupById(groupId);
        boolean existsByGroup = documentRepository.existsByGroupId(groupId);
        if (existsByGroup)
            throw RestException.restThrow(MessageConstants.GROUP_HAS_DOCUMENTS, HttpStatus.CONFLICT);
        groupRepository.delete(group);
        return ApiResult.successResponse(MessageConstants.SUCCESSFULLY_DELETED);
    }

    private Group getGroupById(UUID groupId) {
        return groupRepository
                .findById(groupId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT));
    }

    private Reservoir getReservoirById(UUID reservoirId) {
        return reservoirRepository
                .findById(reservoirId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND));
    }

    private GroupDTO mapGroupToDTO(Group group) {
        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }


}
