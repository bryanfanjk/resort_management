RESORT MANAGEMENT SYSTEM
=========================

Project: TARUMT  Resorts, a luxury hospitality chain.
Main class: resort.management.Main

This is a Java console application for managing resort reservations,
room allocation, VIP priority allocation, checkout, housekeeping,
and front-desk services.

REQUIREMENTS
------------

- Java Development Kit (JDK) installed
- Apache NetBeans installed
- The project opened as a NetBeans project

RUNNING THE APPLICATION WITH NETBEANS
-------------------------------------

Download the folder and unzip it then 

1. Open Apache NetBeans.
2. Select File > Open Project.
3. Open this project folder:

   C:\Users\NYO\Desktop\resort_management

4. In the Projects panel, right-click resort_management.
5. Select Clean and Build.
6. Right-click the project again.
7. Select Run.

The application starts from:

   src/resort/management/Main.java

The main class starts boundary.MainMenu.

MAIN MENU
---------

When the application starts, the following modules are available:

1. Module 1 Walk-In Registrations and Standard Booking Procedure
2. Module 2 VIP and Loyalty Tier Priority Room Allocation
3. Module 3 Housekeeping and Task Log
4. Module 4 Front-Desk Service and Billing
0. Exit

MODULE 1
--------

Module 1 provides the general hotel workflow:

- Add a walk-in reservation
- Check out a customer
- Check in a waiting customer
- View room status
- View general reservation reports
- View the standard waiting list report

A walk-in customer may enter a VIP code. A valid code routes the customer
to the VIP waiting list. A blank or invalid code routes the customer to the
standard waiting list.

MODULE 2
--------

Select option 2 from the main menu to open the VIP allocation module.

The Module 2 menu provides:

1. VIP Customer Summary Report
2. VIP Demand vs. Room Availability Summary
3. VIP Reservation History Report
4. Back to Main Menu

VIP Customer Summary Report supports:

- Filtering by room type
- Searching by customer name
- Sorting by waiting position
- Sorting by customer name
- Sorting by number of guests
- Sorting by nights stayed

VIP Demand vs. Room Availability Summary compares the number of VIPs waiting
for each room type with the number of available rooms. It reports whether:

- Demand exceeds supply
- Supply and demand are balanced
- Supply exceeds demand

VIP Reservation History Report displays VIP customers who have already been
served. It can be filtered by:

- Room type
- Active VIP customers
- Checked-out VIP customers
- All VIP customers

MODULE 3
--------

Module 3 provides housekeeping functions, including room housekeeping status,
cleaning actions, supervisor approval, rollback actions, and housekeeping
reports. Login may be required by the module.

MODULE 4
--------

Module 4 provides front-desk services and billing functions.

INITIAL SYSTEM DATA
-------------------

The application starts with pre-populated data, including:

- Rooms
- Active reservations
- Checked-out reservations
- Standard waiting customers
- VIP waiting customers
- Valid VIP verification codes

The hardcoded data is stored in the dao package.

ROOM ALLOCATION RULES
---------------------

1. VIP waiting customers are checked first.
2. VIP customers are examined in VIP waiting-list order.
3. A customer is skipped when no suitable room is available.
4. The system continues checking later customers in the same list.
5. The first suitable room in RoomData order is selected.
6. Standard customers are checked only when no VIP customer can currently
   be assigned a room.
7. An assigned customer is removed from the relevant waiting list.
8. The new reservation is added to the active reservation list.
9. The room is marked occupied.

A suitable room must:

- Be available
- Match the requested room type
- Have enough capacity for the customer's party

CHECKOUT RULES
--------------

1. Select Check Out from Module 1.
2. Enter the occupied room number.
3. Enter the checkout date.
4. The matching reservation is removed from the active reservation list.
5. The checkout date is stored in the customer record.
6. The reservation is moved to the checked-out reservation list.
7. The room is made available.
8. Housekeeping status is updated when applicable.


