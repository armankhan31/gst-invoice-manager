# GST Invoice Manager

A Spring Boot REST API project for managing customers and GST invoices. It allows users to create customers, generate invoices with multiple items, calculate CGST and SGST automatically, update invoice status, delete invoices, and view total revenue.

This project is designed as a first-year MCA portfolio project using Java, Spring Boot, Spring Data JPA, and MySQL.

## Project Overview

The GST Invoice Manager solves a common business problem: creating invoices for customers and calculating GST totals accurately. Instead of manually calculating item totals, subtotal, CGST, SGST, and grand total, the application calculates these values in the service layer.

Main features:

- Create and view customers
- Create invoices for customers
- Add multiple items to an invoice
- Auto-generate invoice numbers like `INV-001`, `INV-002`
- Calculate subtotal, CGST, SGST, and grand total
- Update invoice status as `PAID`, `UNPAID`, or `CANCELLED`
- Delete invoices
- View total revenue

## Tech Stack

| Technology | Purpose |
| --- | --- |
| Java 17/21 | Main programming language |
| Spring Boot 3.x | Creates and runs the backend application quickly |
| Spring Web | Builds REST APIs |
| Spring Data JPA | Simplifies database operations |
| Hibernate | ORM used by Spring Data JPA |
| MySQL | Stores customers, invoices, and invoice items |
| Maven | Dependency and build management |
| Postman | API testing |
| IntelliJ IDEA | Development IDE |

## Why These Technologies Were Used

- **Spring Boot** reduces boilerplate setup and makes REST API development faster.
- **Spring Data JPA** provides ready-made repository methods like `save`, `findAll`, and `findById`.
- **MySQL** is a widely used relational database and is suitable because invoices have clear table relationships.
- **Hibernate** maps Java classes to database tables.
- **Maven** manages dependencies and project builds.

## Database Schema

### `customer`

| Column | Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key, auto-generated |
| name | VARCHAR | Customer name |
| email | VARCHAR | Customer email |
| phone | VARCHAR | Customer phone number |
| address | VARCHAR | Customer address |
| gstin | VARCHAR | Customer GSTIN |

### `invoice`

| Column | Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key, auto-generated |
| customer_id | BIGINT | Foreign key referencing customer |
| invoice_date | DATE | Invoice creation date |
| invoice_number | VARCHAR | Auto-generated invoice number |
| status | VARCHAR | `PAID`, `UNPAID`, or `CANCELLED` |
| subtotal | DECIMAL | Sum of all item totals |
| cgst | DECIMAL | 9% of subtotal |
| sgst | DECIMAL | 9% of subtotal |
| grand_total | DECIMAL | Subtotal + CGST + SGST |

### `invoice_item`

| Column | Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key, auto-generated |
| invoice_id | BIGINT | Foreign key referencing invoice |
| item_name | VARCHAR | Name of item or service |
| quantity | INT | Item quantity |
| unit_price | DECIMAL | Price per unit |
| total_price | DECIMAL | Quantity * unit price |

## Relationships

- One customer can have many invoices.
- One invoice belongs to one customer.
- One invoice can have many invoice items.
- One invoice item belongs to one invoice.

JPA relationship summary:

```text
Customer 1 -------- * Invoice
Invoice 1 -------- * InvoiceItem
```

## GST Calculation

The GST calculation is handled in the service layer.

```text
totalPrice per item = quantity * unitPrice
subtotal = sum of item totalPrices
CGST = subtotal * 0.09
SGST = subtotal * 0.09
grandTotal = subtotal + CGST + SGST
```

Example:

```text
Item total = 2 * 100 = 200
Subtotal = 200
CGST = 18
SGST = 18
Grand Total = 236
```

## API Reference

Base URL:

```text
http://localhost:8080
```

### 1. Create Customer

Endpoint:

```http
POST /customers
```

Request body:

```json
{
 "name": "Rahul Sharma",
 "email": "rahul@example.com",
 "phone": "9876543210",
 "address": "Pune, Maharashtra",
 "gstin": "27ABCDE1234F1Z5"
}
```

Response body:

```json
{
 "id": 1,
 "name": "Rahul Sharma",
 "email": "rahul@example.com",
 "phone": "9876543210",
 "address": "Pune, Maharashtra",
 "gstin": "27ABCDE1234F1Z5"
}
```

### 2. Get All Customers

Endpoint:

```http
GET /customers
```

Response body:

```json
[
 {
   "id": 1,
   "name": "Rahul Sharma",
   "email": "rahul@example.com",
   "phone": "9876543210",
   "address": "Pune, Maharashtra",
   "gstin": "27ABCDE1234F1Z5"
 }
]
```

### 3. Get Customer By ID

Endpoint:

```http
GET /customers/1
```

Response body:

```json
{
 "id": 1,
 "name": "Rahul Sharma",
 "email": "rahul@example.com",
 "phone": "9876543210",
 "address": "Pune, Maharashtra",
 "gstin": "27ABCDE1234F1Z5"
}
```

### 4. Create Invoice

Endpoint:

```http
POST /invoices
```

Request body:

```json
{
 "customerId": 1,
 "items": [
   {
     "itemName": "Laptop",
     "quantity": 1,
     "unitPrice": 50000
   },
   {
     "itemName": "Mouse",
     "quantity": 2,
     "unitPrice": 500
   }
 ]
}
```

Response body:

```json
{
 "id": 1,
 "customer": {
   "id": 1,
   "name": "Rahul Sharma",
   "email": "rahul@example.com",
   "phone": "9876543210",
   "address": "Pune, Maharashtra",
   "gstin": "27ABCDE1234F1Z5"
 },
 "invoiceDate": "2026-06-21",
 "invoiceNumber": "INV-001",
 "status": "UNPAID",
 "subtotal": 51000.00,
 "cgst": 4590.00,
 "sgst": 4590.00,
 "grandTotal": 60180.00,
 "items": [
   {
     "id": 1,
     "itemName": "Laptop",
     "quantity": 1,
     "unitPrice": 50000.00,
     "totalPrice": 50000.00
   },
   {
     "id": 2,
     "itemName": "Mouse",
     "quantity": 2,
     "unitPrice": 500.00,
     "totalPrice": 1000.00
   }
 ]
}
```

### 5. Get All Invoices

Endpoint:

```http
GET /invoices
```

Response body:

```json
[
 {
   "id": 1,
   "invoiceNumber": "INV-001",
   "status": "UNPAID",
   "subtotal": 51000.00,
   "cgst": 4590.00,
   "sgst": 4590.00,
   "grandTotal": 60180.00,
   "items": []
 }
]
```

### 6. Get Invoice By ID

Endpoint:

```http
GET /invoices/1
```

Response:

Returns one invoice with customer and item details.

### 7. Get Invoices By Customer ID

Endpoint:

```http
GET /invoices/customer/1
```

Response:

Returns all invoices belonging to the selected customer.

### 8. Update Invoice Status

Endpoint:

```http
PATCH /invoices/1/status?status=PAID
```

Allowed status values:

```text
PAID
UNPAID
CANCELLED
```

Response:

Returns the updated invoice.

### 9. Delete Invoice

Endpoint:

```http
DELETE /invoices/1
```

Response:

```text
204 No Content
```

### 10. Get Total Revenue

Endpoint:

```http
GET /invoices/revenue
```

Response body:

```json
{
 "totalRevenue": 60180.00
}
```

## How to Set Up and Run Locally

### Prerequisites

- Java 17 or 21
- IntelliJ IDEA
- MySQL installed locally
- Maven
- Postman

### Steps

1. Clone or download the project.
2. Open it in IntelliJ IDEA.
3. Create the database:

```sql
CREATE DATABASE gst_invoice_db;
```

4. Update MySQL username and password in:

```text
src/main/resources/application.properties
```

5. Run the application:

```bash
mvn spring-boot:run
```

6. Test APIs using Postman:

```text
http://localhost:8080
```

## Sample Postman Request Bodies

### Create Customer

```json
{
 "name": "Rahul Sharma",
 "email": "rahul@example.com",
 "phone": "9876543210",
 "address": "Pune, Maharashtra",
 "gstin": "27ABCDE1234F1Z5"
}
```

### Create Another Customer

```json
{
 "name": "Priya Mehta",
 "email": "priya@example.com",
 "phone": "9123456780",
 "address": "Mumbai, Maharashtra",
 "gstin": "27XYZAB1234C1Z9"
}
```

### Create Invoice

```json
{
 "customerId": 1,
 "items": [
   {
     "itemName": "Website Development",
     "quantity": 1,
     "unitPrice": 25000
   },
   {
     "itemName": "Hosting Setup",
     "quantity": 1,
     "unitPrice": 5000
   }
 ]
}
```

### Update Invoice Status

Use query parameter:

```text
PATCH http://localhost:8080/invoices/1/status?status=PAID
```

No request body is required.

## Important Implementation Details

### Why `@JsonIgnore` Is Used

`Invoice` has a list of `InvoiceItem`, and each `InvoiceItem` has a reference back to `Invoice`. Without `@JsonIgnore`, JSON conversion can keep moving between invoice and item repeatedly, causing infinite recursion.

### Why DTO Is Used

`InvoiceRequest` is used because the client should only send input values:

- customer id
- item name
- quantity
- unit price

The client should not send calculated fields like subtotal, CGST, SGST, or grand total. Those values are calculated by the service layer.

### Why `BigDecimal` Is Used

Money values should be stored and calculated using `BigDecimal` instead of `double` because `double` can create precision errors.

## Known Limitations

- Invoice number generation is simple and suitable for learning, but not safe for high-concurrency production systems.
- Authentication and authorization are not implemented.
- No frontend is included.
- PDF invoice generation is not included.
- Pagination is not added for large customer or invoice lists.
- GST logic only supports CGST and SGST at fixed 9% rates.

## Future Improvements

- Add login and role-based access using Spring Security.
- Generate downloadable PDF invoices.
- Add IGST support for interstate invoices.
- Add pagination and sorting.
- Add search by invoice number or customer name.
- Add email invoice feature.
- Add frontend using React or Angular.
- Add unit tests and integration tests.

## Project Structure

```text
gst-invoice-manager
├── src/main/java/com/example/gstinvoice
│   ├── controller
│   │   ├── CustomerController.java
│   │   └── InvoiceController.java
│   ├── dto
│   │   ├── InvoiceItemDTO.java
│   │   └── InvoiceRequest.java
│   ├── entity
│   │   ├── Customer.java
│   │   ├── Invoice.java
│   │   ├── InvoiceItem.java
│   │   └── InvoiceStatus.java
│   ├── exception
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── repository
│   │   ├── CustomerRepository.java
│   │   └── InvoiceRepository.java
│   ├── service
│   │   ├── CustomerService.java
│   │   └── InvoiceService.java
│   └── GstInvoiceManagerApplication.java
└── src/main/resources
   └── application.properties
```
