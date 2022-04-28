package com.project.simplegw.document.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString(exclude = "comment")
@Accessors(chain = true)
public class CommentDTO {
    private Long id;
    private Long docsId;
    private Long writerId;   // memberDetails의 id
    private String writerTeam;
    private String writerJobTitle;
    private String writerName;
    private String comment;
    private LocalDateTime createdDatetime;
}
