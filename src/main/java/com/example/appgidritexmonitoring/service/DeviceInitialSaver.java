package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.*;
import com.example.appgidritexmonitoring.entity.enums.*;
import com.example.appgidritexmonitoring.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeviceInitialSaver {

    @Value("${app.admin.files.piezometers}")
    private String PATH_TO_PIEZOMETERS_FILE;

    @Value("${app.admin.files.water-level-gauges}")
    private String PATH_TO_WATERLEVELGAUGES_FILE;

    @Value("${app.admin.files.water-level-in-volume}")
    private String PATH_WATER_LEVEL_IN_VOLUME_FILE;

    @Value("${app.admin.files.spillways}")
    private String PATH_TO_SPILLWAYS_FILE;

    @Value("${app.admin.files.water-flow-meters}")
    private String PATH_TO_WATERFLOWMETERS_FILE;

    @Value("${app.admin.files.hydrological-stations}")
    private String PATH_TO_HYDROLOGICALSTATIONS_FILE;

    @Value("${app.admin.files.dam-body-devices}")
    private String PATH_TO_DAMBODYDEVICES_FILE;

    @Value("${app.admin.files.safety-criteria}")
    private String PATH_TO_SAFETY_CRITERIA_FILE;

    @Value("${app.admin.files.crack-gauge}")
    private String PATH_TO_CRACK_GAUGE_FILE;

    @Value("${app.admin.files.plumb}")
    private String PATH_TO_PLUMB_FILE;

    @Value("${app.admin.files.tiltmeters}")
    private String PATH_TO_TILTMETERS_FILE;

    @Value("${app.admin.files.deformometers}")
    private String PATH_TO_DEFORMOMETERS_FILE;

    private final SensorRepository sensorRepository;
    private final GateRepository gateRepository;
    private final PiezometerRepository piezometerRepository;
    private final ReservoirRepository reservoirRepository;
    private final WaterLevelGaugeRepository waterLevelGaugeRepository;
    private final SpillwayRepository spillwayRepository;
    private final WaterFlowMeterRepository waterFlowMeterRepository;
    private final HydrologicalStationRepository hydrologicalStationRepository;
    private final DamBodyDeviceRepository damBodyDeviceRepository;
    private final SafetyCriteriaRepository safetyCriteriaRepository;
    private final CrackGaugeRepository crackGaugeRepository;
    private final PlumbRepository plumbRepository;
    private final WaterLevelInVolumeRepository waterLevelInVolumeRepository;
    private final TiltmeterRepository tiltmeterRepository;
    private final DeformometerRepository deformometerRepository;


    public void saveDevices() {
        boolean toUpdate = false;
        savePiezometers(PATH_TO_PIEZOMETERS_FILE, toUpdate);  //for all
        saveWaterLevelGauges(PATH_TO_WATERLEVELGAUGES_FILE, toUpdate);  //for all
        saveSpillways(PATH_TO_SPILLWAYS_FILE, toUpdate);  //for all
        saveWaterFlowMeters(PATH_TO_WATERFLOWMETERS_FILE, toUpdate);  //for akhangaran
        saveHydrologicalStations(PATH_TO_HYDROLOGICALSTATIONS_FILE, toUpdate);  //for xisorak, akhangaran
        saveDamBodyDevices(PATH_TO_DAMBODYDEVICES_FILE, toUpdate);  //for xisorak
        saveSafetyCriteria(PATH_TO_SAFETY_CRITERIA_FILE);  //for xisorak
        saveCrackGauges(PATH_TO_CRACK_GAUGE_FILE, toUpdate);  //only for andijan
        savePlumbs(PATH_TO_PLUMB_FILE, toUpdate);  //only for andijan
        saveWaterLevelValuesInVolume(PATH_WATER_LEVEL_IN_VOLUME_FILE); //for all
        saveTiltmeters(PATH_TO_TILTMETERS_FILE, toUpdate);  //only for andijan
        saveDeformometers(PATH_TO_DEFORMOMETERS_FILE, toUpdate); //only for charvak
    }

    public void updateDevices() {
        boolean toUpdate = true;
        savePiezometers(PATH_TO_PIEZOMETERS_FILE, toUpdate);  //for all
        saveWaterLevelGauges(PATH_TO_WATERLEVELGAUGES_FILE, toUpdate);  //for all
        saveSpillways(PATH_TO_SPILLWAYS_FILE, toUpdate);  //for all
        //saveWaterFlowMeters(PATH_TO_WATERFLOWMETERS_FILE, toUpdate);  //for akhangaran
        //saveHydrologicalStations(PATH_TO_HYDROLOGICALSTATIONS_FILE, toUpdate);  //for xisorak, akhangaran
        //saveDamBodyDevices(PATH_TO_DAMBODYDEVICES_FILE, toUpdate);  //for xisorak
        //saveSafetyCriteria(PATH_TO_SAFETY_CRITERIA_FILE);  //for xisorak
        saveCrackGauges(PATH_TO_CRACK_GAUGE_FILE, toUpdate);  //only for andijan
        savePlumbs(PATH_TO_PLUMB_FILE, toUpdate);  //only for andijan
        saveWaterLevelValuesInVolume(PATH_WATER_LEVEL_IN_VOLUME_FILE); //for all
        saveTiltmeters(PATH_TO_TILTMETERS_FILE, toUpdate);  //only for andijan
        //saveDeformometers(PATH_TO_DEFORMOMETERS_FILE, toUpdate); //only for charvak
    }

    private List<CSVRecord> readCSVFile(String path) {
        CSVFormat csvFormat = CSVFormat.EXCEL.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
             CSVParser records = csvFormat.parse(bufferedReader)
        ) {
            return records.getRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void savePiezometers(String pathToFile, boolean doUpdate) {
        List<CSVRecord> records = readCSVFile(pathToFile);
        if (Objects.nonNull(records)) {
            for (CSVRecord record : records) {
                Sensor updateSensor = null;
                Piezometer updatePiezometer = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updatePiezometer = piezometerRepository.findPiezometerBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updatePiezometer.getSensor();
                }

                Sensor savedSensor = saveSensor(record, updateSensor);
                savePiezometer(record, updatePiezometer, savedSensor);

            }
        }
    }

    private Piezometer savePiezometer(CSVRecord record,
                                      Piezometer piezometer,
                                      Sensor sensor) {

        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        String gateName = record.get("gate");
        Integer typeNum = parseIntegerFromString(record.get("type"));
        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        String pressureDatasource = record.get("pressureDatasource").trim();
        String tempDatasource = record.get("tempDatasource").trim();
        Double locationPressure = Double.valueOf(record.get("locationPressure").trim());
        String name = record.get("name").trim();

        String lat = record.get("lat").isBlank() ? null : record.get("lat");
        String lon = record.get("long").isBlank() ? null : record.get("long");

        String location = record.get("location").isBlank() ? null : record.get("location");

        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());

        Piezometer savePiezometer = new Piezometer();
        if (Objects.nonNull(piezometer))
            savePiezometer = piezometer;

        savePiezometer.setName(name);
        savePiezometer.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        savePiezometer.setGate(getGateFromName(gateName, reservoirOrdinal));
        savePiezometer.setType(mapPiezometerTypeEnumFromNumber(typeNum));
        savePiezometer.setOrdinal(ordinal);
        savePiezometer.setPressureDatasource(pressureDatasource);
        savePiezometer.setTempDatasource(tempDatasource);
        savePiezometer.setLocationPressure(locationPressure);
        savePiezometer.setLocation(location);
        savePiezometer.setLat(lat);
        savePiezometer.setLon(lon);
        savePiezometer.setSensor(sensor);
        savePiezometer.setActive(true);

        return piezometerRepository.save(savePiezometer);
    }


    private void saveWaterLevelGauges(String pathToFile, boolean doUpdate) {
        List<CSVRecord> records = readCSVFile(pathToFile);
        if (Objects.nonNull(records)) {
            for (CSVRecord record : records) {
                Sensor updateSensor = null;
                WaterLevelGauge updateWaterLevelGauge = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updateWaterLevelGauge = waterLevelGaugeRepository.findWaterLevelGaugeBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updateWaterLevelGauge.getSensor();
                }

                Sensor savedSensor = saveSensor(record, updateSensor);
                saveWaterLevelGauge(record, updateWaterLevelGauge, savedSensor);


            }
        }
    }

    private WaterLevelGauge saveWaterLevelGauge(CSVRecord record,
                                                WaterLevelGauge waterLevelGauge,
                                                Sensor sensor) {

        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());
        Integer typeNum = parseIntegerFromString(record.get("type"));
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        String pressureDatasource = record.get("pressureDatasource").trim();
        String tempDatasource = record.get("tempDatasource").trim();
        Double locationPressure = Double.valueOf(record.get("locationPressure").trim());

        WaterLevelGauge saveWaterLevelGauge = new WaterLevelGauge();
        if (Objects.nonNull(waterLevelGauge))
            saveWaterLevelGauge = waterLevelGauge;

        saveWaterLevelGauge.setSensor(sensor);
        saveWaterLevelGauge.setReservoir(getReservoirByOrdinal(reservoirOrdinal));
        saveWaterLevelGauge.setType(getWaterLevelGaugeEnumFromNum(typeNum));
        saveWaterLevelGauge.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveWaterLevelGauge.setOrdinal(ordinal);
        saveWaterLevelGauge.setPressureDatasource(pressureDatasource);
        saveWaterLevelGauge.setLocationPressure(locationPressure);

        if (!tempDatasource.isEmpty() && !tempDatasource.isBlank())
            saveWaterLevelGauge.setTempDatasource(tempDatasource);

        return waterLevelGaugeRepository.save(saveWaterLevelGauge);
    }

    private void saveWaterLevelValuesInVolume(String pathToFile) {
        List<CSVRecord> records = readCSVFile(pathToFile);
        if (Objects.nonNull(records)) {
            List<WaterLevelInVolume> waterLevelInVolumes = new ArrayList<>();
            List<Reservoir> reservoirs = reservoirRepository.findAllByOrderByNameDesc();
            for (CSVRecord record : records) {
                Reservoir reservoir = null;
                Double waterLevelValue = Double.parseDouble(record.get("water_level_value"));
                Double volumeValue = Double.parseDouble(record.get("volume_value"));
                Integer reservoirNum = parseIntegerFromString(record.get("reservoir").trim());
                if (reservoirNum == 0)
                    reservoir = reservoirs.get(1);
                else if(reservoirNum == 1)
                    reservoir = reservoirs.get(2);
                else
                    reservoir = reservoirs.get(3);

                WaterLevelInVolume waterLevelInVolume = WaterLevelInVolume.builder()
                        .volumeVal(volumeValue)
                        .waterLevel(waterLevelValue)
                        .reservoir(reservoir)
                        .build();
                waterLevelInVolumes.add(waterLevelInVolume);
            }
            waterLevelInVolumeRepository.saveAll(waterLevelInVolumes);
        }
    }

    private void saveSpillways(String pathToFile, boolean doUpdate) {
        List<CSVRecord> records = readCSVFile(pathToFile);
        if (Objects.nonNull(records)) {
            for (CSVRecord record : records) {
                Sensor updateSensor = null;
                Spillway updateSpillway = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updateSpillway = spillwayRepository.findSpillwayBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updateSpillway.getSensor();
                }

                Sensor savedSensor = saveSensor(record, updateSensor);
                saveSpillway(record, updateSpillway, savedSensor);

            }
        }
    }

    private Spillway saveSpillway(CSVRecord record,
                                  Spillway spillway,
                                  Sensor sensor) {

        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        String pressureDatasource = record.get("pressureDatasource").trim();
        String turbidityDatasource = record.get("turbidityDatasource");
        String tempDatasource = record.get("tempDatasource").trim();
        String name = record.get("name").trim();

        Spillway saveSpillway = new Spillway();
        if (Objects.nonNull(spillway))
            saveSpillway = spillway;

        saveSpillway.setActive(true);
        saveSpillway.setPressureDatasource(pressureDatasource);
        saveSpillway.setTurbidityDatasource(turbidityDatasource);
        saveSpillway.setSensor(sensor);
        saveSpillway.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveSpillway.setName(name);
        saveSpillway.setOrdinal(ordinal);
        saveSpillway.setReservoir(getReservoirByOrdinal(reservoirOrdinal));

        if (!tempDatasource.isEmpty() && !tempDatasource.isBlank())
            saveSpillway.setTempDatasource(tempDatasource);

        return spillwayRepository.save(saveSpillway);
    }

    private void saveWaterFlowMeters(String pathToFile, boolean doUpdate) {
        List<CSVRecord> csvRecords = readCSVFile(pathToFile);
        if (Objects.nonNull(csvRecords)) {
            for (CSVRecord record : csvRecords) {
                WaterFlowMeter updateWaterFlowMeter = null;
                if (doUpdate) {
                    String pressureDatasource = record.get("pressureDatasource");
                    updateWaterFlowMeter = waterFlowMeterRepository.findWaterFlowMeterByPressureDatasource(pressureDatasource).orElse(null);
                }
                saveWaterFlowMeter(record, updateWaterFlowMeter);

            }
        }
    }

    private WaterFlowMeter saveWaterFlowMeter(CSVRecord record, WaterFlowMeter waterFlowMeter) {

        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        String pressureDatasource = record.get("pressureDatasource").trim();
        String tempDatasource = record.get("tempDatasource").trim();

        String lat = record.get("lat").isBlank() ? null : record.get("lat");
        String lon = record.get("long").isBlank() ? null : record.get("long");

        WaterFlowMeter saveWaterFlowMeter = new WaterFlowMeter();
        if (Objects.nonNull(waterFlowMeter))
            saveWaterFlowMeter = waterFlowMeter;

        saveWaterFlowMeter.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveWaterFlowMeter.setPressureDatasource(pressureDatasource);
        saveWaterFlowMeter.setTempDatasource(tempDatasource);
        saveWaterFlowMeter.setReservoir(getReservoirByOrdinal(reservoirOrdinal));
        saveWaterFlowMeter.setLat(lat);
        saveWaterFlowMeter.setLon(lon);

        return waterFlowMeterRepository.save(saveWaterFlowMeter);
    }


    private void saveHydrologicalStations(String pathToFile, boolean doUpdate) {
        List<CSVRecord> records = readCSVFile(pathToFile);
        if (Objects.nonNull(records)) {
            for (CSVRecord record : records) {
                Sensor updateSensor = null;
                HydrologicalStation updateHydrologicalStation = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updateHydrologicalStation = hydrologicalStationRepository.findHydrologicalStationBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updateHydrologicalStation.getSensor();
                }

                Sensor savedSensor = saveSensor(record, updateSensor);
                saveHydrologicalStation(record, updateHydrologicalStation, savedSensor);
            }
        }
    }

    private HydrologicalStation saveHydrologicalStation(CSVRecord record,
                                                        HydrologicalStation hydrologicalStation,
                                                        Sensor sensor) {

        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        String pressureDatasource = record.get("pressureDatasource").trim();
        String tempDatasource = record.get("tempDatasource").trim();
        String name = record.get("name").trim();
        Double locationPressure = Double.valueOf(record.get("locationPressure").trim());

               /* String lat = record.get("lat").isBlank() ? null : record.get("lat");
                String lon = record.get("long").isBlank() ? null : record.get("long");*/

        HydrologicalStation saveHydrologicalStation = new HydrologicalStation();
        if (Objects.nonNull(hydrologicalStation))
            saveHydrologicalStation = hydrologicalStation;

        saveHydrologicalStation.setReservoir(getReservoirByOrdinal(reservoirOrdinal));
        saveHydrologicalStation.setSensor(sensor);
        saveHydrologicalStation.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveHydrologicalStation.setPressureDatasource(pressureDatasource);
        saveHydrologicalStation.setTempDatasource(tempDatasource);
        saveHydrologicalStation.setName(name);
        saveHydrologicalStation.setOrdinal(ordinal);
        saveHydrologicalStation.setLocalPressure(locationPressure);
        saveHydrologicalStation.setActive(true);

        return hydrologicalStationRepository.save(saveHydrologicalStation);
    }

    private void saveDamBodyDevices(String pathToFile, boolean doUpdate) {
        List<CSVRecord> csvRecords = readCSVFile(pathToFile);
        if (Objects.nonNull(csvRecords)) {
            for (CSVRecord record : csvRecords) {
                Sensor updateSensor = null;
                DamBodyDevice updateDamBodyDevice = null;
                if (doUpdate) {
                    String name = record.get("name");
                    updateDamBodyDevice = damBodyDeviceRepository.findDamBodyDeviceByName(name).orElse(null);
                    updateSensor = updateDamBodyDevice.getSensor();
                }

                Sensor savedSensor = saveSensor(record, updateSensor);
                saveDamBodyDevice(record, updateDamBodyDevice, savedSensor);

            }

        }
    }

    private DamBodyDevice saveDamBodyDevice(CSVRecord record,
                                            DamBodyDevice damBodyDevice,
                                            Sensor sensor) {

        String gateName = record.get("gate");
        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        String name = record.get("name").trim();
        String propertyDatasource = record.get("readDatasource").trim();
        Integer typeNum = parseIntegerFromString(record.get("type"));
        Double locationPressure = Double.valueOf(record.get("locationPressure").trim());

        DamBodyDevice saveDamBodyDevice = new DamBodyDevice();
        if (Objects.nonNull(damBodyDevice))
            saveDamBodyDevice = damBodyDevice;

        saveDamBodyDevice.setSensor(sensor);
        saveDamBodyDevice.setGate(getGateFromName(gateName, reservoirOrdinal));
        saveDamBodyDevice.setName(name);
        saveDamBodyDevice.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveDamBodyDevice.setPropertyDatasource(propertyDatasource);
        saveDamBodyDevice.setType(getDamBodyTypeByNumber(typeNum));
        saveDamBodyDevice.setLocationPressure(locationPressure);
        saveDamBodyDevice.setActive(true);

        return damBodyDeviceRepository.save(saveDamBodyDevice);
    }

    private void saveSafetyCriteria(String pathToFile) {
        List<CSVRecord> csvRecords = readCSVFile(pathToFile);
        if (Objects.nonNull(csvRecords)) {
            for (CSVRecord record : csvRecords) {
                String name = record.get("name").trim();
                String methodName = record.get("method_name").trim();
                Integer ordinal = parseIntegerFromString(record.get("ordinal"));
                String indicationSourceDB = record.get("indication_source").trim();
                Integer ordinalReservoir = Integer.valueOf(record.get("reservoir"));
                Reservoir reservoir = getReservoirByOrdinal(ordinalReservoir);

                Integer statusNum = Integer.valueOf(record.get("status"));
                SafetyCriteriaStatusEnum status = getSafetyCriteriaStatusFromNum(statusNum);

                SafetyCriteria safetyCriteria = SafetyCriteria.builder()
                        .name(name)
                        .methodName(methodName)
                        .reservoir(reservoir)
                        .indicationSourceDB(indicationSourceDB)
                        .status(status)
                        .ordinal(ordinal)
                        .build();
                safetyCriteriaRepository.save(safetyCriteria);
            }
        }
    }

    private void saveCrackGauges(String pathToFile, boolean doUpdate) {
        List<CSVRecord> csvRecords = readCSVFile(pathToFile);
        if (Objects.nonNull(csvRecords)) {
            for (CSVRecord record : csvRecords) {
                Sensor updateSensor = null;
                CrackGauge updateCrackGauge = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    System.out.println(serialNumber);
                    updateCrackGauge = crackGaugeRepository.findCrackGaugeBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updateCrackGauge.getSensor();
                }
                Sensor savedSensor = saveSensor(record, updateSensor);
                saveCrackGauge(record, updateCrackGauge, savedSensor);

            }
        }
    }

    private CrackGauge saveCrackGauge(CSVRecord record,
                                      CrackGauge crackGauge,
                                      Sensor sensor) {

        String gateName = record.get("gate");
        String name = record.get("name").trim();
        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        String mDatasource = record.get("mDatasource").trim();
        String tempDatasource = record.get("tempDatasource").trim();
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        Double locationPressure = Double.valueOf(record.get("locationPressure").trim());
        String location = record.get("location").isBlank() ? null : record.get("location");
        Integer ordinalReservoir = Integer.valueOf(record.get("reservoir"));
        Reservoir reservoir = getReservoirByOrdinal(ordinalReservoir);

        CrackGauge saveCrackGauge = new CrackGauge();
        if (Objects.nonNull(crackGauge))
            saveCrackGauge = crackGauge;

        saveCrackGauge.setName(name);
        saveCrackGauge.setOrdinal(ordinal);
        saveCrackGauge.setSensor(sensor);
        saveCrackGauge.setGate(getGateFromName(gateName, ordinalReservoir));
        saveCrackGauge.setLocationPressure(locationPressure);
        saveCrackGauge.setLocation(location);
        saveCrackGauge.setMDatasource(mDatasource);
        saveCrackGauge.setTempDatasource(tempDatasource);
        saveCrackGauge.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));

        return crackGaugeRepository.save(saveCrackGauge);
    }

    private void savePlumbs(String pathToFile, boolean doUpdate) {
        List<CSVRecord> csvRecords = readCSVFile(pathToFile);
        if (Objects.nonNull(csvRecords)) {
            for (CSVRecord record : csvRecords) {
                Sensor updateSensor = null;
                Plumb updatePlumb = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updatePlumb = plumbRepository.findByPlumbBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updatePlumb.getSensor();
                }
                Sensor savedSensor = saveSensor(record, updateSensor);
                savePlumb(record, updatePlumb, savedSensor);
            }
        }
    }

    private Plumb savePlumb(CSVRecord record,
                            Plumb plumb,
                            Sensor sensor) {

        String gateName = record.get("gate");
        String name = record.get("name").trim();
        String xDatasource = record.get("xDatasource").trim();
        String yDatasource = record.get("yDatasource").trim();
        String tempDatasource = record.get("tempDatasource").isBlank() ? null : record.get("tempDatasource");
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        Double locationPressure = Double.valueOf(record.get("locationPressure").trim());
        PlumbTypeEnum plumbTypeEnum = mapPlumbTypeEnumFromNumber(Integer.valueOf(record.get("type")));
        Integer ordinalReservoir = Integer.valueOf(record.get("reservoir"));

        Plumb savePlumb = new Plumb();
        if (Objects.nonNull(plumb))
            savePlumb = plumb;

        savePlumb.setName(name);
        savePlumb.setSensor(sensor);
        savePlumb.setGate(getGateFromName(gateName, ordinalReservoir));
        savePlumb.setXDatasource(xDatasource);
        savePlumb.setYDatasource(yDatasource);
        savePlumb.setTempDatasource(tempDatasource);
        savePlumb.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        savePlumb.setLocationPressure(locationPressure);
        savePlumb.setType(plumbTypeEnum);

        return plumbRepository.save(savePlumb);
    }


    private void saveTiltmeters(String pathToFile, boolean doUpdate) {
        List<CSVRecord> csvRecords = readCSVFile(pathToFile);
        if (Objects.nonNull(csvRecords)) {
            for (CSVRecord record : csvRecords) {
                Sensor updateSensor = null;
                Tiltmeter updateTiltmeter = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updateTiltmeter = tiltmeterRepository.findTiltmeterBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updateTiltmeter.getSensor();
                }
                Sensor savedSensor = saveSensor(record, updateSensor);
                saveTiltmeter(record, updateTiltmeter, savedSensor);

            }
        }

    }

    private Tiltmeter saveTiltmeter(CSVRecord record,
                                    Tiltmeter tiltmeter,
                                    Sensor sensor) {

        String gateName = record.get("gate");
        String name = record.get("name").trim();
        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        String sinADatasource = record.get("sinADatasource").trim();
        String sinBDatasource = record.get("sinBDatasource").trim();
        String tempDatasource = record.get("tempDatasource");
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));

        Integer ordinalReservoir = Integer.valueOf(record.get("reservoir"));

        assert deviceDatasourceNum != null;

        Tiltmeter saveTiltmeter = new Tiltmeter();
        if (Objects.nonNull(tiltmeter))
            saveTiltmeter = tiltmeter;

        saveTiltmeter.setName(name);
        saveTiltmeter.setSensor(sensor);
        saveTiltmeter.setSinADatasource(sinADatasource);
        saveTiltmeter.setSinBDatasource(sinBDatasource);
        saveTiltmeter.setTempDatasource(tempDatasource);
        saveTiltmeter.setOrdinal(ordinal);
        saveTiltmeter.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveTiltmeter.setGate(getGateFromName(gateName, ordinalReservoir));

        return tiltmeterRepository.save(saveTiltmeter);
    }

    private void saveDeformometers(String pathToFile, boolean doUpdate) {
        List<CSVRecord> records = readCSVFile(pathToFile);
        if (Objects.nonNull(records)) {
            for (CSVRecord record : records) {
                Sensor updateSensor = null;
                Deformometer updateDeformometer = null;
                if (doUpdate) {
                    String serialNumber = record.get("serialNumber");
                    updateDeformometer = deformometerRepository.findDeformometerBySensorSerialNumber(serialNumber).orElse(null);
                    updateSensor = updateDeformometer.getSensor();
                }

                Sensor savedSensor = saveSensor(record, updateSensor);
                saveDeformometer(record, updateDeformometer, savedSensor);


            }
        }
    }

    private Deformometer saveDeformometer(CSVRecord record,
                                          Deformometer deformometer,
                                          Sensor sensor) {

        Integer ordinal = parseIntegerFromString(record.get("ordinal"));
        Integer reservoirOrdinal = parseIntegerFromString(record.get("reservoir").trim());
        Integer deviceDatasourceNum = parseIntegerFromString(record.get("deviceDatasource"));
        String pressureDatasource = record.get("pressureDatasource").trim();
        String name = record.get("name").trim();
        String tempDatasource = record.get("tempDatasource").trim();

        Deformometer saveDeformometer = new Deformometer();
        if (Objects.nonNull(deformometer))
            saveDeformometer = deformometer;

        saveDeformometer.setSensor(sensor);
        saveDeformometer.setReservoir(getReservoirByOrdinal(reservoirOrdinal));
        saveDeformometer.setDeviceDatasource(mapDeviceDatasourceEnumFromNumber(deviceDatasourceNum));
        saveDeformometer.setOrdinal(ordinal);
        saveDeformometer.setName(name);
        saveDeformometer.setPressureDatasource(pressureDatasource);

        if (!tempDatasource.isEmpty() && !tempDatasource.isBlank())
            saveDeformometer.setTempDatasource(tempDatasource);

        return deformometerRepository.save(saveDeformometer);
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

    private Sensor saveSensor(CSVRecord record, Sensor sensor) {


        String serialNumber = record.get("serialNumber");
        String datalogger = record.get("datalogger");

        String serialNumberStr = Objects.nonNull(serialNumber) ? serialNumber.trim() : null;
        String dataloggerStr = Objects.nonNull(datalogger) ? datalogger.trim() : null;

        Integer multiplexer = parseIntegerFromString(record.get("multiplexer"));

        String a = record.get("ACoefficient");
        String b = record.get("BCoefficient");
        String c = record.get("CCoefficient");
        String d = record.get("DCoefficient");

        String correctionStr = null;

        try {
            correctionStr = record.get("correction");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Double aCoefficient = (!Objects.isNull(a) && !a.isBlank()) ? Double.valueOf(record.get("ACoefficient").trim()) : null;
        Double bCoefficient = (!Objects.isNull(b) && !b.isBlank()) ? Double.valueOf(record.get("BCoefficient").trim()) : null;
        Double cCoefficient = (!Objects.isNull(c) && !c.isBlank()) ? Double.valueOf(record.get("CCoefficient").trim()) : null;
        Double dCoefficient = (!Objects.isNull(d) && !d.isBlank()) ? Double.valueOf(record.get("DCoefficient").trim()) : null;
        Double correction = (!Objects.isNull(correctionStr) && !correctionStr.isBlank()) ? Double.valueOf(record.get("correction").trim()) : null;

        Sensor savedSensor = new Sensor();
        if (Objects.nonNull(sensor))
            savedSensor = sensor;


        savedSensor.setSerialNumber(serialNumberStr);
        savedSensor.setMultiplexer(multiplexer);
        savedSensor.setDatalogger(dataloggerStr);
        savedSensor.setACoefficient(aCoefficient);
        savedSensor.setBCoefficient(bCoefficient);
        savedSensor.setCCoefficient(cCoefficient);
        savedSensor.setDCoefficient(dCoefficient);
        savedSensor.setCorrection(correction);
        savedSensor.setActive(true);

        return sensorRepository.save(savedSensor);
    }


    private Gate getGateFromName(String name, Integer reservoirOrdinal) {
        String gateName = name.trim();
        Reservoir reservoir = getReservoirByOrdinal(reservoirOrdinal);
        return gateRepository.findByNameAndReservoirId(gateName, reservoir.getId()).orElseThrow();
    }

    private DeviceDatasourceEnum mapDeviceDatasourceEnumFromNumber(Integer number) {
        switch (number) {
            case 0 -> {
                return DeviceDatasourceEnum.DISPECHR;
            }
            case 1 -> {
                return DeviceDatasourceEnum.KRZ_TUNEL;
            }
            case 2 -> {
                return DeviceDatasourceEnum.SEMPATERN_TUNELL;
            }
            case 3 -> {
                return DeviceDatasourceEnum.VW;
            }
            case 4 -> {
                return DeviceDatasourceEnum.ANL1;
            }
            case 5 -> {
                return DeviceDatasourceEnum.ANL4;
            }
            case 6 -> {
                return DeviceDatasourceEnum.AKHANGARAN;
            }
            case 7 -> {
                return DeviceDatasourceEnum.OMNIALOG_795;
            }
            case 8 -> {
                return DeviceDatasourceEnum.OMNIALOG_817_ONG;
            }
            case 9 -> {
                return DeviceDatasourceEnum.OMNIALOG_817_CHAP;
            }
            case 10 -> {
                return DeviceDatasourceEnum.OMNIALOG_804_12_SEKSIYA;
            }
            case 11 -> {
                return DeviceDatasourceEnum.OMNIALOG_804_24_SEKSIYA;
            }
            case 12 -> {
                return DeviceDatasourceEnum.GATEWAY_BOSIMSIZ;
            }
            case 13 -> {
                return DeviceDatasourceEnum.OMNIALOG_885_SHELEMER;
            }
            case 14 -> {
                return DeviceDatasourceEnum.OMNIALOG_845_ATVES;
            }
            case 15 -> {
                return DeviceDatasourceEnum.T_1000;
            }
            case 16 -> {
                return DeviceDatasourceEnum.TILTMETERS;
            }
            case 17 -> {
                return DeviceDatasourceEnum.OMNIALOG_804_28_SEKSIYA;
            }
            case 18 -> {
                return DeviceDatasourceEnum.CHARVAK_GATEWAY_BOSIMSIZ;
            }
            case 19 -> {
                return DeviceDatasourceEnum.TASMALI_DRENAJ;
            }
            case 20 -> {
                return DeviceDatasourceEnum.TRANSPORTNI_SH;
            }
            case 21 -> {
                return DeviceDatasourceEnum.SHTOLNIYA_1;
            }
            default -> {
                return DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV;
            }
        }
    }

    private DamBodyDeviceTypeEnum getDamBodyTypeByNumber(Integer typeNum) {
        switch (typeNum) {
            case 0 -> {
                return DamBodyDeviceTypeEnum.PTS;
            }
            case 1 -> {
                return DamBodyDeviceTypeEnum.PDS;
            }
            case 2 -> {
                return DamBodyDeviceTypeEnum.PNGS;
            }
            default -> {
                return DamBodyDeviceTypeEnum.PLPS;
            }
        }
    }

    private Reservoir getReservoirByOrdinal(Integer reservoirOrdinal) {
        Reservoir reservoir;
        if (reservoirOrdinal == 0)
            reservoir = reservoirRepository.findReservoirByName("Ҳисорак сув омбори").orElseThrow();
        else if (reservoirOrdinal == 1)
            reservoir = reservoirRepository.findReservoirByName("Оҳангарон сув омбори").orElseThrow();
        else if (reservoirOrdinal == 2)
            reservoir = reservoirRepository.findReservoirByName("Андижон сув омбори").orElseThrow();
        else
            reservoir = reservoirRepository.findReservoirByName("Чорвок сув омбори").orElseThrow();
        return reservoir;
    }

    private WaterLevelGaugeEnum getWaterLevelGaugeEnumFromNum(Integer typeNum) {
        if (typeNum == 1)
            return WaterLevelGaugeEnum.UPPER_BIEF;
        return WaterLevelGaugeEnum.LOWER_BIEF;
    }

    private List<String> getCoordsFromCSVRecord(CSVRecord record) {
        String lat = record.get("lat").isBlank() ? null : record.get("lat");
        String lon = record.get("long").isBlank() ? null : record.get("long");
        return List.of(lat, lon);
    }


    private PiezometerTypeEnum mapPiezometerTypeEnumFromNumber(Integer number) {
        if (number == 0)
            return PiezometerTypeEnum.WITHOUT_PRESSURE;
        return PiezometerTypeEnum.WITH_PRESSURE;
    }

    private PlumbTypeEnum mapPlumbTypeEnumFromNumber(Integer typeNum) {
        if (typeNum == 1)
            return PlumbTypeEnum.STRAIGHT;
        return PlumbTypeEnum.REVERSE;
    }

    private Integer parseIntegerFromString(String str) {
        return (!str.isBlank() && !str.isEmpty()) ? Integer.valueOf(str.trim()) : null;
    }


}
