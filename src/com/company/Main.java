package com.company;

import java.util.Random;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Stack<Integer> buffer = new Stack<>();
        Stopper stopper = new Stopper();
        Random random = new Random();
	    Thread publisherThread = new Thread(new Publisher(buffer, 3, stopper, random));
	    Thread subscriberThread = new Thread(new Subscriber(buffer, stopper, random));

	    System.out.println("Start");
	    publisherThread.start();
	    subscriberThread.start();

	    Thread.sleep(10 * 1000);
	    stopper.stop();
	    publisherThread.join();
	    subscriberThread.join();
	    System.out.println("Stop");
    }
}

// Klasa zatrzymywania procesu producenta i konsumenta.
class Stopper {
    private boolean _isStopped = false;

    public boolean isStopped() { return _isStopped; }

    public void stop() {
        _isStopped = true;
    }
}

// Producent. Tworzy liczby losowe i dodaje je do bufora. Informuje o tej akcji w konsoli.
// Nie dodaje do bufora jeżeli osiągnął maksymalną wielkość.
class Publisher implements Runnable {
    private final Stack<Integer> _buffer;
    private final int _size;
    private final Random _random;
    private final Stopper _stopper;

    public Publisher(Stack<Integer> buffer, int size, Stopper stopper, Random random) {
        _buffer = buffer;
        _size = size;
        _stopper = stopper;
        _random = random;
    }

    @Override
    public void run() {
        try {
            while (!_stopper.isStopped()) {
                Thread.sleep(_random.nextInt(1000) + 100);
                if (_buffer.size() < _size) {
                    int number = _random.nextInt(100);
                    _buffer.push(number);
                    System.out.println("Pushed " + number + " to buffer.");
                }
            }
        } catch (InterruptedException ex) { }
    }
}

// Konsument. Pobiera liczbę z bufora i wyświetla w konsoli.
// Czeka, jeżeli bufor jest pusty.
class Subscriber implements Runnable {
    private final Stack<Integer> _buffer;
    private final Stopper _stopper;
    private final Random _random;

    public Subscriber(Stack<Integer> buffer, Stopper stopper, Random random) {
        _buffer = buffer;
        _stopper = stopper;
        _random = random;
    }

    @Override
    public void run() {
        try {
            while (!_stopper.isStopped()) {
                Thread.sleep(_random.nextInt(1000) + 100);
                if (_buffer.size() != 0) {
                    int number = _buffer.pop();
                    System.out.println("Get " + number + " from buffer.");
                }
            }
        } catch (InterruptedException ex) { }
    }
}