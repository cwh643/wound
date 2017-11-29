package com.iteye.chenwh.wound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.iteye.chenwh.wound.entity.ArchivesRecord;

public interface ArchivesRecordDao extends PagingAndSortingRepository<ArchivesRecord, Long>, JpaSpecificationExecutor<ArchivesRecord> {

	List<ArchivesRecord> findByInpatientNo(String inpatientNo);

	ArchivesRecord findByInpatientNoAndRecordTime(String inpatientNo, String recordTime);

	ArchivesRecord findByRecordIdAndDeviceId(Long recordId, String deviceId);

	ArchivesRecord findByUuid(String uuid);
	
}
