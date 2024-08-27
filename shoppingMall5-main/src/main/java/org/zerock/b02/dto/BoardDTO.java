package org.zerock.b02.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long bno;

    @NotEmpty
    @Size(min = 3, max = 100)
    private String title; //제목은 3~100자 까지

    @NotEmpty
    private String content; //필요

    @NotEmpty
    private String writer; //필요

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    //첨부파일의 이름들
    private List<String> fileNames;


}
