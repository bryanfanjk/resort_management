================================================================================
TARUMT RESORTS - ROOM RESERVATION & OPTIMIZATION SYSTEM PROTOTYPE
================================================================================
Course: BMCS2063 Data Structures and Algorithms
Assignment Specification Year: 202605

Student Name: Admin (Integrator)
IDE: NetBeans IDE (or running via CLI using Ant)
Project Structure: Standard NetBeans Ant-based Project

--------------------------------------------------------------------------------
1. PROJECT PACKAGES & DIRECTORY LAYOUT
--------------------------------------------------------------------------------
TARUMT_Resorts/
├── build.xml                           # Ant build script
├── manifest.mf                         # JAR manifest (specifies Main-Class)
├── README.txt                          # This documentation file
├── nbproject/                          # NetBeans metadata files
│   ├── project.xml
│   └── project.properties
└── src/                                # Source packages
    └── tarumtresort/
        ├── Main.java                   # Main driver & Seeder
        ├── adt/                        # Custom Abstract Data Types (No JCF used)
        │   ├── ListInterface.java      # List specification
        │   ├── ArrayList.java          # Array-based list implementation
        │   ├── QueueInterface.java     # FIFO Queue specification
        │   ├── LinkedQueue.java        # Linked Node-based queue implementation
        │   ├── PriorityQueueInterface.java # Priority Queue specification
        │   ├── HeapPriorityQueue.java  # Array-based binary max-heap implementation
        │   ├── StackInterface.java     # LIFO Stack specification
        │   ├── LinkedStack.java        # Linked Node-based stack implementation
        │   ├── BSTInterface.java       # Binary Search Tree specification
        │   └── BinarySearchTree.java   # Node-based Binary Search Tree implementation
        ├── entity/                     # Entity Layer (Pure Data Models)
        │   ├── Guest.java              # Guest profile & Loyalty Tiers (Standard-Elite)
        │   ├── Room.java               # Room properties & cleaning statuses
        │   ├── Booking.java            # Booking records (implements Comparable)
        │   └── HousekeepingLog.java    # Cleaning action log representation
        ├── control/                    # Control Layer (Business logic orchestrators)
        │   ├── BookingController.java  # Standard walk-in & FIFO logic
        │   ├── VIPAllocationController.java # VIP tier Priority Queue & Re-heap logic
        │   ├── HousekeepingController.java # Room status cleaning & Stack rollback logic
        │   ├── FrontDeskController.java # Guest BST lookup & confirmation range query logic
        │   ├── LoyaltyController.java  # Points accrual, upgrades & rewards redemptions
        │   └── ReportController.java   # Custom filters and Merge Sort reporting logic
        ├── boundary/                   # Boundary Layer (Input, menus, ANSI graphics)
        │   ├── MainMenuUI.java         # Router UI loop
        │   ├── BookingUI.java          # Standard booking UI boundary
        │   ├── VIPAllocationUI.java    # Priority room allocation UI boundary
        │   ├── HousekeepingUI.java     # Housekeeping logs & rollback UI boundary
        │   ├── FrontDeskUI.java        # Confirmation search & range query UI boundary
        │   ├── LoyaltyUI.java          # Loyalty rewards & promotions UI boundary
        │   └── ReportUI.java           # Management report UI boundary
        └── util/                       # Utilities
            ├── Sorter.java             # Explicit generic Merge Sort algorithm
            └── InputUtil.java          # Scanner helpers with validation loops

--------------------------------------------------------------------------------
2. DATA STRUCTURE ALIGNMENT & ORIGINALITY FEATURES
--------------------------------------------------------------------------------
To satisfy the grading criteria (Ideal 8-10 Marks) for "ADT Specification" and
"Collection ADT implementation - originality", the following have been implemented:

- FIFO Queue (Walk-ins & Standard Bookings):
  Specifies FIFO behavior. We've added an original non-trivial operation:
  `moveToFront(T entry)` which expedites standard registrations (e.g. VIP bump-up).

- Binary Max-Heap Priority Queue (VIP Room Allocation):
  Specifies max-priority based on a composite comparison: primary on Loyalty Tier Rank
  (Elite > Diamond > Platinum > Gold > Silver > Standard), and secondary on booking index
  (chronological FIFO tie-breaking). Includes original operations:
  `changePriority(T oldEntry, T newEntry)` and `remove(T entry)` to dynamically change a
  pending guest's queue priority (re-heaps the underlying array tree).

- LIFO Stack (Housekeeping Log Rollback):
  Tracks room cleaning history. Includes an original operation:
  `popMany(int count)` which allows batch undo/rollback of several steps at once.

- Binary Search Tree (Front-Desk Searching):
  Enables O(log n) lookup of guests by confirmation codes. Includes an original operation:
  `getInOrderRange(K startKey, K endKey)` which executes range queries across nodes in O(log n + k) time.

- Generic Merge Sort (Sorter Utility):
  An explicit generic sorting algorithm implemented from scratch that sorts list entities
  efficiently in O(n log n) comparisons using custom Comparators.

--------------------------------------------------------------------------------
3. HOW TO BUILD AND RUN THE APPLICATION
--------------------------------------------------------------------------------

Option A: Open in NetBeans (Recommended)
1. Launch NetBeans IDE.
2. Go to File -> Open Project.
3. Select the folder "TARUMT_Resorts" (the workspace folder containing this README).
4. NetBeans will detect it as a standard Java SE application project.
5. Click "Run" or press F6.

Option B: Run from Command Line (using Apache Ant)
If you have Apache Ant installed, run:
  ant compile
  ant run

Option C: Run via standard Java tools (CLI)
1. Open PowerShell or Command Prompt.
2. Navigate to the project root directory.
3. Compile all source files into a build directory:
   mkdir build\classes
   javac -d build\classes src\tarumtresort\Main.java src\tarumtresort\adt\*.java src\tarumtresort\entity\*.java src\tarumtresort\control\*.java src\tarumtresort\boundary\*.java src\tarumtresort\util\*.java
4. Run the compiled classes:
   java -cp build\classes tarumtresort.Main

================================================================================
