package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.entity.ReadFile;
import lombok.*;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileReadResult {

    private ReadFile readFile;

    private Map<String, String> readData;

}
