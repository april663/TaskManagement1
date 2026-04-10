package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.WorkFlow;
import com.TaskManagement1.Entity.WorkFlowTransaction;
import com.TaskManagement1.Enum.IssueStatus;
import com.TaskManagement1.Enum.Role;
import com.TaskManagement1.Repository.WorkFlowRepository;
import com.TaskManagement1.Repository.WorkFlowTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class WorkFlowService {

    private final WorkFlowRepository workFlowRepo;
    private final WorkFlowTransactionRepository transactionRepo;

    public WorkFlowService(
            WorkFlowRepository workFlowRepo,
            WorkFlowTransactionRepository transactionRepo
    ) {
        this.workFlowRepo = workFlowRepo;
        this.transactionRepo = transactionRepo;
    }

    // ✅ CREATE
    @Transactional
    public WorkFlow createWorkFlow(WorkFlow wf) {
        wf.getTransactions().forEach(t -> t.setWorkFlow(wf));
        return workFlowRepo.save(wf);
    }

    // ✅ FIXED: FETCH WITH TRANSACTIONS (NO LAZY ERROR)
    public List<WorkFlow> getAllWorkFlowList() {
        return workFlowRepo.findAllWithTransactions();
    }

    // ✅ GET BY ID
    public WorkFlow getById(Long id) {
        return workFlowRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
    }

    // ✅ UPDATE
    @Transactional
    public WorkFlow updateWorkFlow(Long id, WorkFlow wf) {
        WorkFlow existing = getById(id);

        existing.setWorkFlowName(wf.getWorkFlowName());
        existing.setDescription(wf.getDescription());

        existing.getTransactions().clear();
        wf.getTransactions().forEach(t -> {
            t.setWorkFlow(existing);
            existing.getTransactions().add(t);
        });

        return workFlowRepo.save(existing);
    }

    // ✅ DELETE
    @Transactional
    public void deleteWorkFlow(Long id) {
        workFlowRepo.deleteById(id);
    }

    // ✅ ALLOWED TRANSACTIONS FROM STATUS
    public List<WorkFlowTransaction> allowedTransactions(
            Long workflowId,
            IssueStatus fromStatus
    ) {
        return transactionRepo.findByWorkFlowIdAndFromStatus(
                workflowId, fromStatus
        );
    }

    // ✅ VALIDATE SINGLE TRANSITION
    public boolean isTransactionAllowed(
            Long workflowId,
            IssueStatus from,
            IssueStatus to,
            Set<Role> userRoles
    ) {

        List<WorkFlowTransaction> list =
                transactionRepo.findByWorkFlowIdAndFromStatus(workflowId, from);

        for (WorkFlowTransaction t : list) {

            if (!t.getToStatus().equals(to)) continue;

            // No role restriction
            if (t.getAllowedRoles().isEmpty()) return true;

            for (Role r : userRoles) {
                if (t.getAllowedRoles().contains(r)) return true;
            }
        }
        return false;
    }

    // ✅ MAIN METHOD: NEXT ALLOWED STATUSES (ROLE BASED)
    public List<IssueStatus> getAllowedNextStatuses(
            Long workflowId,
            IssueStatus fromStatus,
            Set<Role> roles
    ) {
        return transactionRepo
                .findByWorkFlowIdAndFromStatus(workflowId, fromStatus)
                .stream()
                .filter(t ->
                        t.getAllowedRoles().isEmpty()
                        || t.getAllowedRoles().stream().anyMatch(roles::contains)
                )
                .map(WorkFlowTransaction::getToStatus)
                .distinct()
                .toList();
    }

    // ✅ FIND BY NAME
    public Optional<WorkFlow> findByName(String name) {
        return workFlowRepo.findByWorkFlowName(name);
    }
}