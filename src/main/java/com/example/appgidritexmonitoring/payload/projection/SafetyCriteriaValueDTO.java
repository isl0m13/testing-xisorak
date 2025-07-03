package com.example.appgidritexmonitoring.payload.projection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "water_level_gauge_last_value",
        "water_level_gauge_difference",
        "pds_difference_value",
        "ninth_gate_piezometer_gr",
        "seventeenth_gate_piezometer_gr",
        "spillway_d1_value",
        "spillway_d2_value",
        "spillway_ls2_value",
        "k6_spillway_value",
        "hd_spillway_value",
        "trt_spillway_value",
        "sum_of_spillways_values",
        "pds_p1_m1752_value",
        "pds_p1_m936_value",
        "pds_p2_m1751_value",
        "pds_p3_m850_value",
        "pds_p3_m1606_value",
        "pds_p2_m1109_value",
        "pds_p2_m1159_value",
        "pds_p2_m1172_value",
        "pds_p1_m1171_value",
        "pds_p2_m1153_value",
        "pds_p2_m1655_value",
        "pds_p1_m1043_value",
        "pds_p3_m799_value",
        "pds_p2_m1156_value",
        "pds_p2_m871_value",
        "pds_p1_m1751_value",
        "pts_t2_m2417_value",
        "pts_t1_m2187_value",
        "pngs_g1_m1045_value",
        "pngs_g3_m775_value",
        "pngs_g1_m777_value",
        "pngs_g1_m893_value",
        "s17_p4_value",
        "s17_p6_value",
        "s21_p3_value",
        "s21_p4_value",
        "s23_p1_value",
        "s23_p2_value",
        "s25_p4_value",
        "s27_p1_value",
        "s27_p3_value",
        "s27_p6_value",
        "s31_p1_value",
        "s19_p6_value"
})
public interface SafetyCriteriaValueDTO {

    Double getWater_level_gauge_last_value();

    Double getWater_level_gauge_difference();

    Double getPds_difference_value();

    Double getNinth_gate_piezometer_gr();

    Double getSeventeenth_gate_piezometer_gr();

    Double getSpillway_d1_value();

    Double getSpillway_d2_value();

    Double getSpillway_ls2_value();

    Double getK6_spillway_value();

    Double getHd_spillway_value();

    Double getTrt_spillway_value();

    Double getSum_of_spillways_values();

    Double getPds_p1_m1752_value();

    Double getPds_p1_m936_value();

    Double getPds_p2_m1751_value();

    Double getPds_p3_m850_value();

    Double getPds_p3_m1606_value();

    Double getPds_p2_m1109_value();

    Double getPds_p2_m1159_value();

    Double getPds_p2_m1172_value();

    Double getPds_p1_m1171_value();

    Double getPds_p2_m1153_value();

    Double getPds_p2_m1655_value();

    Double getPds_p1_m1043_value();

    Double getPds_p3_m799_value();

    Double getPds_p2_m1156_value();

    Double getPds_p2_m871_value();

    Double getPds_p1_m1751_value();

    Double getPts_t2_m2417_value();

    Double getPts_t1_m2187_value();

    Double getPngs_g1_m1045_value();

    Double getPngs_g3_m775_value();

    Double getPngs_g1_m777_value();

    Double getPngs_g1_m893_value();

    Double getS17_p4_value();

    Double getS17_p6_value();

    Double getS21_p3_value();

    Double getS21_p4_value();

    Double getS23_p1_value();

    Double getS23_p2_value();

    Double getS25_p4_value();

    Double getS27_p1_value();

    Double getS27_p3_value();

    Double getS27_p6_value();

    Double getS31_p1_value();

    Double getS19_p6_value();

}
