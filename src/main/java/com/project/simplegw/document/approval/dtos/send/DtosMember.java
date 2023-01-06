package com.project.simplegw.document.approval.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosMember {
    // 결재라인 설정할 때와 저장된 결재라인을 보여줄 때 필요한 멤버 정보를 담는 dto
    // DtosTeam, DtosSavedLineDetails에서 사용.

    private Long id;   // Member id를 사용.
    private String jobTitle;
    private String name;
}
