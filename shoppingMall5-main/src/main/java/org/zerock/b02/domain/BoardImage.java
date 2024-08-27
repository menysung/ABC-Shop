package org.zerock.b02.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder //setter 대신에
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board") //board 정보 제외하고 다 들고 옴
public class BoardImage implements Comparable<BoardImage> {

    @Id
    private String uuid; //고유 ID

    private String fileName; //파일 이름

    private int ord; //순번

    @ManyToOne //한개의 게시글에 여러개의 이미지
    private Board board;

    @Override
    public int compareTo(BoardImage other) {
        return this.ord - other.ord; //이미지 객체의 순서를 순번으로 정함
    }

    public void changeBoard(Board board) {
        this.board = board;
    }


}
