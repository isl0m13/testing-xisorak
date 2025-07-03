package com.example.appgidritexmonitoring.payload;

import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTODetails {

    private String firstName;
    private String lastName;
    private Set<ReservoirDTO> reservoirs;
    private List<PagePinDTO> pagePins;
    private String avatarDownloadUri;



}
