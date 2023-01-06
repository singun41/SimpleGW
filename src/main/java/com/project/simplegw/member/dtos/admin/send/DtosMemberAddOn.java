package com.project.simplegw.member.dtos.admin.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosMemberAddOn {
    private Long id;
    private String team;
    private String jobTitle;
    private String name;
    
    private double dayoffQty;
    private double dayoffUse;
}
