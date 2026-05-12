## MySQL Database Design

## Table: patients

- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- phone_number: VARCHAR(20), Not Null
- date_of_birth: DATE, Not Null
- gender: VARCHAR(20)
- address: VARCHAR(255)
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Notes
- Email should be validated later in application code.
- Phone number format should be validated later in application code.
- Patient records should not be deleted if they have appointment history. Use account deactivation instead.

---

## Table: doctors

- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- phone_number: VARCHAR(20), Not Null
- specialization: VARCHAR(100), Not Null
- license_number: VARCHAR(50), Not Null, Unique
- clinic_location_id: INT, Foreign Key → clinic_locations(id)
- is_active: BOOLEAN, Not Null, Default TRUE
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Notes
- Doctors should not have overlapping appointment slots.
- Doctor email and license number should be unique.
- If a doctor leaves the clinic, mark `is_active = FALSE` instead of deleting the doctor.

---

## Table: admin

- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- email: VARCHAR(100), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- role: VARCHAR(50), Not Null, Default 'ADMIN'
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Notes
- Admin passwords should be stored as hashed values.
- Username and email must be unique.
- Admin accounts should be protected with strong password rules.

---

## Table: appointments

- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- appointment_start: DATETIME, Not Null
- appointment_end: DATETIME, Not Null
- status: INT, Not Null, Default 0
  - 0 = Scheduled
  - 1 = Completed
  - 2 = Cancelled
  - 3 = No Show
- reason_for_visit: VARCHAR(255)
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Notes
- Do not delete appointments when a patient or doctor is removed.
- Past appointment history should be retained for recordkeeping.
- Prevent overlapping appointments for the same doctor through application logic or database constraints.
- `appointment_end` should be after `appointment_start`.

---

## Table: clinic_locations

- id: INT, Primary Key, Auto Increment
- location_name: VARCHAR(100), Not Null
- address_line1: VARCHAR(150), Not Null
- address_line2: VARCHAR(150)
- city: VARCHAR(100), Not Null
- state: VARCHAR(50), Not Null
- zip_code: VARCHAR(10), Not Null
- phone_number: VARCHAR(20), Not Null
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Notes
- A doctor can be assigned to a clinic location.
- Phone number and ZIP code formats should be validated later in application code.

---

## Table: doctor_availability

- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- available_date: DATE, Not Null
- start_time: TIME, Not Null
- end_time: TIME, Not Null
- is_available: BOOLEAN, Not Null, Default TRUE

### Notes
- Each doctor should have their own available time slots.
- Patients should only be able to book appointments during available slots.
- `end_time` should be after `start_time`.
- Availability should not overlap for the same doctor.

---

## Table: payments

- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- amount: DECIMAL(10,2), Not Null
- payment_status: INT, Not Null, Default 0
  - 0 = Pending
  - 1 = Paid
  - 2 = Failed
  - 3 = Refunded
- payment_method: VARCHAR(50)
- transaction_reference: VARCHAR(100), Unique
- payment_date: DATETIME

### Notes
- Payments are tied to a specific appointment.
- Payment records should be retained for billing history.
- Amount should be greater than or equal to 0.

---

## MongoDB Collection Design

MongoDB is useful for clinic data that may change often, contain optional fields, or require nested structures. Instead of forcing everything into fixed MySQL tables, MongoDB can store flexible records such as prescriptions, feedback, logs, and messages.

---

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "duration": "5 days",
      "instructions": "Take after food"
    }
  ],
  "doctorNotes": "Patient reported mild fever and body aches.",
  "refillCount": 1,
  "pharmacy": {
    "name": "Walgreens",
    "phone": "555-123-4567",
    "address": "123 Main Street, Edison, NJ"
  },
  "tags": ["fever", "pain relief"],
  "metadata": {
    "createdAt": "2026-05-12T10:30:00Z",
    "lastUpdatedAt": "2026-05-12T10:45:00Z",
    "schemaVersion": 1
  }
}
```

#### Design Notes
- Store `patientId`, `doctorId`, and `appointmentId` instead of the full patient or doctor object.
- This avoids duplicating data already stored in MySQL.
- The `medications` array supports multiple medications from one appointment.
- `metadata.schemaVersion` allows the prescription format to evolve later.

---

### Collection: feedback

```json
{
  "_id": "ObjectId('64def789012')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "rating": 5,
  "comments": "The doctor explained everything clearly and was very helpful.",
  "categories": ["communication", "wait time", "professionalism"],
  "isAnonymous": false,
  "submittedAt": "2026-05-12T15:00:00Z",
  "metadata": {
    "source": "patient_portal",
    "device": "web",
    "schemaVersion": 1
  }
}
```

#### Design Notes
- Feedback can vary in structure, so MongoDB is a good fit.
- Some feedback may include ratings, comments, categories, or anonymous settings.
- Feedback is linked to MySQL records using `appointmentId`, `patientId`, and `doctorId`.

---

### Collection: appointment_logs

```json
{
  "_id": "ObjectId('64ghi345678')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "events": [
    {
      "eventType": "CHECKED_IN",
      "timestamp": "2026-05-12T09:55:00Z",
      "performedBy": {
        "role": "patient",
        "id": 12
      }
    },
    {
      "eventType": "STATUS_CHANGED",
      "timestamp": "2026-05-12T10:05:00Z",
      "performedBy": {
        "role": "admin",
        "id": 2
      },
      "details": {
        "fromStatus": "Scheduled",
        "toStatus": "In Progress"
      }
    }
  ],
  "metadata": {
    "source": "clinic_portal",
    "schemaVersion": 1
  }
}
```

#### Design Notes
- Logs are event-based and may grow over time.
- Using an `events` array keeps appointment activity grouped together.
- Logs help track check-ins, cancellations, updates, and status changes.

---

### Collection: messages

```json
{
  "_id": "ObjectId('64jkl901234')",
  "appointmentId": 51,
  "participants": [
    {
      "role": "patient",
      "id": 12,
      "name": "John Smith"
    },
    {
      "role": "doctor",
      "id": 7,
      "name": "Dr. Emily Carter"
    }
  ],
  "messages": [
    {
      "senderRole": "patient",
      "senderId": 12,
      "message": "Should I fast before my appointment?",
      "sentAt": "2026-05-11T18:30:00Z",
      "readBy": [
        {
          "role": "doctor",
          "id": 7,
          "readAt": "2026-05-11T19:00:00Z"
        }
      ]
    },
    {
      "senderRole": "doctor",
      "senderId": 7,
      "message": "No fasting is required for this consultation.",
      "sentAt": "2026-05-11T19:05:00Z",
      "readBy": [
        {
          "role": "patient",
          "id": 12,
          "readAt": "2026-05-11T19:10:00Z"
        }
      ]
    }
  ],
  "tags": ["pre-appointment", "patient-question"],
  "metadata": {
    "createdAt": "2026-05-11T18:30:00Z",
    "lastMessageAt": "2026-05-11T19:05:00Z",
    "schemaVersion": 1
  }
}
```

#### Design Notes
- Messages are flexible because each conversation can have different participants and message counts.
- The document stores participant IDs instead of full patient or doctor records.
- Small display fields like names can be included for readability, but MySQL remains the source of truth.

---
