package com.project.simplegw.document.dtos.send;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosComment {
    private Long id;
    private Long writerId;   // memberDetails의 id
    private String writerTeam;
    private String writerJobTitle;
    private String writerName;
    private String comment;
    private LocalDateTime createdDatetime;
}
