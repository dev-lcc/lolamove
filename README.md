# L0LAMove

## Summary
* Used clean-code architecture pattern following MVVM(Model-View-ViewModel)
* Setup with multiple modules
	* `app`
		- The main module where Android App screens, application, fragments, resources, and all other modules were integrated.
	* `domain`
		* Consists of datamodels and UseCase interfaces to which the app has direct access to. (UseCase Implementations are abstracted to which are found under `data` module)
	* `data`
		* Consist of UseCase Implementations
		* Also contains business logic codes that bridges multiple datasources(i,e. local and remote) to provide data to each UseCase implementation.
	* `datasource-remote`
		* Consist of business logic codes dealing with remote datasources such as REST API(ex. using Retrofit)
	* `datasource-local`
		* Consist of business logic codes dealing with local datasources such as Local Database(ex. using RoomDB)
* Dependency Injection using Koin 2.0 (https://insert-koin.io/)
* App Project was constructed following Single Activity App pattern.
	* Using 1 Activity to host NavigationView which manages the entire Navigation Graph.

## Navigation Graph
![Navigation Graph](https://i.imgur.com/dddXeBy.jpg)

## Screenshots
### Splashscreen
![Splashscreen](https://i.imgur.com/i97BHYp.png)

### List Of Deliveries
#### Collapsed
![List Of Deliveries - Collapsed](https://i.imgur.com/7SAQ6ug.png)

#### Expanded
![List Of Deliveries - Expanded](https://i.imgur.com/LOmB48r.png)

### Empty Favorites
![Empty Favorites](https://i.imgur.com/8ACrcgl.png)

### Favorites
![Favorites](https://i.imgur.com/pnxQ1EP.png)

### Delivery Detail Screen
#### Expanded
![Delivery Detail Screen - Expanded](https://i.imgur.com/GTtMY6L.png)

#### Collapsed
![Delivery Detail Screen - Collapsed](https://i.imgur.com/dnrsnqB.png)


