# JamJot
Annotate songs in your Spotify playlist with personal notes.

**Live API Demo (Swagger UI):**
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

## Technical Stack

- Backend: Spring Boot
- Database: PostgreSQL
- Authentication: Spotify OAuth
- Documentation: Swagger/OpenAPI and Java Doc
- Hosting: Render
- External API: Spotify Web API

## Live Demo

The API is fully deployed and can be testsed interactively using Swagger UI:

**Swagger UI:**
(https://jamjot.onrender.com)


## Motivation

Jamjot was developed as a backend-focused personal project to explore OAuth integration, third-party API orchestration, and production-style REST API deployment with interactive documentation.
