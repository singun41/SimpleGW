package com.project.simplegw;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.receive.DtorDocsOptions;

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
public class NoticeTests {
    
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
        String params = new ObjectMapper().writeValueAsString(paramObj);

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
    void create() throws Exception {
        DtorDocs dto = new DtorDocs().setTitle("Test notice10").setContent("test content10");

        post(dto, "/notice");
    }


    @Test
    @WithUserDetails(value = "admin")
    void update() throws Exception {
        DtorDocs dto = new DtorDocs().setTitle("Test update notice").setContent("content updated.");

        patch(dto, "/notice/2");
    }


    @Test
    @WithUserDetails(value = "admin")
    void updateOptions() throws Exception {
        DtorDocsOptions dto = new DtorDocsOptions().setUse(true).setDueDate(LocalDate.now().minusDays(1L).toString());
        post(dto, "/notice/options/9");
    }



    @Test
    @WithUserDetails(value = "developer")
    void delete() throws Exception {
        delete("/notice/2");
    }


    @Test
    @WithUserDetails(value = "developer")
    void createTemp() throws Exception {
        DtorDocs dto = new DtorDocs().setTitle("Test notice temp save").setContent("test content save temp");

        post(dto, "/notice/temp");
    }


    @Test
    @WithUserDetails(value = "developer")
    void getMainPageList() throws Exception {
        get("/notice/main-list");
    }
}
