# Gerdavard Olympiad Problem Bank (`gerdavard`)

This project is a git-native, open-source bank of math olympiad problems (statement, solution, tags, difficulty, prerequisite knowledge) in LaTeX, designed for discovering and reusing problems by copying their data directly from the UI.

The MVP will provide a REST API, CLI, and web UI for accessing the problem bank, with simple filtering queries by subject, tag, or difficulty, alongside basic structural validation. LaTeX remains the problem content format. Reuse will be through copying problem data directly from the UI; no dedicated LaTeX package or PDF catalog will be developed. Persian support is future-facing — structural bilingualism is possible but not currently required.

This README describes the planned MVP and repository conventions. The API, CLI, UI, problem bank, and validation workflows are not implemented yet.

## Naming Philosophy

<p dir="ltr">Gerdavard (<bdo dir="rtl">گردآورد</bdo>) is a Persian name that evokes gathering things together, reflecting this repository's identity as a shared collection of olympiad problems.</p>

## Core Philosophy

- **Problems are the smallest reusable unit.** Users copy problem statements, solutions, and metadata directly from the UI for reuse in their own materials.
- **LaTeX is the native format** — not an intermediate; ensures highest math typesetting quality and sustainability.
- **Open source, non-commercial.** Focused on collaborative, generational improvement for all students.

## MVP Usage (Planned)

1. **Browse and Query Problems**

   - **REST API:** serve problem metadata and LaTeX source from the repository's `problems/` and `taxonomy/` structure, with simple filters by subject, tag, or difficulty.
   - **CLI:** list and retrieve problems from the terminal, with the same basic filtering options.
   - **Web UI:** browse problems, filter by subject, tag, or difficulty, and copy problem data, including LaTeX statements, solutions, and metadata, directly from the UI for reuse. The browsing experience is inspired by MathNet.
   - Example queries include problems with subject `ALGEBRA`, tag `FUNCTIONAL_EQUATIONS`, or difficulty `IMO_P1`.
   - API routes, CLI syntax, and implementation details (including hosting, authentication, and caching) are still to be defined.

2. **Adding Problems**

   - Add a new folder `problems/<id>/`, incrementing the problem number (integer ids, no leading zeroes; first id is `1001`).
   - Each problem requires:
     - `problem.en.tex` (LaTeX problem statement, inside a `problem` environment)
     - `solution.en.tex` (LaTeX solution, inside a `solution` environment)
     - `metadata.yaml` (see below)
   - File naming protocol: every problem/solution/hints artifact must match `<kind>.<lang>.tex`, with `lang ∈ {en, fa}`, `kind ∈ {problem, solution}` (future: `hints`). Only `.en` files are required for now; `.fa` is permitted but not required for the MVP.

## Planned Project Structure

The API, CLI, and UI will be part of the MVP; their directory layout is still to be defined.

```plaintext
problems/                   # Main problem bank (directory per problem)
├── 1001/
│   ├── problem.en.tex
│   ├── solution.en.tex
│   └── metadata.yaml
├── ...
taxonomy/                   # Controlled vocabularies
├── difficulty.yaml         # e.g. IMO_P1
├── required_knowledge.yaml # e.g. BASICS_OF_FUNCTIONS
├── subject.yaml            # e.g. ALGEBRA
├── tags_controlled.yaml    # e.g. FUNCTIONAL_EQUATIONS
scripts/                    # CI validation scripts
.github/workflows/          # GitHub Actions validation workflows
```

## metadata.yaml (per problem)
```yaml
source:
  link:                     # primary web link to source/post
  contest:
    name:                   # controlled vocab? TBD — currently free-form slug, e.g. balkan_mathematical_olympiad
    year:
    day:
    problem:
  book:
    cite:                   # citation style, e.g. apa
    page_number:
    problem_number:
  mathnet_id:                # cross-reference to MathNet, if applicable

# optional — revisit requirement after MVP
contributors:
  - <contributor_slug>

difficulty: <enum from taxonomy/difficulty.yaml>

# optional, reserved — shape TBD. For cases where metadata beyond `source`
# is needed about the solution itself, e.g. attributing one of several solutions.
solution:

required_knowledge:
  - <enum(s) from taxonomy/required_knowledge.yaml>

subject: <enum from taxonomy/subject.yaml>

tags:
  controlled:
    - <enum(s) from taxonomy/tags_controlled.yaml>
  uncontrolled:
    - <free-form label(s), not checked against any taxonomy>
```

**Example:**
```yaml
source:
  link: https://artofproblemsolving.com/community/c6h3071445p27726683
  contest:
    name: balkan_mathematical_olympiad_shortlist
    year: 2022
    day:
    problem:
  book:
    cite:
    page_number:
    problem_number:
  mathnet_id:

contributors: []

difficulty: IMO_P1

required_knowledge:
  - BASICS_OF_FUNCTIONS

subject: ALGEBRA

tags:
  controlled:
    - FUNCTIONAL_EQUATIONS
  uncontrolled: []
```

- `difficulty`, `required_knowledge`, `subject`, and `tags.controlled` will be checked against the matching `taxonomy/*.yaml` file by CI.
- `tags.uncontrolled` is free-form and never validated against taxonomy — for informal/community labels (e.g. `TRICKY`, `CLASSIC`) not yet promoted to a controlled tag.
- `required_knowledge` is always a list, even if length 1. Same for `tags.controlled`/`tags.uncontrolled`.
- Under `source`, at least one of `link`, `contest`, or `book` should identify where the problem came from; the others may be left blank.
- `contributors` and `solution` are optional for the MVP and will be revisited (required/optional status, and — for `solution` — its exact shape) once the MVP is validated.
- The full required-vs-optional field matrix enforced by CI is still being finalized.

## CI/CD and Validation

GitHub Actions workflows in `.github/workflows/` are planned to run the following checks on pushes and pull requests:

1. **Metadata vs. taxonomy.** Every `metadata.yaml` parses, and every controlled field (`difficulty`, `required_knowledge`, `subject`, `tags.controlled`) only uses values present in the matching `taxonomy/*.yaml` file.
2. **Compile check for new/changed problems.** Any added or modified `problem.<lang>.tex` / `solution.<lang>.tex` is rendered standalone (via `pdflatex` or similar) to confirm it has no LaTeX compile issues.
3. **Required/optional field rules.** `metadata.yaml` is validated against the defined required/optional field schema. File/directory naming conventions (`<kind>.<lang>.tex`, integer ids) are enforced as part of this check.

## Outside MVP Scope

- Advanced query features beyond simple filtering by subject, tag, or difficulty.
- PDF export of search results may be considered later, but is not part of the MVP.
- No automated correctness/translation checking beyond CI structure. Math/translation review is human/manual.
- No submodules or multi-repo logic.
- No Persian `.fa.tex` or hints artifacts *required* for the MVP. The file-naming protocol allows for future multilingual content, but it is not enforced for now.
