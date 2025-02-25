# 🚀 jFile - Modern File Management System

## 📋 Overview

jFile started as a personal project to solve a daily frustration - managing downloads across my devices. Have you ever started downloading something important on your work laptop, only to realize you needed it on your home computer? Or lost track of which files were downloaded where? That's exactly what motivated me to create jFile!

This project transforms file management from a manual chore into an organized, automated system that works for you. It's built with modern Java technologies and demonstrates how enterprise-grade architecture can solve everyday problems.

I found myself constantly:
- Emailing files to myself
- Using USB drives that I would misplace
- Re-downloading the same files multiple times
- Losing track of where specific files were stored

jFile solved these problems while letting me explore modern Java architecture, distributed systems, and security best practices.

## 🌱 My Learning Journey

Building jFile taught me valuable lessons:
- How to design systems that solve real-world problems
- The importance of security in even seemingly simple applications
- Working with asynchronous processing for better performance
- Managing environment configurations for different deployment scenarios
- Implementing proper error handling for better user experience

## ✨ Key Features

- 🔐 **Secure Access**: Log in from anywhere and manage your downloads securely
- 🔄 **Download from Anywhere to Anywhere**: Initiate downloads to any of your registered devices
- 📊 **Smart Tracking**: Always know what files are where and when they were downloaded
- 🔔 **Notifications**: Get alerts when downloads complete or fail
- 📱 **Device Management**: Easily manage all your connected devices

## 🛠️ Technical Stack

- ☕ **Backend**: Spring Boot 3.4.2
- 🔒 **Security**: JWT authentication with RSA encryption
- 💾 **Database**: PostgreSQL for reliable data storage
- 📨 **Messaging**: Apache Kafka for real-time processing
- ⚙️ **Configuration**: Environment-based setup with dotenv

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- PostgreSQL database
- Apache Kafka

### Quick Setup
1. Clone the repository
2. Configure .env file with your database and download path settings
3. Run with `./gradlew bootRun`
4. Access the API at http://localhost:8080

## 💭 More to come:

I'm excited to continue developing jFile with features like:
- 📁 Smart file organization based on content
- 🔍 Fast look-up for downloaded files from the current user.
