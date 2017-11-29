package com.iteye.chenwh.wound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.iteye.chenwh.wound.entity.RecordImage;

public interface RecordImageDao extends PagingAndSortingRepository<RecordImage, Long>, JpaSpecificationExecutor<RecordImage> {

	@Modifying
	@Query("delete from RecordImage where recordId=?1")
	//@Query(value="delete from record_image where recor_id = ?1",nativeQuery=true)
	void deleteByRecordId(Long recordId);

	List<RecordImage> findByRecordId(Long recordId);
}
