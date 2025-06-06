# PatternTool

## Recommended repository setup

### [Settings](https://github.com/cubuspl42/PatternTool/settings)

#### Pull Requests

- Allow merge commits: **No** (default: **Yes**)
- Allow squash merging: **No** (default: **Yes**)
- Allow rebase merging: **Yes** (default)
- Allow auto-merge: **Yes** (default: **No**)
- Automatically delete head branches: **Yes** (default: **No**)

### [Rulesets](https://github.com/cubuspl42/PatternTool/settings/rules)

#### Main branch ruleset

- Ruleset Name: `main`
- Enforcement status: **Active** (default: **Disabled**)
- Bypass list: (empty)
- Target branches:
  - **Default** (_Include default branch_)

##### Rules
 
###### Branch rules

- Require linear history: **Yes** (default: **No**)
- Require a pull request before merging: **Yes** (default: **No**)
- Require status checks to pass: **Yes**
  - Require branches to be up to date before merging: **Yes** (default: **No**)
  - Status checks that are required:
    - `test`
- Allowed merge methods: **Rebase** (default: **Rebase, Merge, Squash**)
