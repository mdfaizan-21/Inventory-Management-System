# JavaFX UI Migration Guide

## Overview
This document describes the JavaFX-based graphical user interface that has been added to the Inventory Management System. The original console-based application remains functional, and the JavaFX UI provides an alternative, modern interface.

## Architecture

### Package Structure
```
src/main/java/
├── ui/
│   ├── InventoryApp.java              # Main JavaFX Application Entry Point
│   └── controllers/
│       ├── BaseViewController.java     # Interface for view controllers
│       ├── LoginController.java        # Login screen controller
│       ├── RegisterController.java     # Registration controller
│       ├── DashboardController.java    # Main dashboard controller
│       └── views/                      # Individual view controllers
│           ├── DashboardHomeController.java
│           ├── ViewProductsController.java
│           ├── AddProductController.java
│           ├── UpdateProductController.java
│           ├── DeleteProductController.java
│           ├── SearchProductsController.java
│           ├── ReportsController.java
│           └── StockAlertsController.java

src/main/resources/
├── fxml/
│   ├── login.fxml                     # Login view
│   ├── register.fxml                  # Registration view
│   ├── dashboard.fxml                 # Main dashboard layout
│   └── views/                         # Sub-views
│       ├── dashboard_home.fxml
│       ├── view_products.fxml
│       ├── add_product.fxml
│       ├── update_product.fxml
│       ├── delete_product.fxml
│       ├── search_products.fxml
│       ├── reports.fxml
│       └── stock_alerts.fxml
└── css/
    └── styles.css                     # Application styling
```

## Features

### 1. Authentication System
- **Login Screen**: Username and password authentication
- **Registration**: New user registration with email verification
- **OTP Verification**: Email-based OTP verification for new accounts
- **Role-Based Access**: Separate interfaces for Admin and User roles

### 2. Dashboard
- **Statistics Cards**: Display total products, low stock items, and category count
- **Navigation Sidebar**: Easy access to all features
- **Menu Bar**: File operations (Import/Export CSV, Logout, Exit)
- **Status Bar**: Real-time clock and status messages
- **Recent Activity**: Table showing recently added/updated products

### 3. Product Management (Admin Only)

#### View Products
- Searchable table view of all products
- Real-time filtering by name, ID, or category
- Color-coded low stock indicators
- Product details view
- Export to CSV functionality

#### Add Product
- Form-based product creation
- Input validation (required fields, number formats)
- Category selection with predefined options
- Quantity spinner controls
- Price validation
- Threshold limit configuration

#### Update Product
- Search product by ID
- Pre-populated form with current values
- Update all product fields
- Validation on save
- Reset functionality

#### Delete Product
- Search product by ID
- Display product details before deletion
- Confirmation dialog
- Prevents accidental deletions

### 4. Search Products
- **By ID**: Direct product lookup
- **By Category**: Filter products by category
- **By Price Range**: Find products within specified price range
- **Show All**: Display complete inventory
- Results displayed in table format

### 5. Reports & Analytics
- **Generate CSV Reports**: Create timestamped inventory reports
- **Email Reports**: Send reports via email to current user
- **Report History**: View past report generation activities

### 6. Stock Alerts
- **Low Stock Monitoring**: Automatic detection of low stock items
- **Color-Coded Status**:
  - Red: Out of Stock
  - Orange: Low Stock (below threshold)
  - Yellow: Approaching threshold
- **Threshold-Based Alerts**: Customizable stock thresholds per product
- **Background Monitoring**: Admin users receive automated alerts

## User Roles

### Admin Role
Full access to all features:
- ✅ View all products
- ✅ Add new products
- ✅ Update existing products
- ✅ Delete products
- ✅ Search products
- ✅ Generate and email reports
- ✅ View stock alerts
- ✅ Import/Export CSV files
- ✅ Receive automated stock alert emails

### User Role
Read-only access:
- ✅ View all products
- ✅ Search products
- ✅ Generate reports
- ✅ View stock alerts
- ❌ Cannot add products
- ❌ Cannot update products
- ❌ Cannot delete products

## Running the Application

### Using Maven
```bash
# Run the JavaFX application
mvn clean javafx:run
```

### Using JAR (after building)
```bash
# Build the project
mvn clean package

# Run the JAR
java -jar target/Inventory-Management-System-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Direct JavaFX Run
```bash
# Compile and run
mvn clean compile
mvn javafx:run
```

## Dependencies

The following JavaFX dependencies have been added to `pom.xml`:

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.2</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>17.0.2</version>
</dependency>
```

## Configuration

### Main Class
The JavaFX main class is configured in the JavaFX Maven Plugin:
```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>ui.InventoryApp</mainClass>
    </configuration>
</plugin>
```

### Database Configuration
The application uses the same database configuration as the console application. Ensure your MySQL database is configured properly in the DAO implementations.

## UI/UX Features

### Modern Design
- Clean, professional interface
- Intuitive navigation
- Consistent color scheme
- Responsive layout

### Visual Feedback
- **Success**: Green indicators and messages
- **Error**: Red indicators and messages
- **Warning**: Yellow/Orange indicators
- **Info**: Blue indicators

### User Experience
- Form validation with immediate feedback
- Confirmation dialogs for destructive actions
- Loading indicators for long operations
- Helpful error messages
- Tooltips and labels for guidance

## CSS Styling

The application uses a custom CSS file (`styles.css`) with:
- Professional color palette (blues, greens, reds for status)
- Card-based layouts with shadows
- Hover effects on interactive elements
- Responsive table styling
- Custom button styles (primary, secondary)
- Form field styling with focus indicators

## Background Services

### Stock Alert Service (Admin Only)
- Runs automatically for admin users
- Checks stock levels every 10 minutes
- Sends email alerts for low stock items
- Non-blocking background thread

### Email Service
- Sends OTP verification emails
- Sends inventory reports
- Sends stock alert notifications
- Asynchronous operation to prevent UI blocking

## Error Handling

- Input validation on all forms
- Graceful error messages for database issues
- Network error handling for email operations
- File I/O error handling for CSV operations
- Exception catching with user-friendly alerts

## Future Enhancements

Potential improvements for the JavaFX UI:
1. Charts and graphs for analytics
2. Product images support
3. Advanced filtering options
4. Bulk product operations
5. User profile management
6. Print functionality for reports
7. Dashboard customization
8. Export to multiple formats (Excel, PDF)
9. Dark mode theme
10. Localization/i18n support

## Troubleshooting

### JavaFX Not Found
Ensure Java 17 is installed and JavaFX dependencies are downloaded:
```bash
mvn clean install
```

### FXML Load Errors
Check that FXML files are in the correct location under `src/main/resources/fxml/`

### CSS Not Applied
Verify that `styles.css` is in `src/main/resources/css/` and properly referenced in FXML files

### Database Connection Issues
Check database credentials and ensure MySQL is running

## Comparison: Console vs JavaFX UI

| Feature | Console UI | JavaFX UI |
|---------|-----------|-----------|
| User Interface | Text-based | Graphical |
| Navigation | Menu numbers | Sidebar buttons |
| Data Display | Text tables | TableView components |
| Input | Scanner prompts | Forms with validation |
| Visual Feedback | Emojis & text | Colors & dialogs |
| Multi-tasking | Single thread | Background threads |
| User Experience | Basic | Enhanced |
| Learning Curve | Low | Moderate |

## Conclusion

The JavaFX UI provides a modern, user-friendly interface while maintaining all the functionality of the original console application. Both interfaces can coexist, allowing users to choose their preferred method of interaction with the Inventory Management System.
