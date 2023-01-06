package com.project.simplegw.document.dtos.send;

import java.time.LocalDate;
import java.time.LocalTime;

import com.project.simplegw.document.vos.DocsType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(exclude = "content")
public class DtosDocs {
    private Long id;

    private DocsType type;

    private String title;
    private String content;
    
    private Long writerId;
    private String writerTeam;
    private String writerJobTitle;
    private String writerName;

    private LocalDate createdDate;
    private LocalTime createdTime;
}
