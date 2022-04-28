package com.project.simplegw.approval.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PurchaseDocsDTO extends ApprovalDocsDTO {
    private List<PurchaseDTO> purchaseDtoList;
}
