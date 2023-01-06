package com.project.simplegw.document.approval.dtos.send;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosSavedLineDetails {
    // 저장된 결재라인 타이틀 선택시 저장된 라인을 보내주는 dto
    
    private List<DtosMember> approvers;
    private List<DtosMember> referrers;
}
