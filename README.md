# Lobster


## Setup Development Environment

:exclamation:**IMPORTANT:** Every developer must follow the following steps before contributing to the project:

1. Create a local branch named `release-candidate`. This branch is going to be used for the developing stage, whereas the `master` branch is going to be used for the production stage.

    > `git checkout -b release-candidate`

2. Make it so the new local `release-candidate` branch keeps track of the remote developer branch `release-candidate`.

    > `git branch --set-upstream-to=origin/release-candidate`

3. Finally do a pull request so that your local `release-candidate` branch is in sync with the remote one.

    > `git pull`


## Development Workflow

In order to follow a proper branching and development framework every developer must understand and adhere to the following workflow:

* The remote `master` branch represents a stable version of our app that is meant for production (customer ready).
    * This remote branch should not be tampered with by any means. It is up to the repository master to merge the remote `release-candidate` into the remote `master` branch.
* The remote `release-candidate` represents the state of the development environment. Changes that are ready to be deployed to the production stage must first pass through the development stage (this branch) in order to ensure that everything is working correctly. This way we have more confidence that our code is not going to break or disrupt the stable state of the production (customer facing) branch.
* The general idea is that each contributor will create local branches for the features they are working on. After testing that such feature works properly (please DO TEST your feature, do NOT PUSH code that does not work to any remote branch), the developer will then push to the remote `release-candidate` branch. He/she will then notify the git master in order to merge such changes into production stage (the remote `master` branch).

**Here is an example:**

* Suppose you find a bug and want to fix it:
    1. Create local branch to work on bug fix:

        > `git checkout -b user-retrieved-bug-fix`

    2. Make sure your new local branch keeps track of remote `release-candidate`:

        > `git branch --set-upstream-to=origin/release-candidate`

    2. Aid your team in the never ending fight against the infinite legions of BUGS (fix the damn bug).
    3. After you are done implementing the fix to the bug you found in your local branch `too-much-honey-bug-fix` simply stage your changes, commit and push:

        > `git add <whatever-file-or-files-you-changed>`

        > `git commit -m "These damn damn bugs just wont leave me alone, little fu*@*$*@s"`

        > `git push`

## Contribution Rules

The following rules should be followed by any developer contributing to the project. It is of paramount importance that everybody follows them in order to avoid problems with each member's individual contribution when merging, thus saving us time.

1. Create a local branch every time you want to make a contribution (push code to remote `release-candidate` branch).
  * The idea here is that each of us will be making incremental contributions to the project, therefore it is a good practice to create your own local branch everytime you want to make some changes to the project (it does not matter how small the change is).
  * This will save a lot of problems when we are all working at the same time and we try to all push code to remote branch.
  *  It will also make it easier whenever there are merge problems.
  *  This also keeps and maintains the integrity of the remote `release-candidate` branch.
  *  If something goes wrong it is easier to roll back the changes.
  *  After you are done pushing your change to the remote `release-candidate` branch you can simply delete the local branch you created to make the change.
2.   ALWAYS ALWAYS ALWAYS make a pull request before start working on anything!
  * This is probably the most important point. It will save you a lot of time everytime you push code to remote `release-candidate` branch (can avoid merge conflicts substantially).
3. Document your code!
  * Since we are working on a collective project from scratch, there is going to be a lot of code written by different people (specially in the early stages), therefore we need to properly document the code so others can understand it and build upon it!

## Useful links regarding Firebase
* [Firebase starter example (includes auth, database, analytics, etc.)](https://github.com/firebase/quickstart-android)
* [Firebase chat example] (more comprenhensible tutorial that uses auth, storage, etc.)](https://codelabs.developers.google.com/codelabs/firebase-android/index.html?index=..%2F..%2Findex#16)
* ... ADD OTHER RESOURCES HERE

