package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.SafetyCriteria;
import com.example.appgidritexmonitoring.entity.enums.SafetyCriteriaStatusEnum;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.SafetyCriteriaDTO;
import com.example.appgidritexmonitoring.payload.projection.SafetyCriteriaValueDTO;
import com.example.appgidritexmonitoring.repository.SafetyCriteriaRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SafetyCriteriaServiceImpl implements SafetyCriteriaService {

    private final SafetyCriteriaRepository safetyCriteriaRepository;

    @Override
    public ApiResult<?> getValuesOfReservoir(UUID reservoirId) {
        SafetyCriteriaValueDTO safetyCriteriaValues = safetyCriteriaRepository.getValuesOfReservoir(reservoirId);
        return ApiResult.successResponse(safetyCriteriaValues);
    }

    @Override
    public ApiResult<List<SafetyCriteriaDTO>> getSafetyCriteriaStatutesOfReservoir(UUID reservoir) {
        List<SafetyCriteria> safetyCriteriaList = safetyCriteriaRepository.findAllByReservoirIdOrderByOrdinal(reservoir);
        List<SafetyCriteriaDTO> result = safetyCriteriaList.stream().map(this::mapDTOFromSafetyCriteria)
                .toList();
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<?> updateStatus(UUID safetyCriteriaId,
                                     Integer newStatus) {
        SafetyCriteria safetyCriteria = safetyCriteriaRepository
                .findById(safetyCriteriaId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.SAFETY_CRITERIA_NOT_FOUND_WITH_ID, HttpStatus.BAD_REQUEST));
        SafetyCriteriaStatusEnum status = getSafetyCriteriaStatusFromNum(newStatus);
        safetyCriteria.setStatus(status);
        safetyCriteriaRepository.save(safetyCriteria);
        return ApiResult.successResponse();
    }

    private SafetyCriteriaDTO mapDTOFromSafetyCriteria(SafetyCriteria safetyCriteria) {
        return SafetyCriteriaDTO.builder()
                .id(safetyCriteria.getId())
                .name(safetyCriteria.getName())
                .status(safetyCriteria.getStatus().ordinal() + 1)
                .build();
    }

    private SafetyCriteriaStatusEnum getSafetyCriteriaStatusFromNum(Integer statusNum) {
        switch (statusNum) {
            case 1 -> {
                return SafetyCriteriaStatusEnum.WORKING;
            }
            case 2 -> {
                return SafetyCriteriaStatusEnum.POTENTIALLY_DANGEROUS;
            }
            default -> {
                return SafetyCriteriaStatusEnum.PRE_ACCIDENT;
            }
        }
    }


}
