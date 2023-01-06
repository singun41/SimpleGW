package com.project.simplegw.document.approval.dtos.send;

import com.project.simplegw.document.dtos.send.DtosDocs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class DtosApprovalDocsCommon extends DtosDocs {
    // 모든 결재문서 데이터를 프론트로 보낼 때 공통으로 사용하는 클래스
    // 각 디테일이 다른 결재문서들은 이 dto를 상속한 뒤 디테일 dto를 리스트로 붙여서 사용.
    private DtosApprovalLinePack line;
}
