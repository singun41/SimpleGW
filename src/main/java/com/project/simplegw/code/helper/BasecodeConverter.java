package com.project.simplegw.code.helper;

import com.project.simplegw.code.dtos.receive.DtorBasecode;
import com.project.simplegw.code.dtos.send.DtosBasecode;
import com.project.simplegw.code.dtos.send.DtosCodeValue;
import com.project.simplegw.code.entities.Basecode;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BasecodeConverter {
    Basecode getBasecode(DtorBasecode dto);
    DtosBasecode getDtosBasecode(Basecode entity);

    @Mapping(target = "key", source = "code")
    DtosCodeValue getDtosCodeValue(Basecode entity);

    Basecode getEntity(DtorBasecode dto);
}
