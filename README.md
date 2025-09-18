# Personal Loan Application System

## Project Overview

This project, [PersonalLoanApplicationSystem](https://github.com/vikhileshs_Zeta/PersonalLoanApplicationSystem), is designed to automate and streamline the process of applying for personal loans. The system encompasses user authentication, loan eligibility checks, application workflow, admin approval, and notification modules.

### Logic Walkthrough

1. **User Authentication:**  
   The system verifies users based on their credentials, using secure authentication mechanisms.

2. **Loan Eligibility:**  
   After login, users input personal, financial, and employment details. The system applies business logic (such as salary, credit score, and employment status) to determine eligibility.

3. **Application Workflow:**  
   Eligible users can submit loan applications. The workflow includes document uploads, application status tracking, and automated validations.

4. **Admin Approval:**  
   Administrators review submitted applications. The logic includes automated checks for fraud detection, document verification, and manual override capabilities.

5. **Notifications:**  
   The system sends notifications to users at each stage (application received, under review, approved/rejected).


### GitHub Repository

[PersonalLoanApplicationSystem GitHub Repo](https://github.com/Ayushj045/PersonalLoanManagmentSystem)

### Instructions to Run the Project

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Ayushj045/PersonalLoanManagmentSystem
   cd PersonalLoanApplicationSystem
   ```


2. **Install Dependencies**
   - For backend (assume using Java/Spring Boot):
     ```bash
     cd backend
     mvn install
     ```
   - For frontend (assume using React):
     ```bash
     cd frontend
     npm install
     ```

3. **Configure Environment Variables**
   - Create `.env` files in `backend` and `frontend` directories as needed.
   - Add database connection details, API keys, etc.

4. **Run Backend Server**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. **Run Frontend Server**
   ```bash
   cd frontend
   npm start
   ```

6. **Access the Application**
   - The frontend should be accessible at [http://localhost:3000](http://localhost:3000)
   - The backend API should be running on [http://localhost:8080](http://localhost:8080)

8. **Testing**
   - Run test suites using:
     - Backend: `mvn test`
     - Frontend: `npm test`

### Additional Notes

- Ensure Java (11+) and Node.js (16+) are installed.
- Database setup: Use MySQL/PostgreSQL. Import the schema from `backend/db/schema.sql`.
- For any issues, refer to the repository README or open an issue.

---

*For further clarifications, contact the project lead via GitHub issues.*
