# **AI Chatbot Web Application - Backend**

For frontend, please refer to this [repositoty](https://github.com/BaHaMuT-J/AI-Chatbot-Web-Application---Frontend)

## **Overview**

This project is AI chatbot web application. It is a SPA (Single Page Application) that have multiple AI for users to chat with. This project's purpose is to learn about how to develop web applications using Vue.js and Spring Boot and interact with different AI API.

This repository is a backend built using Spring Boot. It uses maven as a build tool, Spring AI for interacting with most AI API, and docker containerized PostgreSQL as the database.

## **Features**

- User authentication and session management
- Secure RESTful APIs
- AI-powered features with Spring AI
- Scalable backend with Spring Boot and PostgreSQL

## **Software Stack**

### **1. Backend**

- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Security:** Spring Security (Session-based authentication)
- **Validation:** Spring Validation
- **AI Features:** Spring AI and HTTPS request
- **API Design:** REST (JSON-based responses)

### **2. Database**

- **Database Engine:** PostgreSQL
- **Containerization:** Docker
- **ORM:** Spring Data JPA

### **3. API Design & Communication**

- **Architecture Style:** RESTful API
- **Request-Response Format:** JSON
- **Common Endpoints:**
    - `POST /api/login` - User authentication
    - `POST /api/user/create` - Register new user
    - `GET /api/ai/models` - Get all AIs available
    - `POST /api/chat/send` - Send message to chat with AI

---

## **Installation & Setup**

### **1. Prerequisites**

- Install [**Java 21**](https://www.oracle.com/th/java/technologies/downloads/)
- Install [**Maven**](https://maven.apache.org/install.html)
- Install [**Docker**](https://docs.docker.com/engine/install/)

### **2. Backend Setup**

- Create `.env` files and include these variables
```
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password
GEMINI_API_KEY=your_api_key
DEEPSEEK_API_KEY=your_api_key
GROQ_API_KEY=your_api_key
MISTRAL_API_KEY=your_api_key
```

for postgres you can choose whatever you like

for API keys please refer to these links [Gemini](https://aistudio.google.com/app/apikey) | [Groq](https://console.groq.com/keys) | [DeepSeek](https://api-docs.deepseek.com/) | [Mistral](https://console.mistral.ai/api-keys/)

- run the application

```sh
# Clone the repository
git clone git@github.com:BaHaMuT-J/AI-Chatbot-Web-Application---Backend.git

# Navigate to backend directory
cd AI-Chatbot-Web-Application---Backend

# Run the database in docker
./start-postgres.sh

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```
