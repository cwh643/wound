/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.iteye.chenwh.wound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.iteye.chenwh.wound.entity.User;

public interface UserDao extends PagingAndSortingRepository<User, Long>,  JpaSpecificationExecutor<User> {
	User findByLoginName(String loginName);

	@Query(" from User u where u.type >0 ")
	List<User> findDocList();
}
