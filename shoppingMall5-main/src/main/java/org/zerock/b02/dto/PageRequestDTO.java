package org.zerock.b02.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1; // 초기값 1
    @Builder.Default
    private int size = 10;
    private String type; // 검색 종류 t(제목), c(내용), w(글쓴이), tc, tw, twc
    private String keyword; // 검색어
    private String link; // 검색조건과 페이징을 문자열로 구성
    private String categoryId;


    // 필요 메소드
    public String[] getTypes(){
        if(type == null || type.isEmpty()){
            return null; // 검색조건이 없을 경우 null 리턴
        }
        return type.split(""); // 배열로 나눔 "tcw" => [t,c,w]
    }

    public Pageable getPageable(String...props){
        // Pageable 객체 page-1(jpa 0이 첫페이지), size(한페이지 데이터 개수), 정렬(입력됨)
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
    }

    public String getLink() {
        if(link == null ){
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length()>0){
                builder.append("&type=" + type);
            }
            if(keyword != null){
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // 예외 처리
                }
            }
            link = builder.toString();
        }
        return link; // 요청 주소를 완성 (페이지, 사이즈, 타입, 키워드)
    }
}
