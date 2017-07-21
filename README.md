# NavigationLibrary


## Installation

**Option 1:**

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
	dependencies {
	        compile 'com.github.nguyenphuc1995:navigation:1.0.1'
	}

```

**Option 2:**

Download it and import as a module
```
If you dont know how to do this, please watch this video below:
https://www.youtube.com/watch?v=StjwFeSzzl4
```

## Changelog

[21/07/17]
    - fix bug 

## Usage

```
If you have already the Google map object:

Step 1: New GLOBAL Navigation object

Navigation navigation = new Navigation;

//dont new Navigation once again

Step 2: Set some objects

navigation.setData(this,googlemap,mode,GoogleApiDirectionKey)

//this is your activity.

//googleMap is a GoogleMap object.

//mode: 0 vietnamese, 1 english, 2 just direction by maneuver.

//GoogleApiDirectionKey is a String content googleApiDirectionKey.

Step 3: Find direction with start place and destination place

navigation.findDirection(startPlace, destPlace);

//startPlace and destPlace are String objects.

Step 4: After find direction you can start navigation diriving

navigation.startNavigation();
 
Step 5: If you want to stop navigation diriving, you can use method

Navigation.stopNavigation();

```
## Note
*The project have a bug: when you drive at high speed (>50km/h at roundabout and >90km/h at straight). The function DetectWrongWay will return wrong result.* 
*This is not final version*
## Authors

Nguyen Huu Phuc (https://github.com/nguyenphuc1995)

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
