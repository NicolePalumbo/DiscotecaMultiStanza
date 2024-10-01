import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiscotecaMultiStanza {

    private int[] personeNellePiste = new int[4];  // Numero di persone in ciascuna delle 4 piste
    private final Object lock = new Object();  // Lock per sincronizzare l'accesso alle piste

    // Metodo sincronizzato per far entrare un gruppo in una pista
    public void entraGruppo(int pista, int nPersone) {
        synchronized (lock) {
            personeNellePiste[pista] += nPersone;
            System.out.println(Thread.currentThread().getName() + " è entrato in pista " + (pista + 1) + " con " + nPersone + " persone.");
        }
    }

    // Metodo sincronizzato per far uscire un gruppo da una pista
    public void esceGruppo(int pista, int nPersone) {
        synchronized (lock) {
            personeNellePiste[pista] -= nPersone;
            System.out.println(Thread.currentThread().getName() + " è uscito dalla pista " + (pista + 1) + " con " + nPersone + " persone.");
        }
    }

    // Metodo per stampare il numero di persone in ciascuna pista
    public void stampaPiste() {
        synchronized (lock) {
            for (int i = 0; i < personeNellePiste.length; i++) {
                System.out.println("Persone in pista " + (i + 1) + ": " + personeNellePiste[i]);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DiscotecaMultiStanza discoteca = new DiscotecaMultiStanza();
        Random random = new Random();

        // Creiamo un pool di thread per simulare i gruppi (es: 10 gruppi)
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Creazione e avvio dei thread (gruppi)
        for (int i = 0; i < 10; i++) {
            int nPersone = random.nextInt(5) + 5;  // Ogni gruppo avrà tra 5 e 10 persone
            executor.submit(() -> {
                try {
                    int pistaCorrente = random.nextInt(4);  // Il gruppo entra in una pista casuale
                    discoteca.entraGruppo(pistaCorrente, nPersone);

                    while (true) {
                        // Rimane nella pista per un tempo casuale (es: tra 1 e 3 secondi)
                        Thread.sleep(random.nextInt(2000) + 1000);

                        // Decide casualmente se cambiare pista o uscire e rientrare
                        if (random.nextBoolean()) {
                            // Il gruppo esce
                            discoteca.esceGruppo(pistaCorrente, nPersone);
                            // Rimane fuori per un tempo casuale (es: tra 1 e 2 secondi)
                            Thread.sleep(random.nextInt(2000) + 1000);
                            // Rientra in una pista casuale
                            pistaCorrente = random.nextInt(4);
                            discoteca.entraGruppo(pistaCorrente, nPersone);
                        } else {
                            // Il gruppo cambia pista
                            discoteca.esceGruppo(pistaCorrente, nPersone);
                            pistaCorrente = random.nextInt(4);  // Sceglie una nuova pista casuale
                            discoteca.entraGruppo(pistaCorrente, nPersone);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Stampa periodica del numero di persone in ciascuna pista ogni 500ms
        while (true) {
            discoteca.stampaPiste();
            Thread.sleep(500);  // Stampa ogni 500ms
        }
    }
}
