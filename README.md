# RossiGo
## Descrizione del Progetto
RossiGo è un'applicazione Java basata su Maven e SQLite per la gestione di una flotta di veicoli in sharing (bici e monopattini), utenti, crediti e noleggi, con un sistema di gestione delle eccezioni e persistenza su database.

## Struttura dei Pacchetti
- `Modello`: Contiene le classi di dominio (`Veicolo`, `Bici`, `Monopattino`, `Utente`, `Noleggio`, `Stato`).
- `Persistenza`: Gestisce la connessione al database (`Database`).
- `Eccezioni`: Raccoglie tutte le eccezioni custom dell'applicazione (`RossiGoException` e figlie).
- `org.example`: Contiene la logica di business (`FlottaService`) e il punto di ingresso.

## Come Eseguire il Progetto
1. Clona il repository.
2. Apri il progetto con un IDE compatibile con Maven (es. IntelliJ IDEA).
3. Esegui il build di Maven per scaricare le dipendenze (`jfiglet`, `codice-fiscale`, `commons-validator`, driver SQLite).
4. Avvia il file `Main.java` situato nel pacchetto `org.example`.

## Scenari Simulati nel Main
Il programma simula i seguenti scenari di test:
1. **Registrazione flotta e utenti**: Inizializzazione di bici, monopattini e utenti con validazione di email e codice fiscale.
2. **Noleggio corretto**: Sblocco di un veicolo da parte di un utente idoneo e successiva terminazione con calcolo del costo e consumo batteria.
3. **Gestione eccezioni e casistiche limite**:
    - Tentativo di sbloccare un veicolo inesistente o già occupato.
    - Tentativo di noleggio con batteria insufficiente (< 15%).
    - Tentativo di sblocco con credito utente insufficiente (< 1.00).
    - Tentativo di mettere in ricarica un veicolo già in uso.

## Motivazione delle Librerie Scelte
- **SQLite JDBC**: Scelto per la sua leggerezza e perché non richiede un server di database esterno, ideale per applicazioni standalone ed esercizi accademici.
- **Apache Commons Validator**: Utilizzato per una validazione robusta e standardizzata degli indirizzi email degli utenti.
- **Libreria Codice Fiscale (`it.kamaladafrica`)**: Per verificare la correttezza formale e di controllo del codice fiscale inserito in fase di registrazione utente.
- **JFiglet**: Utilizzata per generare un banner ASCII art decorativo all'avvio dell'applicazione nel terminale.