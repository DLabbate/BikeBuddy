# BikeBuddy
## Your Personal Biking Assistant


<img src="https://user-images.githubusercontent.com/37638598/79628661-e7677b80-8110-11ea-812a-e0ebd918c5c4.png" width="60">


### Overview
BikeBuddy is an Android mobile application for cyclists seeking to track and improve performance. The mobile application uses a Zephyr HxM sensor for reading heart rate values. Some of the features include:
* Viewing real-time fitness statistics such as Heart Rate, Speed, and Distance
* Viewing real-time GPS directions and location updates
* Recording and saving workouts
* Saving routes
* Performance summary of the user's profile (including total distance travelled, total duration travelled, and more)
* Tracking distance travelled and time spent on different bikes (for users with multiple bikes)

___

### Hardware
Zephyr HxM Sensor. For more information, see (https://www.zephyranywhere.com/system/hxm)
<p align="center">
  <img src="https://user-images.githubusercontent.com/37638598/85801863-c3787580-b711-11ea-87d7-5d08bcd85444.jpg" height="300">
</p>
<p align="center">
  <img src="https://user-images.githubusercontent.com/37638598/85802088-3386fb80-b712-11ea-92b5-758a7835d361.PNG" height="400">
</p>

___

### Login
<img src="https://user-images.githubusercontent.com/37638598/79628973-842b1880-8113-11ea-8f34-9994ebfb1eff.jpg" height="500">

___

### Profile
<img src="https://user-images.githubusercontent.com/37638598/79628998-c7858700-8113-11ea-8119-d24e02273109.jpg" height="500">

___

### Fitness Page
* This page is where the user can record a workout and view important cycling parameters including heart rate, speed and distance.
* Another important element on this page is the cardiac zone in the upper right corner. This icon indicates the intensity of the workout (whether at rest, light intensity, moderate intensity, high intensity, or maximal intensity). This is dependent on the user's age.
* The cyclist has the option to see this information in real time (if they wish to mount the phone on their bike) or record the workout in the background to save battery.
* Lastly, the upper left icon indicates the connection to the Zephyr sensor, whether connected or not.
<img src="https://user-images.githubusercontent.com/37638598/79628846-d7e93200-8112-11ea-922c-a5344e933200.jpg" height="500">

___

### GPS
* GPS functionality implemented using Google’s Map API.
* In this page the user has the ability to get biking path directions to desired destinations by tapping on a specific location.
* The user also has the ability to toggle real time updates on/off, in the upper right corner. This will center the map on the user’s current location, and also update the directions to reach his desired destination. With the real time button selected, these updates will occur automatically. This feature can be toggled off mid workout if the user desires to switch destination.
<img src="https://user-images.githubusercontent.com/37638598/85799636-8ad69d00-b70d-11ea-8203-f271192d07a7.jpg" height="500">

___

### Workout Logs
* The workout logs list all of the users recorded workouts. In order to facilitate searching for workouts, this page includes sorting and filtering functionality. In particular, the workouts can be sorted according to date/distance/duration. It can also be filtered by date to only display workouts in the desired timeframe.
<img src="https://user-images.githubusercontent.com/37638598/85797654-0df5f400-b70a-11ea-8422-8cbae156e832.jpg" height="500">
<img src="https://user-images.githubusercontent.com/37638598/85797726-32ea6700-b70a-11ea-9bf0-4c8025faffb8.jpg" height="500">

* From this list of workouts, the user can select a specific one for a more detailed description. This detailed view will list several parameters such as date, duration, distance, average heart rate, average speed, and calories.
* The user will also be able to see the following:
  * HR graph 
  * Speed graph
  * Time spent in each cardiac zone
  * The route taken by the user for the workout
<img src="https://user-images.githubusercontent.com/37638598/79629022-ebe16380-8113-11ea-9e70-ff8363bf3511.jpg" height="500">
<img src="https://user-images.githubusercontent.com/37638598/79629024-f00d8100-8113-11ea-970f-ca864803a1e9.jpg" height="500">
<img src="https://user-images.githubusercontent.com/37638598/79629028-f4d23500-8113-11ea-9c0f-45321c63c3e0.jpg" height="500">

___

### Performance Summary
* The performance summary gives an overview of the users workout history. It outlines important **Profile Statistics** such as: max HR, total calories burned, total distance travelled, total duration, total workouts, and average distance PER workout. 
<img src="https://user-images.githubusercontent.com/37638598/79629041-1c290200-8114-11ea-84bc-2d8b4cfe920f.jpg" height="500">

* In addition, this page includes a **Progression Chart** where the user can visualize how he/she has progressed in the most recent workouts. In the example below, the user can see his/her average heart rate over the last 10 workouts.
* The user also has the option to view calories burned, distance, and duration of his/her most recent workouts if they want to analyze their progression with respect to these parameters.
<img src="https://user-images.githubusercontent.com/37638598/79629042-1fbc8900-8114-11ea-87c2-61adf01b7532.jpg" height="500">

___

### Bikes
* The user has the option to add his/her bikes to record mileage and duration spent on each bike. Some cyclists have multiple bikes and desire to keep track of statistics on each bike.
* This facilitates keeping track of bike maintenance.
<img src="https://user-images.githubusercontent.com/37638598/79629054-3cf15780-8114-11ea-94f9-3e6a263c6e58.jpg" height="500">
<img src="https://user-images.githubusercontent.com/37638598/79629055-3f53b180-8114-11ea-9413-65bfb152783b.jpg" height="500">
