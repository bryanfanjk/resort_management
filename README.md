# resort_management

## Detailed Integration Documentation

This document describes only the main project located at:

```text
C:\Users\NYO\Desktop\resort_management
```

The separate Downloads project is not part of this project. Its VIP allocation
ideas were integrated into this application, but this project has its own
packages, entities, controllers, DAOs, and startup flow.

## 1. System Purpose

This is a console-based resort hotel management system. It represents a hotel
that is already operating when the program starts. The initial state therefore
contains rooms, active reservations, historical checked-out reservations,
standard waiting customers, VIP waiting customers, and valid VIP codes.

The application supports:

1. Adding a walk-in reservation.
2. Checking out a customer from an occupied room.
3. Assigning a room to one waiting customer.
4. Viewing room status.
5. Viewing reservation and waiting-list reports.

The entry point is `resort.management.Main`. It opens `boundary.MainMenu`,
which opens `boundary.HotelCheckInUI` for the hotel workflow.

## 2. Five Logical Data Sets

The runtime system keeps these logical groups separate:

### Standard waiting customers

Stored in the existing standard `List<WaitingCustomer>` owned by
`HotelController`. These customers do not have VIP priority.

### VIP waiting customers

Stored in a separate `VipList<WaitingCustomer>` owned by
`VipAllocationController`. These customers are always checked before standard
waiting customers during allocation.

### Approved reservations

Stored as `List<Reservation>` in `HotelController`. These are customers who
already have rooms and are currently staying at the resort.

### Checked-out reservations

Stored as another `List<Reservation>`. These are historical records for
customers who previously occupied rooms and have already checked out.

### VIP verification codes

Stored as a `String[]` in `VipAllocationController`, loaded from
`VipCodeData`. This is only a verification source. It is not the VIP waiting
list and it does not contain customer records.

The customer lifecycle is:

```text
Standard walk-in -> standard waiting list -> approved reservation
					  -> checked-out reservation

VIP walk-in     -> VIP waiting list -> approved reservation
					  -> checked-out reservation
```

## 3. Files Added

### `src/dao/StandardWaitingCustomerData.java`

This DAO contains the hardcoded standard waiting customers that exist at
startup. It follows the same factory style as `RoomData`:

```java
public static WaitingCustomer[] createNew()
```

The factory creates each customer with `CustomerType.STANDARD`, wraps the
customer in a `WaitingCustomer`, and returns an array. Waiting positions are
assigned by `HotelController` when the array is loaded.

The private helper keeps repeated construction in one place and prevents
customer type or requested-room information from being accidentally omitted.

### `src/dao/VipWaitingCustomerData.java`

This DAO contains the separate hardcoded VIP waiting customers. It also uses:

```java
public static WaitingCustomer[] createNew()
```

Every customer created here receives `CustomerType.VIP`. The controller loads
these records into the VIP list, never into the standard waiting list.

This separate factory is important because the two waiting groups have
different allocation priority.

### `src/dao/ApprovedReservationData.java`

This DAO contains hardcoded active reservations that exist when the system
starts. It uses:

```java
public static Reservation[] createNew(Room[] rooms)
```

The room array is passed in so each reservation can reference an actual room
created by `RoomData`. The factory finds rooms by room number, constructs
customers with explicit customer types, and returns `Reservation` objects.

These are active records, so `HotelController` marks their rooms as occupied
when loading them.

### `src/dao/CheckedOutReservationData.java`

This DAO contains historical reservations for customers who already checked
out before the program started. It uses:

```java
public static Reservation[] createNew(Room[] rooms)
```

The customers are constructed with both check-in and check-out dates. The
controller loads these records into the completed-reservation list but does
not mark their rooms occupied, because the records are historical.

### `src/control/VipAllocationController.java`

This controller owns the VIP waiting list and VIP verification behavior. It
contains:

- `VipList<WaitingCustomer> vipList`
- Valid codes loaded from `VipCodeData`
- `isValidVipCode(String code)`
- Methods to add, read, remove, and count VIP waiting customers
- `peekNextVip()` and `hasWaitingVip()` helpers
- `getVipList()` for the main controller and reporting layer

Keeping this behavior in its own controller prevents VIP verification and VIP
list operations from being scattered through the UI.

## 4. Files Modified

### `src/entity/Customer.java`

`Customer` now requires a `CustomerType` argument in both constructors.

The two constructors are:

```java
Customer(String customerName, int pax, String checkInDate,
			int nightsStayed, CustomerType customerType)
```

and:

```java
Customer(String customerName, int pax, String checkInDate,
			String checkOutDate, int nightsStayed,
			CustomerType customerType)
```

The first constructor delegates to the second with a null checkout date. The
second is used for checked-out seed records that already have a checkout date.

The class now stores:

```java
private CustomerType customerType;
```

and provides a getter and setter. This is necessary because the customer must
remain visibly standard or VIP while moving between waiting and reservation
collections.

The unfinished constructor that accepted a VIP code was removed. A code is
input used for verification; it is not permanent customer data. The verified
result becomes the customer's `CustomerType`.

### `src/entity/WaitingCustomer.java`

`WaitingCustomer` still extends `Customer` and adds:

- Requested room type
- Waiting position

Its constructor now copies `customer.getCustomerType()`. This prevents a VIP
customer from silently becoming standard when converted into a waiting record.

### `src/control/HotelController.java`

This is the main integration point. Its constructor now loads data in this
order:

```text
1. RoomData.createRooms()
2. ApprovedReservationData.createNew(rooms)
3. CheckedOutReservationData.createNew(rooms)
4. StandardWaitingCustomerData.createNew()
5. VipWaitingCustomerData.createNew()
```

The old controller methods containing long hardcoded customer names, dates,
room numbers, and reservation records were removed. The controller now loads
those records from DAO factories.

The controller also changed to:

- Keep standard waiting customers in `waitingCustomers`.
- Use `VipAllocationController` for VIP waiting customers.
- Check duplicate names across active reservations, checked-out reservations,
  standard waiting customers, and VIP waiting customers.
- Accept a VIP code for new walk-in reservations.
- Route valid codes to the VIP waiting list.
- Route blank or invalid codes to the standard waiting list.
- Scan VIP customers before standard customers during room allocation.
- Remove a selected VIP or standard customer by index.
- Create a reservation and add it to active reservations after assignment.
- Move reservations from active to completed during checkout.
- Expose both waiting lists for other layers.

### `src/boundary/HotelCheckInUI.java`

The walk-in screen now asks:

```text
Enter VIP code, or press Enter for standard customer:
```

The UI collects the raw code and passes it to `HotelController`. The UI does
not decide whether the code is valid. The controller performs verification and
returns a `WaitingCustomer` whose type identifies the destination list.

The UI displays either a VIP waiting-list message or a standard waiting-list
message.

### `src/adt/List.java` and `src/adt/VipList.java`

Both list implementations support indexed operations needed by room
allocation:

- Add at the end
- Add at an index
- Read by index
- Replace by index
- Remove by index
- Check containment
- Check emptiness
- Read size

Indexed removal is important because the first waiting customer may not fit
the available room. The controller can skip that customer and remove a later
customer from the middle of the same list when a suitable room is found.

`List` is used for standard waiting customers and reservations. `VipList` is
used for VIP waiting customers. They remain separate objects even though they
support the same kinds of list operations.

### `src/dao/RoomData.java`

This remains the source of hardcoded room records. Its array order is also the
room tie-breaking order. If multiple rooms satisfy one customer, the first
suitable available room in this array is selected.

### `src/dao/VipCodeData.java`

This remains the source of valid codes such as `VIP100`, `VIP200`, and
`VIP300`. It contains codes only, not VIP customers.

### `src/boundary/GenerateReportUI.java`

The existing report behavior still combines active and completed reservations
and supports room-type and active/checked-out filters. The controller now
exposes the VIP waiting list, but the current waiting-list display still
prints the standard list only. A separate VIP report section remains a small
follow-up UI task.

## 5. Files Removed

### `src/dao/WaitingCustomerData.java`

This combined standard/VIP factory was removed. It was replaced by:

```text
StandardWaitingCustomerData.java
VipWaitingCustomerData.java
```

### `src/dao/ReservationData.java`

This combined active/completed reservation factory was removed. It was
replaced by:

```text
ApprovedReservationData.java
CheckedOutReservationData.java
```

The new files make the source data match the separate runtime collections.

## 6. Startup Flow

The complete startup sequence is:

1. `Main.main()` creates `MainMenu`.
2. `MainMenu.menu()` displays the module menu.
3. Selecting Module 1 creates `HotelCheckInUI`.
4. `HotelCheckInUI` creates `HotelController`.
5. `HotelController` creates rooms from `RoomData`.
6. Approved reservations are loaded from DAO and their rooms become occupied.
7. Checked-out reservations are loaded as historical records.
8. Standard waiting customers are loaded and given positions.
9. VIP waiting customers are loaded into `VipAllocationController` and given
	positions.
10. The hotel menu is displayed with all these records already in memory.

Only active reservations make rooms occupied during startup. Historical
checked-out reservations do not make their old rooms occupied.

## 7. Walk-In Registration Flow

When a staff member adds a walk-in:

1. The UI reads the name.
2. It rejects an empty name.
3. It checks the name against every current and historical customer group.
4. It reads guest count, date, nights, and requested room type.
5. It reads an optional VIP code.
6. It creates a customer with an initial type.
7. It sends the customer, requested room type, and code to the controller.
8. The controller trims the code.
9. `VipAllocationController` compares it with `VipCodeData`.
10. A matching code changes the customer type to `VIP`.
11. A blank or invalid code makes the customer `STANDARD`.
12. The customer is wrapped as a `WaitingCustomer`.
13. VIP records go to the VIP waiting list.
14. Standard records go to the existing standard waiting list.

No room is assigned during registration. Registration only places the customer
into the correct waiting list.

## 8. Room Allocation Flow

One press of `Check In Customer` attempts to assign exactly one room.

### VIP scan

1. Start at the first VIP waiting customer.
2. Search rooms in `RoomData` order.
3. Require the room to be available.
4. Require the room type to match.
5. Require capacity to be at least the customer's party size.
6. If no room matches, record the customer as skipped and continue.
7. If a room matches, remove that VIP customer by index.
8. Resequence the remaining VIP positions.
9. Mark the room occupied.
10. Create a `Reservation`.
11. Add it to active reservations.
12. Return success immediately.

The standard list is never checked after an assignable VIP customer is found.

### Standard scan

The standard list is reached only after every VIP customer has been checked and
none can currently use an available room.

1. Start at the first standard waiting customer.
2. Search rooms in `RoomData` order.
3. Check availability, type, and capacity.
4. Skip unsuitable customers.
5. Remove the first suitable customer by index.
6. Resequence the remaining standard positions.
7. Mark the room occupied.
8. Create and store the approved reservation.
9. Return success.

This prevents the VIP list from blocking the whole system when no VIP customer
can currently be assigned, while still preserving VIP priority.

### Middle removal

The customer at index zero is not guaranteed to fit the first available room.
The controller therefore scans the entire priority list. If a later customer
fits, that later customer is removed from the middle of the list. The skipped
customers remain in their original lists.

### Room tie-breaking

`findAvailableRoom()` scans the room array from beginning to end and returns
the first suitable room. Therefore, if two rooms are suitable, the room that
appears earlier in `RoomData.createRooms()` is selected.

### Failure behavior

Allocation succeeds whenever at least one waiting customer can use an available
room. It returns an unsuccessful `AssignmentResult` only when no VIP or
standard waiting customer can use any available room. The result also retains
the customers skipped during the attempt so the UI can explain the outcome.

## 9. Checkout Flow

When staff select checkout:

1. The UI reads a room number and checkout date.
2. The controller finds the room.
3. It confirms the room is occupied.
4. It finds the matching reservation in `activeReservations`.
5. It removes that reservation from the active list.
6. It sets the customer's checkout date.
7. It adds the same reservation to `completedReservations`.
8. It marks the room available.

The customer is not recreated or deleted. The reservation changes state by
moving collections:

```text
activeReservations -> completedReservations
```

## 10. Reports and Room Status

The room-status screen displays each room's number, type, capacity, and current
status.

The reservation report combines active and completed reservations. It sorts by
check-in date, then room capacity, then nights stayed. It can filter by room
type and by active versus checked-out status.

The controller exposes both standard and VIP waiting lists. The current
`GenerateReportUI` implementation displays the standard waiting list, while a
separate VIP section still needs to be added to the UI to show both groups.

## 11. Layer Responsibilities

### DAO

DAO classes contain hardcoded seed data and factory methods. They do not read
console input or decide allocation priority.

### Entity

Entities hold data only:

- `Customer`: identity, party size, dates, and customer type.
- `WaitingCustomer`: customer plus requested room and waiting position.
- `Reservation`: customer-room relationship.
- `Room`: room details and availability/status.
- `AssignmentResult`: result of one allocation attempt.

### Control

`HotelController` manages the hotel workflow. `VipAllocationController` owns
VIP-specific verification and waiting-list operations.

### Boundary

`MainMenu`, `HotelCheckInUI`, and `GenerateReportUI` handle menu interaction,
staff input, and formatted output.

## 12. Validation

The full Java source tree was compiled directly with `javac` after the DAO
separation and controller integration. Compilation completed without errors.
The compiler reported only an unchecked generic-array warning in
`adt/VipList.java`.

Apache Ant was not available on the environment's `PATH`, so direct `javac`
was used for validation instead of `ant compile`.

## 13. Remaining Follow-Up

The core integration is implemented. The main remaining UI improvement is to
update `GenerateReportUI` so the waiting report prints two sections:

```text
VIP Waiting List
Standard Waiting List
```

The controller already exposes the VIP list for this purpose. The main menu
also retains placeholder entries for modules that are not implemented yet;
those do not affect the hotel check-in, allocation, checkout, or reservation
workflow documented above.
