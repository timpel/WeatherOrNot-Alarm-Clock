# WeatherOrNot Alarm Clock

<p align="center">
  <img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" />
</p>
<p>
An Android Alarm Clock that checks your local weather before it decides to wake you up (or not).

Need an extra 20 minutes to catch the bus if it's raining tomorrow? Maybe if the wind is just right it's worth getting up at dawn to catch the waves? Or if it's snowing, maybe there's just no point in getting up at all today... whatever your needs, WeatherOrNot lets you set an unlimited number of weather-dependent alarms that will ring ONLY if the weather criteria you choose are met at the alarm time.

# How it Works 

Every WeatherOrNotAlarm can have a Default Alarm (which will always ring at the set time) and/or a WeatherAlarm with a selected weather criteria. The ringtone and repeat days of a Default/WeatherAlarm pair can be customized just like a standard Android alarm:
</p>
<div style="display:inline-block;vertical-align:top;">
<img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/screens/Screenshot_20180520-234613.png" width="30%" />
<img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/screens/Screenshot_20180520-234724.png" width="30%" />
<img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/screens/Screenshot_20180520-234813.png" width="30%" />
</div>
</p>

WeatherAlarms can be set to ring based on any of the following criteria:

* Clear Skies
* Cloudy
* Rain
* Snow
* Temperature - Above a given threshold (in Fahrenheit or Celsius)
* Temperature - Below a given threshold (in Fahrenheit or Celsius)
* Wind - Above a given threshold (in mph or km/h) in a given direction (optional)
* Wind - Below a given threshold (in mph or km/h) in a given direction (optional)

<p>
And you can set, edit, and activiate/deactivate an unlimited number of WeatherOrNot alarms:
</p>
</p>
<div style="display:inline-block;vertical-align:top;">
<img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/screens/Screenshot_20180520-234335.png" width="30%" />
<img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/screens/Screenshot_20180520-235300.png" width="30%" />
<img src="https://github.com/timpel/WeatherOrNot-Alarm-Clock/blob/master/screens/Screenshot_20180520-235007.png" width="30%" />
</div>

# Credits

The app gets weather data by connecting to a cloud-hosted server, which in turn makes calls to the wonderful [Dark Sky API](https://darksky.net).
Cute icons were designed by [DinosoftLabs](https://www.flaticon.com/authors/dinosoftlabs) and [Pixel Perfect](https://www.flaticon.com/authors/pixel-perfect) at www.flaticon.com, and used in accordance with flaticon.com license conditions.

("How do I get this great app?" Well, you either 1) wait until I finish testing it out in the wild before putting it up on the Play Store, or 2) if you just can't go another day without a fully-cutomizeable weather-dependent Android alarm clock system, you can compile it from source, at your own risk..)
