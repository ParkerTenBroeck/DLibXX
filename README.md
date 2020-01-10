# **DLibX**

"What is this thing," you ask?
This is a JAVA library created to simplify the process of creating a game, by efficiently handling all user input and all drawing without the need to touch the ugly JAVA graphics API.

[![Download Package][dl-pi]][dl-p] [![Download Jar][dl-ji]][dl-j] [![Download Documentation][dl-di]][dl-d]

Build instructions:

    make compile  -> compile to bin directory (java 7 minimum)
    make document -> generate javadoc in doc directory
    make package  -> package for release
    make test     -> run tests

## **Changelog**

### 1.3.2
- fillArc() now fills arcs

### 1.3.1
- Added thread pool utility class

### 1.3
- Fixed regression
- Re-worked image loading

### 1.2.6

- Fixed super old regression (from rev 133)
- Added simple regex tester

### 1.2.5

- Allow for easier string printing

### 1.2.4

- Fixed regression

### 1.2.3

- Fixed memory issue
- Added version check to helper program

### 1.2.2

- Modified default naming scheme

### 1.2.1

- Hopefully fixed crash
- Crisp drawing default is now false

### 1.2

- Improved the helper program thing by:
  - adding a key press veiwer
  - adding a bexier curve editor
- Added crisp drawing
- Renamed all fields to allow for alphabetical sorting
  - **Not compatible with earlier versions**

### 1.1.2

- Fixed bug with drawing arcs

### 1.1.1

- Added debug info

### 1.1

- Added the ability to draw on the Cartesian plane

### 1.0

- Initial release

[dl-p]: https://bitbucket.org/phinet/dlibx/downloads/DLibX-current.zip
[dl-pi]: https://bitbucket.org/phinet/dlibx/downloads/download-package.png "Download Package"
[dl-j]: https://bitbucket.org/phinet/dlibx/downloads/DLibX.jar
[dl-ji]: https://bitbucket.org/phinet/dlibx/downloads/download-jar.png "Download Jar"
[dl-d]: https://bitbucket.org/phinet/dlibx/downloads/DLibX-Documentation.zip
[dl-di]: https://bitbucket.org/phinet/dlibx/downloads/download-documentation.png "Download Documentation"
