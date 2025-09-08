package com.spring.app.organization.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.domain.MemberDTO;
import com.spring.app.organization.service.OrganizationService;

@Controller
@RequestMapping("/company")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/organization")
    public String organizationPage() {
        return "company/organization"; // organization.jsp
    }

    @ResponseBody
    @GetMapping("/organization/chartData")
    public List<MemberDTO> getOrganizationData() {
        return organizationService.getAllMembersWithDeptAndGrade();
    }
}
