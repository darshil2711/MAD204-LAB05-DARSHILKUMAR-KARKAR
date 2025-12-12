# MAD204 - Lab 5: Media Favorites App

**Course:** MAD204-01 Java Development for MA  
**Lab:** 5  
**Student Name:** [Darshilkumar Karkar]  
**Student ID:** [A00203357]  

## Overview
In this lab, you will combine media handling (images & videos) with persistent storage (SQLite/Room) and JSON conversion (GSON). You will build a mini Media Library App that allows users to pick media, mark favorites, save them in a database, and export/import data as JSON.

## Scenario
You will create a Media Favorites App.
* Users can pick images/videos from the device gallery.
* Media can be viewed inside the app (ImageView or VideoView).
* Users can mark media as favorites, which will be stored in an SQLite/Room database.
* The favorites list is displayed in a RecyclerView.
* Favorites can be exported to JSON using GSON and imported back into the database.

## Learning Targets
* Work with Intents to pick media from gallery.
* Display images and videos in the app.
* Use RecyclerView to list favorite media.
* Store favorites persistently using Room (Entity, DAO, Database).
* Convert objects to/from JSON using GSON.
* Practice exporting and importing data in Android.

## Requirements

### Part A: Project Setup
* Create a new Android project named `Lab5MediaFavoritesApp`.
* Add dependencies in build.gradle:
    * Room (room-runtime, room-compiler).
    * GSON (com.google.code.gson:gson:2.11.0).

### Part B: Picking Media
* Use `ActivityResultContracts.GetContent` to pick a single image/video.
* Display selected media in an ImageView (for images) or VideoView (for videos).
* Implement GetMultipleContents for selecting multiple items.

### Part C: Favorites with Room
* Create an Entity class `FavoriteMedia` with fields: id, uri, type (image/video).
* Create a DAO (`FavoriteDao`) with methods:
    * `insert(FavoriteMedia media)`
    * `getAllFavorites()`
    * `delete(FavoriteMedia media)`
* Create a Room database class (`FavoritesDatabase`).
* When user clicks “Add to Favorites”, insert media into database.
* Show all favorites in a RecyclerView.

### Part D: Export/Import with GSON
* **Export:** Retrieve all favorites from database, convert list to JSON (`gson.toJson(list)`), and log or save it.
* **Import:** Take JSON (hardcoded or loaded), parse into objects (`gson.fromJson`), and insert into database.
* Verify by reloading RecyclerView.

### Part E: Additional Features
* Allow deleting favorites from RecyclerView.
* Add Snackbar “UNDO” when deleting.
* Save the last opened media URI in SharedPreferences and reload on app restart.

## Documentation
When you submit your Java program, include proper documentation. Documentation is part of programming best practices and will count toward your grade.

* **File Header** (at the very top of your .java & .xml file)
    * Include:
        * Course code and lab number
        * Your full name and Student ID
        * Date of Submission
        * A short description of what program does.
* **Class and Method Comments**
    * Use `/** .... **/` above each class and method to describe its purpose.
    * Mention parameters and return values.
* **Inline Comments**
    * Use `//` to explain tricky or important lines.
    * Do not comment every line – just enough to make logic clear.

## Submission
All work for this lab must be submitted through GitHub. You will practice both coding and professional collaboration workflows.

1. **Create a Repository**
    * Go to Github and create a new public repository.
    * Name it: `MAD204-LAB05-YOURNAME`
    * Add a README.md with:
        * Lab title and your name / ID
        * A short description of the project

2. **Code & Documentation**
    * Add your Main.java and Student.java file.
    * Include full documentation:
        * File header (course, lab, your name, description)
        * Method/class Javadoc comments
        * Inline comments for tricky logic.

3. **Commit Requirements**
    * You must have at least 5 commits.
    * Commits should be meaningful and descriptive (not “update” or “fix”).

4. **Pull Request Requirements**
    * You must create at least 3 Pull Requests (PRs), each with a clear title and description.
    * Each PR should represent a logical feature or change. For example:
        * Add Student class and constructors.
        * Implement Gradebook menu and input handling.
        * Add utilities (operator demo, type casting, recursion).
    * Even if you are working alone, you can:
        * Create a new branch (e.g., `feature-student-class`)
        * Push changes
        * Open a PR into main
        * Merge it after review (self-review allowed in this case).

5. **Final Submission**
    * Push your final version to Github.
    * Ensure your repo has:
        * Main.java with complete documentation
        * At least 5 meaningful commits.
        * At least 3 merged pull requests.
        * A README.md explaining your project.

6. **What to Submit to Instructor**
    * Submit the GitHub Repository link.
    * Make sure the repo is public.

## Marking Rubric (25 points)
* Media picker implementation – 4 marks
* Display media (ImageView/VideoView) – 3 marks
* Room Database setup (Entity, DAO, Database) – 5 marks
* RecyclerView favorites list – 4 marks
* GSON export/import – 5 marks
* Documentation (headers, comments) – 2 marks
* GitHub workflow (commits + PRs + README) – 2 marks
