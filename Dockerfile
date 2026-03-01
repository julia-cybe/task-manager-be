# PostgreSQL Database
FROM postgres:16-alpine

# Set environment variables
ENV POSTGRES_DB=taskmanager
ENV POSTGRES_USER=taskuser
ENV POSTGRES_PASSWORD=taskpassword

# Expose PostgreSQL port
EXPOSE 5432

# Use default entrypoint from base image

