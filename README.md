<p align="center">
  <img src="docs/screenshots/main_screen.png" width="280" alt="BLOCkTEL - Ecran principal" />
  &nbsp;&nbsp;&nbsp;
  <img src="docs/screenshots/prefixes_list.png" width="280" alt="BLOCkTEL - Liste des prefixes" />
</p>

<p align="center">
  <strong>Bloqueur d'appels de demarchage pour Android</strong><br/>
  Filtre automatiquement les appels provenant des prefixes ARCEP reserves au demarchage en France.
</p>

<p align="center">
  <a href="#fonctionnement">Fonctionnement</a> &bull;
  <a href="#prefixes-bloques">Prefixes</a> &bull;
  <a href="#installation">Installation</a> &bull;
  <a href="#build">Build</a> &bull;
  <a href="README.en.md">English</a>
</p>

---

# BLOCkTEL

## Fonctionnement

BLOCkTEL utilise `CallScreeningService` (Android 10+) pour intercepter chaque appel entrant.
Le numero est normalise (gestion des formats `+33` / `0033` / `0`) puis compare aux prefixes actifs.
Si le numero correspond a un prefixe de demarchage, l'appel est silencieusement rejete : pas de sonnerie, pas de notification.

L'appel bloque reste visible dans le journal d'appels systeme (choix delibere pour la transparence).

### Caracteristiques

- 17 prefixes ARCEP pre-configures (12 metropolitains + 5 outre-mer)
- Activation/desactivation individuelle par prefixe
- Ajout de prefixes personnalises
- Compteur d'appels bloques (total + aujourd'hui)
- Historique des 200 derniers appels bloques
- Zero dependance externe (hors AndroidX / Material)
- Aucune collecte de donnees, tout reste sur l'appareil

## Prefixes bloques

Les prefixes ci-dessous sont ceux officiellement attribues par l'ARCEP au demarchage telephonique en France. Tous sont actifs par defaut.

### France metropolitaine

| Prefixe | Region |
|---------|--------|
| 01 62, 01 63 | Ile-de-France |
| 02 70, 02 71 | Nord-Ouest |
| 03 77, 03 78 | Nord-Est |
| 04 24, 04 25 | Sud-Est |
| 05 68, 05 69 | Sud-Ouest |
| 09 48, 09 49 | VoIP national |

### Outre-mer

| Prefixe | Territoire |
|---------|-----------|
| 09 475 | Guadeloupe / Saint-Martin / Saint-Barthelemy |
| 09 476 | Guyane |
| 09 477 | Martinique |
| 09 478, 09 479 | La Reunion / Mayotte |

## Installation

### Depuis les releases GitHub

1. Telecharger le dernier APK depuis la page [Releases](https://github.com/macaron-software/blocktel/releases)
2. Installer l'APK sur le telephone (activer "Sources inconnues" si necessaire)
3. Ouvrir BLOCkTEL et accepter l'autorisation de filtrage d'appels

### Via ADB

```bash
adb install blocktel-v1.0.0.apk
```

## Build

```bash
# Cloner le depot
git clone https://github.com/macaron-software/blocktel.git
cd blocktel

# Build debug
./gradlew assembleDebug

# Installer sur appareil connecte
./gradlew installDebug
```

**Prerequis :** Android SDK, JDK 17+.
**Minimum :** Android 10 (API 29). **Cible :** Android 15 (API 35).

## Permissions

| Permission | Usage |
|-----------|-------|
| `BIND_SCREENING_SERVICE` | Permet au systeme de router les appels entrants vers le service de filtrage |
| `READ_PHONE_STATE` | Lecture de l'etat du telephone |
| `READ_CALL_LOG` | Acces au journal d'appels |
| `RECEIVE_BOOT_COMPLETED` | Redemarrage automatique apres reboot |

Au premier lancement, Android demande d'autoriser BLOCkTEL comme service de filtrage d'appels (role `CALL_SCREENING`).

## Architecture

```
com.macaron.blocktel/
  BlockTelApp.kt           Application (initialisation)
  CallBlockerService.kt    CallScreeningService (coeur du blocage)
  NumberNormalizer.kt       Normalisation +33/0033/0
  PrefixRepository.kt      Stockage des regles (SharedPreferences)
  BlockedCallLog.kt        Historique des appels bloques
  BootReceiver.kt          Relance apres reboot
  MainActivity.kt          Interface utilisateur
  ui/
    PrefixAdapter.kt       Liste des prefixes avec toggles
    RecentCallsAdapter.kt  Historique recents
```

## Licence

MIT - Macaron Software
