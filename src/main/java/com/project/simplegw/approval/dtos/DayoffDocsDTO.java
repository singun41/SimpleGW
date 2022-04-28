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
public class DayoffDocsDTO extends ApprovalDocsDTO {
    // 휴가신청서 디테일
    private List<DayoffDTO> dayoffDtoList;
}
