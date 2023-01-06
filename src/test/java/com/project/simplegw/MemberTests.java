package com.project.simplegw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberCreate;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberUpdate;
import com.project.simplegw.member.dtos.admin.receive.DtorPwForceUpdate;
import com.project.simplegw.member.dtos.receive.DtorProfile;
import com.project.simplegw.member.dtos.receive.DtorPwChange;
import com.project.simplegw.system.vos.Role;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberTests {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithUserDetails(value = "admin")
    void createMember() throws Exception {
        DtorMemberCreate dto =
        new DtorMemberCreate().setTeam("경영기획팀").setJobTitle("대리").setName("개발자").setId("developer").setPw("developer11!!");

        String params = new ObjectMapper().writeValueAsString(dto);

        mvc
        .perform(
            MockMvcRequestBuilders
            .post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithUserDetails(value = "admin")
    void updateMember() throws Exception {
        DtorMemberUpdate dto =
        new DtorMemberUpdate().setRole(Role.MANAGER.name())
            .setTeam("Computer Team").setJobTitle("대리")
            .setName("신희철").setNameEng("h.c.shin")
            .setBirthday(LocalDate.parse("1990-01-23")).setDateHire(LocalDate.parse("2013-10-04"))
            .setEmail("singun41@gmail.com").setEmailUse(true);

        String params = new ObjectMapper().writeValueAsString(dto);

        mvc
        .perform(
            MockMvcRequestBuilders
            .patch("/member/3")
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithUserDetails(value = "admin")
    void forcePasswordUpdate() throws Exception {
        DtorPwForceUpdate dto = new DtorPwForceUpdate().setPw("abcabc123!@#");

        String params = new ObjectMapper().writeValueAsString(dto);

        mvc
        .perform(
            MockMvcRequestBuilders
            .patch("/member/3/force-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }




    @Test
    @WithUserDetails(value = "developer")
    void getProfile() throws Exception {
        mvc
        .perform(MockMvcRequestBuilders.get("/member/details"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }



    @Test
    @WithUserDetails(value = "developer")
    void updateProfile() throws Exception {
        DtorProfile dto = new DtorProfile().setNameEng("hee cheol SHIN").setBirthday(LocalDate.parse("1990-01-23")).setMobile("010-1234-1234");

        String params = new ObjectMapper().writeValueAsString(dto);

        mvc
        .perform(
            MockMvcRequestBuilders
            .patch("/member/details")
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithUserDetails(value = "developer")
    void updateMyPassword() throws Exception {
        DtorPwChange dto = new DtorPwChange().setOldPw("abcabc123!@#").setNewPw("test123##$$");

        String params = new ObjectMapper().writeValueAsString(dto);

        mvc
        .perform(
            MockMvcRequestBuilders
            .patch("/member/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithUserDetails(value = "admin")
    void getMembers() throws Exception {

        mvc.perform(
            MockMvcRequestBuilders
            .get("/member/all")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .queryParam("isResigned", "false")
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
