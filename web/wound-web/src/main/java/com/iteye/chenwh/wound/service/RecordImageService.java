package com.iteye.chenwh.wound.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iteye.chenwh.wound.entity.ArchivesRecord;
import com.iteye.chenwh.wound.entity.RecordImage;
import com.iteye.chenwh.wound.repository.RecordImageDao;

@Component
@Transactional
public class RecordImageService extends BaseService {
	
	private RecordImageDao dao;
	
	@Autowired
	public void setDao(RecordImageDao dao) {
		this.dao = dao;
	}
	
	public void deleteByRecordId(Long recordId) {
		dao.deleteByRecordId(recordId);
	}

	public void save(RecordImage image) {
		dao.save(image);
	}

	public List<RecordImage> findByRecordId(Long recordId) {
		return dao.findByRecordId(recordId);
	}

}
