package com.example.appgidritexmonitoring.util;

import com.example.appgidritexmonitoring.entity.*;
import com.example.appgidritexmonitoring.entity.enums.DamBodyDeviceTypeEnum;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.payload.CalculatedRes;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DeviceResultConverter {
    private static final double mPaToMeterMultiplier = 101.978381;
    private static final double kPaToMeterMultiplier = 0.10197162;

    public static List<Double> calculatePressureResult(Piezometer piezometer, double readPressure) {
        double virtualPressureBySensor = calculateVirtualPressureBySensor(piezometer, readPressure);


        List<String> serialNumberOfAndijan = List.of("P232627", "P233612", "P232624", "P232611", "P232619", "P232610", "P232621", "P232620", "P232604",
                "P232613", "P232618", "P232615", "P232606", "P232614", "P232608", "P232622",
                "P232305", "P232626", "P232609", "P232617", "P232607", "P232623", "P232625", "P232616");

        List<String> serialNumberOfCharvak = List.of(
                "P242768", "P242769", "P242770", "P242771", "P242772", "P242773", "P242774", "P242775", "P242776", "P242777",
                "P242778", "P242779", "P242780", "P242781", "P242782", "P242630", "P242631", "P242632", "P242633", "P242634",
                "P242635", "P242636", "P242637", "P242638", "P242639", "P242640", "P242641", "P242642", "P242643", "P242644",
                "P242645", "P242646", "P242647", "P242648", "P242649", "P242650", "P242651", "P242652", "P242653", "P242654",
                "P242655", "P242656", "P242657", "P242658", "P242659", "P242660", "P242661", "P242662", "P242663", "P242664",
                "P242665", "P242666", "P242667", "P242668", "P242669", "P242670", "P242671", "P242672", "P242673", "P242674",
                "P242675", "P242676", "P242677", "P242678", "P242679", "P242680"
        );


        double paToMeterMultiplier;
        String serialNumberOfPiezometer = piezometer.getSensor().getSerialNumber();
        if (serialNumberOfAndijan.contains(serialNumberOfPiezometer) ||
                serialNumberOfCharvak.contains(serialNumberOfPiezometer))
            paToMeterMultiplier = mPaToMeterMultiplier;
        else
            paToMeterMultiplier = kPaToMeterMultiplier;

        //COMPUTED PRESSURE AND VIRTUAL PRESSURE
        return List.of((virtualPressureBySensor * paToMeterMultiplier) + piezometer.getLocationPressure(), virtualPressureBySensor);
    }

    public static CalculatedRes calculatePressureResult(WaterLevelGauge waterLevelGauge, double readPressure) {
        if (waterLevelGauge.getDeviceDatasource().equals(DeviceDatasourceEnum.T_1000)) {
            Sensor sensor = waterLevelGauge.getSensor();
            double aCoefficient = sensor.getACoefficient();
            double bCoefficient = sensor.getBCoefficient();
            double cCoefficient = sensor.getCCoefficient();
            Double dCoefficient = sensor.getDCoefficient();
            double virtualPressure = ((readPressure - aCoefficient) / (bCoefficient - aCoefficient)) * (dCoefficient - cCoefficient) + cCoefficient;
            double result = Math.abs(waterLevelGauge.getLocationPressure() - 9.3855) + virtualPressure;
            return CalculatedRes.make(virtualPressure, result);
        }
        double virtualPressureBySensor = calculateVirtualPressureBySensor(waterLevelGauge, readPressure);

        double paToMeterMultiplier;

        if (List.of(DeviceDatasourceEnum.AKHANGARAN, DeviceDatasourceEnum.CHARVAK_GATEWAY_VODOSLIV).contains(waterLevelGauge.getDeviceDatasource()) ||
                waterLevelGauge.getSensor().getSerialNumber().equals("P232093"))
            paToMeterMultiplier = kPaToMeterMultiplier;
        else
            paToMeterMultiplier = mPaToMeterMultiplier;

        double calculatedPressure = (virtualPressureBySensor * paToMeterMultiplier) + waterLevelGauge.getLocationPressure();

        return CalculatedRes.make(virtualPressureBySensor, calculatedPressure);

    }

    public static double calculateWaterFlow(Spillway spillway, double readPressure) {
        double virtualPressureBySensor = calculateVirtualPressureBySensor(spillway, readPressure);
        Sensor sensor = spillway.getSensor();
        double pressureInCm = (virtualPressureBySensor * 100) + getCorrection(sensor);
        double result;
        if (List.of("P233121", "P233104", "P233103").contains(sensor.getSerialNumber()))
            result = (Math.pow(pressureInCm, 2.47) * 2.3) / 100;
        else
            result = (1.4 * Math.pow(pressureInCm, 2.5)) / 100;
        return Double.isNaN(result) ? 0 : result;
    }

    public static double calculateTurbidityValue(String reservoirName, String readValue) {

        double readValueNum;
        try {
            readValueNum = Double.parseDouble(readValue);
        } catch (Exception ignored) {
            return 0;
        }

        if (reservoirName.startsWith("Андижон") || reservoirName.startsWith("Чорвок")) {

            double computedValue = (readValueNum - 4) / 16 * 1000;
            return computedValue < 0 ? 0 : computedValue;
        }

        return readValueNum;
    }

    private static double getCorrection(Sensor sensor) {
        return sensor.getCorrection() == null ? 0 : sensor.getCorrection();
    }

    public static CalculatedRes calculateWaterFlow(HydrologicalStation hydrologicalStation, double readPressure) {
        //double virtualPressureBySensor = calculateVirtualPressureBySensor(hydrologicalStation, readPressure);
        //double pressureInCm = convertPressureMPatoCm(virtualPressureBySensor);
        CalculatedRes calculatedRes = new CalculatedRes();

        double virtualPressureBySensor = calculateVirtualPressureBySensor(hydrologicalStation, readPressure);
        if (hydrologicalStation.getReservoir().getName().startsWith("Ҳисорак")) {
            calculatedRes.setVirtualRes(virtualPressureBySensor);
            if (hydrologicalStation.getSensor().getSerialNumber().equals("P232089"))
                calculatedRes.setFinalRes(309.9 * (virtualPressureBySensor * kPaToMeterMultiplier));
            else
                calculatedRes.setFinalRes((virtualPressureBySensor * kPaToMeterMultiplier) + 1_000); //2,3018*E18*E18+5,3633*E18+0,0403
        } else {
            if (hydrologicalStation.getSensor().getSerialNumber().equals("P231246"))
                calculatedRes.setFinalRes(12.346 * readPressure);
            else {
                calculatedRes.setVirtualRes(virtualPressureBySensor);
                calculatedRes.setFinalRes((virtualPressureBySensor * mPaToMeterMultiplier) + 1_007);
            }
        }


        return calculatedRes;
    }

    public static double calculatePropertyResult(DamBodyDevice damBodyDevice, double readVal) {
        Sensor sensor = damBodyDevice.getSensor();
        DamBodyDeviceTypeEnum type = damBodyDevice.getType();
        double result;
        switch (type) {
            case PDS ->
                    result = calculateVirtualPropertyBySensor(sensor, readVal / 1000) * 100 + computeCorrection(damBodyDevice, readVal) + damBodyDevice.getLocationPressure();
            case PTS, PLPS -> result = calculateVirtualPropertyBySensor(sensor, readVal / 1000);
            case PNGS -> {
                String damBodyDeviceName = damBodyDevice.getName();
                double readValueCoff = List.of("G2-114", "G5-811", "G3-775", "G1-110", "G1-559", "G2-584").contains(damBodyDeviceName) ? readVal / 1000 : readVal;
                int multiplier = damBodyDeviceName.equals("G1-108") ? 1 : 100;
                result = calculateVirtualPropertyBySensor(sensor, readValueCoff) * multiplier + computeCorrection(damBodyDevice, readVal);
            }
            default -> result = (-readVal) / sensor.getACoefficient() + sensor.getBCoefficient();
        }
        return result;
    }

    public static double calculateSlantByPlumb(Plumb plumb, double readValue) {
        Sensor sensor = plumb.getSensor();
        Double aCoefficient = sensor.getACoefficient();
        Double bCoefficient = sensor.getBCoefficient();
        Double cCoefficient = sensor.getCCoefficient();
        Double dCoefficient = sensor.getDCoefficient();

        return ((readValue - aCoefficient) / (bCoefficient - aCoefficient) * (dCoefficient - cCoefficient)) + cCoefficient;
    }

    public static double calculateTiltValueByTiltmeter(Tiltmeter tiltmeter, double sinBValue) {
        Sensor sensor = tiltmeter.getSensor();
        Double aCoefficient = sensor.getACoefficient();
        Double bCoefficient = sensor.getBCoefficient();
        return (bCoefficient - sinBValue) * aCoefficient;
    }

    private static double computeCorrection(DamBodyDevice damBodyDevice, double readVal) {
        String name = damBodyDevice.getName();
        Double ordinalOfGate = damBodyDevice.getGate().getOrdinal();
        double result;
        if (name.equals("P1-1752") && ordinalOfGate == 5)
            result = (readVal >= 526.2) ? 0.6 : (readVal >= 509.3) ? 0.8 : 0;
        else if (name.equals("P2-857") && ordinalOfGate == 5)
            result = (readVal >= 635.9) ? -0.6 : (readVal >= 554.4) ? -1 : (readVal >= 496.2) ? -1.4 : 0;
        else if (name.equals("P1-936") && ordinalOfGate == 5)
            result = (readVal >= 613) ? 0.4 : (readVal >= 531) ? 0.6 : (readVal >= 490.4) ? 0.8 : 0;
        else if (name.equals("P2-1751") && ordinalOfGate == 5)
            result = (readVal >= 519.9) ? 0.9 : (readVal >= 503.7) ? 1 : (readVal >= 488.5) ? 1.2 : 0;
        else if (name.equals("P1-1746") && ordinalOfGate == 5)
            result = (readVal >= 514.3) ? 1.9 : (readVal >= 511.1) ? 1.95 : (readVal >= 495.7) ? 2 : 0;
        else if (name.equals("P2-863") && ordinalOfGate == 5)
            result = (readVal >= 632.7) ? 0.2 : (readVal >= 554.1) ? 0.4 : (readVal >= 497.9) ? 0.6 : 0;
        else if (name.equals("P1-1653") && ordinalOfGate == 6)
            result = (readVal >= 516.3) ? 1.6 : (readVal >= 498.6) ? 1.7 : (readVal >= 482.4) ? 1.8 : 0;
        else if (name.equals("P2-861") && ordinalOfGate == 6)
            result = (readVal >= 548.9) ? -0.1 : (readVal >= 484.8) ? -0.2 : 0;
        else if (name.equals("P3-850") && ordinalOfGate == 6)
            result = (readVal >= 712.6) ? 0.4 : (readVal >= 604.1) ? 0.6 : (readVal >= 531.6) ? 0.8 : 0;
        else if (name.equals("P2-1733") && ordinalOfGate == 2)
            result = (readVal >= 502.9) ? 2 : 1.4;
        else if (name.equals("P1-1043") && ordinalOfGate == 13)
            result = (readVal >= 536.9) ? 0 : (readVal >= 509.3) ? 0.1 : (readVal >= 485.2) ? 0.2 : 1.1;
        else if (name.equals("P2-1614") && ordinalOfGate == 7)
            result = (readVal >= 549.2) ? 4.8 : (readVal >= 524.3) ? 5.2 : (readVal >= 502.4) ? 5.6 : 0;
        else if (name.equals("P3-1606") && ordinalOfGate == 7)
            result = (readVal >= 512.7) ? 0.8 : (readVal >= 491) ? 0.9 : (readVal >= 471.7) ? 1 : 0;
        else if (name.equals("P1-1658") && ordinalOfGate == 13)
            result = (readVal >= 547.2) ? 1.6 : (readVal >= 522.3) ? 1.8 : (readVal >= 500.3) ? 2 : 0;
        else if (name.equals("P2-1172") && ordinalOfGate == 10)
            result = (readVal >= 576) ? 2.5 : (readVal >= 545.7) ? 3.1 : (readVal >= 541.7) ? 3.2 : (readVal >= 534) ? 3.4 : (readVal >= 526.6) ? 3.6 : (readVal >= 519.5) ? 3.8 : 0;
        else if (name.equals("P2-1109") && ordinalOfGate == 8)
            result = (readVal >= 538.9) ? 3.2 : (readVal >= 515.1) ? 3.5 : (readVal >= 494.2) ? 4 : 0;
        else if (name.equals("P2-1159") && ordinalOfGate == 9)
            result = (readVal >= 522.4) ? 2.3 : (readVal >= 495.7) ? 2.5 : (readVal >= 472.4) ? 2.8 : 0;
        else if (name.equals("P1-1171") && ordinalOfGate == 11)
            result = (readVal >= 564) ? 1.6 : (readVal >= 536.2) ? 2 : (readVal >= 528.9) ? 2 : (readVal >= 515.2) ? 2.2 : (readVal >= 496.4) ? 2.4 : 0;
        else if (name.equals("P2-1156") && ordinalOfGate == 27)
            result = (readVal >= 537.8) ? 3.4 : (readVal >= 505.5) ? 3.9 : (readVal >= 478.6) ? 4.4 : 0;
        else if (name.equals("P1-1173") && ordinalOfGate == 19)
            result = (readVal >= 582.6) ? 1.7 : (readVal >= 532.2) ? 3.2 : (readVal >= 518) ? 3.4 : (readVal >= 504.8) ? 3.6 : (readVal >= 492.4) ? 3.8 : 0;
        else if (name.equals("P2-1177") && ordinalOfGate == 19)
            result = (readVal >= 582.7) ? 0 : (readVal >= 541.3) ? -0.05 : (readVal >= 526.9) ? -0.2 : 0;
        else if (name.equals("P3-1200") && ordinalOfGate == 19)
            result = (readVal >= 575.6) ? 1.4 : (readVal >= 536.1) ? 1.6 : (readVal >= 503.4) ? 2.2 : 0;
        else if (name.equals("P1-1603") && ordinalOfGate == 29)
            result = (readVal >= 551.8) ? 2.2 : (readVal >= 524.6) ? 2.5 : (readVal >= 500.8) ? 2.8 : 0;
        else if (name.equals("P2-1604") && ordinalOfGate == 29)
            result = (readVal >= 525.3) ? 4.4 : (readVal >= 501.5) ? 4.7 : (readVal >= 480.6) ? 5 : 0;
        else if (name.equals("P1-1607") && ordinalOfGate == 19)
            result = (readVal >= 548.9) ? 2.4 : (readVal >= 522.7) ? 2.7 : (readVal >= 499.7) ? 3 : 0;
        else if (name.equals("P2-1659") && ordinalOfGate == 19)
            result = (readVal >= 546.1) ? 2 : (readVal >= 521.5) ? 2.3 : (readVal >= 499.7) ? 2.6 : 0;
        else if (name.equals("P1-1654") && ordinalOfGate == 25)
            result = (readVal >= 537) ? 4.6 : (readVal >= 514.3) ? 5.2 : (readVal >= 505.3) ? 5.4 : (readVal >= 499.5) ? 5.6 : (readVal >= 493.9) ? 5.8 : 0;
        else if (name.equals("P3-878") && ordinalOfGate == 25)
            result = (readVal >= 702.6) ? 0.7 : (readVal >= 572.4) ? 1.2 : (readVal >= 533.9) ? 1.4 : (readVal >= 511.8) ? 1.6 : (readVal >= 492) ? 1.8 : 0;
        else if (name.equals("P2-1661") && ordinalOfGate == 28)
            result = (readVal >= 523.1) ? 3.2 : (readVal >= 500.3) ? 3.6 : (readVal >= 497.3) ? 3.7 : (readVal >= 491.3) ? 3.8 : (readVal >= 491.2) ? 3.9 : (readVal >= 480) ? 4 : 0;
        else if (name.equals("P1-1750") && ordinalOfGate == 33)
            result = (readVal >= 523.9) ? 1.4 : (readVal >= 517.2) ? 1.6 : (readVal >= 500.7) ? 1.8 : 0;
        else if (name.equals("G1-110") && ordinalOfGate == 33)
            result = (readVal >= 684.2) ? 1.3 : (readVal >= 598.1) ? 1.9 : (readVal >= 535.4) ? 2.5 : 0;
        else if (name.equals("P1-1737") && ordinalOfGate == 34)
            result = (readVal >= 500.1) ? 1 : 0;
        else if (name.equals("P2-1744") && ordinalOfGate == 34)
            result = (readVal >= 498.9) ? -1 : 0;
        else if (name.equals("P2-888") && ordinalOfGate == 30)
            result = (readVal >= 612.9) ? 0.4 : (readVal >= 534.4) ? 0.6 : (readVal >= 478.5) ? 0.8 : 0;
        else if (name.equals("P1-1748") && ordinalOfGate == 31)
            result = (readVal >= 550.3) ? 2 : (readVal >= 531.2) ? 2.2 : (readVal >= 513.8) ? 2.4 : 0;
        else if (name.equals("P2-871") && ordinalOfGate == 31)
            result = (readVal >= 617.1) ? 1.6 : (readVal >= 542.3) ? 2.3 : (readVal >= 488.6) ? 3 : 0;
        else if (name.equals("P1-1747") && ordinalOfGate == 31)
            result = (readVal >= 526.1) ? 1.6 : (readVal >= 508.1) ? 1.8 : (readVal >= 491.8) ? 2 : 0;
        else if (name.equals("P2-1770") && ordinalOfGate == 31)
            result = (readVal >= 527.9) ? 2.6 : (readVal >= 509.7) ? 2.9 : (readVal >= 493.1) ? 3.2 : 0;
        else if (name.equals("P1-1729") && ordinalOfGate == 31)
            result = (readVal >= 517) ? 0.8 : (readVal >= 501.2) ? 0.9 : (readVal >= 486.8) ? 1 : 0;
        else if (name.equals("P2-1767") && ordinalOfGate == 31)
            result = (readVal >= 488.6) ? 4.6 : (readVal >= 474.2) ? 5.1 : (readVal >= 461) ? 5.6 : 0;
        else if (name.equals("P1-1052") && ordinalOfGate == 19)
            result = (readVal >= 576.6) ? 0.3 : (readVal >= 541.4) ? 0.5 : (readVal >= 511.5) ? 0.6 : 9.3;
        else if (name.equals("G2-114") && ordinalOfGate == 19)
            result = (readVal >= 519.9) ? 0 : 3.2;
        else if (name.equals("P2-1063") && ordinalOfGate == 25)
            result = (readVal >= 579.6) ? 1 : (readVal >= 544.5) ? 1.2 : (readVal >= 514.3) ? 1.4 : 0;
        else if (name.equals("P1-971") && ordinalOfGate == 25)
            result = (readVal >= 565.6) ? 2 : (readVal >= 554) ? 2.2 : (readVal >= 532.5) ? 2.5 : (readVal >= 503.6) ? 3 : 0;
        else if (name.equals("P1-1740") && ordinalOfGate == 32)
            result = (readVal >= 495) ? -0.4 : 0;
        else if (name.equals("G2-194") && ordinalOfGate == 13)
            result = (readVal >= 536.4) ? 4.3 : (readVal >= 528) ? 4.4 : (readVal >= 523.7) ? 4.5 : (readVal >= 519.4) ? 4.6 : (readVal >= 515.3) ? 4.7 : (readVal >= 511.2) ? 4.8 : (readVal >= 507.2) ? 4.9 : (readVal >= 503.3) ? 5 : 0;
        else if (name.equals("P2-786") && ordinalOfGate == 15)
            result = (readVal >= 577.3) ? 1 : (readVal >= 535.9) ? 1.2 : (readVal >= 501.2) ? 1.4 : 0;
        else if (name.equals("P3-799") && ordinalOfGate == 15)
            result = (readVal >= 580.6) ? 1.3 : (readVal >= 537.4) ? 1.45 : (readVal >= 502.4) ? 1.6 : 0;
        else if (name.equals("P1-1197") && ordinalOfGate == 13)
            result = (readVal >= 592.2) ? -2.4 : (readVal >= 545.5) ? -3.2 : (readVal >= 527.4) ? -3.4 : (readVal >= 519.3) ? -3.6 : (readVal >= 507.8) ? -3.8 : 0;
        else if (name.equals("P2-800") && ordinalOfGate == 17)
            result = (readVal >= 582.9) ? 1 : (readVal >= 538.6) ? 1.3 : (readVal >= 502.7) ? 1.6 : 0;
        else if (name.equals("P3-801") && ordinalOfGate == 17)
            result = (readVal >= 578.1) ? 0.7 : (readVal >= 534.6) ? 1 : (readVal >= 499.2) ? 1.2 : 0;
        else if (name.equals("P2-787") && ordinalOfGate == 19)
            result = (readVal >= 558.9) ? 2.2 : (readVal >= 517.6) ? 2.7 : (readVal >= 486.3) ? 3.2 : 0;
        else if (name.equals("P3-826") && ordinalOfGate == 19)
            result = (readVal >= 578.2) ? 0.4 : (readVal >= 531.5) ? 0.5 : (readVal >= 496.7) ? 0.6 : 0;
        else if (name.equals("P4-781") && ordinalOfGate == 19)
            result = (readVal >= 570.4) ? 2.1 : (readVal >= 526.5) ? 2.8 : (readVal >= 493.6) ? 3.4 : 0;
        else if (name.equals("P3-850") && ordinalOfGate == 23)
            result = (readVal >= 559.9) ? -1 : (readVal >= 514.6) ? -1.2 : (readVal >= 488) ? -1.4 : 0;
        else if (name.equals("P1-783") && ordinalOfGate == 23)
            result = (readVal >= 551.7) ? 1.8 : (readVal >= 513.8) ? 2 : (readVal >= 482.4) ? 2.2 : 0;
        else if (name.equals("P1-1189") && ordinalOfGate == 25)
            result = (readVal >= 593.8) ? 2.6 : (readVal >= 547.1) ? 3.3 : (readVal >= 509.7) ? 4 : 0;
        else if (name.equals("P-809") && ordinalOfGate == 21)
            result = (readVal >= 559.2) ? 2.4 : (readVal >= 519.2) ? 3 : (readVal >= 486.6) ? 3.6 : 0;
        else if (name.equals("P3-1190") && ordinalOfGate == 25)
            result = (readVal >= 588.1) ? 1.5 : (readVal >= 538.6) ? 2 : (readVal >= 499.4) ? 2.4 : 0;
        else if (name.equals("P2-851") && ordinalOfGate == 23)
            result = (readVal >= 517.3) ? 3 : (readVal >= 472.1) ? 3.2 : (readVal >= 460.7) ? 3.4 : 0;
        else
            result = 0;
        return result;
    }

    public static double calculatePropertyResult(Deformometer deformometer, double readPressure) {
        return calculateVirtualPressureBySensor(deformometer, readPressure);
    }

    public static double calculateWaterFlow(double readPressure) {
        double result = readPressure * 300 - 100;
        return result < 1 ? 0 : result;
    }

    public static double convertOhmsToDegrees(double ohmValue) {
        return (ohmValue - 100) / 0.385;
    }

    public static double convertOhmsToDegreesOfPiezometer(Piezometer piezometer, double ohmValue) {
        DeviceDatasourceEnum deviceDatasource = piezometer.getDeviceDatasource();

        if (deviceDatasource.equals(DeviceDatasourceEnum.AKHANGARAN) &&
                Arrays.asList(15, 14).contains(piezometer.getOrdinal())) {
            ohmValue = ohmValue / 1000;
        }
        return (-0.0042) * ohmValue + 36.698;

    }

    private static double convertPressureMPatoCm(double pressureInMpa) {
        return pressureInMpa * 100000 / 9.806;
    }

    private static double convertPressureCmToWaterFlow(double pressureInCm) {
        return 0.3337 * pressureInCm - 1.1433;
    }

    public static <T> double calculateVirtualPressureBySensor(T t, double readPressure) {
        Sensor sensor;
        if (t.getClass() == Piezometer.class)
            sensor = ((Piezometer) t).getSensor();
        else if (t.getClass() == WaterLevelGauge.class)
            sensor = ((WaterLevelGauge) t).getSensor();
        else if (t.getClass() == Spillway.class)
            sensor = ((Spillway) t).getSensor();
        else if (t.getClass() == CrackGauge.class)
            sensor = ((CrackGauge) t).getSensor();
        else if (t.getClass() == HydrologicalStation.class)
            sensor = ((HydrologicalStation) t).getSensor();
        else
            sensor = ((Deformometer) t).getSensor();

        Double aCoefficient = sensor.getACoefficient();
        Double bCoefficient = sensor.getBCoefficient();
        Double cCoefficient = sensor.getCCoefficient();
        Double dCoefficient = sensor.getDCoefficient();
        return (aCoefficient * Math.pow(readPressure, 3) +
                bCoefficient * Math.pow(readPressure, 2) +
                cCoefficient * readPressure + dCoefficient);
    }

    private static double calculateVirtualPropertyBySensor(Sensor sensor, double propertyVal) {

        Double aCoefficient = sensor.getACoefficient();
        Double bCoefficient = sensor.getBCoefficient();
        Double cCoefficient = sensor.getCCoefficient();
        if (!Objects.isNull(aCoefficient) && !Objects.isNull(bCoefficient) && !Objects.isNull(cCoefficient))
            return aCoefficient / Math.pow(propertyVal, 2) + bCoefficient / propertyVal + cCoefficient;
        return 0;
    }


}
