package com.project.simplegw.document.approval.helpers;

import java.math.BigInteger;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsMin;
import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.vos.DocsType;

public class DtosApprovalDocsMinConverter {
    // 받은 결재문서의 기간 검색 후 바인딩(ApproverService, ReferrerService에서 호출)
    public static DtosApprovalDocsMin fromObjs(Object[] objs) {
        BigInteger bigId = (BigInteger) objs[0];

        return new DtosApprovalDocsMin()
            .setId( bigId.longValue() )
            
            .updateDocsType(DocsType.valueOf((String) objs[1]))
            .setTitle((String) objs[2])
            
            .setWriterTeam((String) objs[3])
            .setWriterJobTitle((String) objs[4])
            .setWriterName((String) objs[5])
            
            .setApproverTeam((String) objs[6])
            .setApproverJobTitle((String) objs[7])
            .setApproverName((String) objs[8])
            
            .setSign(Sign.valueOf((String) objs[9]))

            .setCreatedDate( ((java.sql.Date) objs[10]).toLocalDate() );
    }
}
