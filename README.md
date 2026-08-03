# Computer Science Capstone ePortfolio

Welcome to my computer science ePortfolio. I am Andrew Allen and this repository and my accompanying GitHub Pages site showcase my professional development, technical growth, and final capstone project enhancements completed during my CS499 Capstone course at SNHU.

---

## Table of Contents
1. [Professional Self-Assessment](#professional-self-assessment)
2. [Code Review](#code-review-walkthrough)
3. [Capstone Artifact Enhancements](#capstone-artifact-enhancements)
   - [Category 1: Software Engineering and Design](#category-1-software-engineering-and-design)
   - [Category 2: Algorithms and Data Structures](#category-2-algorithms-and-data-structures)
   - [Category 3: Databases](#category-3-databases)

---

## Professional Self-Assessment
TODO

---

## Code Review

Embedded below is my video code review, analyzing the original state of my inventory tracking application. Analyzing the apps codebase enabled me to find areas of improvement in the three main categories for enhancment: Software engineering and design, algorithms and data structures, and databases.

**Code Review Video:** 

[![Watch Code Review Video](https://img.youtube.com/vi/YOUR_VIDEO_ID/0.jpg)]((https://www.youtube.com/embed/h_5UoD4-y0A?si=Odv0ClMpQL69HZND))

---

## Capstone Artifact Enhancements

### Category 1: Software Engineering and Design
**Artifact:** Inventory Tracking Application

**Summary:** 

The original inventory app was created during my CS360 Mobile Architect and Programming course. It allows users to manage their inventory items through raw SQLite CRUD operations. For this enhancment, I completely redesigned and rebuilt the app from scratch using Kotlin, adopting the Model-View-ViewModel (MVVM) design pattern and Jetpack navigation components. This involved migrating away from Java and the Model-View-Controller (MVC) design to a more modular structure that separates the user interface from the business logic, improving maintainability and testability. Key improvements included replacing activity-based structure with fragment-based, implementing proper state management through ViewModel, and configuring navigation using NavController. I prioritized the architectural overhaul over implementing as many features as I could. This ensured that the codebase remains clean and scalable for future upgrades and additions. 

**Full Narrative & Source Code:** TODO

---

### Category 2: Algorithms and Data Structures
**Artifact Name:** Inventory Tracking Application

**Summary:**

The original inventory app did not contain any searching, sorting, or data aggregations. This enhancement changes that. I implemented dynamic multifield sorting using comparator-based approaches, achieving O(n log n) efficiency for organizing inventory items by name, price, or quantity. I also implemented real-time aggregation algorithms within the ViewModel, calculating total inventory value and low-stock item counts with linear O(n) time complexity. Collection transformation pipelines were also added for filtering and view resettings, maintaining an immutable dataset to improve data consistency. These enhancements demonstrate algorithmic principles by replacing direct collection modification with elegant Kotlin collection operations, while ensuring the user interface updates through LiveData and ViewModel integration. These improvements really bring the app together providing data organization and real-time business insights.

**Full Narrative & Source Code:** TODO

---

### Category 3: Databases
**Artifact Name:** Inventory Tracking Application

**Summary:** 

TODO

**Full Narrative & Source Code:** TODO

---

*Hosted via GitHub Pages*