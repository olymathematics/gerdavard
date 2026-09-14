# Gerdavard Olympiad Problem Bank (`gerdavard`)

This project is a git-native, open-source bank of math olympiad problems (statement, solution, tags, difficulty, prerequisite knowledge) in LaTeX, designed for worksheet/booklet generation, reusable problems, and future extensibility.

MVP targets LaTeX-native authors and basic structural validation; post-MVP will add more features (see Roadmap below). Persian support is future-facing — structural bilingualism is possible but not currently required.

## Core Philosophy

- **Problems are the smallest reusable unit.** All documents (worksheets, exams, hint sheets, booklets) are *derived* from the same problem bank.
- **LaTeX is the native format** — not an intermediate; ensures highest math typesetting quality and sustainability.
- **Open source, non-commercial.** Focused on collaborative, generational improvement for all students.

## Usage

1. **Include Single Problems**

   - In your own `.tex` documents, load `\usepackage{olymprepo}` and use:
     - `\repositoryproblem{<id>}` to insert a problem.
     - `\repositoryproblemsolution{<id>}` to insert its solution.
   - Optional `[lang=xx]` argument selects an explicit language; commands error gracefully (no fallback) if the artifact/id/lang isn't found.

2. **Adding Problems**

   - Add a new folder `problems/<id>/`, incrementing the problem number (integer ids, no leading zeroes; first id is `1001`).
   - Each problem requires:
     - `problem.en.tex` (LaTeX problem statement, inside a `problem` environment)
     - `solution.en.tex` (LaTeX solution, inside a `solution` environment)
     - `metadata.yaml` (see below)
   - File naming protocol: every problem/solution/hints artifact must match `<kind>.<lang>.tex`, with `lang ∈ {en, fa}`, `kind ∈ {problem, solution}` (future: `hints`). Only `.en` files are required for now; `.fa` is permitted but not required for the MVP.

## Project Structure
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
olymprepo.sty               # LaTeX package macros
scripts/                    # CI validation scripts
.gitlab-ci.yml              # Pipeline for validation
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

- `difficulty`, `required_knowledge`, `subject`, and `tags.controlled` are checked against the matching `taxonomy/*.yaml` file by CI.
- `tags.uncontrolled` is free-form and never validated against taxonomy — for informal/community labels (e.g. `TRICKY`, `CLASSIC`) not yet promoted to a controlled tag.
- `required_knowledge` is always a list, even if length 1. Same for `tags.controlled`/`tags.uncontrolled`.
- Under `source`, at least one of `link`, `contest`, or `book` should identify where the problem came from; the others may be left blank.
- `contributors` and `solution` are optional for the MVP and will be revisited (required/optional status, and — for `solution` — its exact shape) once the MVP is validated.
- The full required-vs-optional field matrix enforced by CI is still being finalized.

## CI/CD and Validation

On push/MR, three checks run (currently only #1 is implemented, in `scripts/check_subject_taxonomy.py` via `.gitlab-ci.yml`):

1. **Metadata vs. taxonomy.** Every `metadata.yaml` parses, and every controlled field (`difficulty`, `required_knowledge`, `subject`, `tags.controlled`) only uses values present in the matching `taxonomy/*.yaml` file.
2. **Compile check for new/changed problems.** Any added or modified `problem.<lang>.tex` / `solution.<lang>.tex` is rendered standalone (via `pdflatex` or similar) to confirm it has no LaTeX compile issues.
3. **Required/optional field rules.** `metadata.yaml` is validated against the defined required/optional field schema. File/directory naming conventions (`<kind>.<lang>.tex`, integer ids) are enforced as part of this check.

Link reachability (under `source.link`) can optionally be checked but is not required. No hints or translations are required — these are optional.

## Roadmap (Post-MVP)

**Web UI & REST API.** Because AI-assisted development makes it cheap to build, a browsing UI and REST API serving the repository's problems is planned as a near-term post-MVP component:

- REST API reads directly from the repository's `problems/`/`taxonomy/` structure and serves problem metadata + LaTeX source.
- Web UI lets users browse/filter by source, tags, subject, difficulty, etc., and copy the raw LaTeX for a problem directly from the page.
- Deliberately modeled on the MathNet browsing experience.
- No further design has been locked in yet (hosting, auth, caching, etc. are all open).

**Not in MVP scope:**

- No query/filter commands.
- No catalog-PDF build step (dropped; not being implemented).
- No web frontend or REST API for the MVP itself. Pure git, LaTeX, and CI structure for the MVP.
- No automated correctness/translation checking beyond CI structure. Math/translation review is human/manual.
- No submodules or multi-repo logic.
- No Persian `.fa.tex` or hints artifacts *required* for the MVP. The file-naming protocol allows for future multilingual content, but it is not enforced for now.
