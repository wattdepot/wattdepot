# Creating WattDepot documentation


## Overview
WattDepot documentation is built using the [Viewdocs system](http://progrium.viewdocs.io/viewdocs).  You can access the documentation associated with our master branch at [http://wattdepot.viewdocs.io/wattdepot](http://wattdepot.viewdocs.io/wattdepot).  The updated version is generally available within 10 seconds after making the commit.  (Currently, due to issues with Viewdocs, it is best to commit documentation directly to the master branch.  I expect that we will soon be able to manage documentation using our [branch model](/branching).

WattDepot documentation is maintained in the [docs](https://github.com/wattdepot/wattdepot/tree/master/docs) directory, and consists of a set of files in what is basically [Markdown format](http://daringfireball.net/projects/markdown/syntax), although Viewdocs imposes a few changes.

WattDepot documentation is basically organized into four guides: an installation guide, a user guide, a developer guide, and an API guide. These correspond to four subdirectories within the docs directory.  Each section of these guides is maintained as a separate markdown file.

## Making updates

To make changes to existing files, just edit them and commit your changes.  The edits should be live in around 10 seconds after the commit.

Adding a new section to a documentation guide is a little more complicated. Viewdocs requires a top-level [template.html file](https://github.com/wattdepot/wattdepot/blob/master/docs/template.html). Ours implements a Twitter Bootstrap based theme.  The most important thing to know about this file is that the "table of contents" for our documentation exists as a set of pull-down menus in the nav bar. So, to add a new section of documentation:

* Create a new markdown file.
* Add the documentation to it.
* Update the template.html file with a link to the new section.
* Commit your changes.
* Wait 10 seconds, then check to see that the change is live.

## Misc hints

### Images

You can add images using the regular img element, but it is good to add the "img-responsive" class to that tag in order to get the Bootstrap responsive image formatting. 

