package org.gerdavard.problem;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.gerdavard.api.generated.model.Problem;

@WebMvcTest(ProblemController.class)
class ProblemControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProblemService service;

    @Test
    void returnsProblemById() throws Exception {
        Problem problem = new Problem()
                .id(1)
                .metadata(java.util.Map.of("subject", "number_theory"))
                .files(java.util.Map.of("problem.en.tex", "2 + 2 = 4"));
        org.mockito.Mockito.when(service.findById(1)).thenReturn(problem);

        mvc.perform(get("/api/v1/problems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.files['problem.en.tex']").value(containsString("2 + 2")));
    }

    @Test
    void returnsNotFoundForMissingProblem() throws Exception {
        org.mockito.Mockito.when(service.findById(999)).thenThrow(new ProblemNotFoundException(999));

        mvc.perform(get("/api/v1/problems/999"))
                .andExpect(status().isNotFound());
    }
}
