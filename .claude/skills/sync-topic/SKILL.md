---
name: sync-topic
description: Merge the stable master branch into the topic worktree and verify the build.
disable-model-invocation: true
---

1. Check that the current directory is the `topic/` worktree and that `git branch --show-current` prints `topic`. If either check fails, stop.
2. Run `git status --porcelain`. If there are uncommitted changes, list them and ask whether to commit them first (as a Conventional Commit) or abort. Never stash them.
3. Run `git merge master`. The `master` branch is shared through the bare repo, so it doesn't need to be fetched.
4. If the merge conflicts, list the conflicted files with a one-line summary of each side. Resolve only the trivial ones (imports, formatting). Leave the rest to the user, and don't run `git merge --abort` unless asked.
5. If the merge is clean, run `./gradlew build --console=plain` and report pass or fail. On failure, give the first error with its `file:line`.
6. Do not push. Report the merge commit hash and the build result. $ARGUMENTS
