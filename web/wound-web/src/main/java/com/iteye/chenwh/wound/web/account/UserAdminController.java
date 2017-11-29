/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.iteye.chenwh.wound.web.account;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.modules.web.Servlets;

import com.iteye.chenwh.wound.entity.Task;
import com.iteye.chenwh.wound.entity.User;
import com.iteye.chenwh.wound.service.account.AccountService;
import com.iteye.chenwh.wound.utils.CommonUtils;

/**
 * 管理员管理用户的Controller.
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/admin/user")
public class UserAdminController {

	@Autowired
	private AccountService accountService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = CommonUtils.PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "auto") String sortType, Model model,
			ServletRequest request) {
		
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		Page<User> users = accountService.getUsers(searchParams, pageNumber, pageSize, sortType);
		//List<User> users = accountService.getAllUser();
		model.addAttribute("users", users);
		model.addAttribute("menu", 2);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));
		return "account/adminUserList";
	}
	
	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String createForm(Model model) {
		if (!model.containsAttribute("user")) {
			model.addAttribute("user", new User());
		}
		model.addAttribute("action", "create");
		return "account/adminUserForm";
	}
	
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String create(@Valid User user, RedirectAttributes redirectAttributes) {
		User dbUser = accountService.findUserByLoginName(user.getLoginName());
		if (dbUser != null) {
			redirectAttributes.addFlashAttribute("message", "用户名已经存在");
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/admin/user/create";
		}
		accountService.registerUser(user);
		redirectAttributes.addFlashAttribute("message", "创建用户成功");
		return "redirect:/admin/user";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", accountService.getUser(id));
		model.addAttribute("action", "update");
		return "account/adminUserForm";
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
		accountService.updateUser(user);
		redirectAttributes.addFlashAttribute("message", "更新用户" + user.getLoginName() + "成功");
		return "redirect:/admin/user";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		User user = accountService.getUser(id);
		accountService.deleteUser(id);
		redirectAttributes.addFlashAttribute("message", "删除用户" + user.getLoginName() + "成功");
		return "redirect:/admin/user";
	}

	/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出User对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getUser(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("user", accountService.getUser(id));
		}
	}
}
