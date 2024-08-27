package org.zerock.b02.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDTO {
    private String mid;
    private String mpw;
    private String email;
    private String phone;
    private String address;
    private String role;
}
