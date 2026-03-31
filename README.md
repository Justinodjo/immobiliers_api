# Mains d'Or Plus - Backend

Le backend de **Mains d'Or Plus** est une API RESTful construite avec **Spring Boot**. Cette plateforme permet aux salons de beauté, de massage, et d'esthétique de gérer les rendez-vous clients, l'authentification, les paiements et bien plus.

## Prérequis

Avant de démarrer le projet, assurez-vous que vous avez installé les outils suivants :

* [Java 17+](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) (La version 17 ou supérieure est recommandée)
* [Maven](https://maven.apache.org/install.html) pour la gestion des dépendances et de la compilation
* [PostgreSQL](https://www.postgresql.org/download/) (ou une autre base de données compatible JPA)
* [IDE recommandé](https://spring.io/tools) : IntelliJ IDEA, Eclipse, ou VS Code

## Installation

### 1. Clonez le projet

Commencez par cloner le dépôt Git du backend :

```bash
git clone https://github.com/Justinodjo/mainsdorplus_backend.git
cd mainsdorplus_backend
```

### 2. Configuration de la base de données

**PostgreSQL** est utilisé comme base de données dans ce projet. Vous devrez configurer la connexion à la base de données dans le fichier `application.properties` ou `application.yml`.

Exemple de configuration pour PostgreSQL :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mainsdorplus
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Installation des dépendances

Utilisez Maven pour télécharger les dépendances :

```bash
mvn clean install
```

### 4. Démarrer l'application

Une fois les dépendances installées, vous pouvez démarrer l'application Spring Boot avec la commande suivante :

```bash
mvn spring-boot:run
```

Cela démarrera l'application backend sur le port `8080` par défaut.

## Fonctionnalités principales

Le backend de **Mains d'Or Plus** expose plusieurs API REST pour gérer les fonctionnalités suivantes :

* **Gestion des utilisateurs** : Inscription, connexion, gestion des rôles et permissions (avec Spring Security).
* **Gestion des rendez-vous** : Prise de rendez-vous, modification, annulation, et consultation des réservations de services.
* **Notifications** : Notifications push et par email via Spring Mail et Web Push.
* **Gestion des paiements** : Intégration avec des services comme **Twilio** et **Kkiapay** pour la gestion des paiements.
* **Téléchargement de fichiers** : Utilisation d'Apache POI pour la gestion des fichiers Excel et PDF pour l'export des données.
* **WebSocket** : Communication en temps réel pour la gestion des chats et des notifications.

## API Documentation

La documentation de l'API est générée automatiquement avec **Springdoc OpenAPI**. Vous pouvez la consulter en accédant à l'URL suivante après avoir démarré l'application :

```
http://localhost:8080/swagger-ui.html
```

Cette interface Swagger vous permettra de tester les différents endpoints de l'API et d'explorer leurs fonctionnalités.

## Sécurité

L'application utilise **Spring Security** pour sécuriser les endpoints et gérer l'authentification et l'autorisation.

* **JWT Authentication** : Utilisation de **JSON Web Tokens (JWT)** pour sécuriser l'authentification.
* **Rôles** : Les utilisateurs peuvent avoir différents rôles, comme `ADMIN`, `USER`, etc.

Pour tester l'authentification, vous pouvez obtenir un token JWT en envoyant une requête POST à `/login` avec vos identifiants.

Exemple de requête pour obtenir un JWT :

```bash
curl -X POST http://localhost:8080/login -d "username=admin&password=password"
```

Le serveur répondra avec un token JWT que vous pourrez utiliser pour authentifier les autres requêtes.

## Tests

L'application comprend des tests unitaires et d'intégration. Vous pouvez exécuter les tests avec Maven :

```bash
mvn test
```

Cela exécutera tous les tests définis dans le projet et vérifiera que les fonctionnalités du backend fonctionnent comme prévu.

## Structure du projet

Voici un aperçu de la structure du projet :

```bash
mainsdorplus_backend/
├── src/
│   ├── main/
│   │   ├── java/com/immoservice/
│   │   │   ├── controller/         # Les contrôleurs REST pour gérer les requêtes
│   │   │   ├── model/              # Modèles d'entité JPA
│   │   │   ├── repository/         # Repositories pour interagir avec la base de données
│   │   │   ├── service/            # Logique métier
│   │   │   └── config/             # Configuration de sécurité, WebSocket, etc.
│   │   └── resources/
│   │       ├── application.properties  # Configuration de l'application
│   │       └── static/                 # Fichiers statiques (par exemple, HTML, JS)
├── pom.xml                        # Dépendances et configuration Maven
└── README.md                      # Documentation du projet
```

## Contribuer

Les contributions sont les bienvenues ! Si vous souhaitez contribuer au projet, suivez ces étapes :

1. Forkez le projet
2. Créez une branche pour votre fonctionnalité ou correction de bug
3. Effectuez vos modifications
4. Soumettez une pull request

Assurez-vous que tous les tests passent avant de soumettre une pull request.

## Licence

Ce projet est sous licence **MIT**. Consultez le fichier `LICENSE` pour plus d'informations.

---

**Mains d'Or Plus - Backend** : Une solution complète pour la gestion des salons de beauté, de massages et d'esthétique.
