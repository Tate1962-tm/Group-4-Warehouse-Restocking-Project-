package com.awrs.api.controller;

import com.awrs.api.dto.DashboardResponse;
import com.awrs.service.DashboardService;
import com.awrs.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SessionService sessionService;

    public DashboardController(DashboardService dashboardService, SessionService sessionService) {
        this.dashboardService = dashboardService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public DashboardResponse getDashboard(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return dashboardService.getDashboard();
    }
}
