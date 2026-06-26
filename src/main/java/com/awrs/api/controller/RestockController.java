package com.awrs.api.controller;

import com.awrs.api.dto.EvaluateThresholdRequest;
import com.awrs.api.dto.RestockTaskResponse;
import com.awrs.model.RestockTask;
import com.awrs.service.RestockService;
import com.awrs.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restock")
public class RestockController {

    private final RestockService restockService;
    private final SessionService sessionService;

    public RestockController(RestockService restockService, SessionService sessionService) {
        this.restockService = restockService;
        this.sessionService = sessionService;
    }

    @GetMapping("/tasks")
    public List<RestockTaskResponse> listTasks(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return restockService.listTasks().stream().map(RestockTaskResponse::from).toList();
    }

    @GetMapping("/tasks/pending")
    public List<RestockTaskResponse> listPendingTasks(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return restockService.listPendingTasks().stream().map(RestockTaskResponse::from).toList();
    }

    @GetMapping("/tasks/{taskId}")
    public RestockTaskResponse getTask(@RequestHeader("Authorization") String token, @PathVariable String taskId) {
        sessionService.requireUser(token);
        return RestockTaskResponse.from(restockService.getTask(taskId));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<RestockTaskResponse> evaluate(@RequestHeader("Authorization") String token,
                                                        @Valid @RequestBody EvaluateThresholdRequest request) {
        sessionService.requireUser(token);
        RestockTask task = restockService.evaluateThreshold(request.itemSku(), request.locationId());
        if (task == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(RestockTaskResponse.from(task));
    }

    @PostMapping("/evaluate/batch")
    public List<RestockTaskResponse> batchEvaluate(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return restockService.batchEvaluate().stream().map(RestockTaskResponse::from).toList();
    }

    @PostMapping("/tasks/{taskId}/assign")
    public RestockTaskResponse assignTask(@RequestHeader("Authorization") String token,
                                          @PathVariable String taskId) {
        var user = sessionService.requireUser(token);
        return RestockTaskResponse.from(restockService.assignTask(taskId, user.getUsername()));
    }

    @PostMapping("/tasks/{taskId}/complete")
    public RestockTaskResponse completeTask(@RequestHeader("Authorization") String token,
                                            @PathVariable String taskId) {
        var user = sessionService.requireUser(token);
        return RestockTaskResponse.from(restockService.completeTask(taskId, user.getUsername()));
    }
}
