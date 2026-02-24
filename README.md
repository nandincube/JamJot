# JamJot
**Annotate songs in your Spotify playlist with personal notes**

**Interactive API Documentation (Swagger UI):**
[Jamjot API Demo](https://jamjot.onrender.com)

## Overview
Jamjot is a RESTful API that allows users to attach personal annotations/notes to songs within their existing Spotify playlists. The system integrates with the Spotify Web API to retrieve user's playlists and enable storing time-based notes, reflections or contextual metadata linked to specific songs.

This project was built to explore third-party API integration, secure authentication flows and backend data modelling for media annotation system.

## Features

- Spotify OAuth authentication
- Retrieve user playlists via Spotify API
- Add top-level notes to playlists
- Add track-level notes to songs/tracks within playlists
- Add Timestamp-based notes to songs
- CRUD opertations for management of notes
- Per-user secure annotation/note storage


## Architecture and Design Overview
- The system has a layered architecture (Model-View-Controller architecture implemented using a Service-Repository pattern) to facilitate seperation of concerns. In this architecture, the controller layer handles HTTP requests and response mapping, the service layer contains business logic and perfroms ownership validation, and the repository layer manages data persistence using Spring Data JPA.
-  OAuth2 authentication via Spotify was performed to allow for access to protected resources, where access tokens and other relevant credentials are stored and managed by Spring Security.
- The RESTful endpoints provided in this API are secured by Spring Security
- Relational database schema models users, playlist, tracks and timestamps (![ER DIAGRAM](jamjot\docs\JamJot ER-Diagram.drawio.png))
- All annotation operatioins are restricted to authenticated users and validated against the ownership of the playlist being annotated


## Technical Stack

- Backend: Spring Boot
- Database: PostgreSQL
- Authentication: Spotify OAuth
- Documentation: Swagger/OpenAPI and Java Doc
- Hosting: Render
- External API: Spotify Web API

##

## Live Demo

The API is fully deployed and can be testsed interactively using Swagger UI:

**Swagger UI:**
(https://jamjot.onrender.com)


## Motivation

Jamjot was developed as a backend-focused personal project to explore OAuth integration, third-party API orchestration, and production-style REST API deployment with interactive documentation.
