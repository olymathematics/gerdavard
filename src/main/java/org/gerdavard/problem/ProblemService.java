package org.gerdavard.problem;

import org.gerdavard.api.generated.model.Problem;
import org.springframework.stereotype.Service;

@Service
public class ProblemService {
    private final ProblemRepository repository;

    public ProblemService(ProblemRepository repository) {
        this.repository = repository;
    }

    public Problem findById(int id) {
        return repository.findById(id).orElseThrow(() -> new ProblemNotFoundException(id));
    }
}
