package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.ReadFile;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReadFileRepository extends JpaRepository<ReadFile, UUID> {
    boolean existsByNameAndDatasource(String name, DeviceDatasourceEnum datasource);

    Optional<ReadFile> findReadFileByName(String name);

}
