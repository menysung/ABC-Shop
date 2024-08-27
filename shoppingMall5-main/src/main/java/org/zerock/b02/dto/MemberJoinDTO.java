package org.zerock.b02.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinDTO {
    private String mid;
    private String email;
    private String mpw;
    private String phone;
    private String address;
    private boolean del;
    private boolean social;
    private String role;
}
