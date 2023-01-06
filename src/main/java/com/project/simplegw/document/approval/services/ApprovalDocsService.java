package com.project.simplegw.document.approval.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalLinePack;
import com.project.simplegw.document.approval.entities.OngoingApproval;
import com.project.simplegw.document.approval.helpers.ApprovalConverter;
import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ApprovalDocsService {
    private final MemberService memberService;
    private final DocsService docsService;
    private final ApprovalConverter converter;
    private final OngoingApprovalService ongoingApprovalService;
    private final ApproverService approverService;
    private final ReferrerService referrerService;
    private final ApprovalCountService countService;

    @Autowired
    public ApprovalDocsService(
        MemberService memberService, DocsService docsService, ApprovalConverter converter, ApprovalCountService countService,
        OngoingApprovalService ongoingApprovalService, ApproverService approverService, ReferrerService referrerService
    ) {
        this.memberService = memberService;
        this.docsService = docsService;
        this.converter = converter;

        this.ongoingApprovalService = ongoingApprovalService;
        this.approverService = approverService;
        this.referrerService = referrerService;

        this.countService = countService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- same package services ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<String> getTeams() {
        return memberService.getTeams();
    }

    List<MemberData> getTeamMembers(String team) {
        return memberService.getTeamMembers(team);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- same package services ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자 승인/반려 처리 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private boolean isCurrentApprover(Docs docs, LoginUser loginUser) {
        return ongoingApprovalService.isCurrentApprover(docs, loginUser);
    }

    private ServiceMsg approverSign(Docs docs, LoginUser loginUser, Function<ApproverService, ServiceMsg> function) {
        if(docs == null || docs.getId() == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재문서가 없습니다.");

        if(isCurrentApprover(docs, loginUser)) {
            return function.apply(approverService);

        } else {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("현재 결재자 순번이 아닙니다.");
        }
    }


    public ServiceMsg confirmed(DocsType type, Long docsId, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, type);
        return approverSign(docs, loginUser, t -> t.confirm(docs, loginUser));
    }


    public ServiceMsg rejected(DocsType type, Long docsId, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, type);
        return approverSign(docs, loginUser, t -> t.reject(docs, loginUser));
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자 승인/반려 처리 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public Docs getDocsEntity(Long docsId, DocsType type) {   // detail을 가지는 결재문서 entity의 docs를 바인딩하기 위해.
        return docsService.getDocsEntity(docsId, type);
    }


    public ServiceMsg create(DtorDocs dto, DocsType type, Long[] arrApproverId, Long[] arrReferrerId, LoginUser loginUser) {
        Docs docs = docsService.create(dto, type, loginUser);
        
        if(docs == null) {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder(type.getTitle()).append(" 등록 에러입니다. 관리자에게 문의하세요.").toString());

        } else {
            ServiceMsg approverResult = approverService.create(docs, arrApproverId, loginUser);
            ServiceMsg referrerResult = referrerService.create(docs, arrReferrerId, loginUser);

            if(approverResult.getResult() == ServiceResult.SUCCESS && referrerResult.getResult() == ServiceResult.SUCCESS) {
                countService.removeProceedDocsCntCache(loginUser);   // 결재문서 작성자의 결재 진행 카운트 캐시를 업데이트.
                return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docs.getId());

            } else if(approverResult.getResult() == ServiceResult.FAILURE) {
                return approverResult;

            } else {
                return referrerResult;
            }
        }
    }


    public ServiceMsg update(Long docsId, DtorDocs dto, DocsType type, Long[] arrApproverId, Long[] arrReferrerId, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, type);
        OngoingApproval approval = ongoingApprovalService.getOngoingApproval( docs );

        if(approval.getApproverSeq() > 0)   // 최초 결재자가 0번이고, 진행했다면 0보다 큼.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재가 진행되어 수정할 수 없습니다.");
        
        if(approval.getId() == null)   // 결재문서 완결시 OngoingApproval 테이블에서 삭제되므로 null을 리턴한다.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재가 완결되어 수정할 수 없습니다.");
        
        if(docs == null) {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder(type.getTitle()).append(" 수정 에러입니다. 관리자에게 문의하세요.").toString());

        } else if( ! docsService.isOwner(docs, loginUser) ) {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        } else {
            docsService.update(docsId, dto, type);
            ServiceMsg approverResult = approverService.update(docs, arrApproverId, loginUser);
            ServiceMsg referrerResult = referrerService.update(docs, arrReferrerId, loginUser);

            if(approverResult.getResult() == ServiceResult.SUCCESS && referrerResult.getResult() == ServiceResult.SUCCESS)
                return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docs.getId());
        
            else if(approverResult.getResult() == ServiceResult.FAILURE)
                return approverResult;

            else
                return referrerResult;
        }
    }


    public ServiceMsg delete(Long docsId, DocsType type, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, type);
        OngoingApproval approval = ongoingApprovalService.getOngoingApproval( docs );

        if(approval.getApproverSeq() > 0)   // 최초 결재자가 0번이고, 진행했다면 0보다 큼.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재가 진행되어 수정할 수 없습니다.");
        
        if(approval.getId() == null)   // 결재문서 완결시 OngoingApproval 테이블에서 삭제되므로 null을 리턴한다.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재가 완결되어 수정할 수 없습니다.");
        
        if(docs == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder(type.getTitle()).append(" 삭제 에러입니다. 관리자에게 문의하세요.").toString());

        if( ! docsService.isOwner(docs, loginUser) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());
        
        boolean success = docsService.delete(docs);
        if(success) {
            // 결재문서 삭제시 결재자, 참조자 캐시 전체를 제거 --> 해당 결재자, 참조자들 캐시를 삭제하기 위해 검색하는 것이 더 큰 리소스 낭비가 될 수 있다.
            // 결재문서 삭제는 거의 발생하지 않으므로 문제가 없다고 판단된다.
            countService.removeAllApprovalCache(loginUser);
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } else {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("문서 삭제시 오류입니다. 관리자에게 문의하세요.");
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서 view, modify page에서 필요한 결재자 및 참조자 정보 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public DtosApprovalDocsCommon getDocs(Long docsId, DocsType type, LoginUser loginUser) {
        // modify page에서 결재라인 확인하여 결재가 진행됐는지 판단해야 하므로 view, modify 페이지 둘 다 사용.
        return converter.getDtosApprovalDocs( getDtosDocs(docsId, type) ).setLine( getDtosApprovalLinePack(docsId, type, loginUser) );
    }

    private DtosDocs getDtosDocs(Long docsId, DocsType type) {
        return docsService.getDtosDocs(docsId, type);
    }

    public DtosApprovalLinePack getDtosApprovalLinePack(Long docsId, DocsType type, LoginUser loginUser) {
        // modify page에서 결재라인을 바인딩하기 위해 별도로 호출.
        Docs docs = docsService.getDocsEntity(docsId, type);
        
        if(docs.getId() == null)
            return new DtosApprovalLinePack();

        return new DtosApprovalLinePack().setApprovers(approverService.getApprovers(docs)).setReferrers(referrerService.getReferrers(docs, loginUser));
    }


    public ServiceMsg addReferrers(DocsType type, Long docsId, Long[] arrReferrerId, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, type);
        if(docs.getId() == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("적용 대상 문서가 없습니다. 관리자에게 문의하세요.");
        
        return referrerService.add(docs, arrReferrerId, loginUser);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서 view, modify page에서 필요한 결재자 및 참조자 정보 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
