# Release process

The release process consists of several steps.

## Entry conditions
Before starting the release process the `develop` branch must be 
ready for release, all unit tests, checkstyle, findbugs, pmd, etc. pass 
[TravisCI](https://travis-ci.org/).


## Step 1 Create release branch in Github and merge it into the master branch.

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

7. (Optional) Delete the old release branch.

    $ git branch -d release-3.0.1

8. Push the changes to *Github*.

    $ git push origin master
    
9. Check that continuous integration passes [TravisCI](https://travis-ci.org/).
  
## Step 2 Upload the Release's Javadocs to GitHub.
    
1. Move the `target/site/apidocs` to a safe place (e.g. Your home directory).

2. Checkout the `gh-pages` branch.
    
3. Create a new directory under the `javadoc/` subdirectory with the number of your release.
    
    $ cd javadoc/
    $ mkdir 3.0.1
        
4. Move the contents of the saved `apidocs` directory into the new directory.
    
5. Edit the `index.html` file to add the new directory to the list of Release Javadocs.
    
6. Add all the changes to the `gh-branch`.
    
    $ git commit -a -m "Javadoc for 3.0.1"
        
7. Push the changes to the `gh-pages` branch.
    
    $ git push origin gh-pages.
        
8. Switch back the `master` branch.
    
    $ git checkout master 
    
## Step 3 Create a GitHub release.

