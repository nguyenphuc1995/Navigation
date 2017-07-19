# NavigationDrivingLibrary


## Installation

A step by step series of examples that tell you have to get the library:

Download it and import as a module
If you dont know how to do this, please watch this video below:
https://www.youtube.com/watch?v=StjwFeSzzl4
```

## Usage
If you have already the Google map object:
```
Step 1: New Navigation object
Navigation navigation;

navigation = new Navigation(this, googleMap,mode,GoogleApiDirectionKey);
//mode: 0 vietnamese, 1 english, 2 just direction by maneuver;


Step 2: Override 2 method:
public void onLocationChanged(Location location); //The method will call if current location change
public void onDirectionFinderSuccess(); //The method will call if direction finder sucess and you can start navagation.

Step 3: Find direction with start place and destination place
navigationDriving.findDirection(startPlace, destPlace);

Step 4: After find direction you can start navigation diriving
navigation.startNavigation();
 
Step 5: If you want to stop navigation diriving, you can use method
Navigation.stopNavigation();

```
## Note
### The project have a bug: when you drive at high speed (>50km/h at roundabout and >90km/h at strstraight). The function DetectWrongWay will return wrong result. 

## Authors

Nguyen Huu Phuc (https://github.com/nguyenphuc1995/)

## License
--------

    Copyright 2017 SuperClassGroup

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
