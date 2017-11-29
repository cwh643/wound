/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.iteye.chenwh.wound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.iteye.chenwh.wound.entity.Patient;

public interface PatientDao extends PagingAndSortingRepository<Patient, Long>,  JpaSpecificationExecutor<Patient> {

	List<Patient> findByDoctorId(Integer docId);

	Patient findByInpatientNo(String inpatientNo);

	Patient findByInpatientNoAndDeviceId(String inpatientNo, String deviceId);

	Patient findByUuid(String uuid);
}
