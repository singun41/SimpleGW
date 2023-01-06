package com.project.simplegw;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.simplegw.document.approval.dtos.receive.DtorDefaultReport;
import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorDayoff;
import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorDayoffDetails;

@SpringBootTest
@AutoConfigureMockMvc
public class ApprovalTest {

    @Autowired
    private MockMvc mvc;


    private void get(String urlString) throws Exception {
        mvc
        .perform(
            MockMvcRequestBuilders
            .get(urlString)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void post(Object paramObj, String urlString) throws Exception {
        // String params = new ObjectMapper().writeValueAsString(paramObj);

        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule( new JavaTimeModule() );   // LocalDate, LocalTime, LocalDateTime 객체를 매핑하기 위해서 JavaTimeModule을 등록해야 함.
        String params = objMapper.writeValueAsString(paramObj);

        mvc
        .perform(
            MockMvcRequestBuilders
            .post(urlString)
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void postFail(Object paramObj, String urlString) throws Exception {
        // String params = new ObjectMapper().writeValueAsString(paramObj);

        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule( new JavaTimeModule() );   // LocalDate, LocalTime, LocalDateTime 객체를 매핑하기 위해서 JavaTimeModule을 등록해야 함.
        String params = objMapper.writeValueAsString(paramObj);

        mvc
        .perform(
            MockMvcRequestBuilders
            .post(urlString)
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    private void patch(Object paramObj, String urlString) throws Exception {
        String params = new ObjectMapper().writeValueAsString(paramObj);

        mvc
        .perform(
            MockMvcRequestBuilders
            .patch(urlString)
            .contentType(MediaType.APPLICATION_JSON)
            .content(params)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void patchNoParams(String urlString) throws Exception {
        mvc
        .perform(
            MockMvcRequestBuilders
            .patch(urlString)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void delete(String urlString) throws Exception {
        mvc
        .perform(
            MockMvcRequestBuilders
            .delete(urlString)
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }





    @Test
    @WithUserDetails(value = "developer")
    void approvalCreate() throws Exception {   // 결재 승인, 반려 테스트를 위한 기본 결재문서 등록
        DtorDefaultReport dto = (DtorDefaultReport) new DtorDefaultReport().setTitle("test").setContent("test").setArrApproverId( new Long[]{122L, 70L, 23L, 65L} );
        post(dto, "/approval/default");
    }



    @Test
    @WithUserDetails(value = "")
    void approvalConfirmed() throws Exception {
        patchNoParams("/approval/confirmed/DEFAULT/37733");
    }




    // dayoff details validation test
    @Test
    @WithUserDetails(value = "20131004")
    void approvalDayoffCreate() throws Exception {
        
        DtorDayoffDetails e1 = new DtorDayoffDetails().setCode(null).setDateFrom(LocalDate.parse("2022-07-15", DateTimeFormatter.ISO_DATE)).setDateTo(LocalDate.parse("2022-07-15", DateTimeFormatter.ISO_DATE));
        DtorDayoffDetails e2 = new DtorDayoffDetails().setCode("100").setDateFrom(LocalDate.parse("2022-08-12", DateTimeFormatter.ISO_DATE)).setDateTo(LocalDate.parse("2022-08-14", DateTimeFormatter.ISO_DATE));
        List<DtorDayoffDetails> details = Arrays.asList(e1, e2);

        Long[] approvers = {122L, 70L};
        Long[] referrers = {46L, 171L};

        DtorDayoff dayoffDto = (DtorDayoff) new DtorDayoff().setContent("test").setDetails(details).setTitle("test").setArrApproverId(approvers).setArrReferrerId(referrers);

        postFail(dayoffDto, "/approval/dayoff");
    }
}
