# Development Guidelines

Guidelines for code, Git, and documents to ease development

## Test-Driven Development
Test the code and document these tests. Follow the guidelines of Test-Driven Development.

1. Write a unit test or define a test that is comprehensive of your feature
2. If possible, run the test and ensure it fails before you implement
3. Write the minimum code for the test to pass
4. Run the test and ensure it succeeds
5. Refactor the code until it is **simple**
6. Repeat and accumulate unit-tests/features

It may not always be sensible to make unit tests, but at least aim to define a test you can use to assess whether your feature was developed successfully or not. As in, if you can't write a unit test, write a clear definition of how your feature works.

Test that the code works when it should work and fails when it should fail. Make sure your code behaves **exactly** as you expect and you can eloquently explain why that behavior makes sense. Be aware how the code behaves even when the devices reset or are on standby.

## Git Rules

Guidelines for using Github and collaborating

### Repository structure.

The master branch should contain stable code for all the components (app, smart-pin, and inhaler) of the system. Make sure someone reviews your code before you merge anything into the master branch.

For Development, make a branch for every component of the project. When you make a Pull Requests to the master branch, only use these component branches. These branches should be named based on the component (e.g. ```breathe-app```, ```smart-pin```, and ```inhaler```).

Use `git rebase` instead of `git merge` for the pull requests. For a project like this, git rebase helps keep the history of the project linear and easier to follow.

### Example

When you are implementing a feature, only then do you make your own branch. Make it out of the branch of the component you are working on. Append the name of the feature to the original component branch. Implement the feature. Then make a Pull Request to the same component branch. 

For example, if you are building the UI for the app, then you will make a branch out of the ```breathe-app``` branch. You can call your branch ```breathe-app-ui``` and work there. When you are done implementing the UI, make a Pull Request to merge ```breathe-app-ui``` into the ```breathe-app``` branch. A teammate should review the code before the Pull Request is accepted.

Let's say you implemented the UI and database, demoed that to the clients, and everything is working on the ```breathe-app``` branch. Now, you should make a Pull Request from ```breathe-app``` to ```master``` so the updated app code is added to the stable final deliverables.

When the Pull Request is accepted, sync the component branches with the master branch to get the component branches up to speed with the master branch. You can do that by merging the master into the component branches or rebasing the component branches on top of master (rebase is preferable for this project).

### Naming Conventions
- Name a component (app, smart-pin, and inhaler) branch based on the component
- Name your feature by appending the name of the feature to the component branch
- Avoid putting your name anywhere in the code or in the branches
- Name folder and file names with small letters unless this contradicts with a more important guideline
- Use underscores (```_```) instead of spaces in folder and file names

### Commit Messages
Commit messages are important because they tell us at a glance what this commit changes in the code. If we find a bug, we will use these commit messages to identify when the bug was introduced and which part of the code is responsible for it. It is important that you explain what you do in your commits.

Write a short title for the commit message that briefly explains what was changed and write a longer description in the message body. If you are using Git's CLI, the title is the first line and the message body is everything after that.

Reference/Close related issues inside the title/body of the message as appropriate.

## Android Style Guidelines

Here are the guidelines for the Android resources and Java logic. If we find documented, official, Android or Java Style Guides, we will add them.

### Resources and UI
- Name the IDs of all the resources with small letters.
- Use Underscores to separate words in an ID.
- For the UI, append the name of the UI view to the id (```doses_textview``` for example).

### classes
- Use the CamelCase style for variables and declarations.
- For every class, use its name as a tag for the Logger.

### Documentation
Document your methods and code using Javadoc. Don't waste time documenting the obvious but comment on any complex part of your code.

Some API/Method parameters are really important. Explain why these parameters are important and why you chose them. This is a favor for future teams and whoever reads your code.

Record important resources you read and decisions you made. When you are finished with a feature and are near the end of the capstone, update this documentation explaining the most important decisions made while developing that feature, resources to understand it, and possible future steps.

## STM32WB Style Guidelines

The STM32WB is programmed in C. This [page](https://www.cs.umd.edu/~nelson/classes/resources/cstyleguide/) from the University of Maryland contains a concise style guide you can adhere to. This style guide is a little different from ST's style guide so it makes it easier to identify which code strictly belongs to this project.

ST's code can be bloated. Try to reduce it if possible.