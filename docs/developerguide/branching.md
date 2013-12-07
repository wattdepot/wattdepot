# The branching model

The WattDepot branch model is a simplified version of the [nvie branch model](http://nvie.com/posts/a-successful-git-branching-model/).

Here is a summary of the branching model:

<img class="img-responsive" src="http://nvie.com/img/2009/12/Screen-shot-2009-12-24-at-11.32.03.png"/>

## The master and develop branches

There is one central "truth" repository, origin. It is the central push-pull location. Developers can of course clone their own repository and do whatever they want locally, but our model controls the structure of the [GitHub wattdepot/wattdepot](http://github.com/wattdepot/wattdepot) repository.

There are two main branches:

  * master. Consider origin/master to be the main branch where the source code of HEAD always reflects a production-ready state. 

  * develop. Consider origin/develop to be the main branch where the source code of HEAD always reflects a state with the latest delivered development changes for the next release.

When the source code in the develop branch reaches a stable point and
is ready to be released, all of the changes should be merged back into
master somehow and then tagged with a release number. 

## Supporting branches

There are three additional branch types: feature, release and
hotfix. Each has a specific purpose and are bound by strict rules
about originating and merge branches.

### Feature Branches

Feature branches are used to develop new features for the upcoming or
a distant future release.

### Release Branches

Release branches support preparation of a new production release. They
allow for last-minute dotting of i’s and crossing t’s. Furthermore,
they allow for minor bug fixes and preparing meta-data for a release
(version number, build dates, etc.). By doing all of this work on a
release branch, the develop branch is cleared to receive features for
the next big release. 

### Hotfix Branches

Hotfix branches are very much like release branches in that they are
also meant to prepare for a new production release, albeit
unplanned. They arise from the necessity to act immediately upon an
undesired state of a live production version. 

