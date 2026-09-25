package org.gerdavard.problem;

import org.gerdavard.api.generated.ProblemsApi;
import org.gerdavard.api.generated.model.Problem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProblemController implements ProblemsApi {
    private final ProblemService service;

    public ProblemController(ProblemService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<Problem> getProblemById(Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
