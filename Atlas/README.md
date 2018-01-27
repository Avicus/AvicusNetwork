# Atlas Game Manager
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3fc7245904d14c439285678caec1eea0)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Avicus/Atlas&amp;utm_campaign=Badge_Grade) [![Jenkins Badge](https://ci.avicus.net/buildStatus/icon?job=Atlas)](https://ci.avicus.net/job/Atlas)


Build with `mvn clean install`

## Separation of Modules
Modules which are only used in a specific game genre should be placed in their own genre-specific sub-project.
Modules used across genres such as most objectives and checks should be kept in core.

## Module Set JARs
Each module set jar should contain a `module-set.yml` which contains information about the features defined in each sub-project.
```yaml
  name: "Set Name"
  dependancies: [other, names] # Set Name -> set-name
  main: path.to.main.class
```