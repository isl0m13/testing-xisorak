package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.User;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.PagePinAddDTO;
import com.example.appgidritexmonitoring.payload.PagePinDTO;
import com.example.appgidritexmonitoring.repository.UserRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.MessageConstants;
import com.example.appgidritexmonitoring.util.RestConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PagePinServiceImpl implements PagePinService {
    private final UserRepository userRepository;


    @Override
    public ApiResult<PagePinDTO> add(PagePinAddDTO pagePinAddDTO) {
        User user = CommonUtils.getCurrentUser();
        List<PagePinDTO> userSavedPages = getPagePinDTOsFromUser(user);
        //CHECKING PAGE WITH URL

        if (existsPagePinDTOWithUrl(pagePinAddDTO.getUrl(), userSavedPages))
            throw RestException.restThrow(MessageConstants.PAGE_IS_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        //DEACTIVATE ACTIVE PAGE_PIN
        userSavedPages.stream()
                .filter(PagePinDTO::isActive)
                .forEach(page -> page.setActive(false));

        //CREATING ACTIVE PAGE PIN
        PagePinDTO pagePinDTO = PagePinDTO.builder()
                .id(UUID.randomUUID())
                .url(pagePinAddDTO.getUrl())
                .name(pagePinAddDTO.getName())
                .active(true)
                .build();
        userSavedPages.add(pagePinDTO);

        saveUserWithUpdatedPages(user, userSavedPages);
        return ApiResult.successResponse(pagePinDTO);
    }


    @Override
    public ApiResult<PagePinDTO> changeActive(UUID id) {
        User user = CommonUtils.getCurrentUser();
        List<PagePinDTO> pagePinDTOSet = getPagePinDTOsFromUser(user);
        if (pagePinDTOSet.isEmpty())
            throw new RestException(MessageConstants.OBJECT_IS_EMPTY, HttpStatus.CONFLICT);

        //CHANGING ACTIVE PAGE_PIN_DTO AND GETTING
        PagePinDTO pagePinDTO = null;
        for (PagePinDTO currentPagePinDTO : pagePinDTOSet) {
            if (currentPagePinDTO.getId().equals(id)) {
                currentPagePinDTO.setActive(true);
                pagePinDTO = currentPagePinDTO;
            } else
                currentPagePinDTO.setActive(false);
        }

        if (Objects.isNull(pagePinDTO))
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.NOT_FOUND);


        saveUserWithUpdatedPages(user, pagePinDTOSet);

        return ApiResult.successResponse(pagePinDTO, MessageConstants.SUCCESSFULLY_CHANGED);
    }

    @Override
    public ApiResult<?> delete(UUID id) {
        User user = CommonUtils.getCurrentUser();
        List<PagePinDTO> pagePinDTOSet = getPagePinDTOsFromUser(user);
        if (pagePinDTOSet.isEmpty())
            throw new RestException(MessageConstants.OBJECT_IS_EMPTY, HttpStatus.CONFLICT);

        boolean deleted = pagePinDTOSet.removeIf(pagePinDTO -> pagePinDTO.getId().equals(id));
        if (!deleted)
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.NOT_FOUND);
        saveUserWithUpdatedPages(user, pagePinDTOSet);
        return ApiResult.successResponse();
    }


    private List<PagePinDTO> getPagePinDTOsFromUser(User user) {
        String pagesJson = Optional.ofNullable(user.getPages()).orElse("");
        List<PagePinDTO> savedPages;
        try {
            savedPages = pagesJson.isEmpty() ? new ArrayList<>() : RestConstants.OBJECT_MAPPER.readValue(pagesJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_VALID, HttpStatus.CONFLICT);
        }
        return savedPages;
    }

    private boolean existsPagePinDTOWithUrl(String url, List<PagePinDTO> pagePinDTOS) {
        return pagePinDTOS.stream()
                .anyMatch(pagePinDTO -> pagePinDTO.getUrl().equals(url));
    }

    private String parsePagePinDTOsToJson(List<PagePinDTO> userSavedPages) {
        try {
            return RestConstants.OBJECT_MAPPER.writeValueAsString(userSavedPages);
        } catch (JsonProcessingException e) {
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_VALID, HttpStatus.CONFLICT);
        }
    }

    private void saveUserWithUpdatedPages(User user, List<PagePinDTO> updatedPagePinDTOSet) {
        String pages = parsePagePinDTOsToJson(updatedPagePinDTOSet);
        user.setPages(pages);
        userRepository.save(user);
    }

}
