package com.project.simplegw.system.helpers;

import org.mapstruct.Mapper;

import com.project.simplegw.system.dtos.receive.DtorMenuAuthority;
import com.project.simplegw.system.dtos.send.DtosMenuAuthority;
import com.project.simplegw.system.entities.MenuAuthority;

@Mapper(componentModel = "spring")
public interface MenuAuthorityConverter {
    MenuAuthority getEntity(DtorMenuAuthority dto);
    DtosMenuAuthority getDto(MenuAuthority entity);
}
