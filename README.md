# Health-First-PIMS
A pharmacy inventory system
HealthFirst Pharmacy Inventory Management System
README / User Manual
System Specifications
* Operating system: Windows 11 Home Single Language
* System type: 64-bit OS, x64-based processor
* Processor: AMD Ryzen 3 (2.60GHz)
* IDE used: Apache NetBeans IDE 28
Program functionality
* Java Swing desktop application for managing a pharmacy's medicine
* inventory, suppliers, cashier accounts, sales and reports
* Supports role-based access between two user types: Admin and Cashier
* Admin users can manage medicines, suppliers, cashier accounts, and
* view business reports
* Cashier users can process sales through a Point of Sale screen and
* check current stock levels
* All data is stored in and retrieved from a MySQL database, so
* nothing is lost when the program is closed and reopened
Program overview
* On startup, the Login screen appears and asks for a username and
* password
* Admin accounts are taken to the Admin Dashboard, which has tabs for
* Home, Manage Medicines, Manage Suppliers, Manage Users and Reports
* Cashier accounts are taken to the Cashier Dashboard, which has tabs
* for Point of Sale and Stock Check
* Every add, update, delete and sale made in the program is written
* straight to the MySQL database in the background
Class Descriptions
All source files are organised into four packages under src\:
* Gui     - every screen/window in the application (LoginFrame,
AdminDashboard, CashierDashboard, MedicinePanel,
SupplierPanel, UserPanel, ReportsPanel, StockCheckPanel,
POSPanel, BillFrame, and the AppColors theme file)
* Dao        - Data Access Object classes that talk to the database
(MedicineDAO, SupplierDAO, UserDAO, SaleDAO, ReportDAO)
*  Models     - plain data classes representing one real-world "thing" each (Medicine, Supplier, User, Sale, SaleItem)
* Database   - DBConnection, which opens the connection to the MySQL
database used by every DAO class
* Main.java  - the application's entry point; opens the Login screen
Tools and software needed to set this project up
* Apache NetBeans IDE 28 (or newer) - the IDE the project was built in
* JDK 25 (Java Development Kit) - the Java version this project is compiled against
* MySQL Server 8.x - to host the pharmacy_inventory_db database
* MySQL Workbench (optional, but makes importing the database script easier if you are not comfortable with the command line)
* An internet connection is NOT required to run the program, but is needed the first time to install the above software
Setting up the system on your own device
1. Install Apache NetBeans IDE 28 and JDK 25, if not already installed.
2. Install MySQL Server 8.x and make sure it is running (the default
   port 3306 must be free).
3. Import the database:
* Open MySQL Workbench (or the mysql command line client)
* Run the script found at:
> "Health First Pharamacy Database.sql" (project root folder)
> This script creates the pharmacy_inventory_db database, all of
* Its tables, and a small set of sample/test data automatically , you do not need to create any tables by hand.
4. Point the program at your database:
* Open src\database\DBConnection.java in NetBeans
* Update the USERNAME and PASSWORD fields to match your own local
* MySQL login (the URL field can stay as �jdbc:mysql://localhost:3306/pharmacy_inventory_db� unless your
* MySQL server is on a different machine or port)
5. Open the project in NetBeans:
* File > Open Project > select the "Health First_Pharmacy" folder
6. Build and run (see "How to run the program" below).
How to run the program
There are three ways to run this program, depending on what you have
available on your device:
Option A - From NetBeans 
   1. Open the project in NetBeans as described above
   2. Click the green "Run" button, or press F6
   3. The Login screen will open
Option B - From the runnable .jar file (no IDE needed, JDK needed)
   1. Open a Command Prompt
   2. Run: java -jar "dist\Health_First_Pharmacy.jar"
   3. The Login screen will open
Option C - From the standalone Windows executable 
   1. Go to the dist folder
   2. Double-click WamashuduNetshisaulu_pims.exe
   3. The Login screen will open
Where to find things
* Source code (.java files): src\ folder, split into the Gui, Dao, Models and database packages described above
* Runnable jar file: dist\Health_First_Pharmacy.jar
* Standalone Windows executable: dist\WamashuduNetshisaulu_pims.exe
* Database setup script: "Health First Pharamacy Database.sql" (project root folder)
How to test the system
The database script seeds two ready-made accounts so the system can
be tested immediately without creating any data by hand: 
Role
Username
Password
Admin
admin
admin123
Cashier
cashier
cash123

