# Medilabo

Medilabo est une application en microservices qui aide les professionnels de santé à gérer les dossiers patients et, à terme, à évaluer le risque de diabète.

Ce dépôt contient actuellement les livrables du **Sprint 1**.

## Architecture

```mermaid
    User[Organisateur] --> Frontend[Frontend Angular<br/>Port 4200]
    Frontend --> Gateway[Spring Cloud Gateway<br/>Port 8080]

    Gateway --> Auth[Auth Service<br/>Émetteur JWT]
    Gateway --> Patient[Patient Service<br/>API REST]

    Patient --> MySQL[(MySQL<br/>Base patients)]