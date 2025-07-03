package com.example.appgidritexmonitoring.component;


import com.example.appgidritexmonitoring.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleComponent {
    private final ImportService importService;

    /*@Scheduled(fixedDelay = 1000 * 60 * 7, initialDelay = 1000 * 60 * 10)
    public void start() {
        importDevicesToRamFromDB();
        importService.read();
    }*/

    private void importDevicesToRamFromDB(){
        importService.importPiezometersByDeviceDatasource();
        importService.importWaterLevelGaugesByDeviceDatasource();
        importService.importSpillwaysByDeviceDatasource();
        importService.importWaterFlowMetersByDeviceDatasource();
        importService.importHydrologicalStationsByDeviceDatasource();
        importService.importDamBodyDevicesByDeviceDatasource();
    }




}
