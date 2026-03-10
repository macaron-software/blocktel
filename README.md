# BLOCkTEL

Bloqueur d'appels de demarchage pour Android.
Filtre automatiquement les appels entrants provenant des prefixes officiels ARCEP reserves au demarchage telephonique en France.

## Prefixes bloques par defaut

| Prefixe | Region |
|---------|--------|
| 01 62, 01 63 | Ile-de-France |
| 02 70, 02 71 | Nord-Ouest |
| 03 77, 03 78 | Nord-Est |
| 04 24, 04 25 | Sud-Est |
| 05 68, 05 69 | Sud-Ouest |
| 09 48, 09 49 | VoIP national |
| 09 475 | Guadeloupe / St-Martin / St-Barth |
| 09 476 | Guyane |
| 09 477 | Martinique |
| 09 478, 09 479 | La Reunion / Mayotte |

Source : decision ARCEP sur les prefixes dedies au demarchage telephonique.

## Fonctionnement

L'app utilise `CallScreeningService` (Android 10+) pour intercepter les appels entrants.
Pour chaque appel, le numero est normalise (gestion +33 / 0033 / 0) puis compare aux prefixes actifs.
Si le numero correspond, l'appel est silencieusement rejete (pas de sonnerie, pas de notification).

L'appel bloque reste visible dans le journal d'appels systeme (choix delibere pour transparence).

## Permissions requises

- **BIND_SCREENING_SERVICE** : permet au systeme de router les appels entrants vers le service de filtrage
- **READ_PHONE_STATE** : lecture de l'etat du telephone
- **READ_CALL_LOG** : acces au journal d'appels
- **RECEIVE_BOOT_COMPLETED** : redemarrage automatique apres reboot

Au premier lancement, Android demande d'autoriser BLOCkTEL comme service de filtrage d'appels (role `CALL_SCREENING`).

## Build

```bash
# Generer le wrapper Gradle (une seule fois)
gradle wrapper --gradle-version 8.10

# Build debug
./gradlew assembleDebug

# Installer sur appareil connecte
./gradlew installDebug
```

Minimum : Android 10 (API 29).
Cible : Android 15 (API 35).

## Architecture

```
com.macaron.blocktel/
  BlockTelApp.kt           Application (init repos)
  CallBlockerService.kt    CallScreeningService (coeur du blocage)
  NumberNormalizer.kt       Normalisation +33/0033/0
  PrefixRepository.kt      Stockage regles (SharedPreferences)
  BlockedCallLog.kt        Historique appels bloques
  BootReceiver.kt          Relance apres reboot
  MainActivity.kt          Interface utilisateur
  ui/
    PrefixAdapter.kt       Liste des prefixes avec toggles
    RecentCallsAdapter.kt  Historique appels bloques recents
```

Zero dependance externe (hors AndroidX / Material).

## Ajout de prefixes personnalises

Via le menu (+), entrer un prefixe de 3 chiffres minimum.
Les prefixes personnalises peuvent etre supprimes par appui long.
Les prefixes ARCEP par defaut ne peuvent pas etre supprimes, seulement desactives.

## Licence

Usage personnel. Macaron Software.
