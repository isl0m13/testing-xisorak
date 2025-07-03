package com.example.appgidritexmonitoring.payload;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagePinDTO {
    private UUID id;
    private String name;
    private String url;
    private boolean active;


}
