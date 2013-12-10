# Release process

The release process consists of several steps. The first step occurs when the `develop` branch is 
ready for release, all unit tests, checkstyle, findbugs, pmd, etc. pass.

1. Create a release branch from the `develop` branch. Release branches support preparation of a 
new production release. They allow for last-minute dotting of i’s and crossing t’s. Furthermore,
they allow for minor bug fixes and preparing meta-data for a release (version number, build dates, 
etc.). By doing all of this work on a release branch, the develop branch is cleared to receive 
features for the next big release. 

    $ git checkout -b release-3.0.1 develop

2. Update the version number, minor bug fixes, meta-data, etc.  Ensure that the release branch 
passes all unit tests, checkstyle, findbugs, pmd, etc.

3. Commit all the changes to the release branch.

    $ git commit -a -m "Bumped version to 3.0.1."
    
4. Switch to the `master` branch.

    $ git checkout master

5. Merge the release branch into `master`.

    $ git merge --no-ff release-3.0.1

6. Ensure that the `master` branch passes all unit tests, checkstyle, findbugs, pmd, etc.

    $ mvn package
    
7. Tag the `master` branch.

    $ git tag -a v3.0.1 -m "Release 3.0.1"
    
8. Push the changes to *Github*.

    $ git push origin master
    
9. (Optional) Delete the old release branch.

    $ git branch -d release-3.0.1

10. Create a *Github* release.

Document how to create a new release of the system.

Involves committing a new snapshot of JavaDocs, updating release number in pom.xml, merging develop branch to master, etc.
