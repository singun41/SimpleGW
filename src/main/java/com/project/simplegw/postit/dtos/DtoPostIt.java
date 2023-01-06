package com.project.simplegw.postit.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DtoPostIt {
    private int seq;
    private String title;
    private String content;
}
