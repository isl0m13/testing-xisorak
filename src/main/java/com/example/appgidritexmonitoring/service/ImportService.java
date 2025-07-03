package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.*;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.enums.DeviceTypeEnum;
import com.example.appgidritexmonitoring.entity.enums.PlumbTypeEnum;
import com.example.appgidritexmonitoring.payload.CalculatedRes;
import com.example.appgidritexmonitoring.payload.DeviceReadRoute;
import com.example.appgidritexmonitoring.payload.FileReadResult;
import com.example.appgidritexmonitoring.repository.*;

import com.example.appgidritexmonitoring.util.CSVReader;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.DeviceResultConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImportService {

    @Value("${app.dispechr.folder}")
    private String dispechrDirPath;

    @Value("${app.kzr_tunel.folder}")
    private String krzDirPath;

    @Value("${app.sempatern_tunell.folder}")
    private String sempaternDirPath;

    @Value("${app.vw.folder}")
    private String vwDirPath;

    @Value("${app.akhangaran.folder}")
    private String akhangaranDirPath;

    @Value("${app.anl4.folder}")
    private String anl4DirPath;

    @Value("${app.anl1.folder}")
    private String anl1DirPath;

    @Value(("${app.omnialog_795.folder}"))
    private String omnialog_795DirPath;

    @Value(("${app.omnialog_804_12_seksiya.folder}"))
    private String omnialog_804_12DirPath;

    @Value(("${app.omnialog_804_24_seksiya.folder}"))
    private String omnialog_804_24DirPath;

    @Value(("${app.omnialog_817_chap.folder}"))
    private String omnialog_817_chap_DirPath;

    @Value(("${app.omnialog_817_ong.folder}"))
    private String omnialog_817_ong_DirPath;

    @Value(("${app.omnialog_885_shelemer.folder}"))
    private String omnialog_885_shelemer;

    @Value(("${app.gateway_bosimsiz.folder}"))
    private String gateway_bosimsiz_DirPath;

    @Value(("${app.omnialog_845_atves.folder}"))
    private String omnialog_845_atves_DirPath;

    @Value(("${app.t_1000.folder}"))
    private String t_1000_DirPath;

    @Value(("${app.tiltmeters.folder}"))
    private String tiltmetersDirPath;

    @Value(("${app.omnialog_804_28_seksiya.folder}"))
    private String omnialog_804_28DirPath;

    @Value(("${app.gateway_bosimsiz_charvak.folder}"))
    private String charvak_gateway_bosimsiz_DirPath;

    @Value(("${app.gateway_vodosliv_charvak.folder}"))
    private String charvak_gateway_vodosliv_DirPath;

    @Value(("${app.tasmali_drenaj.folder}"))
    private String tasmali_drenaj_DirPath;

    @Value(("${app.transportni_sh.folder}"))
    private String transportni_sh_DirPath;

    @Value(("${app.shtolniya_1.folder}"))
    private String shtolniya_1_sh_DirPath;

    private Map<DeviceDatasourceEnum, List<Piezometer>> piezometersByDatasource;
    private Map<DeviceDatasourceEnum, List<WaterLevelGauge>> waterLevelGaugesByDatasources;
    private Map<DeviceDatasourceEnum, List<Spillway>> spillwaysByDatasources;
    private Map<DeviceDatasourceEnum, List<WaterFlowMeter>> waterFlowMeterByDatasources;
    private Map<DeviceDatasourceEnum, List<HydrologicalStation>> hydrologicalStationsByDatasources;
    private Map<DeviceDatasourceEnum, List<DamBodyDevice>> damBodyDevicesByDatasources;
    private Map<DeviceDatasourceEnum, List<CrackGauge>> crackGaugesByDatasources;
    private Map<DeviceDatasourceEnum, List<Plumb>> plumbsByDatasources;
    private Map<DeviceDatasourceEnum, List<Tiltmeter>> tiltmetersByDatasources;
    private Map<DeviceDatasourceEnum, List<Deformometer>> deformometersByDatasources;

    private final PiezometerMeasurementRepository piezometerMeasurementRepository;
    private final PiezometerRepository piezometerRepository;
    private final ReadFileRepository readFileRepository;
    private final ReservoirRepository reservoirRepository;
    private final WaterLevelGaugeRepository waterLevelGaugeRepository;
    private final WaterLevelGaugeMeasurementRepository waterLevelGaugeMeasurementRepository;
    private final SpillwayRepository spillwayRepository;
    private final SpillwayMeasurementRepository spillwayMeasurementRepository;
    private final WaterFlowMeterRepository waterFlowMeterRepository;
    private final WaterFlowMeterMeasurementRepository waterFlowMeterMeasurementRepository;
    private final HydrologicalStationRepository hydrologicalStationRepository;
    private final HydrologicalStationMeasurementRepository hydrologicalStationMeasurementRepository;
    private final DamBodyDeviceRepository damBodyDeviceRepository;
    private final DamBodyDeviceMeasurementRepository damBodyDeviceMeasurementRepository;
    private final CrackGaugeMeasurementRepository crackGaugeMeasurementRepository;
    private final CrackGaugeRepository crackGaugeRepository;
    private final PlumbRepository plumbRepository;
    private final PlumbMeasurementRepository plumbMeasurementRepository;
    private final WaterLevelInVolumeRepository waterLevelInVolumeRepository;
    private final TiltmeterMeasurementRepository tiltmeterMeasurementRepository;
    private final TiltmeterRepository tiltmeterRepository;
    private final DeformometerRepository deformometerRepository;
    private final DeformometerMeasurementRepository deformometerMeasurementRepository;


    public ImportService(PiezometerMeasurementRepository piezometerMeasurementRepository,
                         PiezometerRepository piezometerRepository,
                         ReadFileRepository readFileRepository,
                         ReservoirRepository reservoirRepository,
                         WaterLevelGaugeRepository waterLevelGaugeRepository,
                         WaterLevelGaugeMeasurementRepository waterLevelGaugeMeasurementRepository,
                         SpillwayRepository spillwayRepository,
                         SpillwayMeasurementRepository spillwayMeasurementRepository,
                         WaterFlowMeterRepository waterFlowMeterRepository,
                         WaterFlowMeterMeasurementRepository waterFlowMeterMeasurementRepository,
                         HydrologicalStationRepository hydrologicalStationRepository,
                         HydrologicalStationMeasurementRepository hydrologicalStationMeasurementRepository,
                         DamBodyDeviceRepository damBodyDeviceRepository,
                         DamBodyDeviceMeasurementRepository damBodyDeviceMeasurementRepository,
                         CrackGaugeRepository crackGaugeRepository,
                         CrackGaugeMeasurementRepository crackGaugeMeasurementRepository,
                         PlumbRepository plumbRepository,
                         PlumbMeasurementRepository plumbMeasurementRepository,
                         WaterLevelInVolumeRepository waterLevelInVolumeRepository,
                         TiltmeterMeasurementRepository tiltmeterMeasurementRepository,
                         TiltmeterRepository tiltmeterRepository,
                         DeformometerRepository deformometerRepository,
                         DeformometerMeasurementRepository deformometerMeasurementRepository) {
        this.piezometerMeasurementRepository = piezometerMeasurementRepository;
        this.piezometerRepository = piezometerRepository;
        this.readFileRepository = readFileRepository;
        this.reservoirRepository = reservoirRepository;
        this.waterLevelGaugeRepository = waterLevelGaugeRepository;
        this.waterLevelGaugeMeasurementRepository = waterLevelGaugeMeasurementRepository;
        this.spillwayRepository = spillwayRepository;
        this.spillwayMeasurementRepository = spillwayMeasurementRepository;
        this.waterFlowMeterRepository = waterFlowMeterRepository;
        this.waterFlowMeterMeasurementRepository = waterFlowMeterMeasurementRepository;
        this.hydrologicalStationRepository = hydrologicalStationRepository;
        this.hydrologicalStationMeasurementRepository = hydrologicalStationMeasurementRepository;
        this.damBodyDeviceRepository = damBodyDeviceRepository;
        this.damBodyDeviceMeasurementRepository = damBodyDeviceMeasurementRepository;
        this.crackGaugeRepository = crackGaugeRepository;
        this.crackGaugeMeasurementRepository = crackGaugeMeasurementRepository;
        this.plumbRepository = plumbRepository;
        this.plumbMeasurementRepository = plumbMeasurementRepository;
        this.waterLevelInVolumeRepository = waterLevelInVolumeRepository;
        this.tiltmeterMeasurementRepository = tiltmeterMeasurementRepository;
        this.tiltmeterRepository = tiltmeterRepository;
        this.deformometerRepository = deformometerRepository;
        this.deformometerMeasurementRepository = deformometerMeasurementRepository;
    }


    //every 60 minutes
    public void read() {
        List<DeviceReadRoute> deviceReadRoutes = generateReadRoutesForNewDeviceFiles();
        for (DeviceReadRoute deviceReadRoute : deviceReadRoutes) {
            Set<File> filesOfReadRoute = deviceReadRoute.getFiles();
            if (!filesOfReadRoute.isEmpty()) {
                for (File currentFile : filesOfReadRoute) {
                    FileReadResult fileReadResult = readFileByDatasourceType(currentFile, deviceReadRoute.getDeviceDatasourceEnum());
                    saveDeviceMeasurements(fileReadResult, deviceReadRoute.getDeviceTypeEnum(), deviceReadRoute.getDeviceDatasourceEnum());
                }
            }
        }
    }

    private void saveDeviceMeasurements(FileReadResult fileReadResult,
                                        DeviceTypeEnum deviceTypeEnum,
                                        DeviceDatasourceEnum deviceDatasourceEnum) {
        switch (deviceTypeEnum) {
            case PIEZOMETER -> {
                List<Piezometer> piezometers = piezometersByDatasource.get(deviceDatasourceEnum);
                savePiezometerMeasurements(piezometers, fileReadResult);
            }
            case WATER_LEVEL_GAUGE -> {
                List<WaterLevelGauge> waterLevelGauges = waterLevelGaugesByDatasources.get(deviceDatasourceEnum);
                saveWaterLevelGaugeMeasurements(waterLevelGauges, fileReadResult);
            }
            case SPILLWAY -> {
                List<Spillway> spillways = spillwaysByDatasources.get(deviceDatasourceEnum);
                saveSpillwayMeasurements(spillways, fileReadResult);
            }
            case WATER_FLOW_METER -> {
                List<WaterFlowMeter> waterFlowMeters = waterFlowMeterByDatasources.get(deviceDatasourceEnum);
                saveWaterFlowMeterMeasurements(waterFlowMeters, fileReadResult);
            }
            case HYDROLOGICAL_STATION -> {
                List<HydrologicalStation> hydrologicalStations = hydrologicalStationsByDatasources.get(deviceDatasourceEnum);
                saveHydrologicalStationMeasurements(hydrologicalStations, fileReadResult);
            }
            case DAM_BODY_DEVICE -> {
                List<DamBodyDevice> damBodyDevices = damBodyDevicesByDatasources.get(deviceDatasourceEnum);
                saveDamBodyDeviceMeasurements(damBodyDevices, fileReadResult);
            }
            case CRACK_GAUGE -> {
                List<CrackGauge> crackGauges = crackGaugesByDatasources.get(deviceDatasourceEnum);
                saveCrackGaugeMeasurements(crackGauges, fileReadResult);
            }
            case PLUMB -> {
                List<Plumb> plumbs = plumbsByDatasources.get(deviceDatasourceEnum);
                savePlumbMeasurements(plumbs, fileReadResult);
            }
            case TILTMETER -> {
                List<Tiltmeter> tiltmeters = tiltmetersByDatasources.get(deviceDatasourceEnum);
                saveTiltmeterMeasurement(tiltmeters, fileReadResult);
            }
            case DEFORMOMETER -> {
                List<Deformometer> deformometers = deformometersByDatasources.get(deviceDatasourceEnum);
                saveDeformometerMeasurements(deformometers, fileReadResult);
            }
        }
    }


    private List<DeviceReadRoute> generateReadRoutesForNewDeviceFiles() {
        List<DeviceReadRoute> deviceReadRoutes = new ArrayList<>();
        List<DeviceTypeEnum> deviceTypeEnums = List.of(
                DeviceTypeEnum.PIEZOMETER,
                DeviceTypeEnum.WATER_LEVEL_GAUGE,
                DeviceTypeEnum.SPILLWAY,
                DeviceTypeEnum.WATER_FLOW_METER,
                DeviceTypeEnum.HYDROLOGICAL_STATION,
                DeviceTypeEnum.DAM_BODY_DEVICE,
                DeviceTypeEnum.CRACK_GAUGE,
                DeviceTypeEnum.PLUMB,
                DeviceTypeEnum.TILTMETER,
                DeviceTypeEnum.DEFORMOMETER
        );

        for (DeviceTypeEnum deviceTypeEnum : deviceTypeEnums) {
            List<DeviceDatasourceEnum> deviceDatasourceList = getDeviceDatasourceListOfDeviceType(deviceTypeEnum);
            for (DeviceDatasourceEnum deviceDatasourceEnum : deviceDatasourceList) {
                Set<File> newFilesOfFolder = getNewFilesFromFolder(deviceDatasourceEnum);
                DeviceReadRoute deviceReadRoute = DeviceReadRoute.builder()
                        .deviceTypeEnum(deviceTypeEnum)
                        .deviceDatasourceEnum(deviceDatasourceEnum)
                        .files(newFilesOfFolder)
                        .build();
                deviceReadRoutes.add(deviceReadRoute);
            }
        }
        return deviceReadRoutes;
    }

    private List<DeviceDatasourceEnum> getDeviceDatasourceListOfDeviceType(DeviceTypeEnum deviceTypeEnum) {
        List<DeviceDatasourceEnum> deviceDatasourceEnums = new ArrayList<>();
        if (deviceTypeEnum.equals(DeviceTypeEnum.PIEZOMETER))
            deviceDatasourceEnums = piezometerRepository.getAllDeviceDatasource();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.WATER_LEVEL_GAUGE))
            deviceDatasourceEnums = waterLevelGaugeRepository.getAllDeviceDatasource();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.SPILLWAY))
            deviceDatasourceEnums = spillwayRepository.getAllDeviceDatasource();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.WATER_FLOW_METER))
            deviceDatasourceEnums = waterFlowMeterRepository.getAllDeviceDatasource();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.HYDROLOGICAL_STATION))
            deviceDatasourceEnums = hydrologicalStationRepository.getAllDeviceDatasource();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.DAM_BODY_DEVICE))
            deviceDatasourceEnums = damBodyDeviceRepository.getAllDatasources();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.CRACK_GAUGE))
            deviceDatasourceEnums = crackGaugeRepository.getAllDatasources();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.PLUMB))
            deviceDatasourceEnums = plumbRepository.getAllDatasources();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.TILTMETER))
            deviceDatasourceEnums = tiltmeterRepository.getAllDatasources();
        else if (deviceTypeEnum.equals(DeviceTypeEnum.DEFORMOMETER))
            deviceDatasourceEnums = deformometerRepository.getAllDatasources();
        return deviceDatasourceEnums;
    }


    public void importPiezometersByDeviceDatasource() {
        List<DeviceDatasourceEnum> deviceDatasourceEnums = Arrays.asList(
                DeviceDatasourceEnum.DISPECHR,
                DeviceDatasourceEnum.KRZ_TUNEL,
                DeviceDatasourceEnum.SEMPATERN_TUNELL,
                DeviceDatasourceEnum.VW,
                DeviceDatasourceEnum.ANL4,
                DeviceDatasourceEnum.AKHANGARAN,
                DeviceDatasourceEnum.OMNIALOG_795,
                DeviceDatasourceEnum.OMNIALOG_817_CHAP,
                DeviceDatasourceEnum.OMNIALOG_817_ONG,
                DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA,
                DeviceDatasourceEnum.GATEWAY_BOSIMSIZ,
                DeviceDatasourceEnum.CHARVAK_GATEWAY_BOSIMSIZ,
                DeviceDatasourceEnum.TASMALI_DRENAJ,
                DeviceDatasourceEnum.TRANSPORTNI_SH,
                DeviceDatasourceEnum.SHTOLNIYA_1
        );
        Map<DeviceDatasourceEnum, List<Piezometer>> newFilesOfFoldersByDatasource = new HashMap<>();
        for (DeviceDatasourceEnum deviceDatasourceEnum : deviceDatasourceEnums) {
            newFilesOfFoldersByDatasource.put(deviceDatasourceEnum, getPiezometersByDeviceDatasource(deviceDatasourceEnum));
        }
        this.piezometersByDatasource = newFilesOfFoldersByDatasource;
    }

    public void importWaterLevelGaugesByDeviceDatasource() {
        List<DeviceDatasourceEnum> deviceDatasourceEnums = List.of(DeviceDatasourceEnum.AKHANGARAN, DeviceDatasourceEnum.ANL4, DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV, DeviceDatasourceEnum.T_1000);
        Map<DeviceDatasourceEnum, List<WaterLevelGauge>> waterLevelGaugesByDatasources = new HashMap<>();
        for (DeviceDatasourceEnum deviceDatasourceEnum : deviceDatasourceEnums) {
            waterLevelGaugesByDatasources.put(deviceDatasourceEnum, waterLevelGaugeRepository.findByDeviceDatasource(deviceDatasourceEnum));
        }
        this.waterLevelGaugesByDatasources = waterLevelGaugesByDatasources;
    }

    public void importSpillwaysByDeviceDatasource() {
        Map<DeviceDatasourceEnum, List<Spillway>> spillwaysByDatasource = new HashMap<>();
        List<DeviceDatasourceEnum> deviceDatasourceEnums = List.of(
                DeviceDatasourceEnum.ANL4,
                DeviceDatasourceEnum.KRZ_TUNEL,
                DeviceDatasourceEnum.SEMPATERN_TUNELL,
                DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_804_28_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_817_CHAP,
                DeviceDatasourceEnum.SHTOLNIYA_1,
                DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV);
        for (DeviceDatasourceEnum deviceDatasourceEnum : deviceDatasourceEnums) {
            spillwaysByDatasource.put(deviceDatasourceEnum, spillwayRepository.findByDeviceDatasource(deviceDatasourceEnum));
        }
        this.spillwaysByDatasources = spillwaysByDatasource;
    }

    public void importWaterFlowMetersByDeviceDatasource() {
        List<WaterFlowMeter> waterFlowMetersAkh = waterFlowMeterRepository.findByDeviceDatasource(DeviceDatasourceEnum.AKHANGARAN);
        HashMap<DeviceDatasourceEnum, List<WaterFlowMeter>> waterFlowMetersByDatasource = new HashMap<>();
        waterFlowMetersByDatasource.put(DeviceDatasourceEnum.AKHANGARAN, waterFlowMetersAkh);
        this.waterFlowMeterByDatasources = waterFlowMetersByDatasource;
    }

    public void importHydrologicalStationsByDeviceDatasource() {
        List<HydrologicalStation> hydrologicalStationsAkh = hydrologicalStationRepository.findByDeviceDatasource(DeviceDatasourceEnum.AKHANGARAN);
        List<HydrologicalStation> hydrologicalStationsXis = hydrologicalStationRepository.findByDeviceDatasource(DeviceDatasourceEnum.ANL1);
        Map<DeviceDatasourceEnum, List<HydrologicalStation>> hydrologicalStationsByDatasource = new HashMap<>();
        hydrologicalStationsByDatasource.put(DeviceDatasourceEnum.AKHANGARAN, hydrologicalStationsAkh);
        hydrologicalStationsByDatasource.put(DeviceDatasourceEnum.ANL1, hydrologicalStationsXis);
        this.hydrologicalStationsByDatasources = hydrologicalStationsByDatasource;
    }

    public void importDamBodyDevicesByDeviceDatasource() {
        Map<DeviceDatasourceEnum, List<DamBodyDevice>> damBodyDeviceByDatasource = new HashMap<>();
        List<DeviceDatasourceEnum> allDatasources = damBodyDeviceRepository.getAllDatasources();
        for (DeviceDatasourceEnum deviceDatasourceEnum : allDatasources) {
            damBodyDeviceByDatasource.put(deviceDatasourceEnum, damBodyDeviceRepository.findByDeviceDatasource(deviceDatasourceEnum));
        }
        this.damBodyDevicesByDatasources = damBodyDeviceByDatasource;
    }

    public void importCrackGaugesByDeviceDatasource() {
        Map<DeviceDatasourceEnum, List<CrackGauge>> crackGaugesByDatasource = new HashMap<>();
        List<CrackGauge> crackGauges885Andijan = crackGaugeRepository.findByDeviceDatasource(DeviceDatasourceEnum.OMNIALOG_885_SHELEMER);
        List<CrackGauge> crackGauges804_12_Andijan = crackGaugeRepository.findByDeviceDatasource(DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA);
        List<CrackGauge> crackGauges804_24_Andijan = crackGaugeRepository.findByDeviceDatasource(DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA);
        crackGaugesByDatasource.put(DeviceDatasourceEnum.OMNIALOG_885_SHELEMER, crackGauges885Andijan);
        crackGaugesByDatasource.put(DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA, crackGauges804_12_Andijan);
        crackGaugesByDatasource.put(DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA, crackGauges804_24_Andijan);
        this.crackGaugesByDatasources = crackGaugesByDatasource;
    }

    public void importPlumbsByDeviceDatasource() {
        Map<DeviceDatasourceEnum, List<Plumb>> plumbsByDatasource = new HashMap<>();
        var deviceDatasourceEnums = List.of(
                DeviceDatasourceEnum.OMNIALOG_845_ATVES,
                DeviceDatasourceEnum.T_1000,
                DeviceDatasourceEnum.OMNIALOG_817_ONG,
                DeviceDatasourceEnum.OMNIALOG_817_CHAP
        );
        for (DeviceDatasourceEnum currentDeviceDatasource : deviceDatasourceEnums) {
            List<Plumb> plumbsByCurrentDeviceDatasource = plumbRepository.findByDeviceDatasource(currentDeviceDatasource);
            plumbsByDatasource.put(currentDeviceDatasource, plumbsByCurrentDeviceDatasource);
        }
        this.plumbsByDatasources = plumbsByDatasource;
    }

    public void importTiltmetersByDeviceDatasource() {
        Map<DeviceDatasourceEnum, List<Tiltmeter>> tiltmetersByDatasource = new HashMap<>();
        var deviceDatasourceEnums = List.of(
                DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA,
                DeviceDatasourceEnum.TILTMETERS
        );
        for (DeviceDatasourceEnum currentDeviceDatasource : deviceDatasourceEnums) {
            List<Tiltmeter> tiltmetersByCurrentDeviceDatasource = tiltmeterRepository.findByDeviceDatasource(currentDeviceDatasource);
            tiltmetersByDatasource.put(currentDeviceDatasource, tiltmetersByCurrentDeviceDatasource);
        }
        this.tiltmetersByDatasources = tiltmetersByDatasource;
    }

    public void importDeformometersByDeviceDatasource() {
        Map<DeviceDatasourceEnum, List<Deformometer>> deformometersByDatasources = new HashMap<>();
        deformometersByDatasources.put(DeviceDatasourceEnum.TRANSPORTNI_SH, deformometerRepository.findByDeviceDatasource(DeviceDatasourceEnum.TRANSPORTNI_SH));

        this.deformometersByDatasources = deformometersByDatasources;
    }


    private FileReadResult readFileByDatasourceType(File file, DeviceDatasourceEnum datasourceEnum) {
        List<DeviceDatasourceEnum> firstTypesOfDeviceEnums = Arrays.asList(
                DeviceDatasourceEnum.DISPECHR,
                DeviceDatasourceEnum.KRZ_TUNEL,
                DeviceDatasourceEnum.SEMPATERN_TUNELL,
                DeviceDatasourceEnum.OMNIALOG_795,
                DeviceDatasourceEnum.OMNIALOG_817_CHAP,
                DeviceDatasourceEnum.OMNIALOG_817_ONG,
                DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_804_28_SEKSIYA,
                DeviceDatasourceEnum.OMNIALOG_885_SHELEMER,
                DeviceDatasourceEnum.OMNIALOG_845_ATVES,
                DeviceDatasourceEnum.TASMALI_DRENAJ,
                DeviceDatasourceEnum.TRANSPORTNI_SH,
                DeviceDatasourceEnum.SHTOLNIYA_1

        );
        List<DeviceDatasourceEnum> secondTypesOfDeviceEnums = Arrays.asList(
                DeviceDatasourceEnum.AKHANGARAN,
                DeviceDatasourceEnum.VW,
                DeviceDatasourceEnum.ANL4,
                DeviceDatasourceEnum.ANL1,
                DeviceDatasourceEnum.GATEWAY_BOSIMSIZ,
                DeviceDatasourceEnum.T_1000,
                DeviceDatasourceEnum.TILTMETERS,
                DeviceDatasourceEnum.CHARVAK_GATEWAY_BOSIMSIZ,
                DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV
        );

        Map<String, String> readData = new HashMap<>();
        if (firstTypesOfDeviceEnums.contains(datasourceEnum))
            readData = CSVReader.readFirstRecordToMapFromCSV(file);
        else if (secondTypesOfDeviceEnums.contains(datasourceEnum))
            readData = CSVReader.readSpecificRecordsToMapFromCSV(file);

        ReadFile readFile = createRecordCompletedReadFile(file);
        return FileReadResult.builder()
                .readFile(readFile)
                .readData(readData)
                .build();
    }


    private void savePiezometerMeasurements(List<Piezometer> piezometers, FileReadResult fileReadResult) {
        if (!piezometers.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            DeviceDatasourceEnum deviceDatasource = piezometers.get(0).getDeviceDatasource();
            LocalDateTime dateTime;

            if (List.of(DeviceDatasourceEnum.AKHANGARAN, DeviceDatasourceEnum.VW,
                    DeviceDatasourceEnum.GATEWAY_BOSIMSIZ, DeviceDatasourceEnum.CHARVAK_GATEWAY_BOSIMSIZ).contains(deviceDatasource))
                dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));
            else
                dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));

            for (Piezometer piezometer : piezometers) {
                String pressure = readData.get(piezometer.getPressureDatasource());
                String temp = readData.get(piezometer.getTempDatasource());
                if ((Objects.nonNull(pressure) && Objects.nonNull(temp)) &&
                        (!pressure.isBlank() && !pressure.equals("NAN")) &&
                        (!temp.isBlank() && !temp.equals("NAN")) && Objects.nonNull(dateTime)) {
                    List<Double> calculatedPressureResults = DeviceResultConverter.calculatePressureResult(piezometer, Double.parseDouble(pressure));
                    double computedPressure = calculatedPressureResults.get(0);
                    double virtualPressure = calculatedPressureResults.get(1);
                    PiezometerMeasurement piezometerMeasurement = PiezometerMeasurement.builder()
                            .piezometer(piezometer)
                            .date(dateTime)
                            .readPressure(Double.valueOf(pressure))
                            .computedPressure(computedPressure)
                            .virtualPressure(virtualPressure)
                            .readFile(fileReadResult.getReadFile())
                            .build();

                    double tempInDegrees = Double.parseDouble(temp);

                    if (List.of(DeviceDatasourceEnum.AKHANGARAN, DeviceDatasourceEnum.VW, DeviceDatasourceEnum.CHARVAK_GATEWAY_BOSIMSIZ).contains(deviceDatasource)
                            || (Objects.nonNull(piezometer.getLocation()) && List.of("np_812", "np_907").contains(piezometer.getLocation()))) {

                        piezometerMeasurement.setTempInOhms(Double.valueOf(temp));
                        tempInDegrees = DeviceResultConverter.convertOhmsToDegreesOfPiezometer(piezometer, tempInDegrees);
                    }

                    piezometerMeasurement.setTempInDegrees(tempInDegrees);

                    if (!piezometerMeasurementRepository.existsByPiezometerIdAndDate(piezometer.getId(), dateTime))
                        piezometerMeasurementRepository.save(piezometerMeasurement);
                }
            }
        }
    }

    private void saveWaterLevelGaugeMeasurements(List<WaterLevelGauge> waterLevelGauges, FileReadResult fileReadResult) {
        if (!waterLevelGauges.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();

            for (WaterLevelGauge waterLevelGauge : waterLevelGauges) {
                LocalDateTime dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));
                String readPressure = readData.get(waterLevelGauge.getPressureDatasource());
                String tempInOhms = readData.get(waterLevelGauge.getTempDatasource());
                if ((Objects.nonNull(readPressure) && Objects.nonNull(dateTime)) &&
                        (!readPressure.isBlank() && !readPressure.equals("NAN"))) {

                    CalculatedRes calculatedRes = DeviceResultConverter.calculatePressureResult(waterLevelGauge, Double.valueOf(readPressure));
                    Double computedPressure = calculatedRes.getFinalRes();
                    Double volumeValue = getVolumeValueFromWaterLevelValue(Double.valueOf(String.format(Locale.US, "%.2f", computedPressure)), waterLevelGauge.getReservoir().getId());
                    Double volumeRes = Objects.nonNull(volumeValue) ? volumeValue * 1_000_000 : null;
                    var waterLevelGaugeMeasurement = WaterLevelGaugeMeasurement.builder()
                            .waterLevelGauge(waterLevelGauge)
                            .date(dateTime)
                            .readFile(fileReadResult.getReadFile())
                            .readPressure(Double.valueOf(readPressure))
                            .virtualPressure(calculatedRes.getVirtualRes())
                            .computedPressure(computedPressure)
                            .volumeValue(volumeRes)
                            .build();


                    if (Objects.nonNull(tempInOhms) && !tempInOhms.isBlank() && !tempInOhms.equals("NAN")) {
                        double ohmValue = Double.parseDouble(tempInOhms);
                        waterLevelGaugeMeasurement.setTempInOhms(ohmValue);
                        waterLevelGaugeMeasurement.setTempInDegrees(DeviceResultConverter.convertOhmsToDegrees(ohmValue));
                    }

                    if (!waterLevelGaugeMeasurementRepository.existsByWaterLevelGaugeIdAndDate(waterLevelGauge.getId(), dateTime))
                        waterLevelGaugeMeasurementRepository.save(waterLevelGaugeMeasurement);
                }
            }


        }
    }

    private void saveSpillwayMeasurements(List<Spillway> spillways, FileReadResult fileReadResult) {
        if (!spillways.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            DeviceDatasourceEnum deviceDatasource = spillways.get(0).getDeviceDatasource();
            Reservoir reservoir = spillways.get(0).getReservoir();
            LocalDateTime dateTime;

            if (List.of(DeviceDatasourceEnum.ANL4, DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV).contains(deviceDatasource))
                dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));
            else
                dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));

            for (Spillway spillway : spillways) {
                String readPressure = readData.get(spillway.getPressureDatasource());
                String readTurbidityValue = readData.get(spillway.getTurbidityDatasource());
                String tempInOhms = readData.get(spillway.getTempDatasource());
                if ((Objects.nonNull(readPressure) && Objects.nonNull(dateTime) &&
                        (!readPressure.isBlank() && !readPressure.equals("NAN")))) {

                    var spillwayMeasurement = SpillwayMeasurement.builder()
                            .spillway(spillway)
                            .date(dateTime)
                            .readFile(fileReadResult.getReadFile())
                            .readPressure(Double.valueOf(readPressure))
                            .computedWaterFlow(DeviceResultConverter.calculateWaterFlow(spillway, Double.parseDouble(readPressure)))
                            .build();

                    //SAVE TURBIDITY VALUE IF VALUE IS NUMBER
                    if (Objects.nonNull(readTurbidityValue) && !readTurbidityValue.isBlank())
                            spillwayMeasurement.setTurbidityValue(DeviceResultConverter.calculateTurbidityValue(reservoir.getName(), readTurbidityValue));


                    if (!spillwayMeasurementRepository.existsBySpillwayIdAndDate(spillway.getId(), dateTime))
                        spillwayMeasurementRepository.save(spillwayMeasurement);
                }
            }


        }
    }

    private void saveWaterFlowMeterMeasurements(List<WaterFlowMeter> waterFlowMeters, FileReadResult fileReadResult) {
        if (!waterFlowMeters.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            DeviceDatasourceEnum deviceDatasource = waterFlowMeters.get(0).getDeviceDatasource();
            LocalDateTime dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));

            for (WaterFlowMeter waterFlowMeter : waterFlowMeters) {
                String readPressure = readData.get(waterFlowMeter.getPressureDatasource());
                String tempInDegrees = readData.get(waterFlowMeter.getTempDatasource());
                if ((Objects.nonNull(readPressure) && Objects.nonNull(dateTime)) &&
                        (!readPressure.isBlank() && !readPressure.equals("NAN"))) {
                    var waterFlowMeterMeasurement = WaterFlowMeterMeasurement.builder()
                            .waterFlowMeter(waterFlowMeter)
                            .date(dateTime)
                            .tempInDegrees(Double.valueOf(tempInDegrees))
                            .readPressure(Double.valueOf(readPressure))
                            .computedWaterFlow(DeviceResultConverter.calculateWaterFlow(Double.parseDouble(readPressure)))
                            .readFile(fileReadResult.getReadFile())
                            .build();
                    if (!waterFlowMeterMeasurementRepository.existsByWaterFlowMeterIdAndDate(waterFlowMeter.getId(), dateTime))
                        waterFlowMeterMeasurementRepository.save(waterFlowMeterMeasurement);
                }
            }
        }
    }

    private void saveHydrologicalStationMeasurements(List<HydrologicalStation> hydrologicalStations, FileReadResult fileReadResult) {
        if (!hydrologicalStations.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            LocalDateTime dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));

            for (HydrologicalStation hydrologicalStation : hydrologicalStations) {
                String readPressure = readData.get(hydrologicalStation.getPressureDatasource());
                String tempInDegrees = readData.get(hydrologicalStation.getTempDatasource());
                if ((Objects.nonNull(readPressure) && Objects.nonNull(dateTime)) &&
                        (!readPressure.isBlank() && !readPressure.equals("NAN"))) {

                    CalculatedRes calculatedRes = DeviceResultConverter.calculateWaterFlow(hydrologicalStation, Double.parseDouble(readPressure));

                    var hydrologicalStationMeasurement = HydrologicalStationMeasurement.builder()
                            .hydrologicalStation(hydrologicalStation)
                            .readFile(fileReadResult.getReadFile())
                            .date(dateTime)
                            .readPressure(Double.valueOf(readPressure))
                            .computedWaterFlow(calculatedRes.getFinalRes())
                            .tempInDegrees(Double.valueOf(tempInDegrees))
                            .build();

                    if (!Objects.isNull(calculatedRes.getVirtualRes()))
                        hydrologicalStationMeasurement.setVirtualPressure(calculatedRes.getVirtualRes());

                    if (!hydrologicalStationMeasurementRepository.existsByHydrologicalStationIdAndDate(hydrologicalStation.getId(), dateTime))
                        hydrologicalStationMeasurementRepository.save(hydrologicalStationMeasurement);
                }
            }
        }
    }

    private void saveDamBodyDeviceMeasurements(List<DamBodyDevice> damBodyDevices, FileReadResult fileReadResult) {
        if (!damBodyDevices.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            LocalDateTime dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));

            for (DamBodyDevice damBodyDevice : damBodyDevices) {
                String propertyVal = readData.get(damBodyDevice.getPropertyDatasource());
                if (Objects.nonNull(propertyVal) && !propertyVal.isBlank() && !propertyVal.equals("NAN")) {

                    DamBodyDeviceMeasurement measurement = DamBodyDeviceMeasurement.builder()
                            .damBodyDevice(damBodyDevice)
                            .readFile(fileReadResult.getReadFile())
                            .date(dateTime)
                            .readValue(Double.valueOf(propertyVal))
                            .computedValue(DeviceResultConverter.calculatePropertyResult(damBodyDevice, Double.parseDouble(propertyVal)))
                            .build();

                    if (!damBodyDeviceMeasurementRepository.existsByDamBodyDeviceIdAndDate(damBodyDevice.getId(), dateTime))
                        damBodyDeviceMeasurementRepository.save(measurement);
                }
            }

        }
    }

    private void saveCrackGaugeMeasurements(List<CrackGauge> crackGauges, FileReadResult fileReadResult) {
        if (!crackGauges.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            LocalDateTime dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));

            for (CrackGauge crackGauge : crackGauges) {
                String propertyVal = readData.get(crackGauge.getMDatasource());
                String tempInDegrees = readData.get(crackGauge.getTempDatasource());
                if (Objects.nonNull(propertyVal) && (!propertyVal.isBlank() && !propertyVal.equals("NAN"))
                        && Objects.nonNull(dateTime)) {
                    double computedValue = DeviceResultConverter.calculateVirtualPressureBySensor(crackGauge, Double.parseDouble(propertyVal));

                    CrackGaugeMeasurement crackGaugeMeasurement = CrackGaugeMeasurement.builder()
                            .crackGauge(crackGauge)
                            .readFile(fileReadResult.getReadFile())
                            .date(dateTime)
                            .readValue(Double.valueOf(propertyVal))
                            .computedValue(computedValue)
                            .build();

                    if (Objects.nonNull(tempInDegrees) && !tempInDegrees.isBlank() && !tempInDegrees.equals("NAN"))
                        crackGaugeMeasurement.setTempInDegrees(Double.valueOf(tempInDegrees));

                    if (!crackGaugeMeasurementRepository.existsByCrackGaugeIdAndDate(crackGauge.getId(), dateTime)) {
                        crackGaugeMeasurementRepository.save(crackGaugeMeasurement);
                    }

                }
            }
        }
    }

    private void savePlumbMeasurements(List<Plumb> plumbs, FileReadResult fileReadResult) {
        if (!plumbs.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            DeviceDatasourceEnum deviceDatasource = plumbs.get(0).getDeviceDatasource();

            LocalDateTime dateTime;

            if (deviceDatasource.equals(DeviceDatasourceEnum.T_1000))
                dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));
            else
                dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));

            for (Plumb plumb : plumbs) {
                String xValue = readData.get(plumb.getXDatasource());
                String yValue = readData.get(plumb.getYDatasource());
                String tempInDegrees = readData.get(plumb.getTempDatasource());

                if ((Objects.nonNull(xValue) && Objects.nonNull(yValue)) &&
                        (!xValue.isBlank() && !yValue.equals("NAN")) && Objects.nonNull(dateTime)) {
                    double xComputedResult = Double.parseDouble(xValue);
                    double yComputedResult = Double.parseDouble(yValue);
                    if (plumb.getType().equals(PlumbTypeEnum.REVERSE)) {
                        xComputedResult = DeviceResultConverter.calculateSlantByPlumb(plumb, xComputedResult);
                        yComputedResult = DeviceResultConverter.calculateSlantByPlumb(plumb, yComputedResult);
                    }

                    PlumbMeasurement plumbMeasurement = PlumbMeasurement.builder()
                            .computedXValue(xComputedResult)
                            .computedYValue(yComputedResult)
                            .plumb(plumb)
                            .date(dateTime)
                            .readFile(fileReadResult.getReadFile())
                            .build();

                    if (plumb.getType().equals(PlumbTypeEnum.STRAIGHT) && !tempInDegrees.isBlank()
                            && !tempInDegrees.equals("NAN"))
                        plumbMeasurement.setTempInDegrees(Double.valueOf(tempInDegrees));

                    if (!plumbMeasurementRepository.existsByPlumbIdAndDate(plumb.getId(), dateTime))
                        plumbMeasurementRepository.save(plumbMeasurement);

                }
            }
        }
    }

    private void saveTiltmeterMeasurement(List<Tiltmeter> tiltmeters, FileReadResult fileReadResult) {
        if (!tiltmeters.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();
            DeviceDatasourceEnum deviceDatasource = tiltmeters.get(0).getDeviceDatasource();

            LocalDateTime dateTime;

            if (deviceDatasource.equals(DeviceDatasourceEnum.TILTMETERS))
                dateTime = CommonUtils.parseLocalDateTimeFromStringDash(readData.get("Date-and-time"));
            else
                dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));

            for (Tiltmeter tiltmeter : tiltmeters) {
                String sinA = readData.get(tiltmeter.getSinADatasource());
                String sinB = readData.get(tiltmeter.getSinBDatasource());
                String tempInDegrees = readData.get(tiltmeter.getTempDatasource());

                if ((Objects.nonNull(sinA) && Objects.nonNull(sinB)) &&
                        (!sinA.isBlank() && !sinB.equals("NAN")) && Objects.nonNull(dateTime)) {
                    double sinAValue = Double.parseDouble(sinA);
                    double sinBValue = Double.parseDouble(sinB);

                    double computedTiltValue = DeviceResultConverter.calculateTiltValueByTiltmeter(tiltmeter, sinBValue);

                    TiltmeterMeasurement tiltmeterMeasurement = TiltmeterMeasurement.builder()
                            .sinAValue(sinAValue)
                            .sinBValue(sinBValue)
                            .computedTiltValue(computedTiltValue)
                            .tiltmeter(tiltmeter)
                            .date(dateTime)
                            .readFile(fileReadResult.getReadFile())
                            .build();

                    if (!tempInDegrees.isBlank() && !tempInDegrees.equals("NAN"))
                        tiltmeterMeasurement.setTempInDegrees(Double.valueOf(tempInDegrees));

                    if (!tiltmeterMeasurementRepository.existsByTiltmeterIdAndDate(tiltmeter.getId(), dateTime))
                        tiltmeterMeasurementRepository.save(tiltmeterMeasurement);
                }
            }

        }
    }

    private void saveDeformometerMeasurements(List<Deformometer> deformometers, FileReadResult fileReadResult) {
        if (!deformometers.isEmpty() && !fileReadResult.getReadData().isEmpty()) {
            Map<String, String> readData = fileReadResult.getReadData();

            for (Deformometer deformometer : deformometers) {
                LocalDateTime dateTime = CommonUtils.parseLocalDateTimeFromStringSlash(readData.get("Date Time"));
                String readValue = readData.get(deformometer.getPressureDatasource());
                String tempInDegrees = readData.get(deformometer.getTempDatasource());
                if ((Objects.nonNull(readValue) && Objects.nonNull(dateTime)) &&
                        (!readValue.isBlank() && !readValue.equals("NAN"))) {

                    double calculateValue = DeviceResultConverter.calculatePropertyResult(deformometer, Double.valueOf(readValue));

                    var deformometerMeasurement = DeformometerMeasurement.builder()
                            .deformometer(deformometer)
                            .date(dateTime)
                            .readFile(fileReadResult.getReadFile())
                            .readValue(Double.valueOf(readValue))
                            .computedValue(calculateValue)
                            .build();


                    if (Objects.nonNull(tempInDegrees) && !tempInDegrees.isBlank() && !tempInDegrees.equals("NAN")) {
                        deformometerMeasurement.setTempInDegrees(Double.parseDouble(tempInDegrees));
                    }

                    if (!deformometerMeasurementRepository.existsByDeformometerIdAndDate(deformometer.getId(), dateTime))
                        deformometerMeasurementRepository.save(deformometerMeasurement);
                }
            }


        }
    }

    private Double getVolumeValueFromWaterLevelValue(Double waterLevelValue, UUID reservoirId) {
        Optional<WaterLevelInVolume> optionalWaterLevelInVolume = waterLevelInVolumeRepository.findByReservoirIdAndWaterLevelEquals(reservoirId, waterLevelValue);
        return optionalWaterLevelInVolume.map(WaterLevelInVolume::getVolumeVal).orElse(null);
    }

    private ReadFile createRecordCompletedReadFile(File file) {
        Reservoir reservoir = reservoirRepository.findReservoirByName("Оҳангарон сув омбори").orElseThrow();
        ReadFile readFile = ReadFile.builder()
                .datasource(mapDeviceDataSourceFromFile(file))     //mapDeviceDataSourceFromFile method
                .path(file.getAbsolutePath())
                .name(file.getName())
                .date(LocalDateTime.now())
                .reservoir(reservoir)
                .build();
        return readFileRepository.save(readFile);
    }

    private Set<File> getNewFilesFromFolder(DeviceDatasourceEnum datasource) {
        File folder = mapDirFromDeviceDatasource(datasource);
        Set<File> newFiles = new HashSet<>();
        File[] files = folder.listFiles();
        if (Objects.nonNull(files)) {
            for (File file : files) {
                if (file.isFile() && !isFileAlreadyRead(file, datasource))
                    newFiles.add(file);
            }
        }
        return newFiles;
    }

    private boolean isFileAlreadyRead(File file, DeviceDatasourceEnum folder) {
        return readFileRepository.existsByNameAndDatasource(file.getName(), folder);
    }

    private List<Piezometer> getPiezometersByDeviceDatasource(DeviceDatasourceEnum datasourceEnum) {
        return piezometerRepository.findAllByDeviceDatasource(datasourceEnum);
    }

    private File mapDirFromDeviceDatasource(DeviceDatasourceEnum datasource) {
        File file = null;
        switch (datasource) {
            case DISPECHR -> file = new File(dispechrDirPath);
            case KRZ_TUNEL -> file = new File(krzDirPath);
            case SEMPATERN_TUNELL -> file = new File(sempaternDirPath);
            case VW -> file = new File(vwDirPath);
            case AKHANGARAN -> file = new File(akhangaranDirPath);
            case ANL4 -> file = new File(anl4DirPath);
            case ANL1 -> file = new File(anl1DirPath);
            case OMNIALOG_795 -> file = new File(omnialog_795DirPath);
            case OMNIALOG_817_CHAP -> file = new File(omnialog_817_chap_DirPath);
            case OMNIALOG_817_ONG -> file = new File(omnialog_817_ong_DirPath);
            case OMNIALOG_804_12_SEKSIYA -> file = new File(omnialog_804_12DirPath);
            case OMNIALOG_804_24_SEKSIYA -> file = new File(omnialog_804_24DirPath);
            case GATEWAY_BOSIMSIZ -> file = new File(gateway_bosimsiz_DirPath);
            case OMNIALOG_885_SHELEMER -> file = new File(omnialog_885_shelemer);
            case OMNIALOG_845_ATVES -> file = new File(omnialog_845_atves_DirPath);
            case T_1000 -> file = new File(t_1000_DirPath);
            case TILTMETERS -> file = new File(tiltmetersDirPath);
            case OMNIALOG_804_28_SEKSIYA -> file = new File(omnialog_804_28DirPath);
            case CHARVAK_GATEWAY_BOSIMSIZ -> file = new File(charvak_gateway_bosimsiz_DirPath);
            case TASMALI_DRENAJ -> file = new File(tasmali_drenaj_DirPath);
            case TRANSPORTNI_SH -> file = new File(transportni_sh_DirPath);
            case SHTOLNIYA_1 -> file = new File(shtolniya_1_sh_DirPath);
            case CHARVAK_GATEWAY_VODOSLIV -> file = new File(charvak_gateway_vodosliv_DirPath);
        }
        return file;
    }

    private DeviceDatasourceEnum mapDeviceDataSourceFromFile(File file) {
        //C:\ftp\Sempatern_tunell\mLog_03_04_24__14_19_56.csv
        //for reservoir servers we put - \\ for other - /
        String absolutePathOfFile = file.getAbsolutePath();
        String pathOfDir = absolutePathOfFile.substring(0, absolutePathOfFile.lastIndexOf("/"));
        if (pathOfDir.equals(dispechrDirPath))
            return DeviceDatasourceEnum.DISPECHR;
        else if (pathOfDir.equals(krzDirPath))
            return DeviceDatasourceEnum.KRZ_TUNEL;
        else if (pathOfDir.equals(sempaternDirPath))
            return DeviceDatasourceEnum.SEMPATERN_TUNELL;
        else if (pathOfDir.equals(akhangaranDirPath))
            return DeviceDatasourceEnum.AKHANGARAN;
        else if (pathOfDir.equals(anl4DirPath))
            return DeviceDatasourceEnum.ANL4;
        else if (pathOfDir.equals(anl1DirPath))
            return DeviceDatasourceEnum.ANL1;
        else if (pathOfDir.equals(omnialog_795DirPath))
            return DeviceDatasourceEnum.OMNIALOG_795;
        else if (pathOfDir.equals(omnialog_804_12DirPath))
            return DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA;
        else if (pathOfDir.equals(omnialog_804_24DirPath))
            return DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA;
        else if (pathOfDir.equals(omnialog_817_ong_DirPath))
            return DeviceDatasourceEnum.OMNIALOG_817_ONG;
        else if (pathOfDir.equals(omnialog_817_chap_DirPath))
            return DeviceDatasourceEnum.OMNIALOG_817_CHAP;
        else if (pathOfDir.equals(gateway_bosimsiz_DirPath))
            return DeviceDatasourceEnum.GATEWAY_BOSIMSIZ;
        else if (pathOfDir.equals(omnialog_885_shelemer))
            return DeviceDatasourceEnum.OMNIALOG_885_SHELEMER;
        else if (pathOfDir.equals(omnialog_845_atves_DirPath))
            return DeviceDatasourceEnum.OMNIALOG_845_ATVES;
        else if (pathOfDir.equals(t_1000_DirPath))
            return DeviceDatasourceEnum.T_1000;
        else if (pathOfDir.equals(tiltmetersDirPath))
            return DeviceDatasourceEnum.TILTMETERS;
        else if (pathOfDir.equals(omnialog_804_28DirPath))
            return DeviceDatasourceEnum.OMNIALOG_804_28_SEKSIYA;
        else if (pathOfDir.equals(charvak_gateway_bosimsiz_DirPath))
            return DeviceDatasourceEnum.CHARVAK_GATEWAY_BOSIMSIZ;
        else if (pathOfDir.equals(tasmali_drenaj_DirPath))
            return DeviceDatasourceEnum.TASMALI_DRENAJ;
        else if (pathOfDir.equals(transportni_sh_DirPath))
            return DeviceDatasourceEnum.TRANSPORTNI_SH;
        else if (pathOfDir.equals(shtolniya_1_sh_DirPath))
            return DeviceDatasourceEnum.SHTOLNIYA_1;
        else if (pathOfDir.equals(charvak_gateway_vodosliv_DirPath))
            return DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV;
        return DeviceDatasourceEnum.VW;
    }







}
