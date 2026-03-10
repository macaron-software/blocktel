# BLOCkTEL — Google Play Store Deployment

## Pre-requis

- Compte Google Play Developer (console.play.google.com)
- AAB signe : `app/build/outputs/bundle/release/app-release.aab` (2.2 MB)
- Icone hi-res : `docs/icon-512.png` (512x512)
- Feature graphic : `docs/feature-graphic-1024x500.png` (1024x500)
- Screenshots : `docs/screenshots/` (2 captures)
- Privacy policy : `privacy.html` (a heberger)

## Etape 1 : Heberger la privacy policy

La privacy policy doit etre accessible via une URL publique.
Option recommandee — GitHub Pages :

```bash
# Depuis le repo blocktel
git checkout --orphan gh-pages
git rm -rf .
cp privacy.html index.html
git add index.html
git commit -m "Privacy policy page"
git push origin gh-pages
```

URL resultante : `https://macaron-software.github.io/blocktel/`

## Etape 2 : Creer l'application sur Play Console

1. Aller sur https://play.google.com/console
2. **Creer une application**
   - Nom : `BLOCkTEL`
   - Langue par defaut : Francais (France)
   - Application ou jeu : Application
   - Gratuite ou payante : Gratuite
3. Accepter les declarations

## Etape 3 : Fiche Play Store

### Informations principales
- **Nom** : BLOCkTEL
- **Description courte** : Bloque automatiquement les appels de demarchage (prefixes ARCEP France).
- **Description longue** : copier depuis `fastlane/metadata/android/fr-FR/full_description.txt`

### Illustrations
- **Icone** : `docs/icon-512.png` (512x512 PNG)
- **Image de presentation** : `docs/feature-graphic-1024x500.png` (1024x500 PNG)
- **Captures d'ecran telephone** : `docs/screenshots/main_screen.png` + `docs/screenshots/prefixes_list.png`
  (minimum 2, entre 320px et 3840px par cote, ratio max 2:1)

### Coordonnees
- Email : contact@macaron-software.com

## Etape 4 : Classification du contenu

1. Aller dans **Politique > Classification du contenu**
2. Remplir le questionnaire IARC :
   - Aucun contenu violent
   - Aucun contenu sexuel
   - Aucun achat integre
   - Aucun partage de donnees
3. Resultat attendu : **PEGI 3 / Everyone**

## Etape 5 : Declarations sur les donnees

1. Aller dans **Politique > Section securite des donnees**
2. Repondre :
   - L'application collecte-t-elle des donnees ? **Non**
   - L'application partage-t-elle des donnees ? **Non**
   - Politique de confidentialite : `https://macaron-software.github.io/blocktel/`

### Declaration des permissions sensibles

READ_CALL_LOG et READ_PHONE_STATE sont des permissions sensibles.
Google demandera une justification :

> **Justification suggeree :**
> BLOCkTEL is a call screening application using Android's CallScreeningService API.
> READ_CALL_LOG is required by the system on some Android versions for the
> CallScreeningService to function. READ_PHONE_STATE is required to detect
> incoming call state. No call log data is collected, transmitted, or stored
> beyond the app's local blocked-call counter. The app has no network access
> and no remote server.

## Etape 6 : Publier l'AAB

1. Aller dans **Publication > Production** (ou Tests internes pour commencer)
2. **Creer une release**
3. Uploader `app/build/outputs/bundle/release/app-release.aab`
4. Nom de la release : `1.0.0`
5. Notes de version : `Version initiale — blocage des 17 prefixes ARCEP de demarchage`
6. **Examiner et publier**

### Conseil : commencer par un test interne

1. Aller dans **Tests > Tests internes**
2. Creer une release avec le meme AAB
3. Ajouter des testeurs (par email)
4. Valider que tout fonctionne
5. Ensuite promouvoir vers Production

## Etape 7 : Verifier la cible (API level)

Google Play exige targetSdk >= 33 depuis 2024. BLOCkTEL cible SDK 35 : OK.

## Rebuild

```bash
# Incrementer versionCode dans app/build.gradle.kts avant chaque upload
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

## Notes importantes

- **Le keystore est critique** : si perdu, impossible de mettre a jour l'app sur le Play Store.
  Sauvegarder `app/blocktel-release.keystore` en lieu sur (pas dans git).
- **Delai de review** : premiere soumission = 1 a 7 jours de review par Google.
- **Permissions sensibles** : READ_CALL_LOG peut declencher une review manuelle supplementaire.
  La justification ci-dessus couvre ce cas.
