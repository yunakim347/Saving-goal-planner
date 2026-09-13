# Saving Goal Planner

## What problems does your application solve?
Saving Goal Planner is a text-based Java application that helps users organise their personal finances in a more structured way. The program allows users to create categories, add subcategories, record income and expense transactions, and set saving goals linked to specific categories. This helps users organise financial records more clearly and track their finances in a structured way.

A major feature of the application is its recursive category structure. Instead of storing all financial records in one flat list, users can build nested categories such as Savings > Travel > Flights. This makes the application more practical for personal finance management. The application also calculates balances by including all nested subcategories, which helps users understand both detailed and overall financial positions.

In addition, the application supports file input and output, so users can save their financial data to a file and load it again later. This means records do not disappear after the program closes.

## A description of the structure of your program
The program is organised into several classes with different responsibilities. `Main` controls the menu system and user interaction. `FinanceManager` manages the top-level categories and saving goals, and handles file saving and loading. `Category` stores transactions and subcategories, which creates the recursive data structure used in the application. `Transaction` represents a single income or expense record. `SavingGoal` stores a saving target and links it to a specific category.

JUnit test cases were also written to check important functions such as balance calculation, recursive category searching, total balance calculation, saving goal progress, and file saving/loading.

## Clear instructions on how to run your application in bullet points
- Open the project in VS Code or another Java IDE.
- Make sure all `.java` files are inside the `src` folder.
- Make sure the JUnit jar file is stored in the `lib` folder.
- Run `Main.java`.
- Follow the menu shown in the terminal to add categories, transactions, subcategories, and saving goals.
- Use the save option to write data to a file and the load option to restore saved data.
- Run `CategoryTest.java` if you want to execute the JUnit tests.