package com.project.simplegw.work.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosWorkRecordForList {   // 부서 업무일지, 업무일지 전체 리스트를 front로 보낼 때 사용.
    private String team;
    private String jobTitle;
    private String name;
    private String todayWork;
    private String nextPlan;
}
