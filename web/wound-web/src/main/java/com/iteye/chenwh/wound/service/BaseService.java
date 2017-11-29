package com.iteye.chenwh.wound.service;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springside.modules.persistence.SearchFilter;

public class BaseService {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	/**
	 * 创建分页请求.
	 */
	protected PageRequest buildPageRequest(int pageNumber, int pagzSize) {
		return new PageRequest(pageNumber - 1, pagzSize);
	}
	
	/**
	 * 创建分页请求.
	 */
	protected PageRequest buildPageRequest(int pageNumber, int pagzSize, Sort sort) {
		return new PageRequest(pageNumber - 1, pagzSize, sort);
	}
	
	protected <T> Page<T>  nativePageQuery(String nativesql, Pageable pageable, Map<String, SearchFilter> filters) {
		return nativePageQuery(nativesql, pageable, filters, null);
	}
	
	protected <T> Page<T> nativePageQuery(String nativesql,
			Pageable pageable, Map<String, SearchFilter> filters,
			Class<T> t) {
		Query countQuery = entityManager.createNativeQuery(" select count(1) from ("+nativesql+") x");
		Query query = null;
		if (t != null) {
			query = entityManager.createNativeQuery(nativesql, t);
		} else {
			query = entityManager.createNativeQuery(nativesql);
		}
		
		query.setMaxResults(pageable.getPageSize());    
	    query.setFirstResult(pageable.getOffset());
	    
	    for (SearchFilter filter : filters.values()) {
			switch (filter.operator) {
			case LIKE:
		    	countQuery.setParameter(filter.fieldName, "%" + filter.value + "%");
				query.setParameter(filter.fieldName, "%" + filter.value + "%");
				break;
			default:
				countQuery.setParameter(filter.fieldName, filter.value);
				query.setParameter(filter.fieldName, filter.value);
				break;
			}
	    }
		long total = Long.parseLong("" + countQuery.getSingleResult());
		return new PageImpl(query.getResultList(), pageable, total);
	}


}
