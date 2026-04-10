package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.Issue;
import com.TaskManagement1.Entity.Sprint;
import com.TaskManagement1.Enum.IssueStatus;
import com.TaskManagement1.Enum.SprintState;
import com.TaskManagement1.Repository.IssueRepository;
import com.TaskManagement1.Repository.SprintRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepo;

    @Autowired
    private IssueRepository issueRepo;

    // CREATE SPRINT
    public Sprint createSprint(Sprint sprint) {

        sprint.setState(SprintState.PLANNED);

        return sprintRepo.save(sprint);
    }

    // GET SPRINT BY PROJECT
    public List<Sprint> getSprintsByProject(Long projectId) {

        return sprintRepo.findByProjectId(projectId);
    }

    // START SPRINT
    @Transactional
    public Sprint startSprint(Long sprintId) {

        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        if (sprint.getState() != SprintState.PLANNED) {
            throw new RuntimeException("Sprint already started");
        }

        sprint.setState(SprintState.ACTIVE);

        if (sprint.getStartDate() == null) {
            sprint.setStartDate(LocalDate.now());
        }

        return sprintRepo.save(sprint);
    }

    // CLOSE SPRINT
    @Transactional
    public Sprint closeSprint(Long sprintId) {

        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        sprint.setState(SprintState.COMPLETE);

        if (sprint.getEndDate() == null) {
            sprint.setEndDate(LocalDate.now());
        }

        List<Issue> issues = issueRepo.findBySprintId(sprintId);

        for (Issue issue : issues) {

            if (issue.getStatus() != IssueStatus.DONE) {

                issue.setSprintId(null);

                issueRepo.save(issue);
            }
        }

        return sprintRepo.save(sprint);
    }

    // ASSIGN ISSUE TO SPRINT
    @Transactional
    public Issue assignIssueToSprint(Long sprintId, Long issueId) {

        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        if (sprint.getState() == SprintState.COMPLETE) {

            throw new RuntimeException("Cannot add issue to completed sprint");
        }

        issue.setSprintId(sprintId);

        return issueRepo.save(issue);
    }

    // BURNDOWN CHART
    public Map<String, Object> getBurndownData(Long sprintId) {

        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        LocalDate start = sprint.getStartDate();

        LocalDate end = sprint.getEndDate() != null
                ? sprint.getEndDate()
                : LocalDate.now();

        List<Issue> issues = issueRepo.findBySprintId(sprintId);

        int totalIssues = issues.size();

        Map<String, Object> chart = new LinkedHashMap<>();

        LocalDate cursor = start;

        while (!cursor.isAfter(end)) {

            int completed = (int) issues.stream()
                    .filter(i -> i.getStatus() == IssueStatus.DONE)
                    .count();

            chart.put(cursor.toString(), totalIssues - completed);

            cursor = cursor.plusDays(1);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("SprintId", sprintId);
        response.put("StartDate", start);
        response.put("EndDate", end);
        response.put("BurnDownData", chart);

        return response;
    }
}