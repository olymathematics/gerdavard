package org.gerdavard.problem;

import org.gerdavard.api.generated.ProblemsApi;
import org.gerdavard.api.generated.model.Problem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProblemController implements ProblemsApi {
    private final ProblemRepository repository;

    public ProblemController(ProblemRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Problem> getProblemById(Integer id) {
        return ResponseEntity.ok(repository.findById(id));
    }
}
