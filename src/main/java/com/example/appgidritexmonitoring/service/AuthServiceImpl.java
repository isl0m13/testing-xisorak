package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Attachment;
import com.example.appgidritexmonitoring.entity.Images;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.User;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.repository.AttachmentRepository;
import com.example.appgidritexmonitoring.repository.UserRepository;
import com.example.appgidritexmonitoring.security.JwtTokenProvider;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.MessageConstants;
import com.example.appgidritexmonitoring.util.RestConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AttachmentRepository attachmentRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtTokenProvider jwtTokenProvider,
                           @Lazy AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder,
                           AttachmentRepository attachmentRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameEqualsIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public ApiResult<TokenDTO> signIn(SignDTO signDTO) {
        User user;
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signDTO.getUserName(), signDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            user = (User) authentication.getPrincipal();
        } catch (DisabledException | LockedException | CredentialsExpiredException disabledException) {
            throw RestException.restThrow(MessageConstants.USER_NOT_FOUND_OR_DISABLED, HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException | UsernameNotFoundException badCredentialsException) {
            throw RestException.restThrow(MessageConstants.LOGIN_OR_PASSWORD_ERROR, HttpStatus.UNAUTHORIZED);
        }
        return ApiResult.successResponse(generateTokenDTO(user), MessageConstants.SUCCESSFULLY_TOKEN_GENERATED);
    }


    @Override
    public ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken) {
        try {
            String userId = jwtTokenProvider.getUserIdFromToken(refreshToken, false);
            User user = getUserByIdOrThrow(UUID.fromString(userId));

            if (!user.isEnabled() || !user.isAccountNonExpired() || !user.isAccountNonLocked() || !user.isCredentialsNonExpired())
                throw RestException.restThrow(MessageConstants.USER_PERMISSION_RESTRICTION, HttpStatus.UNAUTHORIZED);

            return ApiResult.successResponse(generateTokenDTO(user));
        } catch (Exception e) {
            throw RestException.restThrow(MessageConstants.REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ApiResult<UserDTODetails> getUserByToken() {
        User user = CommonUtils.getCurrentUser();
        List<PagePinDTO> pages = getPagePinDTOsFromUser(user);
        Attachment avatar = user.getAvatar();
        Set<ReservoirDTO> reservoirs = user.getReservoirs()
                .stream()
                .map(this::mapReservoirDTOFromReservoir)
                .collect(Collectors.toSet());

        UserDTODetails userDTODetails = UserDTODetails.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .reservoirs(reservoirs)
                .pagePins(pages)
                .avatarDownloadUri((!Objects.isNull(avatar)) ? avatar.getDownloadUri() : null)
                .build();

        return ApiResult.successResponse(userDTODetails);
    }

    @Override
    public User getUserByIdOrThrow(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RestException(MessageConstants.TOKEN_INVALID, HttpStatus.UNAUTHORIZED));
    }

    private TokenDTO generateTokenDTO(User user) {
        LocalDateTime tokenIssuedAt = LocalDateTime.now();
        String accessToken = jwtTokenProvider.generateAccessToken(user, Timestamp.valueOf(tokenIssuedAt));
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        user.setTokenIssuedAt(tokenIssuedAt);
        userRepository.save(user);
        return TokenDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private List<PagePinDTO> getPagePinDTOsFromUser(User user) {
        String pagesJson = Optional.ofNullable(user.getPages()).orElse("");
        List<PagePinDTO> savedPages;
        try {
            savedPages = pagesJson.isEmpty() ? Collections.emptyList() : RestConstants.OBJECT_MAPPER.readValue(pagesJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_VALID, HttpStatus.CONFLICT);
        }
        return savedPages;
    }

    private ReservoirDTO mapReservoirDTOFromReservoir(Reservoir reservoir) {
        return ReservoirDTO.builder()
                .id(reservoir.getId())
                .name(reservoir.getName())
                .build();
    }


}
