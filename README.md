# Weather-App
:sunny: Get the weather of any city in the world!

:partly_sunny: This application is a Windows OS program that allows users to lookup the weather of any desired city. 

## Motivation
I was motivated by Samsung's weather widget and I wanted to implement my own weather application that would give me just as much user interaction that the one on my phone had. I had a ton of fun using numerous API's and am looking forward to incorporating more complicated elements into this project in the future. 

## Tech/framework used
<b>Built with</b>
- JavaFX Software Platform
- [GeoDB Cities API](http://geodb-cities-api.wirefreethought.com/)
- [SendGrid API](https://sendgrid.com/)
- [WeatherBit API](https://www.weatherbit.io/)
- [ControlsFX API](http://fxexperience.com/controlsfx/features/)
- JSon/Gson

## Features
Search up cities by their names and GeoDB Cities API will return a list of cities matching your input. Clicking on the city will generate a weather report of the current temperature, max/min temperatures, precipitation, UV index, snowfall, and more detailed reports. One can also view stastical information using JavaFX BarCharts of the temperature and precipitation. Users can also send email notiifcations of the weather of their desired city. Furthermore, there is a list of favorite cities one can save and remove from. 

## Screenshots

The main weather page displays the temperature, precipitation, timezone, UV index, and many more weather characteristics of the desired city you want to look up! There are buttons below allowing the user to send an email of the weather to anyone they want, and graphical statistics of upcoming dates. 
![readMeWeather](https://user-images.githubusercontent.com/49849754/65392844-05612f00-dd2e-11e9-94b5-28fa559c91ec.jpg)

This is the main menu users are directed to, enter a keyword of the city you desire to lookup and GeoDB Cities API will show a result of all the cities that share a similar name. 
![readMeMenu](https://user-images.githubusercontent.com/49849754/65392841-03976b80-dd2e-11e9-9748-862c1557d348.jpg)

Navigate to your favorite cities setcion which remembers all the cities you saved. These are organized by when you selected to save your favorite city. 
![readMeFavorites](https://user-images.githubusercontent.com/49849754/65392840-02663e80-dd2e-11e9-80e9-d74de8b49603.jpg)

This is one of the bar charts that the app generates over a 16-day interval which displays the relative temperature of the desired city. Click on "Rain Statistics" to see a graph for the precipitation over a 16-day interval. 
![readMeStatistics](https://user-images.githubusercontent.com/49849754/65392843-04c89880-dd2e-11e9-8476-4ef3e43de12d.jpg)

Below is a basic email outline of the weather the application sends to you. It 
![readMeEmail](https://user-images.githubusercontent.com/49849754/65392839-01cda800-dd2e-11e9-95d6-a74aef44e20e.jpg)

Below are notifications implemented using ControlsFx whenever a city is added to or removed from the favorite city sections. 

![readMeFavorite](https://user-images.githubusercontent.com/49849754/65393025-668a0200-dd30-11e9-90e7-f5434c30d0ec.jpg) ![readMeRemove](https://user-images.githubusercontent.com/49849754/65393027-68ec5c00-dd30-11e9-944a-ae354123a80a.jpg)


## Improvements to be made
<b>Future improvements:<n>
  - [ ] Implement this application into Android or make it into a website (TypeScript). 
  - [ ] Use paid subscriptions to API sites in order to access better real-time weather and to gain more access to city databases.
  - [ ] Store historical weather in external databases (MongoDB or FireBase)

## Usage
First, create an API key in .[SendGrid API](https://sendgrid.com/) and another one in .[WeatherBit API](https://www.weatherbit.io/) and paste the api keys into their respective areas by searching for "YOUR API KEY" in the project. 
Click run on the class StageDisplay inside the package ui to run the project. 

## License
MIT License

Copyright (c) [2019] [Eric Kuo]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

