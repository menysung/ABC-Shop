package org.zerock.b02.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//entity 는 테이블과 같은 클래스
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet") //imageSet 제외하고 다 들고 옴
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno; //board 번호(기본키 Id, 자동증가 GeneratedValue) 보통 기본키에는 자동증가가 들어간다

    @Column(length = 500, nullable = false)
    private String title; //제목

    @Column(length = 2000, nullable = false)
    private String content; //내용

    @Column(length = 50, nullable = false)
    private String writer; //글쓴이

    @OneToMany(mappedBy = "board", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true) //BoardImage 의 board 변수
    @Builder.Default
    @BatchSize(size = 20) //일괄 쿼리를 실행할때 한번에 가져오는 entity 수
    private Set<BoardImage> imageSet = new HashSet<>(); //중복 안되는 이미지객체 리스트

    public List<BoardImage> getBoardImages() {
        return new ArrayList<>(imageSet);
    }

    @Transient
    private int replyCount;

    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImage() {
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();
    }


    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
