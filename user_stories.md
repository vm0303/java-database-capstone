# Smart Clinic Managemenrt System - User Stories

## User Story Template

### Title
_As a [user role], I want [feature/goal], so that [reason]._

### Acceptance Criteria
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

### Priority
High / Medium / Low

### Story Points
[Estimated Effort in Points]

### Notes
- [Additional information or edge cases]

---

## Admin User Stories

---

### User Story: Admin Login

#### Title
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

#### Acceptance Criteria
1. Admin can access the login page in the cloud-IDE environment.
2. Admin can enter a valid username and password.
3. System validates credentials and grants access if correct.
4. System displays an error message for invalid login attempts.

#### Priority
High

#### Story Points
3

#### Notes
- Handle account lockout after multiple failed attempts.
- Ensure passwords are securely encrypted.

---

### User Story: Admin Logout

#### Title
_As an admin, I want to log out of the portal, so that I can protect system access when I am done._

#### Acceptance Criteria
1. Admin can click a logout button or link in the portal interface.
2. System securely terminates the active session.
3. Admin is redirected to the login page after logout.
4. Session cookies and authentication tokens are cleared.

#### Priority
High

#### Story Points
2

#### Notes
- Automatically expire inactive sessions after a timeout period.

---

### User Story: Add Doctor Profile

#### Title
_As an admin, I want to add doctors to the portal, so that I can manage doctor profiles in the system._

#### Acceptance Criteria
1. Admin can access a form to enter doctor details.
2. Required fields include doctor name, specialty, email, and contact information.
3. System validates all required fields before submission.
4. Added doctor profiles appear in the doctor directory after saving.

#### Priority
High

#### Story Points
5

#### Notes
- Validate email format and phone number fields.
- Prevent duplicate doctor records.

---

### User Story: Delete Doctor Profile

#### Title
_As an admin, I want to delete a doctor's profile from the portal, so that outdated or incorrect profiles can be removed._

#### Acceptance Criteria
1. Admin can search and select a doctor profile from the portal.
2. System prompts the admin to confirm deletion.
3. Doctor profile is removed successfully after confirmation.
4. Deleted profiles no longer appear in the doctor list.

#### Priority
Medium

#### Story Points
3

#### Notes
- Prevent accidental deletion with a confirmation dialog.
- Restrict deletion if active appointments exist.

---

### User Story: View Monthly Appointment Statistics

#### Title
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

#### Acceptance Criteria
1. Admin can access the MySQL CLI within the cloud-IDE environment.
2. Stored procedure returns appointment counts grouped by month.
3. Results are displayed in a readable tabular format.
4. Months with zero appointments are included in the report.

#### Priority
Medium

#### Story Points
5

#### Notes
- Ensure stored procedure handles empty datasets correctly.
- Future enhancement may include graphical reporting dashboards.

---

## Patient User Stories

---

### User Story: View Doctors Without Login

#### Title
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

#### Acceptance Criteria
1. Patient can access a public doctor directory page.
2. Doctor details such as name, specialty, and contact information are visible.
3. No login or registration is required to view the doctor list.
4. Doctor listings are updated automatically when profiles change.

#### Priority
Medium

#### Story Points
3

#### Notes
- Restrict sensitive doctor information from public view.

---

### User Story: Patient Registration

#### Title
_As a patient, I want to sign up with my personal information, email, and password, so that I can book and manage appointments._

#### Acceptance Criteria
1. Patient can access a registration form from the portal homepage.
2. Patient must enter name, email, password, phone number, and address.
3. System validates that the name is between 3 and 100 characters.
4. System validates that the email follows a valid email format.
5. System validates that the password is at least 6 characters long.
6. System validates that the phone number contains exactly 10 digits.
7. System validates that the address does not exceed 255 characters.
8. Patient receives confirmation after successful signup.

#### Priority
High

#### Story Points
5

#### Notes
- Password should be hidden from JSON responses using write-only access.
- Email should be unique if patients use it to log in.
- Passwords should be securely handled before production use.

---

### User Story: Patient Login

#### Title
_As a patient, I want to log into the portal, so that I can manage my bookings._

#### Acceptance Criteria
1. Patient can enter email and password credentials on the login page.
2. System validates credentials before granting access.
3. Patient dashboard displays upcoming appointments after login.
4. Invalid login attempts display an error message.

#### Priority
High

#### Story Points
4

#### Notes
- Include a “Forgot Password” feature in a future release.

---

### User Story: Patient Logout

#### Title
_As a patient, I want to log out of the portal, so that I can secure my account._

#### Acceptance Criteria
1. Patient can click a logout button or link.
2. System securely terminates the active session.
3. Session data and authentication tokens are cleared.
4. Patient is redirected to the login or home page after logout.

#### Priority
Medium

#### Story Points
2

#### Notes
- Ensure logout works across multiple browser tabs.

---

### User Story: Book Appointment

#### Title
_As a patient, I want to log in and book an hour-long appointment with a doctor, so that I can receive medical consultation._

#### Acceptance Criteria
1. Patient must be logged in before booking an appointment.
2. Patient can search and select a doctor from the directory.
3. Patient can choose an available one-hour appointment slot.
4. System prevents double-booking conflicts.
5. Appointment confirmation is displayed and saved to the patient dashboard.

#### Priority
High

#### Story Points
5

#### Notes
- Future enhancement may include appointment confirmation emails.

---

### User Story: View Upcoming Appointments

#### Title
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

#### Acceptance Criteria
1. Patient can access a dashboard showing upcoming appointments.
2. Appointment details include date, time, and doctor name.
3. Upcoming appointments are displayed in chronological order.
4. Patient can cancel or reschedule appointments if permitted.

#### Priority
Medium

#### Story Points
3

#### Notes
- Notify patients if appointment details are updated or canceled.

---

## Doctor User Stories

---

### User Story: Doctor Login

#### Title
_As a doctor, I want to log into the portal, so that I can manage my appointments._

#### Acceptance Criteria
1. Doctor can access the login page from the portal.
2. Doctor can enter valid login credentials.
3. System validates credentials and grants access upon successful authentication.
4. Invalid login attempts display an appropriate error message.

#### Priority
High

#### Story Points
3

#### Notes
- Ensure secure authentication and encrypted password storage.

---

### User Story: Doctor Logout

#### Title
_As a doctor, I want to log out of the portal, so that I can protect my data._

#### Acceptance Criteria
1. Doctor can click a logout button or link from the portal.
2. System securely terminates the active session.
3. Session data and authentication tokens are cleared.
4. Doctor is redirected to the login page after logout.

#### Priority
High

#### Story Points
2

#### Notes
- Automatically expire inactive sessions after a timeout period.

---

### User Story: View Appointment Calendar

#### Title
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

#### Acceptance Criteria
1. Doctor can access a calendar view of appointments.
2. Calendar displays appointment dates, times, and patient names.
3. Appointments are organized chronologically.
4. Doctor can view daily, weekly, or monthly schedules.

#### Priority
High

#### Story Points
5

#### Notes
- Future enhancement may include calendar synchronization with external services.

---

### User Story: Mark Unavailability

#### Title
_As a doctor, I want to mark my unavailable time slots, so that patients can only book available appointments._

#### Acceptance Criteria
1. Doctor can select dates and times to mark as unavailable.
2. Unavailable time slots are hidden from patient booking options.
3. System prevents bookings during unavailable periods.
4. Doctor can update or remove unavailable time slots when needed.

#### Priority
Medium

#### Story Points
5

#### Notes
- Ensure existing appointments are not affected when marking new unavailable periods.

---

### User Story: Update Doctor Profile

#### Title
_As a doctor, I want to update my profile information, so that patients can view accurate specialization and contact details._

#### Acceptance Criteria
1. Doctor can edit profile fields such as specialization, phone number, and email.
2. System validates updated information before saving.
3. Updated profile information is visible to patients immediately after saving.
4. Doctor receives confirmation after successful profile updates.

#### Priority
Medium

#### Story Points
4

#### Notes
- Restrict editing of system-generated identifiers.

---

### User Story: View Patient Details

#### Title
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared for consultations._

#### Acceptance Criteria
1. Doctor can access a list of upcoming appointments.
2. Doctor can view patient details associated with each appointment.
3. Patient details include name, appointment time, and relevant notes if available.
4. Access to patient information is restricted to authorized doctors only.

#### Priority
High

#### Story Points
5

#### Notes
- Ensure patient information complies with healthcare privacy standards.
