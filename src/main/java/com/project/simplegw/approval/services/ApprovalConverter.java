package com.project.simplegw.approval.services;

import com.project.simplegw.approval.dtos.ApprovalDocsDTO;
import com.project.simplegw.approval.dtos.ApproverDTO;
import com.project.simplegw.approval.dtos.DayoffDTO;
import com.project.simplegw.approval.dtos.DocsApprovalDTO;
import com.project.simplegw.approval.dtos.NamecardDTO;
import com.project.simplegw.approval.dtos.OvertimeDTO;
import com.project.simplegw.approval.dtos.PurchaseDTO;
import com.project.simplegw.approval.dtos.ReferrerDTO;
import com.project.simplegw.approval.dtos.TemplateLineMasterDTO;
import com.project.simplegw.approval.entities.ApprovalDocStatus;
import com.project.simplegw.approval.entities.Approver;
import com.project.simplegw.approval.entities.Dayoff;
import com.project.simplegw.approval.entities.Namecard;
import com.project.simplegw.approval.entities.Overtime;
import com.project.simplegw.approval.entities.Purchase;
import com.project.simplegw.approval.entities.Referrer;
import com.project.simplegw.approval.entities.TemplateLineMaster;
import com.project.simplegw.document.dtos.DocsDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ApprovalConverter {
    // ----- ----- ----- ----- ----- ----- ----- Approval common ----- ----- ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    // Approver getEntity(ApproverDTO dto);
    // Referrer getEntity(ReferrerDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //

    
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    TemplateLineMasterDTO getDto(TemplateLineMaster entity);
    ApproverDTO getDto(Approver entity);
    ReferrerDTO getDto(Referrer entity);

    @Mapping(target = "approverTeam", source = "team")
    @Mapping(target = "approverJobTitle", source = "jobTitle")
    @Mapping(target = "approverName", source = "name")
    DocsApprovalDTO getDto(ApprovalDocStatus entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- DTO to DTO ----- ----- ----- ----- ----- //
    @Mapping(target = "memberId", source = "writerId")
    DocsDTO getDto(ApprovalDocsDTO dto);
    // ----- ----- ----- ----- ----- DTO to DTO ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- Approval common ----- ----- ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- ----- ----- Sub List ----- ----- ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    DayoffDTO getDto(Dayoff entity);
    PurchaseDTO getDto(Purchase entity);
    OvertimeDTO getDto(Overtime entity);
    NamecardDTO getDto(Namecard entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    Dayoff getEntity(DayoffDTO dto);
    Purchase getEntity(PurchaseDTO dto);
    Overtime getEntity(OvertimeDTO dto);
    Namecard getEntity(NamecardDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- Sub List ----- ----- ----- ----- ----- ----- ----- //
}
