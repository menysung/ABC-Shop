package org.zerock.b02.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;
    // 게시글 1개에 댓글 여러개
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board; //게시글 참조 (board_bno 를 참조함)

    private String replyText;
    private String replyer;

    //수정 메서드
    public void changeText(String newText) {
        this.replyText = newText;
    }
}