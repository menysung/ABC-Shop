package org.zerock.b02.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b02.domain.Board;
import org.zerock.b02.domain.Reply;
import org.zerock.b02.dto.BoardListReplyCountDTO;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testInsert() {

        //DB 에 있는 게시글
        Long bno = 100L;

        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("댓글2...")
                .replyer("replyer1")
                .build();

        replyRepository.save(reply);
    }

    @Test
    public void testBoardReplies(){
        Long bno = 100L;
        Pageable pageable = (Pageable) PageRequest.of(0,10, Sort.by("rno").descending());
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        // 결과 page 객체에 댓글 리스트가 포함되어있으므로 getContent 로 리스트를 꺼내서 출력함
        result.getContent().forEach(reply -> {
            log.info(reply);
        });
    }





}
