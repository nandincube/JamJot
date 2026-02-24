# JamJot
**Annotate Songs in Your Spotify Playlists with Personal Notes!**

Interactive API Documentation (Swagger UI) - **Spotify Account Required**:
<br>
<br>
[Jamjot API Docs](https://jamjot.onrender.com)

## Overview
Jamjot is a RESTful API that allows users to attach personal annotations/notes to songs within their existing Spotify playlists. The system integrates with the Spotify Web API to retrieve user's playlists and enable storing time-based notes, reflections or contextual metadata linked to specific songs.

This project was built to explore third-party API integration, secure authentication flows and backend data modelling for a media annotation system.

## Technical Stack

- Backend: Spring Boot
- Database: PostgreSQL
- Authentication: Spotify OAuth
- Documentation: Swagger/OpenAPI and Java Doc
- Hosting: Render
- External API: Spotify Web API

## Features

- Spotify OAuth authentication
- Retrieve user playlists via Spotify API
- Add top-level notes to playlists
- Add track-level notes to songs/tracks within playlists
- Add Timestamp-based notes to songs
- CRUD opertations for management of notes
- Per-user secure annotation/note storage and manipulation

## Architecture and Design Overview
- The system has a layered architecture (Model-View-Controller architecture implemented using a Service-Repository pattern) to facilitate seperation of concerns. In this architecture, the controller layer handles HTTP requests and response mapping, the service layer contains business logic and perfroms ownership validation, and the repository layer manages data persistence using Spring Data JPA.
-  OAuth2 authentication via Spotify is performed to allow for access to protected resources, where access tokens and other relevant credentials are stored and managed by Spring Security.
- The RESTful endpoints provided in are also secured by Spring Security.
- Relational database schema models users, playlist, tracks and timestamps. This data model is shown in the image below:

![ER- Diagram](jamjot/docs/JamJot%20ER-Diagram.drawio.png) 

The ternary relationship "contains" is realised as the "Playlist Member" entity set. This entity set or relation is responsible for storing the set of tracks contained in each playlist. In other words, each entity represents a track's membership in a particular playlist, where tracks can appear in the same playlist multiple times and can appear across different playlists.

- All annotation operations are restricted to authenticated users and validated against the ownership of user playlists

## Installation and Usage

### Prerequisites
- Java 21+
- Maven (or use `./mvnw`)
- PostgreSQL running locally (or a hosted Postgres instance)
- A Spotify Developer app (Client ID/Secret + Redirect URI)

To Run Locally, 

### 1) Clone the project

Clone the repository and enter project using the following commands: 

```bash
git clone https://github.com/nandincube/jamjot.git
cd jamjot
```

### 2) Create a .env file 

The application loads environment variables from a .env file via:

```properties
spring.config.import: file:.env[.properties]
```

Create a .env in the root directory (same level as pom.xml), with the following information:

```properties
SPOTIFY_CLIENT_ID=<spotify_client_id>
SPOTIFY_CLIENT_SECRET=<spotify_client_secret>
REDIRECT_URI=http://127.0.0.1:8081/login/oauth2/code/spotify

DATABASE_URL=jdbc:postgresql://localhost:5432/<database_name>
DATABASE_USER=<postgres_user>
DATABASE_PASSWORD=<database_password>
```

**Ensure that redirect uri matches what is configured in your Spotify Developer Dashboard**

### 3) Create Database 
Create a Postgres database and fill in relevant information in the .env file 

### 4) Run the application
Using the Maven Wrapper, run the following command:

```bash
./mvnw spring-boot:run
```

The server will run on:

http://127.0.0.1:8081/

The API documentation will be available at:

http://127.0.0.1:8081/swagger-ui/index.html


**Note: If running locally, Spotify is configured to allow requests from 127.0.0.1 (as opposed to "localhost"). Therefore, 127.0.0.1 should not be replaced with  localhost in URL/URIs.**


## Live Demo/Documentation

The API is fully deployed and can be tested interactively using Swagger UI:
<br>
<br>
[Jamjot API Docs](https://jamjot.onrender.com)


## Motivation

Jamjot was developed as a backend-focused personal project to explore OAuth integration, third-party API orchestration, and REST API deployment with interactive documentation.


## Future Improvements
- Comprehensive Unit and Integration testing
- Simplification of ID for Playlist Member Entity. The application was originally designed to support playlists where track repetition was not accounted for. In order to accomodation for repetition, track number was added to the Playlist primary key. Although, this is a valid primary key and supports operations, it is not minimal. In future, the trackID can be removed from the the Playlist Member Entity 
