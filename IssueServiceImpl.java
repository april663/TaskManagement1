package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.Issue;
import com.TaskManagement1.Entity.UserAuth;
import com.TaskManagement1.Enum.IssueStatus;
import com.TaskManagement1.Enum.Role;
import com.TaskManagement1.Exception.ResourceNotFoundException;
import com.TaskManagement1.Exception.UnauthorizedException;
import com.TaskManagement1.Repository.IssueRepository;
import com.TaskManagement1.Repository.UserAuthRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepo;
    private final WorkFlowService workFlowService;
    private final UserAuthRepository userRepo;

    public IssueServiceImpl(
            IssueRepository issueRepo,
            WorkFlowService workFlowService,
            UserAuthRepository userRepo
    ) {
        this.issueRepo = issueRepo;
        this.workFlowService = workFlowService;
        this.userRepo = userRepo;
    }

    @Override
    public Issue createIssue(Issue issue) {
        issue.setStatus(IssueStatus.TO_DO);
        return issueRepo.save(issue);
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepo.findAll();
    }

    @Override
    public Issue getIssueById(Long id) {
        return issueRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }

    @Override
    public List<Issue> getIssuesByProjectId(Long projectId) {
        return issueRepo.findByProjectId(projectId);
    }

    @Override
    public List<Issue> getIssuesByAssigneeId(Long assigneeId) {
        return issueRepo.findByAssigneeId(assigneeId);
    }

    @Override
    public Issue assignIssue(Long issueId, Long userId) {
        Issue issue = getIssueById(issueId);
        issue.setAssigneeId(userId);
        return issueRepo.save(issue);
    }

    @Override
    public Issue updateIssue(Long id, Issue issue) {
        Issue existing = getIssueById(id);

        existing.setTitle(issue.getTitle());
        existing.setDescription(issue.getDescription());
        existing.setPriority(issue.getPriority());
        existing.setAssigneeId(issue.getAssigneeId());

        return issueRepo.save(existing);
    }

    // 🔥 WORKFLOW VALIDATED STATUS UPDATE
    @Override
    public Issue updateIssueStatus(Long id, IssueStatus status, String userEmail) {

        Issue issue = getIssueById(id);

        IssueStatus fromStatus = issue.getStatus();
        IssueStatus toStatus = status;

        UserAuth user = userRepo.findByUserOfficialEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = user.getRole();

        boolean allowed = workFlowService.isTransactionAllowed(
                issue.getWorkFlowId(),
                fromStatus,
                toStatus,
                Set.of(role)
        );

        if (!allowed) {
            throw new UnauthorizedException("Workflow transition not allowed");
        }

        issue.setStatus(status);
        return issueRepo.save(issue);
    }

    // ✅ GET ALLOWED NEXT STATUSES
    @Override
    public List<IssueStatus> getAllowedNextStatuses(Long issueId, String userEmail) {

        Issue issue = getIssueById(issueId);

        UserAuth user = userRepo.findByUserOfficialEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = user.getRole();

        return workFlowService.getAllowedNextStatuses(
                issue.getWorkFlowId(),
                issue.getStatus(),
                Set.of(role)
        );
    }

    @Override
    public void deleteIssue(Long id) {
        issueRepo.deleteById(id);
    }
}