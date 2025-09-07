package com.sprocms.jprj.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.json.JSONObject;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = EventsApp.class)
@AutoConfigureMockMvc(addFilters = true)
@ExtendWith({ MockitoExtension.class })
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventsAppTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        BuildProperties buildProperties;

        @Test
        public void Start() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(httpBasic("admin", "sajer")))
                                .andExpect(status().isOk())
                                .andReturn();

                // MvcResult result =
                mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(httpBasic("user1", "user1")))
                                .andExpect(status().isOk())
                                .andReturn();
                // String token = result.getResponse().getContentAsString();
        }

        @Test
        public void getEvents() throws Exception {
                MvcResult result1 = mockMvc
                                .perform(MockMvcRequestBuilders.get("/s/Events").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(401))
                                .andReturn();

                String resultDOW1 = result1.getResponse().getContentAsString();
                JSONObject jsonObj1 = new JSONObject(resultDOW1);
                assertEquals("Unauthorized", jsonObj1.getString("error"));

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.get("/p/Events").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();

                String resultDOW = result.getResponse().getContentAsString();
                JSONObject jsonObj = new JSONObject(resultDOW);
                assertEquals(buildProperties.getVersion(), jsonObj.getString("version"));
        }

        @Test
        public void authentication() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(401))
                                .andReturn();

                mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(httpBasic("suser1", "suser1")))
                                .andExpect(status().is(401))
                                .andReturn();

                mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .header("Authorization", "Bearer " + "kjdgsfkajsd")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(401))
                                .andReturn();

                MediaType APPLICATION_JSON_UTF8 = new MediaType(
                                MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
                                Charset.forName("utf8"));

                mockMvc.perform(MockMvcRequestBuilders.post("/p/auth/op/signup")
                                .content("{\"username\": \"suser1\", \"password\": \"suser1\"}").contentType(
                                                APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(202))
                                .andReturn();

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(httpBasic("suser1", "suser1")))
                                .andExpect(status().isOk())
                                .andReturn();
                String resultDOW = result.getResponse().getContentAsString();
                assertEquals(true, resultDOW.length() > 400);

                mockMvc.perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                .header("Authorization", "Bearer " + resultDOW)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();

                mockMvc.perform(MockMvcRequestBuilders.get("/s/Events").header("Authorization", "Bearer " + resultDOW)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();

        }

        @Test
        public void errorHandling() throws Exception {
                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.get("/p/auth/op/token")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .with(httpBasic("suser1", "suser1")))
                                .andExpect(status().is(401))
                                .andReturn();
                String resultDOW = result.getResponse().getContentAsString();
                JSONObject jsonObj = new JSONObject(resultDOW);
                assertNotNull(jsonObj);
                assertEquals("Unauthorized", jsonObj.getString("error"));
                assertEquals("/p/auth/op/token", jsonObj.getString("path"));

                result = mockMvc.perform(MockMvcRequestBuilders.get("/p/Events/notFound")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(404))
                                .andReturn();
                resultDOW = result.getResponse().getContentAsString();
                jsonObj = new JSONObject(resultDOW);
                assertNotNull(jsonObj);
                assertEquals("Not Found", jsonObj.getString("error"));

                result = mockMvc.perform(MockMvcRequestBuilders.get("/p/Events/error500")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(500))
                                .andReturn();
                resultDOW = result.getResponse().getContentAsString();
                jsonObj = new JSONObject(resultDOW);
                assertNotNull(jsonObj);
                assertEquals("Internal Server Error", jsonObj.getString("error"));
                //assertEquals("some error message", jsonObj.getString("message"));

                result = mockMvc.perform(MockMvcRequestBuilders.get("/p/Events/noError")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status()
                                                .isOk())
                                .andReturn();
                resultDOW = result.getResponse().getContentAsString();
                assertEquals("no error4", resultDOW);
        }

}
