package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Attachment;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.Role;
import com.example.appgidritexmonitoring.entity.User;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.repository.AttachmentRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.repository.RoleRepository;
import com.example.appgidritexmonitoring.repository.UserRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.MessageConstants;
import com.example.appgidritexmonitoring.util.RestConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AttachmentRepository attachmentRepository;
    private final ReservoirRepository reservoirRepository;

    @Override
    public ApiResult<UserDTO> add(UserAddDTO userAddDTO) {
        if (userRepository.existsByUsernameIgnoreCase(userAddDTO.getUserName())) {
            throw RestException.restThrow(MessageConstants.USERNAME_IS_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        Role role = roleRepository.findById(userAddDTO.getRoleId())
                .orElseThrow(() -> new RestException(MessageConstants.ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST));

        User savedUser = userRepository.save(mapUserAddDTOtoUser(userAddDTO, role));


        return ApiResult.successResponse(mapUserToUserDTO(savedUser));
    }


    @Override
    public ApiResult<UserDTO> edit(UserAddDTO userAddDTO, UUID id) {
        User user = getUserById(id);

        //O'ZGARTIRMOQCHI BOLGAN EMAILNI UNIQUELIKKA TEKSHIRISH
        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(userAddDTO.getUserName(), id))
            throw RestException.restThrow(MessageConstants.USERNAME_IS_ALREADY_EXISTS, HttpStatus.CONFLICT);

        // EDIT QILAYTOGAN ROLNI TOPIB OLISH
        Role role = roleRepository.findById(userAddDTO.getRoleId())
                .orElseThrow(() -> new RestException(MessageConstants.ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST));

        //ATTACHMENT ID NULL BO'LMASA ATTACHMENTNI TOPIB OLISH
        Attachment attachment = null;
        if (!Objects.isNull(userAddDTO.getAttachmentId()))
            attachment = attachmentRepository.findById(userAddDTO.getAttachmentId())
                    .orElseThrow(() -> new RestException(MessageConstants.ATTACHMENT_NOT_FOUND, HttpStatus.BAD_REQUEST));


        user.setFirstName(userAddDTO.getFirstName());
        user.setLastName(userAddDTO.getLastName());
        user.setUsername(userAddDTO.getUserName());
        user.setAvatar(attachment);
        user.setRole(role);

        User saved = userRepository.save(user);

        return ApiResult.successResponse(mapUserToUserDTO(saved));
    }

    @Override
    public ApiResult<UserDTO> delete(UUID id) {
        User user = getUserById(id);
        userRepository.deleteById(id);
        return ApiResult.successResponse(MessageConstants.SUCCESSFULLY_DELETED);
    }

    @Override
    public ApiResult<UserDTO> get(UUID id) {
        User user = getUserById(id);
        return ApiResult.successResponse(mapUserToUserDTO(user));
    }

    @Override
    public ApiResult<List<UserDTO>> getAll() {
        List<UserDTO> userDTOS =
                userRepository.findAll().stream().map(this::mapUserToUserDTO).toList();
        return ApiResult.successResponse(userDTOS);
    }


    private User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RestException(MessageConstants.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private Set<Reservoir> getReservoirsByIds(Set<UUID> reservoirIds) {
        if (reservoirIds.isEmpty())
            throw RestException.restThrow(MessageConstants.USER_MUST_HAVE_MIN_ONE_RESERVOIR);

        return reservoirIds.stream()
                .map(reservoirId -> reservoirRepository.findById(reservoirId)
                        .orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND)))
                .collect(Collectors.toSet());
    }
    private ReservoirDTO mapReservoirDTOFromReservoir(Reservoir reservoir){
        return ReservoirDTO.builder()
                .id(reservoir.getId())
                .name(reservoir.getName())
                .build();
    }


    private User mapUserAddDTOtoUser(UserAddDTO userAddDTO, Role role) {
        Set<Reservoir> reservoirs = getReservoirsByIds(userAddDTO.getReservoirs());
        return User.builder()
                .firstName(userAddDTO.getFirstName())
                .lastName(userAddDTO.getLastName())
                .username(userAddDTO.getUserName())
                .password(passwordEncoder.encode(userAddDTO.getPassword()))
                .role(role)
                .reservoirs(reservoirs)
                .enabled(true)
                .build();
    }

    private UserDTO mapUserToUserDTO(User savedUser) {
        Set<ReservoirDTO> reservoirs = savedUser.getReservoirs()
                .stream()
                .map(this::mapReservoirDTOFromReservoir)
                .collect(Collectors.toSet());

        return UserDTO.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .username(savedUser.getUsername())
                .reservoirs(reservoirs)
                .roleId(savedUser.getRole().getId())
                .build();
    }

}
