package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.*;
import com.TaskManagement1.Repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepo;
    private final BoardColumnRepository columnRepo;
    private final BoardCardRepository cardRepo;
    private final IssueRepository issueRepo;

    public BoardServiceImpl(
            BoardRepository boardRepo,
            BoardColumnRepository columnRepo,
            BoardCardRepository cardRepo,
            IssueRepository issueRepo
    ) {
        this.boardRepo = boardRepo;
        this.columnRepo = columnRepo;
        this.cardRepo = cardRepo;
        this.issueRepo = issueRepo;
    }

    @Override
    public Board createBoard(Board board) {
        return boardRepo.save(board);
    }

    @Override
    public Optional<Board> getByBoardId(Long id) {
        return boardRepo.findById(id);
    }

    @Override
    public Optional<Board> findById(Long id) {
        return boardRepo.findById(id);
    }

    @Override
    public List<BoardColumn> getBoardColumns(Long boardId) {
        return columnRepo.findByBoardIdOrderByPosition(boardId);
    }

    @Override
    public List<BoardCard> getBoardCards(Long boardId, Long columnId) {
        return cardRepo.findByBoardIdAndColumnIdOrderByPosition(boardId, columnId);
    }

    @Override
    @Transactional
    public BoardCard addIssueToBoard(Long boardId, Long columnId, Long issueId) {

        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        BoardColumn column = columnRepo.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        List<BoardCard> existing =
                cardRepo.findByBoardIdAndColumnIdOrderByPosition(boardId, columnId);

        int position = existing.size();

        BoardCard card = new BoardCard();
        card.setBoardId(boardId);
        card.setIssueId(issueId);
        card.setColumn(column);
        card.setPosition(position);

        return cardRepo.save(card);
    }
}