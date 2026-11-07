# Computer Science Capstone ePortfolio

Welcome to my computer science ePortfolio. I am Andrew Allen and this repository and my accompanying [GitHub Pages Site](https://peacefuldivide.github.io/ePortfolio/) showcases my professional development, technical growth, and final capstone project enhancements completed during my CS499 Capstone course at SNHU.

---

## Table of Contents
1. [ePortfolio](https://peacefuldivide.github.io/ePortfolio/)
2. [Code Review](#code-review)
3. [Enhancement Plan](#enhancement-plan)
4. [Capstone Artifact Enhancements](#capstone-artifact-enhancements) 
   - [Category 1: Software Engineering and Design](#category-1-software-engineering-and-design)
   - [Category 2: Algorithms and Data Structures](#category-2-algorithms-and-data-structures)
   - [Category 3: Databases](#category-3-databases)
5. [Beyond the Capstone: Kept](#beyond-the-capstone-kept)
6. [License & Copyright](#license--copyright)

---

## Live Portfolio
My interactive ePortfolio, containing my complete Professional Self-Assessment, video code review, and full artifact narratives, is hosted live at:
**[https://peacefuldivide.github.io/ePortfolio/](https://peacefuldivide.github.io/ePortfolio/)**

---

## Code Review

Embedded below is my video code review, analyzing the original architecture and codebase of my Android inventory tracking application. Conducting this comprehensive review allowed me to identify technical deficiencies and structure a formal enhancement plan targeting three primary areas of computer science: Software Engineering and Design, Algorithms and Data Structures, and Databases.

**Code Review Video:** 

[![Watch Code Review Video](https://img.youtube.com/vi/h_5UoD4-y0A/maxresdefault.jpg)](https://www.youtube.com/embed/h_5UoD4-y0A?si=Odv0ClMpQL69HZND)

---

## Enhancement Plan
For a comprehensive breakdown of project milestones, outcome alignments, and technical specifications created prior to development, view my [Enhancement Plan](https://github.com/PeacefulDivide/ePortfolio/blob/main/Enhancement%20Plan/CS%20499%20Enhancement%20Plan%20Andrew%20Allen.pdf).

---

## Capstone Artifact Enhancements

### Category 1: Software Engineering and Design
**Artifact:** Inventory Tracking Application

**Summary:** 
The original inventory app was created during my CS360 Mobile Architecture and Programming course. It allows users to manage their inventory items through raw SQLite CRUD operations. For this enhancement, I completely redesigned and rebuilt the app from scratch using Kotlin, adopting the Model-View-ViewModel (MVVM) design pattern and Jetpack navigation components. This involved migrating away from Java and the Model-View-Controller (MVC) design to a more modular structure that separates the user interface from the business logic, improving maintainability and testability. Key improvements included replacing activity-based structure with fragment-based, implementing proper state management through ViewModel, and configuring navigation using NavController. I prioritized the architectural overhaul over implementing as many features as I could. This ensured that the codebase remains clean and scalable for future upgrades and additions. 

**[View Source Code & Narrative](https://github.com/PeacefulDivide/ePortfolio/tree/main/Artifact%20-%20Software%20Engineering%20and%20Design)**

---

### Category 2: Algorithms and Data Structures
**Artifact Name:** Inventory Tracking Application

**Summary:**
The original inventory app did not contain any searching, sorting, or data aggregations. This enhancement changes that. I implemented dynamic multifield sorting using comparator-based approaches, achieving $O(n \log n)$ efficiency for organizing inventory items by name, price, and/or quantity. I also implemented real-time aggregation algorithms within the ViewModel, calculating total inventory value and low-stock item counts with linear $O(n)$ time complexity. Collection transformation pipelines were also added for filtering and view resettings, maintaining an immutable dataset to improve data consistency. These enhancements demonstrate algorithmic principles by replacing direct collection modification with elegant Kotlin collection operations, while ensuring the user interface updates through LiveData and ViewModel integration. These improvements really bring the app together providing data organization and real-time business insights.

**[View Source Code & Narrative](https://github.com/PeacefulDivide/ePortfolio/tree/main/Artifact%20-%20Algorithms%20and%20Data%20Structures)**

---

### Category 3: Databases
**Artifact Name:** Inventory Tracking Application

**Summary:** 
This enhancement transcended the original app's raw SQLite database implementation. During this enhancement I implemented a Room ORM database architecture, replacing direct access SQLite interactions with a structured MVVM pattern that separates UI, logic, and persistence layers. I defined type-safe data access objects like `InventoryDao`, abstracting database operations through a repository layer, using Kotlin coroutines for asynchronous operations, and implementing comprehensive unit tests `InventoryDaoTest` were created to validate data integrity. These database improvements not only improve code reliability and testability but also demonstrate how modern Android development practices can transform this inventory app into a more resilient and maintainable data management system that protects both performance and accuracy.

**[View Source Code & Narrative](https://github.com/PeacefulDivide/ePortfolio/tree/main/Artifact%20-%20Databases)**

---

## Beyond the Capstone: Kept

After the capstone, I kept building the inventory app. It is now **Kept: Inventory & Collections**, an Android app for tracking the things you own, from household belongings to collections such as vinyl records. It is available on [Google Play](https://play.google.com/store/apps/details?id=com.alttabtechnologies.kept). Its source code is not published here. Read more on the [live portfolio](https://peacefuldivide.github.io/ePortfolio/#kept).

**Highlights:**
- Kotlin, MVVM, fragment-based navigation, and Jetpack Compose for several screens.
- Room with hand-written database migrations (schema version 7) and no destructive fallback.
- Private by design: no accounts, ads, or analytics. Barcode scanning uses the on-device zxing-cpp library, and Google Play Billing is the only component that uses the network.
- A free tier plus an optional Pro upgrade through Google Play Billing.
- Barcode and serial number scanning, reminders, an optional biometric app lock, CSV and PDF export, and backup and restore.
- Unit and instrumented tests, R8 shrinking, and seven languages.

**[Kept Privacy Policy](https://peacefuldivide.github.io/kept-legal/)**

---

## License & Copyright

© 2026 Andrew Blaine Allen. All rights reserved.  
*This repository and its content (including source code, images, and documentation) are provided solely for educational and portfolio review purposes. Unauthorized copying, redistribution, or modification of any part of this project is strictly prohibited.*