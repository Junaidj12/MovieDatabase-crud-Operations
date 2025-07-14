# 🎬 MovieGram

MovieGram is a mobile-first social movie-sharing web app built with **Spring Boot** and **Thymeleaf**, designed for a small group of friends to share, recommend, and manage movies with ease. Think of it as a minimalist Instagram for movies.

Link :- https://moviegram.onrender.com/

## 📱 Features

- 🧑‍🤝‍🧑 Simple friend-based registration (name + mobile only)
- 🏠 Feed page showing movies added by all users
- ➕ Add new movies with title, plot, and poster image (via Cloudinary)
- ❤️ Recommend any movie to a friend with a personal note
- 🔔 In-app notification system for recommendations
- 👤 Profile page showing:
  - Movies added by the user
  - Recommendations received (with delete option)
- 🛠 Admin panel with:
  - Total users & movies
  - Ban users by mobile number
  - Broadcast announcements
  - View/delete all users and movies
- 📱 Fully mobile responsive with a clean UI and bottom navigation

## 💻 Tech Stack

- **Backend**: Spring Boot 3, Spring MVC, Spring Data JPA
- **Frontend**: Thymeleaf, HTML/CSS, Font Awesome
- **Database**: H2 (in-memory) or MySQL/PostgreSQL
- **Image Hosting**: [Cloudinary](https://cloudinary.com/)
- **Deployment**: Docker + Render


## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- Cloudinary account (for image upload)
- GitHub account (for deployment to Render)

### Running Locally

```bash
git clone https://github.com/YOUR_USERNAME/moviegram.git
cd moviegram
./mvnw spring-boot:run
