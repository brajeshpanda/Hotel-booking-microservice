# Hotel-booking-microservice
. User Management Module
Enabling secure user registration, login, and logout functionalities. Implemented secure user
authentication and authorization using Spring Security with JWT tokens. Developed rolebased access control for different user types — Admin, Customer, and HotelOwner.
2. Hotel Catalog Management
Designed RESTful APIs to manage hotel details, including CRUD operations for hotel
name,address and images. Integrated AWS S3 for secure image storage .
3. Room Inventory Management
Designed APIs to manage room categories, occupancy limits. Implemented dynamic
availability tracking to automatically update room counts after everybooking or cancellation.
4. Hotel Search & Aggregation Module
Developed dynamic hotel search APIs with filters (city, availability, price). Implemented
pagination and sorting for efficient data handling.
5. Booking & Reservation Module
Created complete booking flow (create, confirm, cancel, modify) for end-to-end reservations.
Implemented distributed transactions across Booking, Payment, and Inventory. Used Kafka
for asynchronous event handling (Booking Created, Payment Confirmed).
6. Payment Module
Integrated Stripe Payment Gateway for secure online payments and refunds. Developed APIs
for payment initiation, tracking, and refund management. Ensured secure transaction
handling and published payment events using Kafka topics.
7. Reviews & Ratings Module
Developed APIs for users to post, view, and delete hotel reviews. Added admin moderation
for approving or rejecting user reviews.
8. Notification Module
Integrated SendGrid (Email) and Twilio (SMS) for sending booking
confirmations,cancellations. Implemented Kafka-based asynchronous notifications for high
scalability.
9. Monitoring & Logging Module
Integrated ELK Stack for logging and analysis. Implemented Zipkin for distributed tracing to
monitor API latency. Used Spring Boot Actuator for health checks and system performance
metrics.
