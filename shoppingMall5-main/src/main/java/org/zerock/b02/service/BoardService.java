package org.zerock.b02.service;

import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {
    //새 글을 등록
    Long register(BoardDTO boardDTO);
    //글 조회 (글번호)
    BoardDTO readOne(Long bno);
    //글 수정
    void modify(BoardDTO boardDTO);
    //삭제
    void remove(Long bno);

    PageResponseDTO<BoardDTO> list(PageRequestDTO request);

    //댓글갯수 포함
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    //댓글갯수 , 이미지 포함
    PageResponseDTO<BoardAllListDTO> listWithAll(PageRequestDTO pageRequestDTO);

    //디폴트 메소드 추가 (디폴트 메소드는 추상메서드가 아니라 구현가능)
    default Board dtoToEntity(BoardDTO boardDTO) {

        Board board = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();

        if(boardDTO.getFileNames() != null){
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }
        return board;
    }

    default BoardDTO entityToDto(Board board) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        List<String> fileNames =
                board.getImageSet().stream().sorted()
                        .map(boardImage -> boardImage.getUuid()+"_"+boardImage.getFileName())
                        .collect(Collectors.toList());

        boardDTO.setFileNames(fileNames);

        return boardDTO;
    }

}

