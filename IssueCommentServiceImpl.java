package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.IssueComment;
import com.TaskManagement1.Repository.IssueCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueCommentServiceImpl implements IssueCommentService {

    private final IssueCommentRepository commentRepo;

    public IssueCommentServiceImpl(IssueCommentRepository commentRepo) {
        this.commentRepo = commentRepo;
    }

    @Override
    public IssueComment addComment(IssueComment comment) {
        return commentRepo.save(comment);
    }

    @Override
    public List<IssueComment> getCommentsByIssueId(Long issueId) {
        return commentRepo.findByIssueIdOrderByCreatedAt(issueId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepo.deleteById(commentId);
    }
}
