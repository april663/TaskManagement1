package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.Issue;
import com.TaskManagement1.Enum.IssueStatus;

import java.util.List;

public interface IssueService {

    Issue createIssue(Issue issue);

    List<Issue> getAllIssues();

    Issue getIssueById(Long id);

    List<Issue> getIssuesByProjectId(Long projectId);

    List<Issue> getIssuesByAssigneeId(Long assigneeId);

    Issue assignIssue(Long issueId, Long userId);

    Issue updateIssue(Long id, Issue issue);

    Issue updateIssueStatus(Long id, IssueStatus status, String userEmail);

    List<IssueStatus> getAllowedNextStatuses(Long issueId, String userEmail);

    void deleteIssue(Long id);
}