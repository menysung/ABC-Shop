package org.zerock.b02.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private boolean img;

    //업로드된 파일의 uuid 값과 파일이름, 이미지 여부를 객체로 구성
    //getLink() 를 통해서 첨부파일의 경로 처리에 사용함
    //이미지 파일의 경우 썸네일 파일주소 리턴, 아니면 파일주소 리턴
    public String getLink(){
        if (img){
            return "S_"+uuid +"_"+fileName;
        }else {
            return uuid+"_"+fileName;
        }
    }
}
