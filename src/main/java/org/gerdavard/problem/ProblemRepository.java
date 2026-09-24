package org.gerdavard.problem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.gerdavard.api.generated.model.Problem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

@Repository
public class ProblemRepository {
    private final Path problemsRoot;
    private final Yaml yaml = new Yaml();

    public ProblemRepository(@Value("${gerdavard.problems-root:problems}") String problemsRoot) {
        this.problemsRoot = Path.of(problemsRoot).toAbsolutePath().normalize();
    }

    public Optional<Problem> findById(int id) {
        Path problemDirectory = problemsRoot.resolve(Integer.toString(id)).normalize();
        if (!problemDirectory.startsWith(problemsRoot) || !Files.isDirectory(problemDirectory)) {
            return Optional.empty();
        }

        try {
            Map<String, String> files = new LinkedHashMap<>();
            try (var paths = Files.list(problemDirectory)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(".tex"))
                        .sorted()
                        .forEach(path -> files.put(path.getFileName().toString(), read(path)));
            }

            Path metadataPath = problemDirectory.resolve("metadata.yaml");
            Map<String, Object> metadata = Files.exists(metadataPath)
                    ? yaml.load(read(metadataPath))
                    : Map.of();

            return Optional.of(new Problem().id(id).metadata(metadata == null ? Map.of() : metadata).files(files));
        } catch (IOException | ClassCastException exception) {
            throw new IllegalStateException("Could not read problem " + id, exception);
        }
    }

    private String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + path, exception);
        }
    }
}
