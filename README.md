# Medilabo

Medilabo est une application en microservices destinée aux professionnels de santé. Elle permet de gérer les dossiers patients, les notes des praticiens et l'évaluation du risque de diabète.

Le projet couvre les trois sprints demandés.

## Architecture

L'application est composée de plusieurs microservices indépendants.

L'utilisateur accède au frontend Angular depuis son navigateur sur le port `4200`. Le frontend communique uniquement avec la Spring Cloud Gateway sur le port `8080`, qui constitue le point d'entrée unique de l'application.

La gateway route les requêtes vers les microservices internes :

- **Auth Service** : authentifie l'organisateur et émet des JWT signés.
- **Patient Service** : gère les dossiers patients et communique avec sa base de données MySQL.
- **Notes Service** : gère l'historique des notes médicales et communique avec MongoDB.
- **Assessment Service** : calcule le niveau de risque de diabète. Il ne possède pas de base de données ; il interroge Patient Service pour les données personnelles et Notes Service pour les notes médicales.

La gateway et les trois services métier valident le JWT reçu. L'Assessment Service transmet aussi le JWT aux services qu'il interroge.

| Service | Technologie | Responsabilité |
|---|---|---|
| `frontend-service` | Angular + Nginx | Interface utilisateur |
| `gateway-service` | Spring Cloud Gateway / WebFlux | Point d'entrée unique, routage et validation JWT |
| `auth-service` | Spring Boot / Spring Security | Authentification de l'organisateur et émission de JWT |
| `patient-service` | Spring Boot / MVC / JPA | Gestion des dossiers patients |
| `notes-service` | Spring Boot / MVC / MongoDB | Gestion des notes des praticiens |
| `assessment-service` | Spring Boot / MVC | Calcul du risque de diabète |
| `mysql` | MySQL 8.4 | Base de données relationnelle des patients |
| `mongodb` | MongoDB 8 | Base NoSQL des notes médicales |

## Modèles de données

Chaque microservice possède et gère exclusivement sa propre base de données. Il n'existe pas de jointure SQL entre les microservices.

### Patient Service — MySQL

La base relationnelle `medilabo_patients` contient la table `patients`.

| Champ | Type | Contraintes | Description |
|---|---|---|---|
| `id` | `BIGINT` | clé primaire, générée automatiquement | Identifiant du patient |
| `first_name` | `VARCHAR(150)` | obligatoire | Prénom |
| `last_name` | `VARCHAR(150)` | obligatoire | Nom |
| `birth_date` | `DATE` | obligatoire, date passée | Date de naissance |
| `gender` | `VARCHAR(1)` | obligatoire : `M` ou `F` | Genre |
| `address` | `VARCHAR(255)` | facultatif | Adresse postale |
| `phone` | `VARCHAR(40)` | facultatif | Numéro de téléphone |

### Notes Service — MongoDB

La base NoSQL `medilabo_notes` contient la collection `notes`.

| Champ | Type | Description |
|---|---|---|
| `_id` | `ObjectId` | Identifiant généré par MongoDB |
| `patientId` | `Long` | Identifiant du patient concerné |
| `text` | `String` | Contenu libre de la note médicale |
| `createdAt` | `Instant` | Date et heure de création |
| `modifiedAt` | `Instant` | Date et heure de dernière modification |

Les notes sont récupérées par `patientId` et triées par `createdAt` décroissant afin d'afficher l'observation la plus récente en premier.

`patientId` constitue une référence logique vers le Patient Service. MongoDB ne possède pas de clé étrangère : cette séparation respecte l'indépendance des microservices.

## Sécurité

1. L'organisateur se connecte depuis le frontend Angular.
2. Le frontend envoie les identifiants à `POST /api/auth/login` via la gateway.
3. L'Auth Service authentifie l'organisateur en mémoire et crée un JWT signé avec une clé privée RSA.
4. Le frontend envoie ensuite le JWT dans l'en-tête `Authorization: Bearer <token>`.
5. La gateway valide le JWT avant de router chaque requête.
6. Patient Service, Notes Service et Assessment Service valident également le JWT.
7. Assessment Service transmet le JWT aux deux services qu'il interroge.

L'Auth Service possède la clé privée RSA. Les autres microservices utilisent uniquement la clé publique correspondante.

## Fonctionnalités

### Sprint 1 — Patients

- Authentification de l'organisateur avec JWT.
- Liste des patients.
- Recherche par nom.
- Consultation de la fiche détaillée d'un patient.
- Création et modification d'un patient.
- Validation des données obligatoires.
- MySQL dédié au Patient Service.
- Quatre patients de démonstration insérés automatiquement dans Docker.

### Sprint 2 — Notes médicales

- Affichage de l'historique des notes sur la fiche patient.
- Création de nouvelles notes de consultation.
- Conservation du texte original et des retours à la ligne.
- Tri des notes de la plus récente à la plus ancienne.
- MongoDB dédié au Notes Service.
- Insertion automatique des notes des cas de test dans Docker.

### Sprint 3 — Évaluation du risque

- Assessment Service sans base de données dédiée.
- Récupération des données auprès de Patient Service et Notes Service.
- Recherche insensible à la casse et aux accents.
- Calcul du niveau de risque : `None`, `Borderline`, `In Danger` ou `Early onset`.
- Affichage du niveau de risque dans la fiche patient.
- Validation de l'algorithme avec des tests unitaires.

## Prérequis

- Docker Desktop
- Java 21
- Node.js et npm, pour le développement local du frontend
- Git

## Configuration des secrets

Créer un fichier `.env` à partir du modèle :

```bash
cp .env.example .env
```

Compléter le fichier `.env` :

```properties
DB_USERNAME=medilabo_app
DB_PASSWORD=your_database_password
MYSQL_ROOT_PASSWORD=your_mysql_root_password
ORGANIZER_PASSWORD=your_organizer_password
```

Le fichier `.env` est ignoré par Git et ne doit jamais être commit.

## Génération des clés JWT

La clé privée RSA est ignorée par Git. Après avoir cloné le dépôt, générer une nouvelle paire de clés :

```bash
mkdir -p auth-service/src/main/resources/keys

openssl genpkey -algorithm RSA \
  -out auth-service/src/main/resources/keys/private.pem \
  -pkeyopt rsa_keygen_bits:2048

openssl rsa -pubout \
  -in auth-service/src/main/resources/keys/private.pem \
  -out /tmp/medilabo-public.pem
```

Copier ensuite la clé publique dans les services concernés :

```bash
cp /tmp/medilabo-public.pem auth-service/src/main/resources/keys/public.pem
cp /tmp/medilabo-public.pem gateway-service/src/main/resources/keys/public.pem
cp /tmp/medilabo-public.pem patient-service/src/main/resources/keys/public.pem
cp /tmp/medilabo-public.pem notes-service/src/main/resources/keys/public.pem
cp /tmp/medilabo-public.pem assessment-service/src/main/resources/keys/public.pem
```

## Lancer l'application avec Docker

Depuis la racine du dépôt :

```bash
docker compose up --build -d
```

Vérifier l'état des conteneurs :

```bash
docker compose ps
```

Ouvrir ensuite :

- Frontend : <http://localhost:4200>
- API Gateway : <http://localhost:8080>

Arrêter les conteneurs sans supprimer les données MySQL et MongoDB :

```bash
docker compose down
```

Pour supprimer également les volumes de bases de données :

```bash
docker compose down -v
```

## Tests

### Auth Service

```bash
cd auth-service
./mvnw test
```

### Patient Service

```bash
cd patient-service
./mvnw test
```

### Notes Service

```bash
cd notes-service
./mvnw test
```

### Assessment Service

```bash
cd assessment-service
./mvnw test
```

### Frontend Angular

```bash
cd frontend-service
npm ci
npm test -- --watch=false
npm run build
```

La gateway est vérifiée par les tests d'intégration manuels via Docker, Postman et le frontend.

## Endpoints API

Tous les endpoints, sauf `POST /api/auth/login`, exigent un JWT valide avec le rôle `ORGANIZER`.

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Authentifie l'organisateur et retourne un JWT |
| `GET` | `/api/patients` | Retourne tous les patients |
| `GET` | `/api/patients/{id}` | Retourne un patient par identifiant |
| `GET` | `/api/patients/search?lastName=value` | Recherche par nom |
| `POST` | `/api/patients` | Crée un patient |
| `PUT` | `/api/patients/{id}` | Met à jour un patient |
| `GET` | `/api/notes/patients/{patientId}` | Retourne les notes d'un patient |
| `POST` | `/api/notes/patients/{patientId}` | Ajoute une note à un patient |
| `GET` | `/api/assessments/patients/{patientId}` | Retourne le niveau de risque de diabète |

## Résultats attendus des données de démonstration

| Patient | Niveau de risque attendu |
|---|---|
| TestNone | None |
| TestBorderline | Borderline |
| TestInDanger | In Danger |
| TestEarlyOnset | Early onset |

## Green Code

L'objectif du Green Code est de réduire l'empreinte environnementale d'un service numérique tout au long de son cycle de vie : calcul, mémoire, stockage, transferts réseau et infrastructure.

### Mesures déjà appliquées

- Assessment Service ne possède pas de base de données : il ne stocke pas une copie inutile des patients ou des notes.
- Le calcul de risque est déclenché uniquement à l'ouverture d'une fiche patient ou après l'ajout d'une note ; il n'y a pas de polling automatique.
- Le frontend ne charge que les données d'un patient sélectionné.
- Les notes sont conservées dans MongoDB, adapté à un texte médical non structuré et évolutif.
- Les services sont isolés : chaque microservice possède uniquement les données nécessaires à sa responsabilité.

### Améliorations possibles

- Ajouter une pagination des patients et des notes afin d'éviter de transférer un historique trop volumineux.
- Retourner des DTO dédiés contenant uniquement les champs nécessaires à chaque écran.
- Mesurer régulièrement CPU, mémoire, volume réseau et temps de réponse lors des tests de charge.
- Dimensionner les ressources des conteneurs selon l'usage réel.
- Mettre en place une politique de conservation ou d'archivage des données conforme aux besoins médicaux et réglementaires.
- Réaliser une revue d'écoconception lors des évolutions importantes du projet.

### Références

- [Référentiel général de l'écoconception des services numériques — RGESN 2024](https://ecoresponsable.numerique.gouv.fr/publications/referentiel-general-ecoconception/)
- [Déclaration d'écoconception — Numérique écoresponsable](https://ecoresponsable.numerique.gouv.fr/declaration-ecoconception/)
