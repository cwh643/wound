package com.iteye.chenwh.wound.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iteye.chenwh.wound.entity.Patient;
import com.iteye.chenwh.wound.repository.PatientDao;

@Component
@Transactional
public class PatientService extends BaseService {

	private PatientDao dao;

	public List<Patient> findPtientList(Integer docId) {
		return dao.findByDoctorId(docId);
	}
	
	@Autowired
	public void setDao(PatientDao dao) {
		this.dao = dao;
	}

	public Patient findPatient(String inpatientNo) {
		return dao.findByInpatientNo(inpatientNo);
	}
	
	public Patient findPatientByUuid(String uuid) {
		return dao.findByUuid(uuid);
	}

	public void savePatient(Patient patient) {
		dao.save(patient);
	}

	public Patient findPatient(String deviceId, String inpatientNo) {
		return dao.findByInpatientNoAndDeviceId(inpatientNo, deviceId);
	}
	
}
