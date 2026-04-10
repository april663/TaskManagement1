package com.TaskManagement1.Service;

import com.TaskManagement1.Entity.Board;
import com.TaskManagement1.Entity.BoardCard;
import com.TaskManagement1.Entity.BoardColumn;

import java.util.List;
import java.util.Optional;

public interface BoardService {

    Board createBoard(Board board);

    Optional<Board> getByBoardId(Long id);

    List<BoardColumn> getBoardColumns(Long boardId);

    List<BoardCard> getBoardCards(Long boardId, Long columnId);

    BoardCard addIssueToBoard(Long boardId, Long columnId, Long issueId);

    Optional<Board> findById(Long id);

}