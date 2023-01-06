package com.project.simplegw.document.dtos.send;

import java.util.List;

import com.project.simplegw.document.approval.dtos.send.DtosReferrer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class DtosDocsAddReferrer extends DtosDocs {
    // 일반문서에서 공유기능이 있는 view 페이지로 데이터를 보낼 때 사용: 회의록
    private List<DtosReferrer> referrers;
}
