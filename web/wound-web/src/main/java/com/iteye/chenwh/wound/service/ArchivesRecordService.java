package com.iteye.chenwh.wound.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.iteye.chenwh.wound.entity.ArchivesRecord;
import com.iteye.chenwh.wound.entity.Patient;
import com.iteye.chenwh.wound.repository.ArchivesRecordDao;

@Component
@Transactional
public class ArchivesRecordService extends BaseService {
	
	private ArchivesRecordDao dao;
	
	public List<ArchivesRecord> findByInpatientNo(String inpatientNo) {
		return dao.findByInpatientNo(inpatientNo);
	}
	
	public Page<ArchivesRecord> getRecordList(Map<String, Object> searchParams, int pageNumber, int pageSize, String sortType) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Pageable pageable = buildPageRequest(pageNumber, pageSize);
		StringBuilder sql = new StringBuilder();
		sql.append(" select p.inpatient_no as inpatient_no, r.device_id as device_id, r.record_time as record_time, u.name as doctor_name, p.name as patient_name, p.sex as patient_sex, r.is_operation as is_operation ");
		sql.append(" from archives_record r " );
		sql.append(" LEFT JOIN patient_info p on r.inpatient_no = p.inpatient_no " );
		sql.append(" LEFT JOIN wouuser u on r.doctor_id = u.id ");
		sql.append(" where 1=1 ");
		if (filters.containsKey("LIKE_deviceId")) {
			sql.append(" and r.device_id like :deviceId ");
		}
		if (filters.containsKey("LIKE_patientName")) {
			sql.append(" and p.name like :patientName ");
		}
		if (filters.containsKey("GTE_beginTime")) {
			sql.append(" and r.record_time >= :beginTime ");
		}
		if (filters.containsKey("LTE_endTime")) {
			sql.append(" and r.record_time <= :endTime ");
		}
		sql.append(" order by record_time desc ");
		return nativePageQuery(sql.toString(), pageable, filters);
	}

	/**
	 * 创建分页请求.
	 */
	private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {
		Sort sort = null;
		if ("auto".equals(sortType)) {
			sort = new Sort(Direction.DESC, "id");
		} else if ("title".equals(sortType)) {
			sort = new Sort(Direction.ASC, "title");
		}

		return new PageRequest(pageNumber - 1, pagzSize, sort);
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<ArchivesRecord> buildSpecification(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		//filters.put("user.id", new SearchFilter("user.id", Operator.EQ, userId));
		Specification<ArchivesRecord> spec = DynamicSpecifications.bySearchFilter(filters.values(), ArchivesRecord.class);
		return spec;
	}

	@Autowired
	public void setDao(ArchivesRecordDao dao) {
		this.dao = dao;
	}

	public ArchivesRecord findRecordById(Long id) {
		return dao.findOne(id);
	}

	public void saveRecord(ArchivesRecord record) {
		dao.save(record);
	}

	public ArchivesRecord findRecord(String inpatientNo, String recordTime) {
		return dao.findByInpatientNoAndRecordTime(inpatientNo, recordTime);
	}

	public ArchivesRecord findRecord(String deviceId, Long recordId) {
		return dao.findByRecordIdAndDeviceId(recordId, deviceId);
	}

	public ArchivesRecord findRecord(String uuid) {
		return dao.findByUuid(uuid);
	}

	public List<ArchivesRecord> findRecordHistroy(String inpatientNo) {
		return dao.findByInpatientNo(inpatientNo);
	}
	

}
