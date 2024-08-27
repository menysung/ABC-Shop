package org.zerock.b02.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
    // 여러개의 파일 리스트
    private List<MultipartFile> files;
}
