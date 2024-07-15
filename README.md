# CAFE-SHOP-SYSTEM
BITP3123 PROJECT

# Cafe and Delivery Applications Project

## Project Overview

This project consists of two interconnected applications with a shared database, designed to simulate a Cafe ordering system and a Delivery system. The Cafe application is used by staff to manage orders, while the Delivery application is used by riders to fulfill delivery tasks.

## Applications Involved

### 1. Cafe Application
- **Purpose:** Manage and process orders within the cafe.
- **Main Functions:**
  - Insert the order details
  - Insert the customer details
  - View order status
  - Update order status (e.g., from "pending" to "preparing")
  - View delivery status
  - Update delivery status (e.g., from "Accept" to "Reject")
  - Place an order

### 2. Delivery Application
- **Purpose:** Manage and fulfill delivery tasks for the cafe orders.
- **Main Functions:**
  - View assigned delivery tasks
  - Update delivery status (e.g., from "Accept" to "Reject")
  - View delivery task

### 3. Architecture diagram apps and the middleware
  - The Architecture diagram for Cafe and Delivery App can be seen at Architecture diagram.png
  - The Architecture diagram for the middleware (backend service) can be seen at Middleware.png

### 4. Middleware Communication
  - The Cafe and Delivery applications communicate using sockets to ensure real-time data distribution.

### 5. List of URL Endpoints
  ### Cafe Application Endpoints
   - `POST /orders`: Create a new order
   - `GET /orders/{id}`: Get the status of a specific order
   - `PUT /orders/{id}`: Update the status of a specific order

  ### Delivery Application Endpoints
   - `GET /deliveries`: Get all delivery tasks
   - `GET /deliveries/{id}`: Get the status of a specific delivery
   - `PUT /deliveries/{id}`: Update the status of a specific delivery


### 5. Middleware Functions/Features
   - **Order Management:**
     - Receive new orders from the Cafe application
     - Notify the Delivery application of new delivery tasks

   - **Delivery Management:**
     - Update delivery status and notify the Cafe application of status changes
  
