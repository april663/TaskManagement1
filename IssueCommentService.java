package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.IssueComment;

import java.util.List;

public interface IssueCommentService {

    IssueComment addComment(IssueComment comment);

    List<IssueComment> getCommentsByIssueId(Long issueId);

    void deleteComment(Long commentId);
}
