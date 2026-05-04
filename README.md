# Argent de Poche — Child Pocket Money Manager

Application Android native pour gérer l'argent de poche des enfants.

## Fonctionnalités

- 👨‍👧‍👦 **Gestion multi-profils** : créez un profil par enfant avec nom et couleur personnalisée
- 💰 **Argent de poche hebdomadaire** : définissez un montant et un jour de versement automatique
- ➕➖ **Transactions manuelles** : ajoutez ou retirez de l'argent facilement avec un motif
- 📋 **Historique complet** : consultez toutes les transactions avec date et type
- 💳 **Solde en temps réel** : calculé depuis les transactions, jamais déphasé
- 🔒 **100% local** : aucune connexion réseau, données sur le téléphone uniquement
- 🌍 **Multi-devise** : EUR, USD, GBP, CHF, CAD, JPY
- 🌙 **Thème clair/sombre** : selon le système ou au choix

## Architecture

- **Language** : Kotlin 2.0.21
- **UI** : Jetpack Compose + Material 3
- **Navigation** : Navigation Compose 2.8.4 (type-safe routes)
- **Persistance** : Room 2.6.1
- **DI** : Hilt 2.52
- **Architecture** : MVVM + Clean Architecture (single-module)
- **Fond de tâche** : WorkManager (versement auto toutes les 24h)

## Structure du projet

```
app/src/main/kotlin/com/example/pocketmoney/
├── core/          # Utilitaires (Money, Clock)
├── data/          # Room, DataStore, WorkManager, Repositories impl.
├── domain/        # Modèles, interfaces, use cases
├── di/            # Modules Hilt
└── ui/            # Écrans Compose, ViewModels, Navigation
```

## Instructions de build

### Prérequis

- **JDK 17** (recommandé : Temurin/Corretto 17)
- **Android Studio Ladybug** (2024.2.x) ou plus récent
- **Android SDK** : compileSdk 35, minSdk 26

### Build depuis Android Studio

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/ybonnel/child-pocket-money.git
   ```
2. Ouvrir le projet dans Android Studio
3. Synchroniser Gradle (File → Sync Project with Gradle Files)
4. Lancer sur un émulateur ou appareil physique (Android 8.0+)

### Build depuis la ligne de commande

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (nécessite une signature)
./gradlew assembleRelease

# Tests unitaires
./gradlew testDebugUnitTest

# Lint
./gradlew lint
```

L'APK debug sera dans `app/build/outputs/apk/debug/app-debug.apk`.

### Configuration requise

- Android 8.0 (API 26) minimum
- Android 15 (API 35) cible
- Espace disque : ~50 Mo (base de données locale)

## Utilisation

1. **Ajouter un enfant** : appuyez sur le bouton `+` de l'écran d'accueil
2. **Configurer l'argent de poche** : dans le profil enfant, définissez montant et jour
3. **Ajouter/retirer de l'argent** : depuis l'écran détail, utilisez les boutons `+` et `−`
4. **Supprimer une transaction** : faites glisser la transaction vers la gauche
5. **Changer la devise** : Paramètres → Devise

## Licence

MIT
