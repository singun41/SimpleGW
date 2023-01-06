package com.project.simplegw.postit.helpers;

import com.project.simplegw.postit.dtos.DtoPostIt;
import com.project.simplegw.postit.entities.PostIt;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostItConverter {
    PostIt getPostIt(DtoPostIt dto);
    DtoPostIt getDtoPostIt(PostIt entity);
}
