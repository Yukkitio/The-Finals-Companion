![Logo](https://upload.wikimedia.org/wikipedia/commons/5/5c/The_Finals_logo_stacked.svg)

# The Finals Leaderboard App

## Description

Cette application Android offre une interface intuitive pour consulter le leaderboard du jeu THE FINALS. Utilisant une API dédiée pour récupérer les données en temps réel, elle permet aux utilisateurs de rester à jour avec les performances des joueurs du TOP 10 000. En plus de visualiser le leaderboard, les utilisateurs peuvent ajouter un joueurs au favori pour un accès rapide et effectuer des recherches ciblées pour trouver des informations spécifiques.

## Fonctionnalités

- **Visualisation du Leaderboard :** Affichez le classement actuel des joueurs dans THE FINALS, avec des informations détaillées sur leur rang, perte ou gain de places, et plus.
- **Gestion des Favoris :** Ajoutez et gérez un joueur favori pour accéder rapidement à ses performance et statistiques.
- **Recherche :** Utilisez la fonction de recherche pour trouver rapidement un joueur par son nom et consulter ses détails.
- **Rafraîchissement des Données :** Rafraîchissez les données du leaderboard et du favoris à tout moment pour obtenir les informations les plus récentes.
- **Interface Utilisateur Simpliste :** Naviguez facilement à travers l'application grâce à une interface minimaliste.

## Installation de l'APK

Pour installer l'application sur votre appareil Android, vous pouvez télécharger le fichier APK directement depuis ce dépôt Git. Voici les étapes à suivre :

1. Accédez au dossier `app/release` dans ce dépôt.
2. Téléchargez le fichier `TFC_V1.apk`.

*Notez que l'installation d'applications de sources inconnues peut présenter des risques de sécurité. Assurez-vous de comprendre ces risques avant de procéder.*

## Technologies Utilisées

- Android Studio
- Kotlin
- Retrofit/Gson pour les requêtes API
- SharedPreferences pour la gestion des favoris
- Architecture MVVM

## API Reference

### Récupération du leaderboard

Effectuez une requête GET pour récupérer le leaderboard actuel :

```http
GET https://api.the-finals-leaderboard.com/v1/leaderboard/s2/crossplay?raw=true
```

### Réponse

La réponse sera un tableau d'objet JSON chacun contenant :

| Champ | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `r` | `int` | Le rang du joueur World Wide |
| `name` | `string` | Nom du joueur Embark |
| `ri` | `int` | Index de rang, utilisé pour déterminer l'icône de rang |
| `p` | `int` | ?? |
| `ori` | `int` | Ancien index de rang il y a 24h |
| `or` | `int` | Ancien rang World Wide il y a 24h |
| `op` | `int` | ?? |
| `c` | `int` | Potentiellement pour "cashouts", mais non utilisé dans l'interface |
| `steam` | `string` | Nom d'utilisateur Steam |
| `xbox` | `string` | Nom d'utilisateur Xbox |
| `psn` | `string` | Nom d'utilisateur PSN |

#### Récupération d'informations sur le joueur
Effectuez une requête GET pour récupérer des informations sur un joueur spécifique :
```http
  GET https://api.the-finals-leaderboard.com/v1/leaderboard/s2/crossplay?raw=true&name={player_name}
```

| Paramètre | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `player_name`      | `string` | **Required**. Nom du joueur recherché |

## Remerciements

Merci à ChatGPT pour l'assistance apportée dans le développement de cette application. 

Un remerciement spécial également à [leonlarsson](https://github.com/leonlarsson/the-finals-api) pour le wrapper de l'API de THE FINALS et pour la documentation.

## Comment Contribuer

Les contributions, en particulier dans les domaines de l'optimisation des performances, ainsi que les suggestions d'améliorations de l'UX/UI, sont très appréciées. Si vous avez des idées ou des améliorations à proposer, n'hésitez pas à :

1. Forker le dépôt.
2. Créer une nouvelle branche (`git checkout -b feature/amazingFeature`).
3. Commiter vos modifications (`git commit -m 'Ajout de fonctionnalités incroyables'`).
4. Pousser vers la branche (`git push origin feature/amazingFeature`).
5. Ouvrir une Pull Request.

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE.md) pour plus de détails.
