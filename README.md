# StyleAnalyzer


https://github.com/user-attachments/assets/aa232595-d79b-45f9-9059-5714f2777e42

## Tech Stack

| Category              | Technology                               |
|--------------------|-----------------------------------------------|
| Language        | Kotlin                   |
| UI      | Jetpack Compose       |
| Architecture   | MVVM + Clean Architecture |
| DI | Hilt                                 |
| Database       | Room                            |
| Navigation     | Jetpack Navigation                    |

## Architecture

```
app/
├── data/           # Data layer (Repository, Database, API)
├── domain/         # Domain layer (UseCase, Model)
├── presentation/   # Presentation layer (UI, ViewModel)
├── di/             # Dependency Injection
└── util/           # Utility functions
```
