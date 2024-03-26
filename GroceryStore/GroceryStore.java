// name: Phuong Tran (Alex)
// id:   010796179

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class GroceryStoreSemaphores {
  final Semaphore waiting_area;
  final Semaphore produce_area;
  final Semaphore general_area;
  final Semaphore frozen_area;
  final Semaphore cash_register;

  public GroceryStoreSemaphores(Semaphore waiting_area, Semaphore produce_area, Semaphore general_area, Semaphore frozen_area, Semaphore cash_register) {
    this.waiting_area = waiting_area;
    this.produce_area = produce_area;
    this.general_area = general_area;
    this.frozen_area = frozen_area;
    this.cash_register = cash_register;
  }
}

class GroceryStoreAtomicIntegers {
  final AtomicInteger waiting_area_atomic_int;
  final AtomicInteger produce_area_atomic_int;
  final AtomicInteger general_area_atomic_int;
  final AtomicInteger frozen_area_atomic_int;
  final AtomicInteger cash_register_atomic_int;

  public GroceryStoreAtomicIntegers(AtomicInteger waiting_area_lock, AtomicInteger produce_area_lock, AtomicInteger general_area_lock, AtomicInteger frozen_area_lock, AtomicInteger cash_register_lock) {
    this.waiting_area_atomic_int = waiting_area_lock;
    this.produce_area_atomic_int = produce_area_lock;
    this.general_area_atomic_int = general_area_lock;
    this.frozen_area_atomic_int = frozen_area_lock;
    this.cash_register_atomic_int = cash_register_lock;
  }
}

public class GroceryStore {
  static final int WAITING_AREA_CAPACITY = 40;
  static final int PRODUCE_AREA_CAPACITY = 20;
  static final int GENERAL_AREA_CAPACITY = 25;
  static final int FROZEN_AREA_CAPACITY = 30;
  static final int CASH_REGISTER_CAPACITY = 10;

  public static void main(String[] args){
    long start_time = System.nanoTime();
    long elapsed_time_in_milliseconds = 0;
    Random random = new Random(0);
    int time_duration_in_unit = Integer.parseInt(args[0]);
    int time_duration_in_milliseconds = time_duration_in_unit * 100;
    int num_customer_total = Integer.parseInt(args[1]);
    ArrayList<Thread> threads = new ArrayList<Thread>();
    Semaphore waiting_area  = new Semaphore(WAITING_AREA_CAPACITY);
    Semaphore produce_area  = new Semaphore(PRODUCE_AREA_CAPACITY);
    Semaphore general_area  = new Semaphore(GENERAL_AREA_CAPACITY);
    Semaphore frozen_area   = new Semaphore(FROZEN_AREA_CAPACITY);
    Semaphore cash_register = new Semaphore(CASH_REGISTER_CAPACITY);
    AtomicInteger waiting_area_lock  = new AtomicInteger();
    AtomicInteger produce_area_lock  = new AtomicInteger();
    AtomicInteger general_area_lock  = new AtomicInteger();
    AtomicInteger frozen_area_lock   = new AtomicInteger();
    AtomicInteger cash_register_lock = new AtomicInteger();

    GroceryStoreSemaphores s = new GroceryStoreSemaphores(waiting_area,produce_area, general_area,frozen_area,cash_register);
    GroceryStoreAtomicIntegers m = new GroceryStoreAtomicIntegers(waiting_area_lock,produce_area_lock,general_area_lock, frozen_area_lock,cash_register_lock);

    System.out.println("Please open the output.txt file to read the log.");
    System.out.println("Note that the log is not complete until the program stop executing.");
    System.out.println("If you can't find the output.txt file, you may need to refresh your file navigator or type 'ls' again.");

    try {
      PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
      System.setOut(out);
    } catch (FileNotFoundException e) {
      System.err.println("Exception caught: " + e.getMessage());
    }

    for(int i = 0; i < num_customer_total; i++) {
      elapsed_time_in_milliseconds = (System.nanoTime() - start_time) / 1_000_000;
      if(elapsed_time_in_milliseconds > time_duration_in_milliseconds) break;
        Customer c = new Customer(i, s, m);
        Thread t   = new Thread(c);
        threads.add(t);
        t.start();
    }
    System.out.println("*** At this point, no more customer can enter the grocery store ***");

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException ignore) {
      }
    }
  }
}
