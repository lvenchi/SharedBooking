# SharedBooking

A simple android app created to test the new Android Jetpack components.


This app allows a user (authenticated either with facebook ora google) to publish an appointment or to set one.
Each action that involves two users, will trigger a firebase push notification for each of these users.
The notifications are handled by a Firebase Function (with Nodejs) whose code is in my GitHub.
